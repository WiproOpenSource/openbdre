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
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

/**
 * Created by vishnu on 12/17/14.
 */
public class StageLoad extends ETLBase {

    private static final Logger LOGGER = Logger.getLogger(StageLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"ied", "instance-exec-id", " instance exec id"},
            {"minId", "min-batch-id", " Min batch Id"},
            {"maxId", "max-batch-id", " Max batch Id"}
    };

    public void execute(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        String minId = commandLine.getOptionValue("minId");
        String maxId = commandLine.getOptionValue("maxId");
        loadStageHiveTableInfo(processId);
        CreateRawBaseTables createRawBaseTables =new CreateRawBaseTables();
        String[] createTablesArgs={"-p",processId,"-instExecId",instanceExecId };
        createRawBaseTables.executeStageLoad(createTablesArgs);

        //Getting Stage view information
        String stageViewName = stgView;
        String stageViewDbName = stgDb;

        //Getting base table info
        String baseTableName = baseTable;
        String baseDbName = baseDb;

        processStageLoad(stageViewDbName, stageViewName, baseDbName, baseTableName,instanceExecId, minId, maxId,processId);
    }

    //Read the partition keys from hive table
    //input - database name, table Name
    //output - partition column names as comma seperated


    private void processStageLoad(String stageDbName, String viewName, String baseDbName, String baseTableName, String instanceExecId, String minBatchId, String maxBatchId,String stageLoadProcessId) {
        try {

            Connection baseCon = getHiveJDBCConnection(baseDbName);

            Statement baseConStatement = baseCon.createStatement();

            String stageTableName = baseTableName + "_"+ instanceExecId;
         //stage table creation moved to rawload

            LOGGER.debug("Reading fields from stage table");
            String fieldNames = getColumnNames(stageLoadProcessId);
            LOGGER.info("Field names in the stage are " + fieldNames);
            LOGGER.debug("Reading partitions from stage table");
            String partitionKeys = getPartitionKeys(stageLoadProcessId);
            /** partitionKeys will contain comma, so there is no need to
             * provide FILE_FIELD_SEPERATOR after partitionKeys in query
             */

            GetGeneralConfig generalConfig = new GetGeneralConfig();
            String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
            String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
            ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(stageLoadProcessId));

            String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
            String addSerde = "add jar "+serdePath;
            baseConStatement.execute(addSerde);

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

    private String getPartitionKeys(String stageLoadProcessId) throws ETLException {

        StringBuilder stringBuilder = new StringBuilder("");
        String result="";
        GetProperties getPropertiesOfRawTable = new GetProperties();
        LOGGER.info("process is " + stageLoadProcessId);
        java.util.Properties partitionproperties = getPropertiesOfRawTable.getProperties(stageLoadProcessId, "partition");
        String partitions = partitionproperties.getProperty("partition_columns");
        LOGGER.info("list of partitions is " + partitions);
        if(!("".equals(partitions)) && !(partitions == null)) {
            String[] partitionKeys = partitions.split(",");
            for (int i = 0; i < (partitionKeys.length); i++) {
                stringBuilder.append(partitionKeys[i].split(" ")[0]);
                stringBuilder.append(",");
            }
        }
        LOGGER.debug("Partition column is"+ stringBuilder);
        LOGGER.debug("Size of result is"+stringBuilder.length());
        result = stringBuilder.toString();
        result=result.substring(0,result.length());
        LOGGER.info(result);
        return result;
    }

    private String getColumnNames(String stageLoadProcessId) throws ETLException {
        GetProperties getPropertiesOfRawTable = new GetProperties();
        String result="";
        StringBuilder columnList = new StringBuilder();
        GetProperties getFileType = new GetProperties();
        java.util.Properties baseProperties = getFileType.getProperties(stgLoad, "base-table");
        String fileType = baseProperties.getProperty("file_type");

        if("json".equalsIgnoreCase(fileType)){
            GetProperties getPropertiesOfBaseColumns = new GetProperties();
            java.util.Properties basePropertiesOfColumns = getPropertiesOfBaseColumns.getProperties(stgLoad, "base-cols");
            Enumeration columns = basePropertiesOfColumns.propertyNames();
            List<String> orderOfCloumns = Collections.list(columns);
            Collections.sort(orderOfCloumns, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    int n1=Integer.valueOf(o1.split("\\.")[1]);
                    int n2=Integer.valueOf(o2.split("\\.")[1]);
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
            for(String column:baseColumns){
                if(column.contains(".")){
                    String firstColumnName = column.split("\\.")[0];
                    if(!columnList.toString().contains(firstColumnName))
                        columnList.append(firstColumnName+",");
                }
                else
                    columnList.append(column+",");
            }
            result = columnList.substring(0, columnList.length() - 1);
            LOGGER.debug("column list = " + result);
        }
        else {
            java.util.Properties columnValues = getPropertiesOfRawTable.getProperties(stageLoadProcessId, "base-columns");
            Enumeration e = columnValues.propertyNames();
            if (!columnValues.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    columnList.append(key.replaceAll("transform_", ""));
                    columnList.append(",");
                }
                result = columnList.substring(0, columnList.length() - 1);
                LOGGER.debug("column list = " + result);
            }
        }


        return result;
    }
}
