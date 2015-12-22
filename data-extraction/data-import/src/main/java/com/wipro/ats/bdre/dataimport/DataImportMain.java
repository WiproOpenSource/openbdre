/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.dataimport;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by MI294210 on 05-02-2015.
 */
public class DataImportMain extends BaseStructure {

    private static final Logger LOGGER = Logger.getLogger(DataImportMain.class);

    //Created PARAMS_STRUCTURE variable to get the environment value.
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of the import process which requires properties"},
            {"bid", "batch-id", " Target batch id of the process"},
            {"cg", "config-group", "Configuration group of the process to run "},
            {"eid", "instance-exec-id", "Instance execution id of the running process "}
    };

    public static void main(String[] args) {
        DataImportMain dataImportMain = new DataImportMain();
        dataImportMain.execute(args);

    }

    private void execute(String[] params) {
        try {

            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String processId = commandLine.getOptionValue("process-id");
            String configGroup = commandLine.getOptionValue("config-group");
            String batchId = commandLine.getOptionValue("batch-id");
            String instanceExecId = commandLine.getOptionValue("instance-exec-id");

            //fetching import table common configurations
            GetProperties getProperties = new GetProperties();
            Properties commonProperties = getProperties.getProperties(processId, configGroup);

            //fetching columns

            String[] columns = null;
            if (commonProperties.getProperty("columns") != null) {
                String colList=commonProperties.getProperty("columns");
                String columnList=colList.replaceAll("\\s+","");
                columns=columnList.split(",");
            }

            String[] param = {processId, batchId,instanceExecId};
            int result = 0;
            result = ToolRunner.run(new Configuration(), new HDFSImport(commonProperties, columns), param);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
            throw new ETLException(e);
        }
    }


}


