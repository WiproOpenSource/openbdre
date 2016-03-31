package com.wipro.ats.bdre.clustermigration;

import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by cloudera on 3/29/16.
 */
public class MigrationPreprocessor {

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
        createSourceStgTable(st, sourceColumnList, sourceDb, table);
    }

    public static List<String> getCurrentSourcePartitionList(Statement st, String sourceDb, String table) throws Exception {
        ResultSet rsPartitions = st.executeQuery("show partitions " + sourceDb + "." + table);
        List<String> sourcePartitionList = new ArrayList<>();
        while (rsPartitions.next()) {
            sourcePartitionList.add(rsPartitions.getString(1));
        }
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
            System.out.println("editedBusinessPartition = " + editedBusinessPartition);
        }
        return businessPartitionSet;
    }

    public static List<String> getCurrentSourceColumnList(String sourceDb, String table) throws Exception {
        List<String> sourceColumnList = new ArrayList<>();
        DatabaseMetaData metaData = getHiveJDBCConnection(sourceDb).getMetaData();
        ResultSet rsColumns = metaData.getColumns(null, null, table, null);
        while (rsColumns.next()) {
            String columnName = rsColumns.getString("COLUMN_NAME");
            String dataType = rsColumns.getString("TYPE_NAME");
            sourceColumnList.add(columnName + " " + dataType);
        }
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
            System.out.println("addedColumn = " + addedColumn);
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

    public static void createSourceStgTable(Statement st, List<String> sourceColumnList, String sourceDb, String table) throws Exception {
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
        String stgTableDDL = "create table " + sourceDb + "." + table + "_stg" + " (" + finalColumns.substring(0, finalColumns.length() - 1) + ") " + "partitioned by (" + partitionList.substring(0, partitionList.length() - 1) + ") stored as orc";
        System.out.println("stgTableDDL = " + stgTableDDL);
        st.executeUpdate("drop table if exists " + sourceDb + "." + table + "_stg");
        st.executeUpdate(stgTableDDL);
    }

}
