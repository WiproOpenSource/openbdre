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
            {"p", "process-id", " Process id of ETLDriver"},
            {"instExecId", "instance-exec-id", " instance exec id"},
    };
    public void execute(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        init(processId);

        Integer rawLoadProcessId = rawLoad.getProcessId();
        Integer stgLoadProcessId = stgLoad.getProcessId();
        Integer baseLoadProcessId = baseLoad.getProcessId();

        //Getting raw table information from properties with raw-table as config group
        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(rawLoadProcessId.toString(), "raw-table");
        String rawTableName = rawPropertiesOfTable.getProperty("table-name");
        String rawTableDbName = rawPropertiesOfTable.getProperty("table-db");
        String rawInputFormat = rawPropertiesOfTable.getProperty("input-format");
        String rawOutputFormat = rawPropertiesOfTable.getProperty("output-format");
        String rawSerdeClass = rawPropertiesOfTable.getProperty("serde");
        String rawSerdeProperties="";
        String rawTableProperties="";
        String rawColumnList="";

        // fetching column names in a string list from properties with raw-columns as config group
        GetProperties getPropertiesOfRawColumns = new GetProperties();
        java.util.Properties rawPropertiesOfColumns = getPropertiesOfRawColumns.getProperties(rawLoadProcessId.toString(), "raw-column");
        Enumeration columns = rawPropertiesOfColumns.propertyNames();
        List<String> rawColumns = new ArrayList<>();
        if (rawPropertiesOfColumns.size() != 0) {
            while (columns.hasMoreElements()) {
                String key = (String) columns.nextElement();
                rawColumns.add(rawPropertiesOfColumns.getProperty(key));
            }
        }

        // fetching column datatypes in a string list from properties with raw-data-types as config group
        GetProperties getPropertiesOfRawDataTypes = new GetProperties();
        java.util.Properties rawPropertiesOfDataTypes = getPropertiesOfRawDataTypes.getProperties(rawLoadProcessId.toString(), "raw-dtype");
        Enumeration dataTypes = rawPropertiesOfDataTypes.propertyNames();
        List<String> rawDataTypes = new ArrayList<>();
        if (rawPropertiesOfColumns.size() != 0) {
            while (dataTypes.hasMoreElements()) {
                String key = (String) dataTypes.nextElement();
                rawDataTypes.add(rawPropertiesOfDataTypes.getProperty(key));
            }
        }

        // forming a comma separated string in the form of col1 datatype1, col2 datatype2, col3 datatype3 etc.
        for(int i=0; i<rawColumns.size();i++){
            rawColumnList+=rawColumns.get(i)+" "+rawDataTypes.get(i)+",";
        }
        String rawColumnsWithDataTypes = rawColumnList.substring(0,rawColumnList.length()-1);

        // fetching raw table serde-properties from properties table for a delimited file
        if(rawInputFormat.equalsIgnoreCase("delimited")){
            StringBuilder sList = new StringBuilder();
            GetProperties getSerdeProperties = new GetProperties();
            java.util.Properties listForRawSerdeProps = getSerdeProperties.getProperties(rawLoadProcessId.toString(),"raw-sprops");
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

        //fetching raw table serde-properties from properties table for a xml file
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


        // fetching raw table table-properties from properties table
        StringBuilder tList = new StringBuilder();
        GetProperties getTableProperties = new GetProperties();
        java.util.Properties listForRawTableProps = getTableProperties.getProperties(rawLoadProcessId.toString(),"raw-tprops");
        Enumeration e = listForRawTableProps.propertyNames();
        if (listForRawTableProps.size() != 0) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                tList.append("\"" + key + "\" = \"" + listForRawTableProps.getProperty(key) + "\",");
            }
        }


        String rawTableDdl = "";

        //generating raw table ddl for a delimited file
        if(rawInputFormat.equalsIgnoreCase("delimited")) {
            rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                    " partitioned by (batchid bigint) ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'  WITH SERDEPROPERTIES (" + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'" +
                    " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'";

            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        //generating raw table ddl for a xml file
        else if(rawInputFormat.equalsIgnoreCase("xml")){
            rawTableProperties=tList.substring(0, tList.length() - 1);
            LOGGER.debug("rawTableProperties = " + rawTableProperties);
            rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " )" +
                    " partitioned by (batchid bigint) ROW FORMAT SERDE 'com.wipro.ats.bdre.io.xml.XmlSerDe' WITH SERDEPROPERTIES " +
                    " ( " + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'com.wipro.ats.bdre.io.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' " +
                    "TBLPROPERTIES ( " + rawTableProperties + " )";
            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }


        //Getting Stage view information
        String rawViewName = rawTableName+"_view";
        String rawViewDbName = rawTableDbName;
        String rawViewDdl = "";

       /* raw view ddl is generated by fetching keys and values from properties table with config group as base-columns
          columns are generated by fetching keys and values in the form of value1 as key1, value2 as key2, value3 as key3 etc..*/


        // fetching column names and aliases as a string from properties with base-columns as config group
        GetProperties getPropertiesOfViewColumns = new GetProperties();
        java.util.Properties viewPropertiesOfColumns = getPropertiesOfViewColumns.getProperties(stgLoadProcessId.toString(), "base-cols");
        Enumeration viewColumnsList = viewPropertiesOfColumns.propertyNames();
        StringBuilder viewColumns = new StringBuilder();
        if (viewPropertiesOfColumns.size() != 0) {
            while (viewColumnsList.hasMoreElements()) {
                String key = (String) viewColumnsList.nextElement();
                viewColumns.append(viewPropertiesOfColumns.getProperty(key) + " AS "+ key + ",");
            }
        }

        // adding partition column (additional comma already present at the end of viewColumns
        String viewColumnsWithDataTypes = viewColumns+ "batchid";

        rawViewDdl+= "CREATE VIEW IF NOT EXISTS "+ rawViewDbName+"."+rawViewName+"  AS SELECT "+viewColumnsWithDataTypes+ " FROM "+rawTableDbName+"."+rawTableName;
        LOGGER.debug(rawViewDdl);

        //Getting base table information
        GetProperties getPropertiesOfBaseTable = new GetProperties();
        java.util.Properties basePropertiesOfTable = getPropertiesOfBaseTable.getProperties(baseLoadProcessId.toString(), "base-table");
        String baseTableName = basePropertiesOfTable.getProperty("table-name");
        String baseTableDbName = basePropertiesOfTable.getProperty("table-db");
        String baseTableDdl = "";

        // generating base table ddl
        // fetching column names list
        GetProperties getPropertiesOfBaseColumns = new GetProperties();
        java.util.Properties basePropertiesOfColumns = getPropertiesOfBaseColumns.getProperties(baseLoadProcessId.toString(), "base-cols");
        Enumeration baseColumnsList = basePropertiesOfColumns.propertyNames();
        StringBuilder baseColumns = new StringBuilder();
        if (basePropertiesOfColumns.size() != 0) {
            while (baseColumnsList.hasMoreElements()) {
                String key = (String) baseColumnsList.nextElement();
                baseColumns.append(key + " "+ basePropertiesOfColumns.getProperty(key)+ ",");
            }
        }
        //removing trailing comma
        String baseColumnsWithDataTypes = baseColumns.substring(0,baseColumns.length()-1);
        baseTableDdl+="CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + baseTableName + " (" + baseColumnsWithDataTypes + ") partitioned by (instanceexecid bigint) stored as orc";;
        LOGGER.debug(baseTableDdl);

        String stgTableName=baseTableName+"_"+instanceExecId;
        String stgTableDdl="";
        stgTableDdl+="CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + stgTableName + " (" + baseColumnsWithDataTypes + ") partitioned by (instanceexecid bigint) stored as orc";;
        //Now create the tables/view if not exists.
        checkAndCreateRawTable(rawTableDbName, rawTableName, rawTableDdl);
        checkAndCreateRawView(rawViewDbName, rawViewName, rawViewDdl);
        checkAndCreateStageTable(baseTableDbName, stgTableName, stgTableDdl);
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
                LOGGER.info("Creating raw table using "+ddl);
                stmt.executeUpdate(ddl);
                LOGGER.info("Raw table created.");
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating raw table" + e);
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
                LOGGER.info("Creating view using "+ddl);
                stmt.executeUpdate(ddl);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating view" + e);
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
                LOGGER.info("Creating base table using "+ddl);
                stmt.executeUpdate(ddl);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating base table" + e);
            throw new ETLException(e);
        }
    }

    private void checkAndCreateStageTable(String dbName, String baseTable, String ddl) {
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + baseTable + "'");
            if (!rs.next()) {
                LOGGER.info("Stage table does not exist.Creating Table " + baseTable);
                LOGGER.info("Creating stage table using "+ddl);
                stmt.executeUpdate(ddl);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating base table" + e);
            throw new ETLException(e);
        }
    }
}