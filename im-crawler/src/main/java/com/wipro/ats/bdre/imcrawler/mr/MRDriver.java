package com.wipro.ats.bdre.imcrawler.mr; /**
 * Created by AS294216 on 18-09-2015.
 */


import com.wipro.ats.bdre.ResolvePath;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.mapred.AvroWrapper;
import org.apache.avro.mapred.Pair;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.net.URI;

public class MRDriver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        String processId = args[0];
        String instanceExecId = args[1];
        String mapperCount = args[2];
        String outputPath = args[3];
        
        //set conf for the mappers to use
        conf.setInt("sub.process.id", Integer.parseInt(processId));
        conf.setLong("instance.exec.id", Long.parseLong(instanceExecId));
        conf.setInt("number.of.mappers", Integer.parseInt(mapperCount));
        //conf.setInt("number.of.concurrent.threads", Integer.parseInt(numThread));
        ///conf.set("mapreduce.task.classpath.user.precedence", "true");
        Job job = Job.getInstance(conf);
        job.setJarByClass(MRDriver.class);
        job.setJobName("crawler-" + processId+"-"+instanceExecId);
        AvroJob.setOutputKeySchema(job, Pair.getPairSchema(Contents.getClassSchema(), Schema.create(Type.INT)));
        job.setOutputValueClass(NullWritable.class);

        job.setMapperClass(TokenizerMapper.class);
        job.setNumReduceTasks(0);

        job.setInputFormatClass(CrawlInputFormat.class);

        GetGeneralConfig generalConfig = new GetGeneralConfig();
        GeneralConfig gc = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name");
        conf.set("fs.defaultFS", gc.getDefaultVal());
        conf.set("fs.default.name",gc.getDefaultVal());

        String jtHostName = generalConfig.byConigGroupAndKey("scripts_config", "jobTrackerHostName").getDefaultVal();
        String jtPort = generalConfig.byConigGroupAndKey("scripts_config", "jobTrackerPort").getDefaultVal();

        conf.set("mapred.job.tracker","quickstart.cloudera:8032");

        System.out.println("fs.default.name : - " + conf.get("fs.defaultFS"));
        // It prints uri  as : hdfs://10.214.15.165:9000 or something...
        String uri = conf.get("fs.defaultFS")+outputPath;

        FileSystem fs = FileSystem.get(URI.create(uri),getConf());
        System.out.println("filesystem : - " + fs);

        FileOutputFormat.setOutputPath(job, new Path( ResolvePath.replaceVars(uri)));
        job.waitForCompletion(true);
        return 0;
    }

    public static class TokenizerMapper
            extends Mapper<IntWritable, Text, AvroWrapper<Pair<Contents, Integer>>, NullWritable> {
        private final static IntWritable one = new IntWritable(1);

        public void map(IntWritable key, Text value, Context context
        ) throws IOException, InterruptedException {
            //System.out.println("key = " + key);
            //word.set(value.toString());
            String[] objectContent = value.toString().split("--__--");
            CrawlOutput crawlOutput = new CrawlOutput(Integer.parseInt(objectContent[2]), objectContent[7],
                    objectContent[1], objectContent[5], objectContent[6], objectContent[4], objectContent[0],
                    objectContent[3], objectContent[8].getBytes());

            context.write(new AvroWrapper<Pair<Contents, Integer>>
                    (new Pair<Contents, Integer>(crawlOutput, Integer.parseInt(key.toString()))), NullWritable.get());
        }
    }

}