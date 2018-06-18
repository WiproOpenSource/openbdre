package com.wipro.analytics.fetchers;

import com.wipro.analytics.HiveConnection;
import com.wipro.analytics.beans.HDFSQuotaInfo;
import com.wipro.analytics.beans.HDFSQuotaInfo2;
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
public class HDFSQuotaFetcher2 {
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
            for (String hdfsPath : FOLDERS_TO_MONITOR_HDFS_QUOTA.split(",")) {
                ContentSummary parentContentSummary = fs.getContentSummary(new Path(hdfsPath));
                long parentNameQuota = parentContentSummary.getQuota();
                long parentDirNumFiles = parentContentSummary.getFileCount()+parentContentSummary.getDirectoryCount();
                double parentSpaceQuota = parentContentSummary.getSpaceQuota()/1024.0/1024.0;
                double parentDirSize = parentContentSummary.getSpaceConsumed()/1024.0/1024.0;

                FileStatus[] fileStatus = fs.listStatus(new Path(hdfsPath));
                for (FileStatus fileStat : fileStatus) {
                    if (fileStat.isDirectory()) {

                        HDFSQuotaInfo2 hdfsQuotaInfo2 = new HDFSQuotaInfo2();
                        hdfsQuotaInfo2.setParentDir(hdfsPath);
                        hdfsQuotaInfo2.setSubDir(fileStat.getPath().toString());
                        hdfsQuotaInfo2.setParentNameQuota(parentNameQuota);
                        hdfsQuotaInfo2.setParentDirNumFiles(parentDirNumFiles);
                        hdfsQuotaInfo2.setParentSpaceQuota(parentSpaceQuota);
                        hdfsQuotaInfo2.setParentDirSize(parentDirSize);


                        ContentSummary contentSummary = fs.getContentSummary(fileStat.getPath());
                        hdfsQuotaInfo2.setSubdirNumfiles(contentSummary.getDirectoryCount()+contentSummary.getFileCount());
                        hdfsQuotaInfo2.setSubdirSize(contentSummary.getSpaceConsumed()/1024.0/1024.0);
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
                            HDFSQuotaFetcher2 hdfsQuotaFetcher = new HDFSQuotaFetcher2();
                            hdfsQuotaFetcher.getHDFSQuota();
                        }catch(Exception ex) {
                            ex.printStackTrace(); //or loggger would be better
                        }
                    }
                }, startDelay, scheduleInterval, timeUnitForSchedule);
    }


}
