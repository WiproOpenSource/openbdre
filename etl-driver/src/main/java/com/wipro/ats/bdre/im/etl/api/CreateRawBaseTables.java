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

package com.wipro.ats.bdre.im.etl.api;

import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

        init(processId);
        //Getting raw table information
        Integer rawLoadProcessId = rawLoad.getProcessId();

        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(rawLoadProcessId.toString(), "raw-table");
        String rawTableName = rawPropertiesOfTable.getProperty("table-name");
        String rawDbName = rawPropertiesOfTable.getProperty("table-db");
        String rawInputFormat = rawPropertiesOfTable.getProperty("input-format");
        String rawOutputFormat = rawPropertiesOfTable.getProperty("output-format");
        String rawSerdeClass = rawPropertiesOfTable.getProperty("serde");
        String rawSerdeProperties="";
        String rawTableProperties="";
        String rawColumnList="";
        GetProperties getPropertiesOfRawColumns = new GetProperties();
        java.util.Properties rawPropertiesOfColumns = getPropertiesOfRawColumns.getProperties(rawLoadProcessId.toString(), "raw-table");
        Enumeration columns = rawPropertiesOfColumns.propertyNames();
        List<String> rawColumns = new ArrayList<>();
        if (rawPropertiesOfColumns.size() != 0) {
            while (columns.hasMoreElements()) {
                String key = (String) columns.nextElement();
                rawColumns.add(rawPropertiesOfColumns.getProperty(key));
            }
        }

        GetProperties getPropertiesOfRawDataTypes = new GetProperties();
        java.util.Properties rawPropertiesOfDataTypes = getPropertiesOfRawDataTypes.getProperties(rawLoadProcessId.toString(), "raw-data-types");
        Enumeration dataTypes = rawPropertiesOfDataTypes.propertyNames();
        List<String> rawDataTypes = new ArrayList<>();
        if (rawPropertiesOfColumns.size() != 0) {
            while (dataTypes.hasMoreElements()) {
                String key = (String) dataTypes.nextElement();
                rawDataTypes.add(rawPropertiesOfDataTypes.getProperty(key));
            }
        }


        for(int i=0; i<rawColumns.size();i++){
            rawColumnList+=rawColumns.get(i)+" "+rawDataTypes.get(i)+",";
        }
        String rawColumnsWithDataTypes = rawColumnList.substring(0,rawColumnList.length()-1);
        if(rawInputFormat.equalsIgnoreCase("delimited")){
            StringBuilder sList = new StringBuilder();
            GetProperties getSerdeProperties = new GetProperties();
            java.util.Properties listForRawSerdeProps = getSerdeProperties.getProperties(rawLoadProcessId.toString(),"raw-serde-props");
            Enumeration e = listForRawSerdeProps.propertyNames();
            if (listForRawSerdeProps.size() != 0) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    sList.append("'"+key  + "' = '" + listForRawSerdeProps.getProperty(key) + "',");
                }
            }
            rawSerdeProperties=sList.substring(0, sList.length() - 1);
            LOGGER.debug("rawSerdeProperties = " + rawSerdeProperties);
        }
        else if(rawInputFormat.equalsIgnoreCase("xml")){
            StringBuilder sList = new StringBuilder();
            GetProperties getSerdeProperties = new GetProperties();
            java.util.Properties listForRawSerdeProps = getSerdeProperties.getProperties(rawLoadProcessId.toString(),"raw-serde-props");
            Enumeration e = listForRawSerdeProps.propertyNames();
            if (listForRawSerdeProps.size() != 0) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    sList.append("\"" + key + "\" = \"" + listForRawSerdeProps.getProperty(key) + "\",");
                }
            }
            rawSerdeProperties=sList.substring(0, sList.length() - 1);
            LOGGER.debug("rawSerdeProperties = " + rawSerdeProperties);
        }

        StringBuilder tList = new StringBuilder();
        GetProperties getTableProperties = new GetProperties();
        java.util.Properties listForRawTableProps = getTableProperties.getProperties(rawLoadProcessId.toString(),"raw-table-props");
        Enumeration e = listForRawTableProps.propertyNames();
        if (listForRawTableProps.size() != 0) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                tList.append("\"" + key + "\" = \"" + listForRawTableProps.getProperty(key) + "\",");
            }
        }
        rawTableProperties=tList.substring(0, tList.length() - 1);
        LOGGER.debug("rawTableProperties = " + rawTableProperties);

        String rawTableDdl = "";
        if(rawInputFormat.equalsIgnoreCase("delimited")) {
            rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                    " partitioned by (batchid bigint) ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'  WITH SERDEPROPERTIES (" + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'" +
                    " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'";

            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        else if(rawInputFormat.equalsIgnoreCase("xml")){
            rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " )" +
                    " partitioned by (batchid bigint) ROW FORMAT SERDE 'com.wipro.ats.bdre.io.xml.XmlSerDe' WITH SERDEPROPERTIES " +
                    " ( " + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'com.wipro.ats.bdre.io.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' " +
                    "TBLPROPERTIES ( " + rawTableProperties + " )";
            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }






        //Getting Stage view information
        String rawViewName = getRawView().getTableName();
        String rawViewDbName = getRawView().getDbName();
        String rawViewDdl = getRawView().getDdl();
        //Getting core table information
        String baseTableName = getBaseTable().getTableName();
        String baseTableDbName = getBaseTable().getDbName();
        String baseTableDdl = getBaseTable().getDdl();
        //Now create the tables/view if not exists.
        checkAndCreateRawTable(rawDbName, rawTableName, rawTableDdl);
        checkAndCreateRawView(rawViewDbName, rawViewName, rawViewDdl);
        checkAndCreateBaseTable(baseTableDbName, baseTableName, baseTableDdl);
    }

    private void checkAndCreateRawTable(String dbName, String tableName, String ddl) {
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

    private void checkAndCreateRawView(String dbName, String stageViewName, String ddl) {
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

    private void checkAndCreateBaseTable(String dbName, String baseTable, String ddl) {
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