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
import java.util.*;

/**
 * Created by vishnu on 12/14/14.
 * Modified by Arijit
 */
public class CreateRawBaseTables extends ETLBase {
    private static final Logger LOGGER = Logger.getLogger(CreateRawBaseTables.class);
    private static final String PROCESSID = "process-id";
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"instExecId", "instance-exec-id", " instance exec id"},
    };
    public void executeRawLoad(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue(PROCESSID);
        rawLoad=processId;


        //Getting raw table information from properties with raw-table as config group
        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(rawLoad, "raw-table");
        String rawTableName = rawPropertiesOfTable.getProperty("table_name");
        String rawTableDbName = rawPropertiesOfTable.getProperty("table_db");
        String fileType = rawPropertiesOfTable.getProperty("file_type");
        String rawSerdeProperties = "";
        String rawTableProperties = "";
        String rawColumnList = "";

        // fetching column names in a string list from properties with raw-columns as config group
        GetProperties getPropertiesOfRawColumns = new GetProperties();
        java.util.Properties rawPropertiesOfColumns = getPropertiesOfRawColumns.getProperties(rawLoad, "raw-cols");
        Enumeration columns = rawPropertiesOfColumns.propertyNames();
        List<String> orderOfCloumns = Collections.list(columns);
        List<String> orderedColumns = sortList(orderOfCloumns);
        List<String> rawColumns = new ArrayList<String>();
        if (!rawPropertiesOfColumns.isEmpty()) {
            for (String columnOrder : orderedColumns) {
                String key = columnOrder;
                rawColumns.add(rawPropertiesOfColumns.getProperty(key));
            }
        }

        // fetching column datatypes in a string list from properties with raw-data-types as config group
        GetProperties getPropertiesOfRawDataTypes = new GetProperties();
        java.util.Properties rawPropertiesOfDataTypes = getPropertiesOfRawDataTypes.getProperties(rawLoad, "raw-data-types");
        Enumeration dataTypes = rawPropertiesOfDataTypes.propertyNames();
        List<String> orderOfDataTypes = Collections.list(dataTypes);
        List<String> orderedDataTypes = sortList(orderOfDataTypes);
        List<String> rawDataTypes = new ArrayList<String>();
        if (!rawPropertiesOfColumns.isEmpty()) {
            for (String columnOrder : orderedDataTypes) {
                String key = columnOrder;
                rawDataTypes.add(rawPropertiesOfDataTypes.getProperty(key));
            }
        }

        // forming a comma separated string in the form of col1 datatype1, col2 datatype2, col3 datatype3 etc.
        for (int i = 0; i < rawColumns.size(); i++) {
            rawColumnList += rawColumns.get(i) + " " + rawDataTypes.get(i) + ",";
        }
        String rawColumnsWithDataTypes = rawColumnList.substring(0, rawColumnList.length() - 1);

        // fetching raw table serde-properties from properties table for a delimited file
        if ("delimited".equalsIgnoreCase(fileType)) {
            StringBuilder sList = new StringBuilder();
            GetProperties getSerdeProperties = new GetProperties();
            java.util.Properties listForRawSerdeProps = getSerdeProperties.getProperties(rawLoad, "raw-serde-props");
            Enumeration e = listForRawSerdeProps.propertyNames();
            if (!listForRawSerdeProps.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    sList.append("'" + key + "' = '" + listForRawSerdeProps.getProperty(key) + "',");
                }
                rawSerdeProperties = sList.substring(0, sList.length() - 1);
                LOGGER.debug("rawSerdeProperties = " + rawSerdeProperties);
            }


        }

        //fetching raw table serde-properties from properties table for a xml file
        else if ("xml".equalsIgnoreCase(fileType)) {
            StringBuilder sList = new StringBuilder();
            GetProperties getSerdeProperties = new GetProperties();
            java.util.Properties listForRawSerdeProps = getSerdeProperties.getProperties(rawLoad, "raw-serde-props");
            Enumeration e = listForRawSerdeProps.propertyNames();
            if (!listForRawSerdeProps.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    sList.append("\"" + key + "\" = \"" + listForRawSerdeProps.getProperty(key) + "\",");
                }
                rawSerdeProperties = sList.substring(0, sList.length() - 1);
                LOGGER.debug("rawSerdeProperties = " + rawSerdeProperties);
            }

        }

        StringBuilder tList = new StringBuilder();
        // fetching raw table table-properties from properties table for xml
        if ("xml".equalsIgnoreCase(fileType)) {
            tList = new StringBuilder();
            GetProperties getTableProperties = new GetProperties();
            java.util.Properties listForRawTableProps = getTableProperties.getProperties(rawLoad, "raw-table-props");
            Enumeration e = listForRawTableProps.propertyNames();
            if (!listForRawTableProps.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    tList.append("\"" + key + "\" = \"" + listForRawTableProps.getProperty(key) + "\",");
                }

            }
        } else if ("mainframe".equalsIgnoreCase(fileType)) {
            // fetching raw table table-properties from properties table for mainframe
            //'copybook.inputformat.cbl.hdfs.path' = 'copybook/example.cbl'
            tList = new StringBuilder();
            GetProperties getTableProperties = new GetProperties();
            java.util.Properties listForRawTableProps = getTableProperties.getProperties(rawLoad, "raw-table-props");
            Enumeration e = listForRawTableProps.propertyNames();
            if (!listForRawTableProps.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    tList.append("'" + key + "' = '" + listForRawTableProps.getProperty(key) + "',");
                }

            }
        }

        String rawTableDdl = "";

        //generating raw table ddl for a delimited file
        if ("delimited".equalsIgnoreCase(fileType)) {
            rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                    " partitioned by (batchid bigint) ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'  WITH SERDEPROPERTIES (" + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'" +
                    " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'";

            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        //generating raw table ddl for a xml file
        else if ("xml".equalsIgnoreCase(fileType)) {
            rawTableProperties = tList.substring(0, tList.length() - 1);
            LOGGER.debug("rawTableProperties = " + rawTableProperties);
            rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " )" +
                    " partitioned by (batchid bigint) ROW FORMAT SERDE 'com.ibm.spss.hive.serde2.xml.XmlSerDe' WITH SERDEPROPERTIES " +
                    " ( " + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'com.ibm.spss.hive.serde2.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' " +
                    "TBLPROPERTIES ( " + rawTableProperties + " )";
            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        if ("mainframe".equalsIgnoreCase(fileType)) {
            rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                    " partitioned by (batchid bigint)  ROW FORMAT DELIMITED FIELDS TERMINATED BY '1'\n" +
                    "STORED AS INPUTFORMAT 'com.cloudera.sa.copybook.mapred.CopybookInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat' " +
                    "TBLPROPERTIES (" + rawTableProperties + " )";
            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        checkAndCreateRawTable(rawTableDbName, rawTableName, rawTableDdl);

    }

    public void executeStageLoad(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue(PROCESSID);
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        stgLoad = processId;


        //Getting raw table information from properties with raw-table as config group
        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties basePropertiesOfTable = getPropertiesOfRawTable.getProperties(stgLoad, "base-table");
        String baseTableName = basePropertiesOfTable.getProperty("table_name");
        String baseTableDbName = basePropertiesOfTable.getProperty("table_db");
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(stgLoad, "raw-table");
        String rawTableName = rawPropertiesOfTable.getProperty("table_name_raw");
        String rawTableDbName = rawPropertiesOfTable.getProperty("table_db_raw");

        //Getting Stage view information
        String rawViewName = rawTableName + "_view";
        String rawViewDbName = rawTableDbName;
        String rawViewDdl = "";
        String baseTableDdl = "";



       /* raw view ddl is generated by fetching keys and values from properties table with config group as base-columns
          columns are generated by fetching keys and values in the form of value1 as key1, value2 as key2, value3 as key3 etc..*/


        // fetching column names and aliases as a string from properties with base-columns as config group
        GetProperties getPropertiesOfViewColumns = new GetProperties();
        java.util.Properties viewPropertiesOfColumns = getPropertiesOfViewColumns.getProperties(stgLoad, "base-columns");
        Enumeration viewColumnsList = viewPropertiesOfColumns.propertyNames();
        StringBuilder viewColumns = new StringBuilder();
        if (!viewPropertiesOfColumns.isEmpty()) {
            while (viewColumnsList.hasMoreElements()) {
                String key = (String) viewColumnsList.nextElement();
                viewColumns.append(viewPropertiesOfColumns.getProperty(key) + " AS " + key.replaceAll("transform_", "") + ",");
            }
        }

        java.util.Properties viewPropertiesOfColumnsPartition = getPropertiesOfViewColumns.getProperties(stgLoad, "partition");
        String partitionViewColumn = viewPropertiesOfColumnsPartition.getProperty("partition_columns");
        LOGGER.info("partition columns " + partitionViewColumn);
        if (!("".equals(partitionViewColumn)) && !(partitionViewColumn == null)) {
            String[] partitionViewColumns = partitionViewColumn.split(",");
            for (String viewColumn : partitionViewColumns) {
                viewColumns.append(viewColumn.split(" ")[0] + " AS " + viewColumn.split(" ")[0] + ",");
            }
        }

        // adding partition column (additional comma already present at the end of viewColumns
        String viewColumnsWithDataTypes = viewColumns + "batchid";

        rawViewDdl += "CREATE VIEW IF NOT EXISTS " + rawViewDbName + "." + rawViewName + "  AS SELECT " + viewColumnsWithDataTypes + " FROM " + rawTableDbName + "." + rawTableName;
        LOGGER.debug(rawViewDdl);


        // generating stage table ddl
        // fetching column names list
        GetProperties getPropertiesOfBaseColumns = new GetProperties();
        java.util.Properties basePropertiesOfColumns = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-columns");
        java.util.Properties basePropertiesOfDataTypes = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-data-types");
        Enumeration baseColumnsList = basePropertiesOfColumns.propertyNames();
        StringBuilder baseColumns = new StringBuilder();
        if (!basePropertiesOfColumns.isEmpty()) {
            while (baseColumnsList.hasMoreElements()) {
                String key = (String) baseColumnsList.nextElement();
                baseColumns.append(basePropertiesOfColumns.getProperty(key) + " " + basePropertiesOfDataTypes .getProperty(key.replaceAll("transform_","")) + ",");
            }
        }
        //removing trailing comma
        String baseColumnsWithDataTypes = baseColumns.substring(0, baseColumns.length() - 1);
        java.util.Properties partitionproperties = getPropertiesOfRawTable.getProperties(stgLoad, "partition");
        String partitionColumns = partitionproperties.getProperty("partition_columns");
        if (partitionColumns == null)
            partitionColumns = "";
        baseTableDdl += "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + baseTableName + " (" + baseColumnsWithDataTypes + ") partitioned by (" + partitionColumns + " instanceexecid bigint) stored as orc";

        LOGGER.debug(baseTableDdl);

        String stgTableName = baseTableName + "_" + instanceExecId;
        String stgTableDdl = "";
        stgTableDdl += "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + stgTableName + " (" + baseColumnsWithDataTypes + ") partitioned by (" + partitionColumns + " instanceexecid bigint) stored as orc";

        checkAndCreateRawView(rawViewDbName, rawViewName, rawViewDdl);
        checkAndCreateStageTable(baseTableDbName, stgTableName, stgTableDdl);
        checkAndCreateBaseTable(baseTableDbName, baseTableName, baseTableDdl);
    }


    private static String getQuery(String name){
        return "SHOW TABLES LIKE '" + name + "'";
    }

    private void checkAndCreateRawTable(String dbName, String tableName, String ddl) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(tableName));
            if (!rs.next()) {
                LOGGER.info("Raw table does not exist Creating table " + tableName);
                LOGGER.info("Creating raw table using " + ddl);
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
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(stageViewName));
            if (!rs.next()) {
                LOGGER.debug("View does not exist. Creating View " + stageViewName);
                LOGGER.info("Creating view using " + ddl);
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
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(baseTable));
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
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(baseTable));
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
    
    private List<String> sortList(List<String> list) {
        List<Integer> integerList = new ArrayList<Integer>();
        for(itemList : list) {
            integerList.add(new Integer(itemList));
        }
        Collections.sort(integerList);

        List<String> stringList = new ArrayList<String>();

        for(integerValue : integerList) {
            stringList.add(integerValue.toString());
        }
        
        return stringList;
    }
}