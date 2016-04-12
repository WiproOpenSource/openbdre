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

package com.wipro.ats.bdre.clustermigration;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.clustermigration.beans.MigrationPreprocessorInfo;
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by cloudera on 3/29/16.
 */
public class MigrationPreprocessor extends BaseStructure{
    private static final Logger LOGGER = Logger.getLogger(MigrationPreprocessor.class);
    private static String sourceRegularColumns=new String();   //comma-separated with datatype
    private static String sourcePartitionColumns=new String(); //comma-separated with datatype
    private static String stgPartitionColumns=new String();    //replacing source tech_partition with bdre tech partition
    private static String stgTableDDL=new String();            //contains source stage table ddl
    private static String destTableDDL=new String();           //contains destination table ddl
    private static String filterCondition=new String();
    private static String srcStgTableLocation=new String();
    private static String destTableLocation=new String();
    private static final String[][] PARAMS_STRUCTURE = {
            {"pp", "parent-process-id", "Parent Process id of migration preprocessor"},
            {"p", "process-id", " Process id of migration preprocessor"},
            {"ied", "instance-exec-id", " instance exec id of preprocessor"}
    };


    protected static Connection getHiveJDBCConnection(String dbName, String hiveConnection) throws Exception {
        try {
            Class.forName(IMConstant.HIVE_DRIVER_NAME);
            String hiveUser = IMConfig.getProperty("etl.hive-jdbcuser");
            String hivePassword = IMConfig.getProperty("etl.hive-jdbcpassword");
            Connection con = DriverManager.getConnection(hiveConnection + "/" + dbName, hiveUser, hivePassword);
            con.createStatement().execute("set hive.exec.dynamic.partition.mode=nonstrict");
            con.createStatement().execute("set hive.exec.dynamic.partition=true");
            con.createStatement().execute("set hive.exec.max.dynamic.partitions.pernode=1000");
            return con;
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    public MigrationPreprocessorInfo execute(String[] params) throws Exception{

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String parentProcessId=commandLine.getOptionValue("parent-process-id");
        String processId = commandLine.getOptionValue("process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        MigrationPreprocessorInfo mpInfo = prepareMigrate(parentProcessId,processId,instanceExecId);
        return mpInfo;
    }
    private MigrationPreprocessorInfo prepareMigrate(String parentProcessId, String processId, String instanceExecId) throws Exception {
        MigrationPreprocessorInfo migrationPreprocessorInfo = new MigrationPreprocessorInfo();
        Properties params=getParams(parentProcessId,"hive-migration");
        String table = params.get("src-table").toString();
        String sourceDb = params.get("src-db").toString();
        String destDb= params.get("dest-db").toString();
        String sourceStgtable=table + "_stg";
        String bdreTechPartition=params.get("bdre-tech-pt").toString();
        String sourceNameNodeAddress=params.get("src-nn").toString();
        String destNameNodeAddress=params.get("dest-nn").toString();
        String sourceJobTrackerAddress=params.get("src-jt").toString();
        String destJobTrackerAddress=params.get("dest-jt").toString();
        String sourceHiveConnection=params.get("src-hive").toString();
        String destHiveConnection=params.get("dest-hive").toString();
        Connection conn = getHiveJDBCConnection(sourceDb,sourceHiveConnection);
        Statement st = conn.createStatement();
        List<String> sourcePartitionList = getCurrentSourcePartitionList(st, sourceDb, table);

        List<String> previousPartitionList = getLastSourcePartitionList(processId, table);

        Set<String> modifiedBusinessPartitionSet = getChangedBusinessPartitionSet(sourcePartitionList, previousPartitionList);

        List<String> sourceColumnList = getCurrentSourceColumnList(sourceDb, table, sourceHiveConnection);

        List<String> destColumnList = getDestColumnList(processId, table);

        List<String> addedColumnList = getNewRegularColumnsAtSourceList(sourceColumnList, destColumnList);

        //persisting the current partition info to process logs
        logCurrentSourcePartitions(sourcePartitionList, processId, instanceExecId, table);

        //persisting the current column info to process logs
        logCurrentSourceColumns(sourceColumnList, processId, instanceExecId, table);

        //creating the source stage table
        formStageAndDestTableDDLs(st, sourceColumnList, sourceDb, destDb, table,sourceStgtable,bdreTechPartition, processId,instanceExecId);

        if(checkIfDestTableExists(destDb,table,destHiveConnection))
            alterDestTable(st,addedColumnList,destDb,table);
        else
            execDestTableDDL(destDb,destHiveConnection);

        filterCondition=formFilterCondition(modifiedBusinessPartitionSet,sourcePartitionColumns);
        LOGGER.debug("filterCondition = " + filterCondition);


        srcStgTableLocation=getTableLocation(sourceDb,sourceHiveConnection,sourceStgtable);
        destTableLocation=getTableLocation(destDb,destHiveConnection,table);
        migrationPreprocessorInfo=setOozieUtilProperties(sourceDb,destDb,sourceStgtable,table,sourceJobTrackerAddress,sourceNameNodeAddress,destNameNodeAddress,sourceHiveConnection,processId,instanceExecId,migrationPreprocessorInfo);

        st.close();
        conn.close();
        return migrationPreprocessorInfo;
    }

    private List<String> getCurrentSourcePartitionList(Statement st, String sourceDb, String table) throws Exception {
        ResultSet rsPartitions = st.executeQuery("show partitions " + sourceDb + "." + table);
        List<String> sourcePartitionList = new ArrayList<>();
        while (rsPartitions.next()) {
            sourcePartitionList.add(rsPartitions.getString(1));
        }
        rsPartitions.close();
        return sourcePartitionList;
    }

    private List<String> getLastSourcePartitionList(String processId, String table) {
        //obtaining the partition data corresponding to the previous execution for comparison with current partition data
        ProcessLog processLog = new ProcessLog();
        List<ProcessLogInfo> partitionLogInfos = processLog.listLastInstanceRef(Integer.parseInt(processId), table + " partition");
        List<String> previousPartitionList = new ArrayList<>();
        for (ProcessLogInfo processLogInfo : partitionLogInfos) {
            previousPartitionList.add(processLogInfo.getMessage());
        }
        return previousPartitionList;
    }

    private Set<String> getChangedBusinessPartitionSet(List<String> sourcePartitionList, List<String> previousPartitionList) {
        List<String> deletedPartitionList = new ArrayList<>(previousPartitionList);
        deletedPartitionList.removeAll(sourcePartitionList);
        List<String> addedPartitionList = new ArrayList<>(sourcePartitionList);
        addedPartitionList.removeAll(previousPartitionList);
        List<String> removedBusinessPartitionList = new ArrayList<>();
        List<String> addedBusinessPartitionList = new ArrayList<>();

        for (String addedPartition : addedPartitionList) {
            addedBusinessPartitionList.add(addedPartition.substring(0, addedPartition.lastIndexOf("/")));
        }

        for (String deletedPartition : deletedPartitionList) {
            removedBusinessPartitionList.add(deletedPartition.substring(0, deletedPartition.lastIndexOf("/")));
        }
        Set<String> businessPartitionSet = new HashSet<>();
        businessPartitionSet.addAll(removedBusinessPartitionList);
        businessPartitionSet.addAll(addedBusinessPartitionList);

        for (String editedBusinessPartition : businessPartitionSet) {
            LOGGER.debug("editedBusinessPartition = " + editedBusinessPartition);
        }
        return businessPartitionSet;
    }

    private List<String> getCurrentSourceColumnList(String sourceDb, String table, String sourceHiveConnection) throws Exception {
        List<String> sourceColumnList = new ArrayList<>();
        DatabaseMetaData metaData = getHiveJDBCConnection(sourceDb,sourceHiveConnection).getMetaData();
        ResultSet rsColumns = metaData.getColumns(null, sourceDb, table, null);
        while (rsColumns.next()) {
            String columnName = rsColumns.getString("COLUMN_NAME");
            String dataType = rsColumns.getString("TYPE_NAME");
            sourceColumnList.add(columnName + " " + dataType);
        }
        rsColumns.close();
        return sourceColumnList;
    }

    private List<String> getDestColumnList(String processId, String table) {
        //obtaining the column data corresponding to the previous execution for comparison with current partition data
        ProcessLog processLog = new ProcessLog();
        List<ProcessLogInfo> columnLogInfos = processLog.listLastInstanceRef(Integer.parseInt(processId), table + " column");
        List<String> previousColumnList = new ArrayList<>();
        for (ProcessLogInfo processLogInfo : columnLogInfos) {
            previousColumnList.add(processLogInfo.getMessage());
        }
        return previousColumnList;
    }

    private List<String> getNewRegularColumnsAtSourceList(List<String> sourceColumnList, List<String> previousColumnList) {
        List<String> addedColumnList = new ArrayList<>(sourceColumnList);
        //TODO: if previous list is empty it means there are no log entries in process log currently. But this could be the first run of the whole migration program, hence there will obviously be no log entries, in this case add logic to test destination columns using gethivejdbcconnection or you can skip this logic entirely and get columns always from hive metadata.
        if(previousColumnList.size()>0) addedColumnList.removeAll(previousColumnList);

        for (String addedColumn : addedColumnList) {
            LOGGER.debug("addedColumn = " + addedColumn);
        }
        return addedColumnList;
    }

    private void logCurrentSourcePartitions(List<String> sourcePartitionList, String processId, String instanceExecId, String table) {
        ProcessLog processLog = new ProcessLog();
        List<ProcessLogInfo> partitionLogInfoList = new ArrayList<>();
        for (String sourcePartition : sourcePartitionList) {
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            processLogInfo.setAddTs(new Date());
            processLogInfo.setProcessId(Integer.parseInt(processId));
            processLogInfo.setMessage(sourcePartition);
            processLogInfo.setInstanceRef(Long.parseLong(instanceExecId));
            processLogInfo.setLogCategory("C2C");
            processLogInfo.setMessageId(table + " partition");
            partitionLogInfoList.add(processLogInfo);
        }
        processLog.logList(partitionLogInfoList);
    }

    private void logCurrentSourceColumns(List<String> sourceColumnList, String processId, String instanceExecId, String table) {
        ProcessLog processLog = new ProcessLog();
        List<ProcessLogInfo> columnLogInfoList = new ArrayList<>();
        for (String sourceColumn : sourceColumnList) {
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            processLogInfo.setAddTs(new Date());
            processLogInfo.setProcessId(Integer.parseInt(processId));
            processLogInfo.setMessage(sourceColumn);
            processLogInfo.setInstanceRef(Long.parseLong(instanceExecId));
            processLogInfo.setLogCategory("C2C");
            processLogInfo.setMessageId(table + " column");
            columnLogInfoList.add(processLogInfo);
        }
        processLog.logList(columnLogInfoList);
    }

    private void formStageAndDestTableDDLs(Statement st, List<String> sourceColumnList, String sourceDb, String destDb, String table, String sourceStgtable, String bdreTechPartition, String processId,String instanceExecId) throws Exception {
        ResultSet rsPartitionList = st.executeQuery("desc account");
        int index = 0;
        StringBuffer partitionList = new StringBuffer("");
        List<String> sourcePartitionColumnList = new ArrayList<>();
        while (rsPartitionList.next()) {
            if (rsPartitionList.getString(1).equals("# Partition Information")) {
                index++;
            }
            if (index > 0) index++;
            if (index > 4) {
                String partitionNameAndDataType = rsPartitionList.getString(1) + " " + rsPartitionList.getString(2).toUpperCase();
                partitionList.append(partitionNameAndDataType + ",");
                sourcePartitionColumnList.add(partitionNameAndDataType);
            }
        }

        sourceColumnList.removeAll(sourcePartitionColumnList);
        //removing partition columns from the total columns list and concatenating with commas
        StringBuffer finalColumns = new StringBuffer("");
        for (String sourceColumn : sourceColumnList) {
            finalColumns.append(sourceColumn + ",");
        }
        sourceRegularColumns=finalColumns.substring(0, finalColumns.length() - 1);
        sourcePartitionColumns=partitionList.substring(0, partitionList.length() - 1);
        stgPartitionColumns=sourcePartitionColumns.substring(0,sourcePartitionColumns.lastIndexOf(","))+","+bdreTechPartition;
        stgTableDDL = "create external table " + sourceDb + "." + sourceStgtable + " (" + sourceRegularColumns + ") " + "partitioned by (" + stgPartitionColumns + ") stored as orc location '/tmp/"+processId+"/"+instanceExecId+"'";
        destTableDDL = "create table " + destDb + "." + table  + " (" + sourceRegularColumns + ") " + "partitioned by (" + stgPartitionColumns + ") stored as orc";
        LOGGER.debug("stgTableDDL = " + stgTableDDL);
        LOGGER.debug("destTableDDL = " + destTableDDL);
        execStageTableDDL(st,sourceDb,sourceStgtable);
        rsPartitionList.close();
    }

    private void execStageTableDDL(Statement st,String sourceDb, String sourceStgtable) throws Exception{
        st.executeUpdate("drop table if exists " + sourceDb + "." + sourceStgtable);
        st.executeUpdate(stgTableDDL);
    }

    private boolean checkIfDestTableExists(String destDb,String table, String destHiveConnection) throws Exception{
        Connection conn = getHiveJDBCConnection(destDb,destHiveConnection);
        Statement st = conn.createStatement();
        ResultSet rsPartitions = st.executeQuery("show tables");
        boolean destTableExists=false;
        while (rsPartitions.next()) {
            if(rsPartitions.getString(1).equalsIgnoreCase(table)) {
                destTableExists=true;
                break;
            }
        }
        rsPartitions.close();
        st.close();
        conn.close();
        return destTableExists;
    }

    private void alterDestTable(Statement st, List<String> addedColumnList, String destDb,String table) throws Exception{
        StringBuffer addedColumnsWithDatatypes = new StringBuffer("");
        for(String addedColumn:addedColumnList){
            addedColumnsWithDatatypes.append(addedColumn+",");
        }
        if(addedColumnsWithDatatypes.length()>0){
            LOGGER.debug("Additional columns have been found");
            String alterDestTableDDL="alter table "+destDb+"."+table+" add columns ("+addedColumnsWithDatatypes.substring(0,addedColumnsWithDatatypes.length()-1)+")";
            LOGGER.debug("alterDestTableDDL = " + alterDestTableDDL);
            st.executeUpdate(alterDestTableDDL);
        }
        else {
            LOGGER.debug("No additional columns have been found");
        }
    }

    private void execDestTableDDL(String destDb,String destHiveConnection) throws Exception{
        Connection conn = getHiveJDBCConnection(destDb,destHiveConnection);
        Statement st = conn.createStatement();
        LOGGER.debug("Destination table not found. Hence creating one");
        st.executeUpdate(destTableDDL);
        st.close();
        conn.close();
    }

    private String formFilterCondition(Set<String> modifiedBusinessPartitionSet, String sourcePartitionColumns){
        StringBuffer filterCondition=new StringBuffer();
        Map<String,String> partitionDataTypeMap = new HashMap<>();
        String[] partitionArray = sourcePartitionColumns.split(",");
        for(int i=0;i<partitionArray.length;i++){
            partitionDataTypeMap.put(partitionArray[i].toUpperCase().substring(0,partitionArray[i].indexOf(" ")),partitionArray[i].toUpperCase().substring(partitionArray[i].indexOf(" "),partitionArray[i].length()));
        }
        for(String busPartition:modifiedBusinessPartitionSet){
            String onePartition="";
            String[] eachPartitionValue = busPartition.split("/");
            StringBuffer totalRow=new StringBuffer("");
            for(int i=0; i<eachPartitionValue.length;i++){
                String partitionDataType=partitionDataTypeMap.get(eachPartitionValue[i].split("=")[0].toUpperCase());
                String colAndPartitionValue[]=eachPartitionValue[i].split("=");
                if(partitionDataType.trim().contains("STRING")||partitionDataType.trim().contains("CHAR")||partitionDataType.trim().contains("DATE")||partitionDataType.trim().contains("TIME")) {
                    colAndPartitionValue[1]="'"+colAndPartitionValue[1]+"'";
                }
                onePartition=colAndPartitionValue[0]+"="+colAndPartitionValue[1] +" AND ";
                totalRow.append(onePartition);
            }
            String trimmedTotalRow=totalRow.substring(0,totalRow.lastIndexOf(" AND "));
            filterCondition.append(trimmedTotalRow).append(" OR ");
        }

        return(filterCondition.toString().isEmpty()?"'a'='a'":filterCondition.substring(0,filterCondition.lastIndexOf(" OR ")));
    }

    private String removeDataTypesFromColumnList(String commaSeparatedColumns){
        StringBuffer columnListWithoutDataTypes= new StringBuffer("");
        String[] columns = commaSeparatedColumns.split(",");
        for(int i=0;i<columns.length;i++){
            columnListWithoutDataTypes.append(columns[i].substring(0,columns[i].indexOf(" "))).append(",");
        }
        return columnListWithoutDataTypes.toString().substring(0,columnListWithoutDataTypes.length()-1);
    }

    private String getTableLocation(String sourceDb,String hiveConnection,String sourceStgTable) throws Exception{
        Connection conn = getHiveJDBCConnection(sourceDb,hiveConnection);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("desc formatted "+sourceStgTable);
        String tableLocation=new String();
        while (rs.next()) {
            if(rs.getString(1).trim().equals("Location:")) tableLocation=rs.getString(2);
        }
        return tableLocation;
    }

    private MigrationPreprocessorInfo setOozieUtilProperties(String sourceDb,String destDb,String sourceStgtable,String table, String sourceJobTrackerAddress, String sourceNameNodeAddress, String destNameNodeAddress, String srcHiveConnection,String processId, String instanceExecId, MigrationPreprocessorInfo migrationPreprocessorInfo){
        String stgPartitionsWithoutDataTypes=removeDataTypesFromColumnList(stgPartitionColumns);
        String stgRegColumnsWithoutDataTypes=removeDataTypesFromColumnList(sourceRegularColumns);
        migrationPreprocessorInfo.setSrcStgDb(sourceDb);
        migrationPreprocessorInfo.setSrcStgTable(sourceStgtable);
        migrationPreprocessorInfo.setStgAllPartCols(stgPartitionsWithoutDataTypes);
        migrationPreprocessorInfo.setSrcRegularCols(stgRegColumnsWithoutDataTypes);
        migrationPreprocessorInfo.setSrcBPCols(stgPartitionsWithoutDataTypes.substring(0,stgPartitionsWithoutDataTypes.lastIndexOf(",")));
        migrationPreprocessorInfo.setSrcDb(sourceDb);
        migrationPreprocessorInfo.setSrcTable(table);
        migrationPreprocessorInfo.setFilterCondition(filterCondition);
        migrationPreprocessorInfo.setJtAddress(sourceJobTrackerAddress);
        migrationPreprocessorInfo.setNnAddress(sourceNameNodeAddress);
        //replacing the source namenode(quickstart.cloudera or sandbox.hortonworks.com) with actual public ip obtained from through properties (src-nn)
        migrationPreprocessorInfo.setSrcStgTablePath(sourceNameNodeAddress+srcStgTableLocation.substring(srcStgTableLocation.indexOf("/",7),srcStgTableLocation.length())+"/*");
        migrationPreprocessorInfo.setDestStgFolderPath(destNameNodeAddress+"/tmp/"+processId+"/"+instanceExecId);
        migrationPreprocessorInfo.setDestStgFolderContentPath(destNameNodeAddress+"/tmp/"+processId+"/"+instanceExecId+"/");
        migrationPreprocessorInfo.setDestTablePath(destTableLocation);
        migrationPreprocessorInfo.setDestDb(destDb);
        migrationPreprocessorInfo.setDestTable(table);
        migrationPreprocessorInfo.setDestFileSystem(destNameNodeAddress);
        return migrationPreprocessorInfo;
    }

    public Properties getParams(String pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(pid, configGroup);
        return listForParams;
    }

}
