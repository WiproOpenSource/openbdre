package com.wipro.analytics.fetchers;

import com.wipro.analytics.beans.RunningJobsInfo;
import org.codehaus.jackson.JsonNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final String runningJobsFile = "/home/cloudera/runningjobs";
    private final String runningJobsAggregatedFile = "/home/cloudera/runningjobsaggregated";
    static int counter = 0;
    static int aggregateCounter =0;
    
    public JsonNode readJsonNode(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return objectMapper.readTree(conn.getInputStream());
    }
    
    public void getAppsData(){
        try {
            URL runningAppsUrl = new URL("http://localhost:8088/ws/v1/cluster/apps?states=running");
            JsonNode rootNode = readJsonNode(runningAppsUrl);
            JsonNode apps = rootNode.path("apps").path("app");
            BufferedWriter writer = new BufferedWriter( new FileWriter(runningJobsFile,true));
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

                writer.write(runningJobsInfo.toString()+"\n");

            }
            writer.close();
            System.out.println("counter = " + counter);
            if(counter == 5){
                counter = 0;
                aggregateCounter++;
                Files.copy(new File(runningJobsFile).toPath(),new File(runningJobsAggregatedFile+aggregateCounter).toPath(), StandardCopyOption.REPLACE_EXISTING);
                PrintWriter pw = new PrintWriter(runningJobsFile);
                pw.close();
            }

        } catch (Exception e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {

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
                }, 0, 20, TimeUnit.SECONDS);
    }

}