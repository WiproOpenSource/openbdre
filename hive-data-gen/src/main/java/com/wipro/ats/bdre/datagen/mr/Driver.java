/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.datagen.mr;

import com.wipro.ats.bdre.datagen.Table;
import com.wipro.ats.bdre.datagen.util.Config;
import com.wipro.ats.bdre.datagen.util.TableUtil;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;


public class Driver extends Configured implements Tool {

    /**
     * @param args the cli arguments
     */
    public int run(String[] args)
            throws IOException, InterruptedException, ClassNotFoundException {

        Configuration conf = getConf();

        String processId = args[0];
        Path outputDir = new Path(args[1]);
        if (outputDir.getFileSystem(getConf()).exists(outputDir)) {
            throw new IOException("Output directory " + outputDir +
                    " already exists.");
        }
        Properties dataProps = Config.getDataProperties(processId);
        Properties tableProps = Config.getTableProperties(processId);
        TableUtil tableUtil = new TableUtil();
        Table table = tableUtil.formTableFromConfig(processId);


        //set in the conf for mappers to use
        conf.set(Config.SEPARATOR_KEY, tableProps.getProperty("separator"));
        conf.set(Config.PID_KEY,processId);
        conf.setLong(Config.NUM_ROWS_KEY, Long.parseLong(dataProps.getProperty("numRows")));
        conf.setInt(Config.NUM_SPLITS_KEY, Integer.parseInt(dataProps.getProperty("numSplits")));

        Job job = Job.getInstance(new Cluster(conf), conf);
        Path mrOutputPath = new Path(outputDir.toString() + "/MROUT/" + table.getTableName());
        FileOutputFormat.setOutputPath(job, mrOutputPath);
        job.setJobName("Datagen-" + table.getTableName());
        job.setJarByClass(Driver.class);
        job.setMapperClass(RecordGenMapper.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(RangeInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.waitForCompletion(true);

        //merge and create a single file
        FileSystem srcFs = outputDir.getFileSystem(getConf());
        FileSystem destFs = outputDir.getFileSystem(getConf());
        Path srcDir = mrOutputPath;
        Path destFile = new Path(outputDir.toString() + "/" + table.getTableName());
        FileUtil.copyMerge(srcFs, srcDir, destFs, destFile, true, conf, "");

        //Return file info oozie params
        RegisterFileInfo registerFileInfo=new RegisterFileInfo();
        registerFileInfo.setBatchId(null);
        registerFileInfo.setCreationTs(new Timestamp(new Date().getTime()));
        registerFileInfo.setFileHash("0");
        registerFileInfo.setFileSize(0L);
        registerFileInfo.setPath(destFile.toString());
        registerFileInfo.setSubProcessId(Integer.parseInt(processId));
        OozieUtil oozieUtil= new OozieUtil();
        oozieUtil.persistBeanData(registerFileInfo,false);
        return 0;
    }


    public static enum Counters {CHECKSUM}


}