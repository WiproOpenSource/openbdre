package com.wipro.analytics.fetchers;

import com.wipro.analytics.HiveConnection;
import com.wipro.analytics.beans.HDFSQuotaInfo;
import com.wipro.analytics.beans.QueueInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by cloudera on 4/3/17.
 */
public class HDFSQuotaFetcher {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String hdfsQuotaFile = DataFetcherMain.HDFS_QUOTA_FILE;
    private final static String hdfsQuotaAggregatedDir = DataFetcherMain.HDFS_QUOTA_AGGREGATED_DIR;
    private static final String hdfsQuotaTable = DataFetcherMain.HDFS_QUOTA_TABLE;
    private static final long scheduleInterval = DataFetcherMain.SCHEDULE_INTERVAL;
    private static final long aggregationInterval = DataFetcherMain.AGGREGATION_INTERVAL;
    private static final String lineSeparator = DataFetcherMain.FILE_LINE_SEPERATOR;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final String NAME_NODE_HOST = DataFetcherMain.NAMENODE_HOST;
    private static final String NAME_NODE_PORT = DataFetcherMain.NAMENODE_PORT;
    private static final String FOLDERS_TO_MONITOR_HDFS_QUOTA = DataFetcherMain.FOLDERS_TO_MONITOR_FOR_HDFS_QUOTA;
    private static List<HDFSQuotaInfo> hdfsQuotaInfoList = new ArrayList<HDFSQuotaInfo>();

    static int counter = 0;
    static int aggregateCounter =0;
    static boolean firstOccurance = false;
    static String parentMonDirectory = "";

    public void getHDFSQuota() {

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://" + NAME_NODE_HOST + ":" + NAME_NODE_PORT);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(hdfsQuotaFile, true));
            counter++;
            FileSystem fs = FileSystem.get(conf);
            hdfsQuotaInfoList.clear();
            for (String hdfsPath : FOLDERS_TO_MONITOR_HDFS_QUOTA.split(",")) {
                firstOccurance = true;
                parentMonDirectory = hdfsPath;
                //getAllFilePath(hdfsPath, fs);

                ContentSummary contentSummary = fs.getContentSummary(new Path(hdfsPath));
                HDFSQuotaInfo hdfsQuotaInfo = new HDFSQuotaInfo();
                hdfsQuotaInfo.setHdfsPath(hdfsPath);
                long quota = contentSummary.getQuota();
                hdfsQuotaInfo.setQuota(quota);
                long spaceQuota = contentSummary.getSpaceQuota();
                hdfsQuotaInfo.setSpaceQuota(spaceQuota);
                long spaceConsumed = contentSummary.getSpaceConsumed();
                hdfsQuotaInfo.setSpaceConsumed(spaceConsumed);
                long numFiles = contentSummary.getFileCount()+contentSummary.getDirectoryCount();
                hdfsQuotaInfo.setNumFiles(numFiles);
                hdfsQuotaInfo.setMonitorDirectory(true);
                hdfsQuotaInfo.setTimestamp(new Timestamp(Calendar.getInstance().getTime().getTime()));
                writer.write(hdfsQuotaInfo.toString()+lineSeparator);


                FileStatus[] fileStatus = fs.listStatus(new Path(hdfsPath));
                for (FileStatus fileStat : fileStatus) {
                    if (fileStat.isDirectory()) {
                        ContentSummary contentSummary2 = fs.getContentSummary(fileStat.getPath());
                        HDFSQuotaInfo hdfsQuotaInfo2 = new HDFSQuotaInfo();
                        hdfsQuotaInfo2.setHdfsPath(fileStat.getPath().toString());
                        long quota2 = contentSummary2.getQuota();
                        hdfsQuotaInfo2.setQuota(quota2);
                        long spaceQuota2 = contentSummary2.getSpaceQuota();
                        hdfsQuotaInfo2.setSpaceQuota(spaceQuota2);
                        long spaceConsumed2 = contentSummary2.getSpaceConsumed();
                        hdfsQuotaInfo2.setSpaceConsumed(spaceConsumed2);
                        long numFiles2 = contentSummary2.getFileCount()+contentSummary.getDirectoryCount();
                        hdfsQuotaInfo2.setNumFiles(numFiles2);
                        hdfsQuotaInfo2.setMonitorDirectory(false);
                        hdfsQuotaInfo2.setParentMonitorDirectory(hdfsPath);
                        hdfsQuotaInfo2.setTimestamp(new Timestamp(Calendar.getInstance().getTime().getTime()));
                        writer.write(hdfsQuotaInfo2.toString()+lineSeparator);

                    } else {
                        // fileList.add(fileStat.getPath().toString());
                    }
                }

            }

            writer.close();
            System.out.println("hdfs quota counter = " + counter);
            if (counter == aggregationInterval / scheduleInterval) {
                counter = 0;
                if (new File(hdfsQuotaFile).length() != 0) {
                    aggregateCounter++;
                    String fileName = hdfsQuotaAggregatedDir + "hdfsquota-" + System.currentTimeMillis();
                    Files.copy(new File(hdfsQuotaFile).toPath(), new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    PrintWriter pw = new PrintWriter(hdfsQuotaFile);
                    pw.close();
                    HiveConnection hiveConnection = new HiveConnection();
                    hiveConnection.loadIntoHive(fileName, hdfsQuotaTable);
                }
            }
        
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        
    }

    public static void schedule(long startDelay, long scheduleInterval, TimeUnit timeUnitForSchedule) {


        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                            HDFSQuotaFetcher hdfsQuotaFetcher = new HDFSQuotaFetcher();
                            hdfsQuotaFetcher.getHDFSQuota();
                        }catch(Exception ex) {
                            ex.printStackTrace(); //or loggger would be better
                        }
                    }
                }, startDelay, scheduleInterval, timeUnitForSchedule);
    }

    public static List<String> getAllFilePath(String hdfsPath, FileSystem fs) throws FileNotFoundException, IOException {
        List<String> fileList = new ArrayList<String>();
        FileStatus[] fileStatus = fs.listStatus(new Path(hdfsPath));
        for (FileStatus fileStat : fileStatus) {
            if (fileStat.isDirectory()) {
                ContentSummary contentSummary = fs.getContentSummary(fileStat.getPath());
                HDFSQuotaInfo hdfsQuotaInfo = new HDFSQuotaInfo();
                hdfsQuotaInfo.setHdfsPath(fileStat.getPath().toString());
                long quota = contentSummary.getQuota();
                hdfsQuotaInfo.setQuota(quota);
                long spaceQuota = contentSummary.getSpaceQuota();
                hdfsQuotaInfo.setSpaceQuota(spaceQuota);
                long spaceConsumed = contentSummary.getSpaceConsumed();
                hdfsQuotaInfo.setSpaceConsumed(spaceConsumed);
                long numFiles = contentSummary.getFileCount()+contentSummary.getDirectoryCount();
                hdfsQuotaInfo.setNumFiles(numFiles);

                if(firstOccurance) {
                    hdfsQuotaInfo.setMonitorDirectory(true);
                    firstOccurance = false;
                }
                else {
                    hdfsQuotaInfo.setMonitorDirectory(false);
                    hdfsQuotaInfo.setParentMonitorDirectory(parentMonDirectory);

                }

                hdfsQuotaInfoList.add(hdfsQuotaInfo);
                //fileList.add(fileStat.getPath().toString());
                //getAllFilePath(fileStat.getPath().toString(), fs);

            } else {
               // fileList.add(fileStat.getPath().toString());
            }
        }
        return fileList;
    }

    public static void main(String[] args) {
        try {
            Configuration configuration = new Configuration();
            configuration.set("fs.defaultFS","hdfs://localhost:8020");
            //2. Get the instance of the HDFS
            FileSystem hdfs = FileSystem.get(configuration);
           // FileStatus[] out = hdfs.listStatus(new Path("/user/cloudera"));
           // System.out.println("out.length = " + out.length);
            System.out.println(" = " +  hdfs.getFileStatus(new Path("/user/cloudera/wf")).getLen());
            
        }
        catch (Exception e){
            
        }
        

    }
    
}
