package com.wipro.ats.bdre.imcrawler.mr;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.imcrawler.crawler.PropertyConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by AS294216 on 9/29/15.
 */
public class MRMain extends BaseStructure {
    protected static final Logger logger = LoggerFactory.getLogger(MRMain.class);

    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "sub-process-id", "Sub Process id of the step"},
            {"i", "instance-exec-id", "Instance Exec Id for the process"},
    };

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new MRMain().getCommandLine(args, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("sub-process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        /*try {
            Class.forName("org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }*/
        PropertyConfig propertyConfig = PropertyConfig.getPropertyConfig(Integer.parseInt(processId));
        String numMappers = propertyConfig.getNumMappers().toString();
        logger.info("Arguments:- sub Process Id: "+ processId + "instance-exec-id:"+ instanceExecId + "number of mappers:"+numMappers);
        //String numThreads = commandLine.getOptionValue("num-threads");
        String params[] = new String[]{processId, instanceExecId, numMappers};
        int res = ToolRunner.run(new Configuration(), new MRDriver(), params);
        System.exit(res);
    }
}
