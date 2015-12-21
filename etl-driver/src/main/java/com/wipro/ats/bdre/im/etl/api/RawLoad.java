/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api;

import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by vishnu on 12/14/14.
 * Modified by Arijit
 */
public class RawLoad extends ETLBase {
    private static final Logger LOGGER = Logger.getLogger(RawLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"lof", "list-of-files", " List of files"}
    };

    public void execute(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        String env = commandLine.getOptionValue("environment-id");
        String listOfFiles = commandLine.getOptionValue("list-of-files");
        init(processId, env);
        //Getting raw table information
        String rawTableName = getRawTable().getTableName();
        String rawDbName = getRawTable().getDbName();
        String rawTableDdl = getRawTable().getDdl();
        //Now load file to table
        loadRawLoadTable(rawDbName, rawTableName, rawTableDdl, listOfFiles, env);

    }

    private void loadRawLoadTable(String dbName, String tableName, String ddl, String listOfFiles, String env) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            String[] files = listOfFiles.split(IMConstant.FILE_ROW_SEPERATOR);
            HashMap<String, String> batchFiles = new HashMap<String, String>();
            for (int i = 0; i < files.length; i++) {
                String[] columns = files[i].split(IMConstant.FILE_FIELD_SEPERATOR);
                batchFiles.put(columns[0], columns[2]);
            }
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();

            LOGGER.debug("Inserting data into the table");

            for (String key : batchFiles.keySet()) {
                String query = "LOAD DATA INPATH '" + batchFiles.get(key) + "' OVERWRITE INTO TABLE " + tableName
                + " PARTITION (batchid='" + key + "')";
                LOGGER.info("Raw load query " + query);
                stmt.executeUpdate(query);
            }
            stmt.close();
            con.close();
            LOGGER.info("Raw load completed.");

        } catch (Exception e) {
            LOGGER.error("Error In RawLoad" + e);
            throw new ETLException(e);
        }

    }


}