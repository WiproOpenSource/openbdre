/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.datagen;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.datagen.mr.Driver;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by arijit on 3/1/15.
 */
public class GeneratorMain extends BaseStructure {
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "sub-process-id", "Sub Process id of the step"},
            {"o", "output-path", "Output path of the data generation job where the final output is stored"},
    };
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new GeneratorMain().getCommandLine(args, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("sub-process-id");
        String outputDir = commandLine.getOptionValue("output-path");
        String params[] =new String[]{processId,outputDir};
        int res = ToolRunner.run(new Configuration(), new Driver(), params);
        System.exit(res);
    }
}
