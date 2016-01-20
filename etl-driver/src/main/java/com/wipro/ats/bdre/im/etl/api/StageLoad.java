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

import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by vishnu on 12/17/14.
 */
public class StageLoad extends ETLBase {

    private static final Logger LOGGER = Logger.getLogger(StageLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"spId", "sub-process-id", " Process id of Stage Load"},
            {"ied", "instance-exec-id", " instance exec id"},
            {"minId", "min-batch-id", " Min batch Id"},
            {"maxId", "max-batch-id", " Max batch Id"}
    };

    public void execute(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        String subProcessId = commandLine.getOptionValue("sub-process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        String minId = commandLine.getOptionValue("minId");
        String maxId = commandLine.getOptionValue("maxId");
        init(processId);

        //Getting Stage view information
        String stageViewName =stgView;
        String stageViewDbName = stgDb;

        //Getting base table info
        String baseTableName = baseTable;
        String baseDbName = baseDb;
      //  String baseTableDdl = getBaseTable().getDdl();

        processStageLoad(stageViewDbName, stageViewName, baseDbName, baseTableName,instanceExecId, minId, maxId,subProcessId);
    }

    //Read the partition keys from hive table
    //input - database name, table Name
    //output - partition column names as comma seperated;


    private void processStageLoad(String stageDbName, String viewName, String baseDbName, String baseTableName, String instanceExecId, String minBatchId, String maxBatchId,String stageLoadProcessId) {
        try {

            Connection rawCon = getHiveJDBCConnection(stageDbName);
            Connection baseCon = getHiveJDBCConnection(baseDbName);
/*            baseCon.setClientInfo("hive.exec.dynamic.partition.mode", "nonstrict");
            baseCon.setClientInfo("hive.exec.dynamic.partition", "true");
            baseCon.setClientInfo("hive.exec.max.dynamic.partitions.pernode", "1000")*/;
            Statement rawConStatement = rawCon.createStatement();
            Statement baseConStatement = baseCon.createStatement();

            String stageTableName = baseTableName + "_"+ instanceExecId;
         //stage table creation moved to rawload
            /*   //checking if stage table exists. If not create one
            String stQuery = "SHOW TABLES LIKE '" + stageTableName + "'";
            LOGGER.info("stQuery="+stQuery);
            ResultSet rs = baseConStatement.executeQuery(stQuery);

            if (!rs.next()) {

                Table etlBaseTable = getMetaStoreClient().getTable(baseDbName, baseTableName);
                Table etlStageTable= etlBaseTable.deepCopy();
                etlStageTable.setTableName(stageTableName);
                StorageDescriptor storageDescriptor=etlStageTable.getSd();
                storageDescriptor.setLocation(storageDescriptor.getLocation() + "_" + instanceExecId);
                etlStageTable.setSd(storageDescriptor);
                getMetaStoreClient().createTable(etlStageTable);
            }*/

            LOGGER.debug("Reading fields from stage table");
            String fieldNames = getColumnNames(baseDbName, stageTableName,stageLoadProcessId);
            LOGGER.info("Field names in the stage are " + fieldNames);
            LOGGER.debug("Reading partitions from stage table");
            String partitionKeys = getPartitionKeys(baseDbName,stageTableName,stageLoadProcessId);
            /** partitionKeys will contain comma, so there is no need to
             * provide FILE_FIELD_SEPERATOR after partitionKeys in query
             */
            String query = "INSERT OVERWRITE TABLE " + baseDbName +"."+ stageTableName +
                    " PARTITION ( " + partitionKeys + "instanceexecid) SELECT " +
            fieldNames + IMConstant.FILE_FIELD_SEPERATOR + partitionKeys + instanceExecId + " FROM " + stageDbName + "."+ viewName +
                    " where batchid>=" + minBatchId + " and " + " batchid<=" + maxBatchId;

            LOGGER.info(query);
            baseConStatement.execute("set hive.exec.dynamic.partition.mode=nonstrict");
            baseConStatement.execute("set hive.exec.dynamic.partition=true");
            baseConStatement.execute("set hive.exec.max.dynamic.partitions.pernode=1000");

            baseConStatement.executeUpdate(query);
            LOGGER.debug("StageLoad Completed...");

        } catch (Exception e) {

            LOGGER.error(e);
            throw new ETLException(e);
        }
    }

    private String getPartitionKeys(String dbName, String tableName,String stageLoadProcessId) throws Exception {

        StringBuffer stringBuffer = new StringBuffer("");
        String result="";
//        HiveMetaStoreClient hclient = getMetaStoreClient();
//        Table stageTable = hclient.getTable(dbName, tableName);
//        List<FieldSchema> partitionKeys = stageTable.getPartitionKeys();
//        LOGGER.debug("Size of List partitionKeys"+partitionKeys.size());
        GetProperties getPropertiesOfRawTable = new GetProperties();
        LOGGER.info("process is " + stageLoadProcessId);
        java.util.Properties partitionproperties = getPropertiesOfRawTable.getProperties(stageLoadProcessId, "partition");
        String partitions = partitionproperties.getProperty("partition_columns");
        LOGGER.info("list of partitions is " + partitions);
        if(!("".equals(partitions)) || !(partitions == null)) {
            String[] partitionKeys = partitions.split(",");
            for (int i = 0; i < (partitionKeys.length); i++) {
                stringBuffer.append(partitionKeys[i].split(" ")[0]);
                stringBuffer.append(",");
            }
        }
        LOGGER.debug("Partition column is"+ stringBuffer);
        LOGGER.debug("Size of result is"+stringBuffer.length());
        result = stringBuffer.toString();
        result=result.substring(0,result.length());
        LOGGER.info(result);
        return result;
    }

    private String getColumnNames(String dbName, String tableName,String stageLoadProcessId) throws Exception {
//        List<FieldSchema> fields = getMetaStoreClient().getFields(dbName, tableName);
        GetProperties getPropertiesOfRawTable = new GetProperties();
        String result="";
        StringBuilder columnList = new StringBuilder();
        java.util.Properties columnValues = getPropertiesOfRawTable.getProperties(stageLoadProcessId, "base-columns");
        Enumeration e = columnValues.propertyNames();
        if (columnValues.size() != 0) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                columnList.append(key.replaceAll("transform_",""));
                columnList.append(",");
            }
            result=columnList.substring(0, columnList.length() - 1);
            LOGGER.debug("column list = " + result);
        }


        return result;
    }
}
