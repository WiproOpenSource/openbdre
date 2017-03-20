package com.wipro.analytics.fetchers;

import com.wipro.analytics.HiveConnection;
import com.wipro.analytics.beans.RunningJobsInfo;
import org.codehaus.jackson.JsonNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;


/**
 * Created by cloudera on 3/15/17.
 */

public class RunningJobsFetcher {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String runningJobsFile = DataFetcherMain.runningJobsFile;
    private final static String runningJobsAggregatedDir = DataFetcherMain.runningJobsAggregatedDir;
    private final static String resourceManagerHost = DataFetcherMain.resourceManagerHost;
    private final static String resourceManagerPort = DataFetcherMain.resourceManagerPort;
    private static final long scheduleInterval = DataFetcherMain.scheduleInterval;
    private static final long aggregationInterval = DataFetcherMain.aggregationInterval;
    private static final String lineSeparator = DataFetcherMain.FILE_LINE_SEPERATOR;
    private static final String runningJobsTable = "RUNNING_JOBS";
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static int counter = 0;
    static int aggregateCounter =0;
    
    public JsonNode readJsonNode(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return objectMapper.readTree(conn.getInputStream());
    }
    
    public void getAppsData(){
        try {
            URL runningAppsUrl = new URL("http://"+resourceManagerHost+":"+resourceManagerPort+"/ws/v1/cluster/apps?states=running");
            JsonNode rootNode = readJsonNode(runningAppsUrl);
            JsonNode apps = rootNode.path("apps").path("app");
            BufferedWriter writer = new BufferedWriter( new FileWriter(runningJobsFile,true));
            counter++;
            for (JsonNode app : apps) {
                String applicationId = app.get("id").asText();
                String applicationName = app.get("name").asText();
                String applicationState = app.get("state").asText();
                String applicationType = app.get("applicationType").asText();
                String finalState = app.get("finalStatus").asText();
                String progress = app.get("progress").asText();
                String username = app.get("user").asText();
                String queueName = app.get("queue").asText();
                long startTime = app.get("startedTime").getLongValue();
                long elapsedTime = app.get("elapsedTime").getLongValue();
                long finishTime = app.get("finishedTime").getLongValue();
                String trackingUrl = app.get("trackingUrl") != null? app.get("trackingUrl").asText() : null;
                int numContainers = app.get("runningContainers").getIntValue();
                int allocatedMB = app.get("allocatedMB").getIntValue();
                int allocatedVCores = app.get("allocatedVCores").getIntValue();
                long memorySeconds = app.get("memorySeconds").getLongValue();
                long vcoreSeconds = app.get("vcoreSeconds").getLongValue();

                RunningJobsInfo runningJobsInfo = new RunningJobsInfo();
                runningJobsInfo.setApplicationId(applicationId);
                runningJobsInfo.setApplicationName(applicationName);
                runningJobsInfo.setApplicationState(applicationState);
                runningJobsInfo.setApplicationType(applicationType);
                runningJobsInfo.setFinalState(finalState);
                runningJobsInfo.setProgress(progress);
                runningJobsInfo.setUsername(username);
                runningJobsInfo.setQueueName(queueName);
                runningJobsInfo.setStartTime(startTime);
                runningJobsInfo.setElapsedTime(elapsedTime);
                runningJobsInfo.setFinishTime(finishTime);
                runningJobsInfo.setTrackingUrl(trackingUrl);
                runningJobsInfo.setNumContainers(numContainers);
                runningJobsInfo.setAllocatedMB(allocatedMB);
                runningJobsInfo.setAllocatedVCores(allocatedVCores);
                runningJobsInfo.setMemorySeconds(memorySeconds);
                runningJobsInfo.setVcoreSeconds(vcoreSeconds);

                //write this runningjobinfo to file

                runningJobsInfo.setTimestamp(new Timestamp(Calendar.getInstance().getTime().getTime()));
                writer.write(runningJobsInfo.toString()+lineSeparator);

            }
            writer.close();
            System.out.println("running counter = " + counter);
            if(counter == aggregationInterval/scheduleInterval){
                counter = 0;
                if(new File(runningJobsFile).length() !=0) {
                    aggregateCounter++;
                    Files.copy(new File(runningJobsFile).toPath(), new File(runningJobsAggregatedDir +"running-"+ System.currentTimeMillis()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    PrintWriter pw = new PrintWriter(runningJobsFile);
                    pw.close();
                    HiveConnection hiveConnection = new HiveConnection();
                    hiveConnection.loadIntoHive(runningJobsAggregatedDir,runningJobsTable);
                }
            }

        } catch (Exception e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }
    }
    
    public static void schedule(long startDelay, long scheduleInterval, TimeUnit timeUnitForSchedule) {
        DataFetcherMain dataFetcherMain = new DataFetcherMain();
        dataFetcherMain.init();
          final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                            RunningJobsFetcher runningJobsFetcher = new RunningJobsFetcher();
                            runningJobsFetcher.getAppsData();
                        }catch(Exception ex) {
                            ex.printStackTrace(); //or loggger would be better
                        }
                    }
                }, startDelay, scheduleInterval, timeUnitForSchedule);
    }

}