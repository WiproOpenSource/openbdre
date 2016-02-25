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

import com.wipro.ats.bdre.ResolvePath;
import com.wipro.ats.bdre.datagen.Table;
import com.wipro.ats.bdre.datagen.util.Config;
import com.wipro.ats.bdre.datagen.util.TableUtil;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;


public class Driver extends Configured implements Tool {
    private static final Logger LOGGER = Logger.getLogger(Driver.class);
    /**
     * @param args the cli arguments
     */
    @Override
    public int run(String[] args)
            throws IOException, InterruptedException, ClassNotFoundException {

        Configuration conf = getConf();
        GetGeneralConfig generalConfig = new GetGeneralConfig();
        GeneralConfig gc = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name");
        conf.set("fs.defaultFS", gc.getDefaultVal());

        String processId = args[0];
        Path outputDir = new Path(ResolvePath.replaceVars(args[1]));

        Properties dataProps = Config.getDataProperties(processId);
        Properties tableProps = Config.getTableProperties(processId);

        TableUtil tableUtil = new TableUtil();
        Table table = tableUtil.formTableFromConfig(processId);
        FileSystem fs=FileSystem.get(conf);
        LOGGER.info("Default FS ="+conf.get("fs.defaultFS"));
        //set in the conf for mappers to use
        conf.set(Config.SEPARATOR_KEY, tableProps.getProperty("separator"));
        conf.set(Config.PID_KEY,processId);
        conf.setLong(Config.NUM_ROWS_KEY, Long.parseLong(dataProps.getProperty("numRows")));
        conf.setInt(Config.NUM_SPLITS_KEY, Integer.parseInt(dataProps.getProperty("numSplits")));

        Job job = Job.getInstance(conf);
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

        Path srcDir = mrOutputPath;
        Path destFile = new Path(outputDir.toString() + "/" + table.getTableName());
        FileUtil.copyMerge(fs, srcDir, fs, destFile, true, conf, "");

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


    public static enum Counters {
        CHECKSUM
    }


}