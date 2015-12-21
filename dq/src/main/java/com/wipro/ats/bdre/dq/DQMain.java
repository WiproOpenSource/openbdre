/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.dq;

import com.wipro.ats.bdre.BaseStructure;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

import java.util.HashMap;

/**
 * Created by arijit on 2/27/15.
 */
public class DQMain extends BaseStructure {
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of the DQ job"},
            {"s", "source-file-path", "File path of the source file to be validated. It should be one single file or use list-of-files style input."},
            {"d", "destination-directory", "Directory directory"}

    };

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new DQMain().getCommandLine(args, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        String env = commandLine.getOptionValue("environment-id");
        String sPath = commandLine.getOptionValue("source-file-path");
        String destDir = commandLine.getOptionValue("destination-directory");

        int result=0;
        if(sPath.indexOf(';')!=-1 || sPath.indexOf(',')!=-1){
            String[] lof = sPath.split(";");
            String entries[] = lof[0].split(",");
            String[] params = {processId, env, entries[2], destDir};
            result = ToolRunner.run(new Configuration(),new DQDriver(), params);

        }else{
            String[] params = {processId, env, sPath, destDir};
            result = ToolRunner.run(new Configuration(),new DQDriver(), params);
        }

        System.exit(result);

    }
}
