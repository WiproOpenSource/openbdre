package com.wipro.analytics;

import com.wipro.analytics.fetchers.DataFetcherMain;

import java.sql.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.File;

/**
 * Created by cloudera on 3/19/17.
 */
public class HiveConnection {

    private static final String HIVE_DRIVER_NAME = DataFetcherMain.HIVE_DRIVER_NAME;
    private static final String HIVE_USER = DataFetcherMain.HIVE_USER;
    private static final String HIVE_PASSWORD = DataFetcherMain.HIVE_PASSWORD;
    private static final String FILE_LINE_SEPERATOR = DataFetcherMain.FILE_LINE_SEPERATOR;
    private static final String FILE_FIELD_SEPERATOR = DataFetcherMain.FILE_FIELD_SEPERATOR;
    private static final String DBNAME = DataFetcherMain.DATABASE_NAME;
    private static final String HIVE_CONNECTION_URL = DataFetcherMain.HIVE_CONNECTION_URL;
    private static final String NAME_NODE_HOST = DataFetcherMain.NAMENODE_HOST;
    private static final String NAME_NODE_PORT = DataFetcherMain.NAMENODE_PORT;
    public static Connection getHiveJDBCConnection(String dbName, String hiveConnection) throws SQLException {
        try {
            Class.forName(HIVE_DRIVER_NAME);
            String hiveUser = HIVE_USER;
            String hivePassword = HIVE_PASSWORD;
            Connection connection = DriverManager.getConnection(hiveConnection + "/" + dbName, hiveUser, hivePassword);
            connection.createStatement().execute("set hive.exec.dynamic.partition.mode=nonstrict");
            connection.createStatement().execute("set hive.exec.dynamic.partition=true");
            connection.createStatement().execute("set hive.exec.max.dynamic.partitions.pernode=1000");
            return connection;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


    public void loadIntoHive(String filename, String tableName){
        try {

	        Configuration conf = new Configuration();
            conf.set("fs.defaultFS","hdfs://"+NAME_NODE_HOST+":"+NAME_NODE_PORT);
            FileSystem fs = FileSystem.get(conf);
		    File sourceFile = new File(filename);
            fs.copyFromLocalFile(new Path(sourceFile.getPath()),new Path("/tmp/"+tableName,sourceFile.getName()));
            String hdfsDir="/tmp/"+tableName;
            sourceFile.delete();

            Connection conn = getHiveJDBCConnection(DBNAME,HIVE_CONNECTION_URL);
            Statement stmt = conn.createStatement();
            String stageLoadQuery = "LOAD DATA INPATH '" + hdfsDir + "' OVERWRITE INTO TABLE " + tableName +"_STG";
            System.out.println("stageLoadQuery = " + stageLoadQuery);
            stmt.executeUpdate(stageLoadQuery);

            String insertDataQuery = "";
            if(tableName.equalsIgnoreCase("FINISHED_JOBS")){
                insertDataQuery = "INSERT   INTO TABLE "+ tableName+" PARTITION(fetchdate, fetchhour) SELECT id ,name , queue , username , state , submitTime , startTime , finishTime ,avgMapTime , avgReduceTime , avgShuffleTime , avgMergeTime , gcTime , usedPhysicalMemory ,  cpuTimeSpentMaps , cpuTimeSpentReducers , cpuTimeSpentTotal ,vCoreSecondsMaps, vCoreSecondsReducers, memorySecondsMaps,memorySecondsReducers, slotsTimeMaps,slotsTimeReducers, timeMaps, timeReducers , noOfMaps , noOfReducers , occupiedMemory , allocatedMemory, usedPerAllocatedMemory ,usedPerAllocatedCPU ,totalFileBytesRead ,totalFileBytesWritten   ,totalFileReadOps   ,  totalFileLargeReadOps   ,totalFileWriteOps   ,totalHDFSBytesRead   ,totalHDFSBytesWritten   ,totalHDFSReadOps   ,  totalHDFSLargeReadOps   ,totalHDFSWriteOps ,actionId, workflowId, fetchTime ,to_date(fetchTime), hour(fetchTime) FROM "+tableName+"_STG";
            }
            else if(tableName.equalsIgnoreCase("RUNNING_JOBS")){
                insertDataQuery = "INSERT INTO TABLE "+ tableName+" PARTITION(fetchdate, fetchhour) SELECT applicationId ,applicationName , applicationState , applicationType , finalState , progress , username , queueName  , startTime , elapsedTime , finishTime , trackingUrl , numContainers , allocatedMB , allocatedVCores , memorySeconds , vcoreSeconds  ,nodeId , containerId , fetchTime ,to_date(fetchTime), hour(fetchTime) FROM "+tableName+"_STG";
            }

            else if(tableName.equalsIgnoreCase("QUEUES")){
                insertDataQuery = "INSERT INTO TABLE "+ tableName+" PARTITION(fetchdate, fetchhour) SELECT queueName , absoluteAllocatedCapacity , absoluteUsedCapacity , usedMemory , usedCores , numContainers , queueState , maxApplications , numApplications , numActiveApplications , numPendingApplications  , queueType , users , fetchTime ,to_date(fetchTime), hour(fetchTime) FROM "+tableName+"_STG";
            }
            else if(tableName.equalsIgnoreCase("HDFS_QUOTA")){
                insertDataQuery = "INSERT INTO TABLE "+ tableName+" PARTITION(fetchdate, fetchhour) SELECT parentDir ,subDir , parentNameQuota , parentDirNumFiles , subdirNumfiles , parentSpaceQuota , parentDirSize, subdirSize ,fetchTime ,to_date(fetchTime), hour(fetchTime) FROM "+tableName+"_STG";
                // insertDataQuery = "INSERT INTO TABLE "+ tableName+" PARTITION(fetchdate, fetchhour) SELECT hdfspath , quota , numFiles , spaceQuota , spaceConsumed , isMonitorDirectory, parentMonitorDirectory,fetchTime ,to_date(fetchTime), hour(fetchTime) FROM "+tableName+"_STG";
            }
            else if(tableName.equalsIgnoreCase("MR_TASKS")){
                insertDataQuery = "INSERT INTO TABLE "+ tableName+" PARTITION(fetchdate, fetchhour) SELECT applicationId ,taskId ,taskProgress ,taskState , taskType , taskStartTime ,taskFinishTime , taskElapsedTime , runningTaskAttemptId ,  taskAttemptState , assignedContainerId , nodeHttpAddress , nodeId ,containerState , containerUsername , containerTotalMemoryNeededMB ,containerTotalVCoresNeeded , fetchTime ,to_date(fetchTime), hour(fetchTime) FROM "+tableName+"_STG";
            }
            else if(tableName.equalsIgnoreCase("GANGLIA_METRICS")){
                insertDataQuery = "INSERT INTO TABLE "+ tableName+" PARTITION(fetchdate, fetchhour) SELECT clusterName, nodeHostName, metric, metricValue, metricValueTime ,fetchTime ,to_date(fetchTime), hour(fetchTime) FROM "+tableName+"_STG";
            }
            System.out.println("insertDataQuery = " + insertDataQuery);
            stmt.executeUpdate(insertDataQuery);

            stmt.close();
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
