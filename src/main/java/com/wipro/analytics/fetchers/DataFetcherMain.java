package com.wipro.analytics.fetchers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.util.LongBitFormat;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by SR294224 on 3/20/2017.
 */
public class DataFetcherMain {

    public static String FOLDERS_TO_MONITOR_FOR_HDFS_QUOTA;
    public static long START_DELAY;
    public static long SCHEDULE_INTERVAL;
    public static long AGGREGATION_INTERVAL;
    public static TimeUnit TIMEUNIT_FOR_SCHEDULE;
    public static String MR_TASKS_FILE;
    public static String MR_TASKS_AGGREGATED_DIR;
    public static String MR_TASKS_TABLE;
    public static String RUNNING_JOBS_FILE;
    public static String RUNNING_JOBS_AGGREGATED_DIR;
    public static String FINISHED_JOBS_FILE;
    public static String FINISHED_JOBS_AGGREGATED_DIR;
    public static String HDFS_QUOTA_FILE;
    public static String HDFS_QUOTA_AGGREGATED_DIR;
    public static String QUEUES_FILE;
    public static String QUEUES_AGGREGATED_DIR;
    public static String RUNNING_JOBS_TABLE;
    public static String FINISHED_JOBS_TABLE;
    public static String HDFS_QUOTA_TABLE;
    public static String QUEUE_TABLE;
    public static String RESOURCE_MANAGER_HOST;
    public static String RESOURCE_MANAGER_PORT;
    public static String JOBHISTORY_SERVER_HOST;
    public static String JOBHISTORY_SERVER_PORT;
    public static String NAMENODE_HOST;
    public static String NAMENODE_PORT;
    public static String HIVE_DRIVER_NAME;
    public static String HIVE_USER;
    public static String HIVE_PASSWORD;
    public static String FILE_LINE_SEPERATOR;
    public static String FILE_FIELD_SEPERATOR;
    public static String DATABASE_NAME;
    public static String HIVE_CONNECTION_URL;
    public static String CLUSTER_NAME;
    public static String HOST_NAMES_LIST;
    public static String GANGLIA_WEBSERVER_HOSTNAME;
    public static long STARTING_FETCH_INTERVAL_FROM_GANGLIA;
    public static long SCHEDULE_INTERVAL_GANGLIA;
    public static String GANGLIA_METRIC_FILE;
    public static String GANGLIA_METRICS_TABLE;


    public static void main(String args[]) {
        try {
            init();
            //checking if any aggregated data is present before restart
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://" + NAMENODE_HOST + ":" + NAMENODE_PORT);

            try {
                FileSystem fs = FileSystem.get(conf);

                File aggregateQueuesDir = new File(QUEUES_AGGREGATED_DIR);
                for (File aggregateQueuesFile : aggregateQueuesDir.listFiles()) {
                    fs.copyFromLocalFile(new Path(aggregateQueuesFile.getPath()), new Path("/tmp/" + QUEUE_TABLE, aggregateQueuesFile.getName()));
                }

                File aggregaterunningJobsDir = new File(RUNNING_JOBS_AGGREGATED_DIR);
                for (File aggregaterunningJobsFile : aggregaterunningJobsDir.listFiles()) {
                    fs.copyFromLocalFile(new Path(aggregaterunningJobsFile.getPath()), new Path("/tmp/" + RUNNING_JOBS_TABLE, aggregaterunningJobsFile.getName()));
                }

                File aggregatefinishedJobsDir = new File(FINISHED_JOBS_AGGREGATED_DIR);
                for (File aggregatefinishedJobsFile : aggregatefinishedJobsDir.listFiles()) {
                    fs.copyFromLocalFile(new Path(aggregatefinishedJobsFile.getPath()), new Path("/tmp/" + FINISHED_JOBS_TABLE, aggregatefinishedJobsFile.getName()));
                }

                File aggregatehdfsQuotaDir = new File(HDFS_QUOTA_AGGREGATED_DIR);
                for (File aggregatehdfsQuotaFile : aggregatehdfsQuotaDir.listFiles()) {
                    fs.copyFromLocalFile(new Path(aggregatehdfsQuotaFile.getPath()), new Path("/tmp/" + HDFS_QUOTA_TABLE, aggregatehdfsQuotaFile.getName()));
                }

                File aggregateMRTasksDir = new File(MR_TASKS_AGGREGATED_DIR);
                for (File aggregateMRTasksFile : aggregateMRTasksDir.listFiles()) {
                    fs.copyFromLocalFile(new Path(aggregateMRTasksFile.getPath()), new Path("/tmp/" + MR_TASKS_TABLE, aggregateMRTasksFile.getName()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


           // FinishedJobsFetcher.schedule(START_DELAY, SCHEDULE_INTERVAL, TIMEUNIT_FOR_SCHEDULE);
           // RunningJobsFetcher.schedule(START_DELAY, SCHEDULE_INTERVAL, TIMEUNIT_FOR_SCHEDULE);
           // QueueFetcher.schedule(START_DELAY, SCHEDULE_INTERVAL, TIMEUNIT_FOR_SCHEDULE);
           // HDFSQuotaFetcher2.schedule(START_DELAY, SCHEDULE_INTERVAL, TIMEUNIT_FOR_SCHEDULE);
           // MRTasksFetcher.schedule(START_DELAY, SCHEDULE_INTERVAL, TIMEUNIT_FOR_SCHEDULE);
            GangliaMetricsFetcher.schedule(START_DELAY, SCHEDULE_INTERVAL, TIMEUNIT_FOR_SCHEDULE);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {

        }
    }

    public static void init() {
        Properties properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("/home/cloudera/config.properties");
            properties.load(input);

            // get the properties value
            FOLDERS_TO_MONITOR_FOR_HDFS_QUOTA = properties.getProperty("FOLDERS_TO_MONITOR_FOR_HDFS_QUOTA");
            START_DELAY = Long.parseLong(properties.getProperty("START_DELAY"));
            SCHEDULE_INTERVAL = Long.parseLong(properties.getProperty("SCHEDULE_INTERVAL"));
            AGGREGATION_INTERVAL = Long.parseLong(properties.getProperty("AGGREGATION_INTERVAL"));
            TIMEUNIT_FOR_SCHEDULE = TimeUnit.valueOf(properties.getProperty("TIMEUNIT_FOR_SCHEDULE"));
            RUNNING_JOBS_FILE = properties.getProperty("RUNNING_JOBS_FILE");
            RUNNING_JOBS_AGGREGATED_DIR = properties.getProperty("RUNNING_JOBS_AGGREGATED_DIR");
            RUNNING_JOBS_TABLE = properties.getProperty("RUNNING_JOBS_TABLE");
            MR_TASKS_FILE = properties.getProperty("MR_TASKS_FILE");
            MR_TASKS_AGGREGATED_DIR = properties.getProperty("MR_TASKS_AGGREGATED_DIR");
            MR_TASKS_TABLE = properties.getProperty("MR_TASKS_TABLE");
            HDFS_QUOTA_FILE = properties.getProperty("HDFS_QUOTA_FILE");
            HDFS_QUOTA_AGGREGATED_DIR = properties.getProperty("HDFS_QUOTA_AGGREGATED_DIR");
            HDFS_QUOTA_TABLE = properties.getProperty("HDFS_QUOTA_TABLE");
            FINISHED_JOBS_FILE = properties.getProperty("FINISHED_JOBS_FILE");
            FINISHED_JOBS_AGGREGATED_DIR = properties.getProperty("FINISHED_JOBS_AGGREGATED_DIR");
            FINISHED_JOBS_TABLE = properties.getProperty("FINISHED_JOBS_TABLE");
            QUEUES_FILE = properties.getProperty("QUEUES_FILE");
            QUEUES_AGGREGATED_DIR = properties.getProperty("QUEUES_AGGREGATED_DIR");
            QUEUE_TABLE = properties.getProperty("QUEUE_TABLE");
            RESOURCE_MANAGER_HOST= properties.getProperty("RESOURCE_MANAGER_HOST");
            RESOURCE_MANAGER_PORT = properties.getProperty("RESOURCE_MANAGER_PORT");
            JOBHISTORY_SERVER_HOST = properties.getProperty("JOBHISTORY_SERVER_HOST");
            JOBHISTORY_SERVER_PORT = properties.getProperty("JOBHISTORY_SERVER_PORT");
            NAMENODE_HOST =properties.getProperty("NAMENODE_HOST");
            NAMENODE_PORT = properties.getProperty("NAMENODE_PORT");
            HIVE_DRIVER_NAME= properties.getProperty("HIVE_DRIVER_NAME");
            HIVE_USER= properties.getProperty("HIVE_USER");
            HIVE_PASSWORD= properties.getProperty("HIVE_PASSWORD");
            FILE_LINE_SEPERATOR= properties.getProperty("FILE_LINE_SEPERATOR");
            FILE_FIELD_SEPERATOR= properties.getProperty("FILE_FIELD_SEPERATOR");
            DATABASE_NAME = properties.getProperty("DATABASE_NAME");
            HIVE_CONNECTION_URL= properties.getProperty("HIVE_CONNECTION_URL");
            CLUSTER_NAME= properties.getProperty("CLUSTER_NAME");
            HOST_NAMES_LIST= properties.getProperty("HOST_NAMES_LIST");
            GANGLIA_WEBSERVER_HOSTNAME= properties.getProperty("GANGLIA_WEBSERVER_HOSTNAME");
            STARTING_FETCH_INTERVAL_FROM_GANGLIA= Long.parseLong(properties.getProperty("STARTING_FETCH_INTERVAL_FROM_GANGLIA"));
            SCHEDULE_INTERVAL_GANGLIA= Long.parseLong(properties.getProperty("SCHEDULE_INTERVAL_GANGLIA"));
            GANGLIA_METRIC_FILE= properties.getProperty("GANGLIA_METRIC_FILE");
            GANGLIA_METRICS_TABLE= properties.getProperty("GANGLIA_METRICS_TABLE");



        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
