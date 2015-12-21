/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.dataimport;

import com.cloudera.sqoop.SqoopOptions;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;
import org.apache.sqoop.tool.ImportTool;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * Created by MI294210 on 05-02-2015.
 */


public class HDFSImport extends Configured implements Tool {
    private static final Logger LOGGER = Logger.getLogger(HDFSImport.class);

    private Properties commonProperties;
    private String processId;
    private String batchId;
    private String instanceExecId;
    private String env;
    private int size = 0;
    private String[] cols;
    private String tableName;
    private String lastValue;
    private String prevLastValue;
    private String incrementMode;
    private String query;


    public HDFSImport(Properties commonProperties, String[] columns) {
        this.commonProperties = commonProperties;
        if (columns != null) {
            size = columns.length;
            cols = new String[size];
            for (int i = 0; i < size; i++)
                cols[i] = columns[i];
        }
    }

    public int run(String[] param) throws Exception {

        processId = param[0];
        batchId = param[1];
        instanceExecId = param[2];
        env = param[3];

        Configuration conf = getConf();

        tableName = commonProperties.getProperty("table");
        if (commonProperties.getProperty("incr.mode") != null) {
            incrementMode = commonProperties.getProperty("incr.mode");
        }
        if (commonProperties.getProperty("query") != null) {
            query = commonProperties.getProperty("query");
        }
        String driver = commonProperties.getProperty("driver");
        Class.forName(driver).newInstance();

        try {
            SqoopOptions options = new SqoopOptions();
            options.setDriverClassName(driver);

            //reading properties from IMConfig file
            String targetDir = IMConfig.getProperty("data-import.target-dir", env);
            String jarOutputDir = IMConfig.getProperty("data-import.jar-output-dir", env) + "/" + processId + "/" + batchId;
            String hadoopHome = IMConfig.getProperty("data-import.hadoop-home", env);

            //setting the parameters of sqoopOption
            options.setHadoopHome(hadoopHome);
			//options.setHadoopMapRedHome(hadoopHome);
            options.setJarOutputDir(jarOutputDir);
            String outputDir = targetDir + "/" + processId + "/tmp";
            options.setTargetDir(outputDir);

            options.setConnManagerClassName("org.apache.sqoop.manager.GenericJdbcManager");
            options.setConnectString(commonProperties.getProperty("db"));
            options.setUsername(commonProperties.getProperty("username"));
            options.setPassword(commonProperties.getProperty("password"));

            int mappers = Integer.parseInt(commonProperties.getProperty("mappers"));
            options.setNumMappers(mappers);
            if (mappers > 1) {
                options.setSplitByCol(commonProperties.getProperty("splitbycol"));
            }
            options.setFileLayout(SqoopOptions.FileLayout.valueOf(commonProperties.getProperty("file.layout")));
            options.setJobName("importJob");


            //Checking the condition for importing by table ,columns or query
            String importType = commonProperties.getProperty("import");
            if (null != commonProperties.getProperty("query") && "" != commonProperties.getProperty("query")) {
                options.setSqlQuery(query);//import using the query

            } else {
                if (null != commonProperties.getProperty("columns")) {
                    if (size != 0) {

                        options.setTableName(tableName);
                        options.setColumns(cols);        //importing table or columns

                    }
                }

                if (!("None".equalsIgnoreCase(incrementMode)) && incrementMode != null) {
                    ProcessLog processLog = new ProcessLog();
                    ProcessLogInfo processLogInfo = new ProcessLogInfo();
                    String logCategory;
                    logCategory = "IncrementalImport";
                    String msgId = "last value";
                    processLogInfo = processLog.getLastValue(processId, msgId, logCategory);
                    if (processLogInfo != null) {
                        lastValue = processLogInfo.getMessage();
                        prevLastValue = lastValue;
                    }

                    options.setIncrementalMode(SqoopOptions.IncrementalMode.valueOf(incrementMode));
                    options.setIncrementalTestColumn(commonProperties.getProperty("check.col"));
                    options.setIncrementalLastValue(lastValue);

                }

            }


            //running the import job
            int ret = new ImportTool().run(options);
            if (ret == 0) {

                lastValue = options.getIncrementalLastValue();
                LOGGER.debug(lastValue);
                LOGGER.debug(prevLastValue);

                //adding the process log
                ProcessLog processLog = new ProcessLog();
                ProcessLogInfo processLogInfo = new ProcessLogInfo();
                processLogInfo.setProcessId(Integer.parseInt(processId));
                processLogInfo.setAddTs(new Timestamp(new Date().getTime()));
                processLogInfo.setInstanceRef(Long.parseLong(instanceExecId));


                if (prevLastValue != null && prevLastValue.equals(lastValue)) {
                    LOGGER.info("No new records imported.");
                    processLogInfo.setLogCategory("IncrementalImport");
                    processLogInfo.setMessage("0");
                    processLogInfo.setMessageId("Number of imported records");
                    processLog.log(processLogInfo);
                } else {

                    LOGGER.info("Imported sucessfully.The file will be registered. ");

                    Path outputDirPath = new Path(targetDir);
                    FileSystem srcFs = outputDirPath.getFileSystem(getConf());
                    FileSystem destFs = outputDirPath.getFileSystem(getConf());

                    //temporary directory to store multiple partitions
                    Path tmpDir = new Path(outputDir);
                    //merging output files to one target file
                    Path targetFile = new Path(targetDir + "/" + processId + "/" + batchId);
                    if (srcFs.exists(tmpDir)) {
                        FileUtil.copyMerge(srcFs, tmpDir, destFs, targetFile, true, conf, "");
                    }


                    //Return file info oozie params
                    RegisterFileInfo registerFileInfo = new RegisterFileInfo();
                    registerFileInfo.setBatchId(Long.parseLong(batchId));
                    registerFileInfo.setCreationTs(new Timestamp(new Date().getTime()));
                    FileChecksum hdfsChecksum = destFs.getFileChecksum(targetFile);
                    String fileHash = hdfsChecksum == null ? "0" : hdfsChecksum.toString();
                    registerFileInfo.setFileHash(fileHash);
                    registerFileInfo.setFileSize(destFs.getFileStatus(targetFile).getLen());
                    registerFileInfo.setPath(targetFile.toString());
                    registerFileInfo.setSubProcessId(Integer.parseInt(processId));
                    OozieUtil oozieUtil = new OozieUtil();
                    oozieUtil.persistBeanData(registerFileInfo, false);

                    if (null != commonProperties.getProperty("query") && "" != commonProperties.getProperty("query")) {
                        //adding log for import by query
                        processLogInfo.setLogCategory("ImpQuery");
                        processLogInfo.setMessage(query);
                        processLogInfo.setMessageId("query");
                        processLog.log(processLogInfo);
                    } else {
                        //adding log for incremental import
                        if (!("None".equalsIgnoreCase(incrementMode)) && incrementMode != null) {
                            processLogInfo.setLogCategory("IncrementalImport");
                            processLogInfo.setMessage(lastValue);
                            processLogInfo.setMessageId("last value");
                            processLog.log(processLogInfo);

                                   /* processLogInfo.setLogCategory("IncrImport");
                                    long numRecords = ConfigurationHelper.getNumMapOutputRecords();
                                    String newRecords = Long.toString(numRecords);
                                    processLogInfo.setMessage("0");
                                    processLogInfo.setMessageId("Number of imported records");
                                    processLog.log(processLogInfo);
                                   */


                        }
                        //adding log for normal import
                        else {

                            processLogInfo.setLogCategory("Import");
                            processLogInfo.setMessage(tableName);
                            processLogInfo.setMessageId("table name");
                            processLog.log(processLogInfo);

                        }

                    }


                }

            }


        } catch (Exception e) {
            throw new ETLException(e);
        }
        return 0;
    }
}

