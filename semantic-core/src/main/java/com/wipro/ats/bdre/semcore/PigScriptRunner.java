package com.wipro.ats.bdre.semcore;

import com.wipro.ats.bdre.IMConfig;
import org.apache.hadoop.conf.Configuration;
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
        Configuration config = new Configuration();
        config.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name"));

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
