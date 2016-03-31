package com.wipro.ats.bdre.clustermigration;

import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by cloudera on 3/29/16.
 */
public class MigrationPreprocessor {
    private static final Logger LOGGER = Logger.getLogger(MigrationPreprocessor.class);
    public static String sourceRegularColumns=new String();   //comma-separated with datatype
    public static String sourcePartitionColumns=new String(); //comma-separated with datatype
    public static String stgPartitionColumns=new String();    //replacing source tech_partition with bdre tech partition
    public static String stgTableDDL=new String();            //contains source stage table ddl
    public static String destTableDDL=new String();           //contains destination table ddl
    protected static Connection getHiveJDBCConnection(String dbName) throws Exception {
        try {
            Class.forName(IMConstant.HIVE_DRIVER_NAME);
            String hiveConnection = "jdbc:hive2://quickstart.cloudera:10000";
            String hiveUser = "cloudera";
            String hivePassword = "cloudera";
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

    public static void main(String[] args) throws Exception {
        String processId = args[0];
        String instanceExecId = args[1];
        String table = "account";
        String sourceDb = "sourcedb";
        String destDb= "destdb";
        String bdreTechPartition="instanceexecid bigint";
        Connection conn = getHiveJDBCConnection(sourceDb);
        Statement st = conn.createStatement();
        List<String> sourcePartitionList = getCurrentSourcePartitionList(st, sourceDb, table);

        List<String> previousPartitionList = getLastSourcePartitionList(processId, table);

        Set<String> modifiedBusinessPartitionSet = getChangedBusinessPartitionSet(sourcePartitionList, previousPartitionList);

        List<String> sourceColumnList = getCurrentSourceColumnList(sourceDb, table);

        List<String> destColumnList = getDestColumnList(processId, table);

        List<String> addedColumnList = getNewRegularColumnsAtSourceList(sourceColumnList, destColumnList);

        //persisting the current partition info to process logs
        logCurrentSourcePartitions(sourcePartitionList, processId, instanceExecId, table);

        //persisting the current column info to process logs
        logCurrentSourceColumns(sourceColumnList, processId, instanceExecId, table);

        //creating the source stage table
        formStageAndDestTableDDLs(st, sourceColumnList, sourceDb, destDb, table, bdreTechPartition);

        if(checkIfDestTableExists(destDb,table))
            alterDestTable(st,addedColumnList,destDb,table);
        else
            execDestTableDDL(st);

        String filterCondition=formFilterCondition(modifiedBusinessPartitionSet,sourcePartitionColumns);
        LOGGER.debug("filterCondition = " + filterCondition);
        st.close();
        conn.close();
    }

    public static List<String> getCurrentSourcePartitionList(Statement st, String sourceDb, String table) throws Exception {
        ResultSet rsPartitions = st.executeQuery("show partitions " + sourceDb + "." + table);
        List<String> sourcePartitionList = new ArrayList<>();
        while (rsPartitions.next()) {
            sourcePartitionList.add(rsPartitions.getString(1));
        }
        rsPartitions.close();
        return sourcePartitionList;
    }

    public static List<String> getLastSourcePartitionList(String processId, String table) {
        //obtaining the partition data corresponding to the previous execution for comparison with current partition data
        ProcessLog processLog = new ProcessLog();
        List<ProcessLogInfo> partitionLogInfos = processLog.listLastInstanceRef(Integer.parseInt(processId), table + " partition");
        List<String> previousPartitionList = new ArrayList<>();
        for (ProcessLogInfo processLogInfo : partitionLogInfos) {
            previousPartitionList.add(processLogInfo.getMessage());
        }
        return previousPartitionList;
    }

    public static Set<String> getChangedBusinessPartitionSet(List<String> sourcePartitionList, List<String> previousPartitionList) {
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

    public static List<String> getCurrentSourceColumnList(String sourceDb, String table) throws Exception {
        List<String> sourceColumnList = new ArrayList<>();
        DatabaseMetaData metaData = getHiveJDBCConnection(sourceDb).getMetaData();
        ResultSet rsColumns = metaData.getColumns(null, sourceDb, table, null);
        while (rsColumns.next()) {
            String columnName = rsColumns.getString("COLUMN_NAME");
            String dataType = rsColumns.getString("TYPE_NAME");
            sourceColumnList.add(columnName + " " + dataType);
        }
        rsColumns.close();
        return sourceColumnList;
    }

    public static List<String> getDestColumnList(String processId, String table) {
        //obtaining the column data corresponding to the previous execution for comparison with current partition data
        ProcessLog processLog = new ProcessLog();
        List<ProcessLogInfo> columnLogInfos = processLog.listLastInstanceRef(Integer.parseInt(processId), table + " column");
        List<String> previousColumnList = new ArrayList<>();
        for (ProcessLogInfo processLogInfo : columnLogInfos) {
            previousColumnList.add(processLogInfo.getMessage());
        }
        return previousColumnList;
    }

    public static List<String> getNewRegularColumnsAtSourceList(List<String> sourceColumnList, List<String> previousColumnList) {
        List<String> addedColumnList = new ArrayList<>(sourceColumnList);
        addedColumnList.removeAll(previousColumnList);

        for (String addedColumn : addedColumnList) {
            LOGGER.debug("addedColumn = " + addedColumn);
        }
        return addedColumnList;
    }

    public static void logCurrentSourcePartitions(List<String> sourcePartitionList, String processId, String instanceExecId, String table) {
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

    public static void logCurrentSourceColumns(List<String> sourceColumnList, String processId, String instanceExecId, String table) {
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

    public static void formStageAndDestTableDDLs(Statement st, List<String> sourceColumnList, String sourceDb, String destDb, String table, String bdreTechPartition) throws Exception {
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
        stgTableDDL = "create table " + sourceDb + "." + table + "_stg" + " (" + sourceRegularColumns + ") " + "partitioned by (" + stgPartitionColumns + ") stored as orc";
        destTableDDL = "create table " + destDb + "." + table  + " (" + sourceRegularColumns + ") " + "partitioned by (" + stgPartitionColumns + ") stored as orc";
        LOGGER.debug("stgTableDDL = " + stgTableDDL);
        LOGGER.debug("destTableDDL = " + destTableDDL);
        execStageTableDDL(st,sourceDb,table);
        rsPartitionList.close();
    }

    public static void execStageTableDDL(Statement st,String sourceDb, String table) throws Exception{
        st.executeUpdate("drop table if exists " + sourceDb + "." + table + "_stg");
        st.executeUpdate(stgTableDDL);
    }

    public static boolean checkIfDestTableExists(String destDb,String table) throws Exception{
        Connection conn = getHiveJDBCConnection(destDb);
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

    public static void alterDestTable(Statement st, List<String> addedColumnList, String destDb,String table) throws Exception{
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
    public static void execDestTableDDL(Statement st) throws Exception{
        LOGGER.debug("Destination table not found. Hence creating one");
        st.executeUpdate(destTableDDL);
    }

    public static String formFilterCondition(Set<String> modifiedBusinessPartitionSet, String sourcePartitionColumns){
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

        return(filterCondition.toString().isEmpty()?"":filterCondition.substring(0,filterCondition.lastIndexOf(" OR ")));
    }

}
