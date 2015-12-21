/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api;

import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by vishnu on 12/14/14.
 * Modified by Arijit
 */
public class CreateRawBaseTables extends ETLBase {
    private static final Logger LOGGER = Logger.getLogger(CreateRawBaseTables.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"}
    };

    public void execute(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        String env = commandLine.getOptionValue("environment-id");

        init(processId, env);
        //Getting stage table information
        String rawTableName = getRawTable().getTableName();
        String rawDbName = getRawTable().getDbName();
        String rawTableDdl = getRawTable().getDdl();
        //Getting Stage view information
        String rawViewName = getRawView().getTableName();
        String rawViewDbName = getRawView().getDbName();
        String rawViewDdl = getRawView().getDdl();
        //Getting core table information
        String baseTableName = getBaseTable().getTableName();
        String baseTableDbName = getBaseTable().getDbName();
        String baseTableDdl = getBaseTable().getDdl();
        //Now create the tables/view if not exists.
        checkAndCreateRawTable(rawDbName, rawTableName, rawTableDdl, env);
        checkAndCreateRawView(rawViewDbName, rawViewName, rawViewDdl, env);
        checkAndCreateBaseTable(baseTableDbName, baseTableName, baseTableDdl, env);
    }

    private void checkAndCreateRawTable(String dbName, String tableName, String ddl, String env) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
            if (!rs.next()) {
                LOGGER.info("Raw table does not exist Creating table " + tableName);
                LOGGER.info("Creating stage table using "+ddl);
                stmt.executeUpdate(ddl);
            }
            LOGGER.debug("Inserting data into the table");
            stmt.close();
            con.close();
            LOGGER.info("Raw load completed.");

        } catch (Exception e) {
            LOGGER.error("Error In RawLoad" + e);
            throw new ETLException(e);
        }

    }

    private void checkAndCreateRawView(String dbName, String stageViewName, String ddl, String env) {
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + stageViewName + "'");
            if (!rs.next()) {
                LOGGER.debug("View does not exist. Creating View " + stageViewName);
                stmt.executeUpdate(ddl);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error " + e);
            throw new ETLException(e);
        }
    }

    private void checkAndCreateBaseTable(String dbName, String baseTable, String ddl, String env) {
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + baseTable + "'");
            if (!rs.next()) {
                LOGGER.info("Base table does not exist.Creating Table " + baseTable);
                stmt.executeUpdate(ddl);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error " + e);
            throw new ETLException(e);
        }
    }
}