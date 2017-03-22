package com.wipro.analytics.fetchers;

import java.util.concurrent.TimeUnit;

/**
 * Created by SR294224 on 3/20/2017.
 */
public class DataFetcherMain {

    public static long startDelay;
    public static long scheduleInterval;
    public static long aggregationInterval;
    public static TimeUnit timeUnitForSchedule;
    public static String runningJobsFile;
    public static String runningJobsAggregatedDir;
    public static String finishedJobsFile;
    public static String finishedJobsAggregatedDir;
    public static String queuesFile;
    public static String queuesAggregatedDir;
    public static String resourceManagerHost;
    public static String resourceManagerPort;
    public static String jobHistoryServerHost;
    public static String jobHistoryServerPort;
    public static String nameNodeHost;
    public static String nameNodePort;
    public static String HIVE_DRIVER_NAME;
    public static String HIVE_USER;
    public static String HIVE_PASSWORD;
    public static String FILE_LINE_SEPERATOR;
    public static String FILE_FIELD_SEPERATOR;
    public static String DBNAME;
    public static String HIVE_CONNECTION_URL;

    public static void main(String args[]){
        init();
        FinishedJobsFetcher.schedule(startDelay,scheduleInterval,timeUnitForSchedule);
        RunningJobsFetcher.schedule(startDelay,scheduleInterval,timeUnitForSchedule);
        QueueFetcher.schedule(startDelay,scheduleInterval,timeUnitForSchedule);
    }
    public static void init(){
        startDelay = 0;
        scheduleInterval = 10;
        aggregationInterval = 30;
        timeUnitForSchedule = TimeUnit.SECONDS;
        runningJobsFile = "/home/openbdre/runningjobs";
        runningJobsAggregatedDir = "/home/openbdre/aggregatedrunning/";
        finishedJobsFile = "/home/openbdre/finishedjobs";
        finishedJobsAggregatedDir = "/home/openbdre/aggregatedfinished/";
        queuesFile = "/home/openbdre/queues";
        queuesAggregatedDir = "/home/openbdre/aggregatedqueues/";
        resourceManagerHost="localhost";
        resourceManagerPort="8088";
        jobHistoryServerHost="localhost";
        jobHistoryServerPort="19888";
        nameNodeHost="localhost";
        nameNodePort="8020";
        HIVE_DRIVER_NAME="org.apache.hive.jdbc.HiveDriver";
        HIVE_USER= "openbdre";
        HIVE_PASSWORD="openbdre";
        FILE_LINE_SEPERATOR="\n";
        FILE_FIELD_SEPERATOR="\t";
        DBNAME="monitor";
        HIVE_CONNECTION_URL="jdbc:hive2://localhost:10000";
    }
}
