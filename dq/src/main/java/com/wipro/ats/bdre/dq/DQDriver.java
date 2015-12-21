/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */
package com.wipro.ats.bdre.dq;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * @author Satish Kumar
 *         <p/>
 *         MapReduce program that uses the Drool API to validate the records based on rules defined in DroolFile.
 *         <p/>
 *         To run: bin/hadoop jar DQValidator.jar com.wipro.ats.bdre.dq.DataQualityCheckJob
 *         <input-file-path> <output-dir-path> <drl-file-path>
 */

public class DQDriver extends Configured implements Tool {
    private static Logger LOGGER = Logger.getLogger(DQDriver.class);

    public int run(String[] arg) throws Exception {
        String processId = arg[0];
        String env = arg[1];
        String sPath = arg[2];
        String destDir = arg[3];

        Properties props = new GetProperties().getProperties(processId, "dq");
        LOGGER.debug("props=" + props);
        Configuration conf = getConf();

        conf.set("dq.process.id", processId);
        conf.set("dq.env.id", env);
        Job job = Job.getInstance(conf);
        job.setJobName("Data Quality " + processId);
        job.setJarByClass(DQDriver.class);
        job.setMapperClass(DQMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        //Reducer is not required
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        Path inputFilePath= new Path(sPath);
        FileInputFormat.addInputPath(job,inputFilePath);
        FileOutputFormat.setOutputPath(job, removeIfExistAndSetOutputPath(conf, destDir));
        MultipleOutputs.addNamedOutput(job, DQConstants.GOOD_RECORDS_FILE,
                TextOutputFormat.class, Text.class, NullWritable.class);
        MultipleOutputs.addNamedOutput(job, DQConstants.BAD_RECORDS_FILE,
                TextOutputFormat.class, Text.class, NullWritable.class);
        MultipleOutputs.addNamedOutput(job, DQConstants.FILE_REPORT_FILE,
                TextOutputFormat.class, Text.class, NullWritable.class);

        if (!job.waitForCompletion(true)) {
            return 1;
        }


        Path outputDir = new Path(destDir);
        FileSystem srcFs = outputDir.getFileSystem(getConf());
        FileSystem destFs = outputDir.getFileSystem(getConf());

        //Valid Records
        Path goodFilesSrcDir = new Path(destDir + "/" + DQConstants.INTERMEDIATE_GOOD_RECORD_OUTPUT_DIR);
        //Input and quality filtered file should have same name (but different path)
        Path goodDestFile = new Path(destDir + "/" + inputFilePath.getName());
        if(srcFs.exists(goodFilesSrcDir)) {
            FileUtil.copyMerge(srcFs, goodFilesSrcDir, destFs, goodDestFile, true, conf, "");
        }
        // Invalid Records
        Path badFilesSrcDir = new Path(destDir + "/" + DQConstants.INTERMEDIATE_BAD_RECORD_OUTPUT_DIR);
        Path badDestFile = new Path(destDir + "/" + DQConstants.BAD_RECORDS_FILE);
        if(srcFs.exists(badFilesSrcDir)) {
            FileUtil.copyMerge(srcFs, badFilesSrcDir, destFs, badDestFile, true, conf, "");
        }

        // Preparing report aggregation job
        Job fileReportAggregationJob = Job.getInstance(conf);
        fileReportAggregationJob.setJobName("File Report Computing "+ processId);
        fileReportAggregationJob.setJarByClass(DQMain.class);

        fileReportAggregationJob.setMapperClass(DQFileReportMapper.class);
        fileReportAggregationJob.setMapOutputKeyClass(Text.class);
        fileReportAggregationJob.setMapOutputValueClass(IntWritable.class);

        fileReportAggregationJob.setReducerClass(DQFileReportReducer.class);
        fileReportAggregationJob.setOutputKeyClass(Text.class);
        fileReportAggregationJob.setOutputValueClass(Text.class);

        fileReportAggregationJob.setNumReduceTasks(1);

        Path fileReportDir = new Path(destDir + "/" + DQConstants.INTERMEDIATE_REPORT_OUTPUT_DIR);
        Path fileReportOutputDir = new Path(destDir + "/" + DQConstants.AGGREGATED_REPORT_PLACEHOLDER_FOLDER);

        FileInputFormat.addInputPath(fileReportAggregationJob, fileReportDir);
        FileOutputFormat.setOutputPath(fileReportAggregationJob, fileReportOutputDir);

        if (!fileReportAggregationJob.waitForCompletion(true)) {
            return 1;
        }

        // Merge Report Records MR stuffs
        Path reportsSrcDir = new Path(destDir + "/" + DQConstants.AGGREGATED_REPORT_PLACEHOLDER_FOLDER);
        Path reportsDestFile = new Path(destDir + "/" + DQConstants.FILE_REPORT_FILE);
        FileUtil.copyMerge(srcFs, reportsSrcDir, destFs, reportsDestFile, true, conf, "");

        Path reportDestFile = new Path(outputDir.toString() + "/"
                + DQConstants.FILE_REPORT_FILE);
        //Read the report file from HDFS and report the percentage
        DQStats dqStats = getQualityStats(getConf(), reportDestFile);
        LOGGER.info("Percentage of good records :" + dqStats.getGoodPercent());
        props = new GetProperties().getProperties(processId, "dq");
        String strThreshold=props.getProperty("min.pass.threshold.percent");
        float threshold=Float.parseFloat(strThreshold);
        dqStats.setThreshold(threshold);
        //Update the result in metadata
        logResult(dqStats, processId, 0L);
        if(dqStats.getGoodPercent()<threshold){
            LOGGER.error("DQ check did not pass");
            throw new DQValidationException(dqStats);
        }
        LOGGER.info(dqStats);
        FileChecksum hdfsChecksum = destFs.getFileChecksum(goodDestFile);
        String fileHash=hdfsChecksum==null?"0":hdfsChecksum.toString();
        //Return file info oozie params
        RegisterFileInfo registerFileInfo = new RegisterFileInfo();
        registerFileInfo.setBatchId(null);
        registerFileInfo.setCreationTs(new Timestamp(new Date().getTime()));
        registerFileInfo.setFileHash(fileHash);
        registerFileInfo.setFileSize(destFs.getFileStatus(goodDestFile).getLen());
        registerFileInfo.setPath(goodDestFile.toString());
        registerFileInfo.setSubProcessId(Integer.parseInt(processId));
        OozieUtil oozieUtil = new OozieUtil();
        oozieUtil.persistBeanData(registerFileInfo, false);

        return 0;
    }

    private Path removeIfExistAndSetOutputPath(Configuration conf, String path) throws IOException {
        FileSystem fileSystem = FileSystem.get(conf);
        Path outputPath = new Path(path);
        fileSystem.delete(outputPath);
        return outputPath;
    }

    private DQStats getQualityStats(Configuration conf, Path outputDir) throws IOException {
        int goodRecords = 0;
        int badRecords = 0;
        FileSystem destFs = outputDir.getFileSystem(getConf());
        FSDataInputStream in = destFs.open(outputDir);
        OutputStream out = new OutputStream() {
            private StringBuilder string = new StringBuilder();
            @Override
            public void write(int x) throws IOException {
                this.string.append((char) x);
            }
            public String toString() {
                return this.string.toString();
            }
        };

        IOUtils.copyBytes(in, out, conf, false);

        String raw = out.toString();

        for (String str : raw.split("\n")) {
            String[] tokens = str.toString().split("\t");
            if (tokens[0].toString().equals(DQConstants.GOOD_RECORDS_FILE.trim())) {
                goodRecords = Integer.parseInt(tokens[1]);
            } else if (tokens[0].toString().equals(DQConstants.BAD_RECORDS_FILE.trim())) {
                badRecords = Integer.parseInt(tokens[1]);
            }
        }
        DQStats dqStats=new DQStats();
        dqStats.setGoodPercent((goodRecords * 100.0F) / (goodRecords + badRecords));
        dqStats.setNumBad(badRecords);
        dqStats.setNumGood(goodRecords);
        return dqStats;
    }
    private void logResult(DQStats dqStats,String processId, Long instanceRef){
        ProcessLog processLog=new ProcessLog();
        ProcessLogInfo processLogInfo = new ProcessLogInfo();
        processLogInfo.setProcessId(new Integer(processId));
        processLogInfo.setAddTs(new Date());
        processLogInfo.setInstanceRef(instanceRef);
        processLogInfo.setLogCategory("dq");

        //Logging num good records
        processLogInfo.setMessageId("good.records.count");
        processLogInfo.setMessage(dqStats.getNumGood() + "");
        LOGGER.debug("Process id is :"+ processLogInfo.getProcessId()+" TS is "+ processLogInfo.getAddTs()+"instance ref"+processLogInfo.getInstanceRef()+"" +
                "message"+processLogInfo.getMessage());
        processLog.log(processLogInfo);
        //Logging num bad records
        processLogInfo.setMessageId("rejected.records.count");
        processLogInfo.setMessage(dqStats.getNumBad()+"");
        processLog.log(processLogInfo);
        //Logging percent
        processLogInfo.setMessageId("good.records.percent");
        processLogInfo.setMessage(dqStats.getGoodPercent() + "%");
        processLog.log(processLogInfo);

        //Logging threshold
        processLogInfo.setMessageId("good.records.threshold");
        processLogInfo.setMessage(dqStats.getThreshold() + "%");
        processLog.log(processLogInfo);

        //Logging status
        processLogInfo.setMessageId("did.dq.pass");
        processLogInfo.setMessage((dqStats.getGoodPercent()>= dqStats.getThreshold())+"");
        processLog.log(processLogInfo);

    }
}