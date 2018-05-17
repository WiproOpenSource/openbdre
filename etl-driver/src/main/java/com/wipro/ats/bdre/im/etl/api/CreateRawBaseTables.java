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
import com.wipro.ats.bdre.im.jsonschema.JsonSchemaReader;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue;
import com.wipro.ats.bdre.md.dao.jpa.Process;
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
    String fileTypeSelected;
    String processIdSelected;

    public void executeRawLoad(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue(PROCESSID);
        processIdSelected = processId;
        rawLoad=processId;


        //Getting raw table information from properties with raw-table as config group
        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(rawLoad, "raw-table");
        String rawTableName = rawPropertiesOfTable.getProperty("table_name");
        String rawTableDbName = rawPropertiesOfTable.getProperty("table_db");
        String fileType = rawPropertiesOfTable.getProperty("file_type");
        fileTypeSelected = fileType;
        String rawSerdeProperties = "";
        String rawTableProperties = "";
        String rawColumnList = "";

        // fetching column names in a string list from properties with raw-columns as config group
        GetProperties getPropertiesOfRawColumns = new GetProperties();
        java.util.Properties rawPropertiesOfColumns = getPropertiesOfRawColumns.getProperties(rawLoad, "raw-cols");
        Enumeration columns = rawPropertiesOfColumns.propertyNames();
        List<String> orderOfCloumns = Collections.list(columns);
         Collections.sort(orderOfCloumns, new Comparator<String>() {
            public int compare(String o1, String o2) {
                int n1=Integer.valueOf(o1.split("\\.")[1]);
                int n2=Integer.valueOf(o2.split("\\.")[1]);
                return (n1 - n2);
            }
        });
        List<String> rawColumns = new ArrayList<String>();
        if (!rawPropertiesOfColumns.isEmpty()) {
            for (String columnOrder : orderOfCloumns) {
                String key = columnOrder;
                rawColumns.add(rawPropertiesOfColumns.getProperty(key));
            }
        }

        // fetching column datatypes in a string list from properties with raw-data-types as config group
        GetProperties getPropertiesOfRawDataTypes = new GetProperties();
        java.util.Properties rawPropertiesOfDataTypes = getPropertiesOfRawDataTypes.getProperties(rawLoad, "raw-data-types");
        Enumeration dataTypes = rawPropertiesOfDataTypes.propertyNames();
        List<String> orderOfDataTypes = Collections.list(dataTypes);
        Collections.sort(orderOfDataTypes, new Comparator<String>() {

            public int compare(String o1, String o2) {
                int n1=Integer.valueOf(o1.split("\\.")[1]);
                int n2=Integer.valueOf(o2.split("\\.")[1]);
                return (n1 - n2);
            }
        });
        List<String> rawDataTypes = new ArrayList<String>();
        if (!rawPropertiesOfColumns.isEmpty()) {
            for (String columnOrder : orderOfDataTypes) {
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
        if ("delimited".equalsIgnoreCase(fileType) || "xlsx".equalsIgnoreCase(fileType)) {
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

        //fetching raw table serde-properties from properties table for a json file
        /*else if ("Json".equalsIgnoreCase(fileType)) {
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

        }*/

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
        /*else if ("Json".equalsIgnoreCase(fileType)) {
            // fetching raw table table-properties from properties table for json
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
        }*/


        String rawTableDdl = "";


        //generating raw table ddl for a delimited file
        if ("delimited".equalsIgnoreCase(fileType) || "xlsx".equalsIgnoreCase(fileType) ) {
            if(!rawSerdeProperties.equals("")) {
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                        " partitioned by (batchid bigint) ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'  WITH SERDEPROPERTIES (" + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'" +
                        " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'";
            }
            else{
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                        " partitioned by (batchid bigint) ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'   STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'" +
                        " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'";

            }
            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        //generating raw table ddl for a xml file
        else if ("xml".equalsIgnoreCase(fileType)) {
            rawTableProperties = tList.substring(0, tList.length() - 1);
            LOGGER.debug("rawTableProperties = " + rawTableProperties);

            if(rawSerdeProperties.equals("") && rawTableProperties.equals("")) {
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " )" +
                        " partitioned by (batchid bigint) ROW FORMAT SERDE 'com.ibm.spss.hive.serde2.xml.XmlSerDe' " +
                        "STORED AS INPUTFORMAT 'com.ibm.spss.hive.serde2.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' ";
            }
            else if(rawSerdeProperties.equals("") ){
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " )" +
                        " partitioned by (batchid bigint) ROW FORMAT SERDE 'com.ibm.spss.hive.serde2.xml.XmlSerDe'" +
                        " STORED AS INPUTFORMAT 'com.ibm.spss.hive.serde2.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' " +
                        "TBLPROPERTIES ( " + rawTableProperties + " )";

            }
            else if(rawTableProperties.equals("")){
                rawTableDdl +="CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " )" +
                        " partitioned by (batchid bigint) ROW FORMAT SERDE 'com.ibm.spss.hive.serde2.xml.XmlSerDe' WITH SERDEPROPERTIES " +
                        " ( " + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'com.ibm.spss.hive.serde2.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' " ;
            }
            else{
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " )" +
                        " partitioned by (batchid bigint) ROW FORMAT SERDE 'com.ibm.spss.hive.serde2.xml.XmlSerDe' WITH SERDEPROPERTIES " +
                        " ( " + rawSerdeProperties + " ) STORED AS INPUTFORMAT 'com.ibm.spss.hive.serde2.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' " +
                        "TBLPROPERTIES ( " + rawTableProperties + " )";
            }

            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        else if ("json".equalsIgnoreCase(fileType)) {

                String hiveRawSchema = new JsonSchemaReader().generateJsonSchema(rawColumnsWithDataTypes);
                LOGGER.info("rawColumnsWithDataTypes"+rawColumnsWithDataTypes);
                LOGGER.info("hiveRawSchema"+hiveRawSchema);
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + hiveRawSchema + " )" +
                        " partitioned by (batchid bigint) ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe' " +
                        "STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat' ";

            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }

        if ("mainframe".equalsIgnoreCase(fileType)) {
            if(!rawTableProperties.equals("")) {
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                        " partitioned by (batchid bigint)  ROW FORMAT DELIMITED FIELDS TERMINATED BY '1'\n" +
                        "STORED AS INPUTFORMAT 'com.cloudera.sa.copybook.mapred.CopybookInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat' " +
                        "TBLPROPERTIES (" + rawTableProperties + " )";
            }
            else{
                rawTableDdl += "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawTableName + " ( " + rawColumnsWithDataTypes + " ) " +
                        " partitioned by (batchid bigint)  ROW FORMAT DELIMITED FIELDS TERMINATED BY '1'\n" +
                        "STORED AS INPUTFORMAT 'com.cloudera.sa.copybook.mapred.CopybookInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat' " ;
            }
            LOGGER.debug("rawTableDdl= " + rawTableDdl);
        }
        checkAndCreateRawTable(rawTableDbName, rawTableName, rawTableDdl);

    }

    public void executeStageLoad(String[] params) {
        System.out.println("Hiieee.. I am using viewColumns1 and baseColumns1");
        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue(PROCESSID);
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        stgLoad = processId;
        processIdSelected = processId;


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
        java.util.Properties viewPropertiesOfDataTypes = getPropertiesOfViewColumns.getProperties(stgLoad, "base-data-types");
        Enumeration viewColumnsList = viewPropertiesOfColumns.propertyNames();
        List<String> viewColumns1=Collections.list(viewColumnsList);
        Collections.sort(viewColumns1, new Comparator<String>() {

            public int compare(String o1, String o2) {
                int n1 = Integer.valueOf(o1.split("\\.")[1]);
                int n2 = Integer.valueOf(o2.split("\\.")[1]);
                return (n1 - n2);
            }
        });
        StringBuilder viewColumns = new StringBuilder();
        int tokenize = 0;
        if (!viewPropertiesOfColumns.isEmpty()) {
            for (String key : viewColumns1) {
                //String key = (String) viewColumnsList.nextElement();
                //if("json".equalsIgnoreCase(fileTypeSelected)){
                    String columnName = viewPropertiesOfColumns.getProperty(key);
                    LOGGER.info("columnName is "+columnName);
                    if(columnName.contains(".")){
                        String firstColumnName = columnName.split("\\.")[0];
                        LOGGER.info("firstColumnName is "+ firstColumnName);
                        if(!viewColumns.toString().contains(firstColumnName))
                            viewColumns.append(firstColumnName + " AS " + firstColumnName + ",");
                    }
                    else {
                        if(viewPropertiesOfColumns.getProperty(key).contains("tokenize"))
                            tokenize = 1;
                        /*if (columnName.contains("tokenize")){
                            if(viewPropertiesOfDataTypes.getProperty(key.split("\\.")[0].replaceAll("transform_", "")).equals("Timestamp"))
                                viewColumns.append("CAST("+"from_unixtime(unix_timestamp("+viewPropertiesOfColumns.getProperty(key)+",'yyyyMMddHHmmss')) AS "+viewPropertiesOfDataTypes.getProperty(key.split("\\.")[0].replaceAll("transform_", ""))+") AS "+key.split("\\.")[0].replaceAll("transform_", "")+",");
                            else
                                viewColumns.append("CAST("+viewPropertiesOfColumns.getProperty(key)+" AS "+viewPropertiesOfDataTypes.getProperty(key.split("\\.")[0].replaceAll("transform_", ""))+") AS "+key.split("\\.")[0].replaceAll("transform_", "")+",");
                        }*/
                        //else
                            //viewColumns.append(viewPropertiesOfColumns.getProperty(key) + " AS " + key.split("\\.")[0].replaceAll("transform_", "") + ",");
                        viewColumns.append( key.split("\\.")[0].replaceAll("transform_", "")+ " AS " + key.split("\\.")[0].replaceAll("transform_", "") + ",");
                    }
                //}//
                //else
                //viewColumns.append(viewPropertiesOfColumns.getProperty(key) + " AS " + key.replaceAll("transform_", "") + ",");
                LOGGER.info("viewColumns Details "+ viewColumns.toString());
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
        LOGGER.info("rawViewDdl is"+rawViewDdl);
        LOGGER.debug(rawViewDdl);


        // generating stage table ddl
        // fetching column names list
        String baseColumnList = "";

        GetProperties getFileType = new GetProperties();
        java.util.Properties baseProperties = getFileType.getProperties(stgLoad, "base-table");
        String fileType = baseProperties.getProperty("file_type");

        String baseColumnsWithDataTypes=null;

        if("json".equalsIgnoreCase(fileType)) {

            GetProperties getPropertiesOfBaseColumns = new GetProperties();
            java.util.Properties basePropertiesOfColumns = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-cols");
            Enumeration columns = basePropertiesOfColumns.propertyNames();
            List<String> orderOfCloumns = Collections.list(columns);
            Collections.sort(orderOfCloumns, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    int n1 = Integer.valueOf(o1.split("\\.")[1]);
                    int n2 = Integer.valueOf(o2.split("\\.")[1]);
                    return (n1 - n2);
                }
            });
            List<String> baseColumns = new ArrayList<String>();
            if (!basePropertiesOfColumns.isEmpty()) {
                for (String columnOrder : orderOfCloumns) {
                    String key = columnOrder;
                    baseColumns.add(basePropertiesOfColumns.getProperty(key));
                }
            }

            // fetching column datatypes in a string list from properties with raw-data-types as config group
            GetProperties getPropertiesOfBaseDataTypes = new GetProperties();
            java.util.Properties basePropertiesOfDataTypes = getPropertiesOfBaseDataTypes.getProperties(stgLoad, "base-data-type");
            Enumeration dataTypes = basePropertiesOfDataTypes.propertyNames();
            List<String> orderOfDataTypes = Collections.list(dataTypes);
            Collections.sort(orderOfDataTypes, new Comparator<String>() {

                public int compare(String o1, String o2) {
                    int n1 = Integer.valueOf(o1.split("\\.")[1]);
                    int n2 = Integer.valueOf(o2.split("\\.")[1]);
                    return (n1 - n2);
                }
            });
            List<String> baseDataTypes = new ArrayList<String>();
            if (!basePropertiesOfColumns.isEmpty()) {
                for (String columnOrder : orderOfDataTypes) {
                    String key = columnOrder;
                    baseDataTypes.add(basePropertiesOfDataTypes.getProperty(key));
                }
            }

            // forming a comma separated string in the form of col1 datatype1, col2 datatype2, col3 datatype3 etc.
            for (int i = 0; i < baseColumns.size(); i++) {
                baseColumnList += baseColumns.get(i) + " " + baseDataTypes.get(i) + ",";
            }
            baseColumnsWithDataTypes = baseColumnList.substring(0, baseColumnList.length() - 1);
        }
        else {

            GetProperties getPropertiesOfBaseColumns = new GetProperties();
            java.util.Properties basePropertiesOfColumns = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-columns");
            java.util.Properties basePropertiesOfDataTypes = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-data-types");
            Enumeration baseColumnsList = basePropertiesOfColumns.propertyNames();
            List<String> baseColumns1=Collections.list(baseColumnsList);
            Collections.sort(baseColumns1, new Comparator<String>() {

                public int compare(String o1, String o2) {
                    int n1 = Integer.valueOf(o1.split("\\.")[1]);
                    int n2 = Integer.valueOf(o2.split("\\.")[1]);
                    return (n1 - n2);
                }
            });


            StringBuilder baseColumns = new StringBuilder();
            if (!basePropertiesOfColumns.isEmpty()) {
                for (String key : baseColumns1) {
                    //String key = (String) baseColumnsList.nextElement();
                    baseColumns.append(key.split("\\.")[0].replaceAll("transform_", "") + " " + basePropertiesOfDataTypes.getProperty(key.split("\\.")[0].replaceAll("transform_", "")) + ",");
                }
            }
            //removing trailing comma
            LOGGER.info("Length of baseColumns" + baseColumns.length());
            baseColumnsWithDataTypes = baseColumns.substring(0, baseColumns.length() - 1);
            LOGGER.info("baseColumnsWithDataTypes is " + baseColumnsWithDataTypes);
        }

        java.util.Properties partitionproperties = getPropertiesOfRawTable.getProperties(stgLoad, "partition");
        String partitionColumns = partitionproperties.getProperty("partition_columns");
        if (partitionColumns == null)
            partitionColumns = "";

        if("json".equalsIgnoreCase(fileType)){
            String baseTableSchema = new JsonSchemaReader().generateJsonSchema(baseColumnsWithDataTypes);
            //orc format removed for schema evolution for stage and base table
            baseTableDdl += "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + baseTableName + " (" + baseTableSchema + ") partitioned by (" + partitionColumns + " instanceexecid bigint)  ";
        }
        else {
            System.out.println("orc not used");
            baseTableDdl += "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + baseTableName + " (" + baseColumnsWithDataTypes + ") partitioned by (" + partitionColumns + " instanceexecid bigint) ";
        }
        LOGGER.debug(baseTableDdl);

        String stgTableName = baseTableName + "_" + instanceExecId;
        String stgTableDdl = "";
        if("json".equalsIgnoreCase(fileType)){
            String stageTableSchema = new JsonSchemaReader().generateJsonSchema(baseColumnsWithDataTypes);
            stgTableDdl += "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + stgTableName + " (" + stageTableSchema + ") partitioned by (" + partitionColumns + " instanceexecid bigint) ";
        }
        else
            stgTableDdl += "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + stgTableName + " (" + baseColumnsWithDataTypes + ") partitioned by (" + partitionColumns + " instanceexecid bigint) ";

        checkAndCreateRawView(rawViewDbName, rawViewName, rawViewDdl);
        checkAndCreateStageTable(baseTableDbName, stgTableName, stgTableDdl);
        checkAndCreateBaseTable(baseTableDbName, baseTableName, baseTableDdl,fileType);

        if(tokenize == 1){
            String tokenizeTableName = baseTableName+"_tokenize";
            String rawIntermediateTableName = rawTableName+"_intermediate";
            String stageIntermediateTableName = stgTableName+"_intermediate";

            String tokenizeColumnsWithDataTypes= null;
            String rawIntermediateColumnsWithDataTypes = null;

            //creating schema for tokenized table in base database
            GetProperties getPropertiesOfBaseColumns = new GetProperties();
            java.util.Properties basePropertiesOfColumns = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-columns");
            java.util.Properties basePropertiesOfDataTypes = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-data-types");
            Enumeration baseColumnsList = basePropertiesOfColumns.propertyNames();
            List<String> baseColumns1=Collections.list(baseColumnsList);
            Collections.sort(baseColumns1, new Comparator<String>() {

                public int compare(String o1, String o2) {
                    int n1 = Integer.valueOf(o1.split("\\.")[1]);
                    int n2 = Integer.valueOf(o2.split("\\.")[1]);
                    return (n1 - n2);
                }
            });


            StringBuilder tokenizeColumns = new StringBuilder();
            StringBuilder rawIntermediateColumns = new StringBuilder();
            if (!basePropertiesOfColumns.isEmpty()) {
                for (String key : baseColumns1) {
                    //String key = (String) baseColumnsList.nextElement();
                    tokenizeColumns.append(key.split("\\.")[0].replaceAll("transform_", "") + " " + basePropertiesOfDataTypes.getProperty(key.split("\\.")[0].replaceAll("transform_", "")) + ",");
                    rawIntermediateColumns.append(key.split("\\.")[0].replaceAll("transform_", "") + " " + basePropertiesOfDataTypes.getProperty(key.split("\\.")[0].replaceAll("transform_", ""))+ ",");
                    if(basePropertiesOfColumns.getProperty(key).contains("tokenize"))
                        tokenizeColumns.append(key.split("\\.")[0].replaceAll("transform_", "")+"_actual" + " " + basePropertiesOfDataTypes.getProperty(key.split("\\.")[0].replaceAll("transform_", "")) + ",");
                }
            }
            //removing trailing comma
            LOGGER.info("Length of tokenizedTableColumn" + tokenizeColumns.length());
            tokenizeColumnsWithDataTypes = tokenizeColumns.substring(0, tokenizeColumns.length() - 1);
            LOGGER.info("tokenizeColumnsWithDataTypes is " + tokenizeColumnsWithDataTypes);
            String tokenizeTableDdl = "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + tokenizeTableName + " (" + tokenizeColumnsWithDataTypes + ") partitioned by (" + partitionColumns + " instanceexecid bigint) ";

            checkAndCreateTokenizedTable(baseTableDbName,tokenizeTableName,tokenizeTableDdl);

            rawIntermediateColumnsWithDataTypes = rawIntermediateColumns.substring(0, rawIntermediateColumns.length() - 1);
            String rawIntermediateDdl = "CREATE TABLE IF NOT EXISTS " + rawTableDbName + "." + rawIntermediateTableName + " (incr_id INT,"+rawIntermediateColumnsWithDataTypes + ")";
            String stageIntermediateDdl = "CREATE TABLE IF NOT EXISTS " + baseTableDbName + "." + stageIntermediateTableName + " (incr_id INT,"+rawIntermediateColumnsWithDataTypes + ")";

            checkAndCreateRawIntermediateTable(rawTableDbName,rawIntermediateTableName,rawIntermediateDdl);
            checkAndCreateStageIntermediateTable(baseTableDbName, stageIntermediateTableName, stageIntermediateDdl);

        }
    }


    private static String getQuery(String name){
        return "SHOW TABLES LIKE '" + name + "'";
    }

    private void checkAndCreateRawTable(String dbName, String tableName, String ddl) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            //if("json".equalsIgnoreCase(fileTypeSelected)) {
                GetGeneralConfig generalConfig = new GetGeneralConfig();
                String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processIdSelected));

            String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
                String addSerde = "add jar "+serdePath;
                stmt.execute(addSerde);
            //}
            String deleteQuery="DROP TABLE IF EXISTS " + dbName + "." + tableName;
            stmt.executeUpdate(deleteQuery);
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

    private void checkAndCreateRawIntermediateTable(String dbName, String tableName, String ddl) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            //if("json".equalsIgnoreCase(fileTypeSelected)) {
            GetGeneralConfig generalConfig = new GetGeneralConfig();
            String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
            String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
            ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processIdSelected));

            String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
            String addSerde = "add jar "+serdePath;
            stmt.execute(addSerde);
            //}
            String deleteQuery="DROP TABLE IF EXISTS " + dbName + "." + tableName;
            stmt.executeUpdate(deleteQuery);
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(tableName));
            if (!rs.next()) {
                LOGGER.info("Raw intermediate table does not exist Creating table " + tableName);
                LOGGER.info("Creating raw intermediate table using " + ddl);
                stmt.executeUpdate(ddl);
                LOGGER.info("Raw intermediate table created.");
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating raw intermediate table" + e);
            throw new ETLException(e);
        }

    }

    private void checkAndCreateRawView(String dbName, String stageViewName, String ddl) {
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            String deleteQuery="DROP VIEW IF EXISTS " + dbName + "." + stageViewName;
            stmt.executeUpdate(deleteQuery);
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(stageViewName));
            if (!rs.next()) {
                GetGeneralConfig generalConfig = new GetGeneralConfig();
                String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processIdSelected));

                String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
                String addSerde = "add jar "+serdePath;
                stmt.execute(addSerde);

                if(ddl.contains("tokenize")){
                    String customUDFPath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/etl-driver-1.1-SNAPSHOT.jar";
                    String addUDF = "add jar "+customUDFPath;
                    stmt.execute(addUDF);
                    String tempFunction = "create temporary function tokenize as \'com.wipro.ats.bdre.im.HiveUDF\'";
                    LOGGER.info("Temporary function creation "+tempFunction);
                    stmt.execute(tempFunction);
                }

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

    private void checkAndCreateBaseTable(String dbName, String baseTable, String ddl,String fileType) {
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(baseTable));
            if (!rs.next()) {
                if("json".equalsIgnoreCase(fileType)) {
                    LOGGER.info("file type is json");
                    GetGeneralConfig generalConfig = new GetGeneralConfig();
                    String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                    String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                    ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processIdSelected));

                    String serdePath = hdfsURI + "/user/" + bdreLinuxUserName + "/wf/1/5/" + process.getParentProcessId() + "/lib/hive-hcatalog-core-0.13.1.jar";
                    String addSerde = "add jar " + serdePath;
                    stmt.execute(addSerde);
                }
                LOGGER.info("Base table does not exist.Creating Table " + baseTable);
                LOGGER.info("Creating base table using "+ddl);
                stmt.executeUpdate(ddl);
            }
            else{

            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating base table" + e);
            throw new ETLException(e);
        }
    }

    private void checkAndCreateStageIntermediateTable(String dbName, String baseTable, String ddl) {
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(baseTable));
            if (!rs.next()) {
                GetGeneralConfig generalConfig = new GetGeneralConfig();
                String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processIdSelected));

                String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
                String addSerde = "add jar "+serdePath;
                stmt.execute(addSerde);

                LOGGER.info("Stage intermediate table does not exist.Creating Table " + baseTable);
                LOGGER.info("Creating stage intermediate table using "+ddl);
                stmt.executeUpdate(ddl);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating stage intermediate table" + e);
            throw new ETLException(e);
        }
    }

    private void checkAndCreateStageTable(String dbName, String baseTable, String ddl) {
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(baseTable));
            if (!rs.next()) {
                GetGeneralConfig generalConfig = new GetGeneralConfig();
                String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processIdSelected));

                String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
                String addSerde = "add jar "+serdePath;
                stmt.execute(addSerde);

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




    private void checkAndCreateTokenizedTable(String dbName, String tokenizeTable, String ddl){
        try {

            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(CreateRawBaseTables.getQuery(tokenizeTable));
            if (!rs.next()) {
                GetGeneralConfig generalConfig = new GetGeneralConfig();
                String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processIdSelected));

                String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
                String addSerde = "add jar "+serdePath;
                stmt.execute(addSerde);

                LOGGER.info("Tokenize table does not exist.Creating Table " + tokenizeTable);
                LOGGER.info("Creating tokenize table using "+ddl);
                stmt.executeUpdate(ddl);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            LOGGER.error("Error while creating tokenize table" + e);
            throw new ETLException(e);
        }
    }
}