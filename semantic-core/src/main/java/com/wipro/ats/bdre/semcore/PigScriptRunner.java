package com.wipro.ats.bdre.semcore;

import com.wipro.ats.bdre.IMConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.pig.ExecType;
import org.apache.pig.PigServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by su324335 on 7/25/16.
 */
public class PigScriptRunner {

    public static void main(String[] args) throws Exception{
        String script = args[0];
        System.out.println("script = " + script);
        Configuration config = new Configuration();
        config.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
        config.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        config.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name"));
        config.set("pig.use.overriden.hadoop.configs","true");
        config.set("fs.default.name","hdfs://quickstart.cloudera:8020");
        config.set("mapred.job.tracker","quickstart.cloudera:8032");

        FileSystem fs = FileSystem.get(config);

        PigServer pigServer = new PigServer(ExecType.MAPREDUCE,config);
        Integer noOfParams = args.length-1;
        Map<String,String> paramMap = new HashMap<String,String>();

        for(int i=1; i<noOfParams;i++)
        {
            String[] param = args[i].split("=");
            paramMap.put(param[0],param[1]);
        }
        pigServer.registerScript(script,paramMap);

    }

}
