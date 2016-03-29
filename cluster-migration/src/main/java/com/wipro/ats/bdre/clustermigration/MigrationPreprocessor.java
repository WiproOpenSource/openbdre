package com.wipro.ats.bdre.clustermigration;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;

/**
 * Created by cloudera on 3/29/16.
 */
public class MigrationPreprocessor {

    protected static Connection getHiveJDBCConnection(String dbName) throws Exception{
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
    public static void main(String[] args) throws Exception{
        String processId = args[0];
        String instanceExecId= args[1];
        String table="account";
        String sourceDb="sourcedb";
        Connection conn = getHiveJDBCConnection(sourceDb);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("show partitions "+sourceDb+"."+table);
        List<String> sourcePartitionList = new ArrayList<>();
        while (rs.next()) {
            sourcePartitionList.add(rs.getString(1));
        }
        //obtaining the partition data corresponding to the previous execution for comparison with current partition data
        ProcessLog processLog = new ProcessLog();
        List<ProcessLogInfo> processLogInfos=processLog.listLastInstanceRef(Integer.parseInt(processId));
        List<String> previousPartitionList = new ArrayList<>();
        for(ProcessLogInfo processLogInfo:processLogInfos){
            previousPartitionList.add(processLogInfo.getMessage());
        }
        List<String> deletedPartitionList = new ArrayList<>(previousPartitionList);
        deletedPartitionList.removeAll(sourcePartitionList);
        List<String> addedPartitionList = new ArrayList<>(sourcePartitionList);
        addedPartitionList.removeAll(previousPartitionList);
        List<String> removedBusinessPartitionList=new ArrayList<>();
        List<String> addedBusinessPartitionList=new ArrayList<>();

        for(String addedPartition:addedPartitionList){
            addedBusinessPartitionList.add(addedPartition.substring(0,addedPartition.lastIndexOf("/")));
        }

        for(String deletedPartition:deletedPartitionList){
            removedBusinessPartitionList.add(deletedPartition.substring(0,deletedPartition.lastIndexOf("/")));
        }
        Set<String> businessPartitionSet = new HashSet<String>();
        businessPartitionSet.addAll(removedBusinessPartitionList);
        businessPartitionSet.addAll(addedBusinessPartitionList);

        for(String editedBusinessPartition:businessPartitionSet){
            System.out.println("editedBusinessPartition = " + editedBusinessPartition);
        }

        List<ProcessLogInfo> processLogInfoList = new ArrayList<>();
        for(String sourcePartition:sourcePartitionList) {
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            processLogInfo.setAddTs(new Date());
            processLogInfo.setProcessId(Integer.parseInt(processId));
            processLogInfo.setMessage(sourcePartition);
            processLogInfo.setInstanceRef(Long.parseLong(instanceExecId));
            processLogInfo.setLogCategory("C2C");
            processLogInfo.setMessageId(table);
            processLogInfoList.add(processLogInfo);
        }

        //persisting the current partition info to process logs
        processLog.logList(processLogInfoList);
    }
}
