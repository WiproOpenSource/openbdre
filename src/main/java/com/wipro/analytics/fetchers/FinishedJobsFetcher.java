package com.wipro.analytics.fetchers;

import com.wipro.analytics.beans.FinishedJobsInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by cloudera on 3/18/17.
 */
public class FinishedJobsFetcher {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String finishedJobsFile = "/home/cloudera/finishedjobs";
    private final String finishedJobsAggregatedFile = "/home/cloudera/finishedjobsaggregated";
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static int counter = 0;
    static int aggregateCounter =0;
    static long finishedTimeBegin =0;
    static long finishedTimeEnd;


    public JsonNode readJsonNode(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return objectMapper.readTree(conn.getInputStream());
    }

    public void getHistoryAppsData(){
        try {
            long currentTime = System.currentTimeMillis();
            System.out.println("currentTime = " + currentTime);
            System.out.println("finishedTimeBegin = " + finishedTimeBegin);
            URL historyAppsUrl = new URL("http://localhost:19888/ws/v1/history/mapreduce/jobs?finishedTimeBegin="+finishedTimeBegin+"&finishedTimeEnd="+currentTime);
            finishedTimeBegin = currentTime;
            JsonNode rootNode = readJsonNode(historyAppsUrl);
            JsonNode jobsArray = rootNode.path("jobs").path("job");
            String[] jobIdsArray = new String[jobsArray.size()];
            int jobCounter=0;
            counter++;
            for(JsonNode job: jobsArray){
                String jobId = job.get("id").asText();
                jobIdsArray[jobCounter++] = jobId;
            }
            BufferedWriter writer = new BufferedWriter( new FileWriter(finishedJobsFile,true));

            for(String jobId : jobIdsArray){
                FinishedJobsInfo finishedJobsInfo = new FinishedJobsInfo();

                URL jobURL = new URL("http://localhost:19888/ws/v1/history/mapreduce/jobs/"+jobId);
                JsonNode job = readJsonNode(jobURL).path("job");
                String applicationName = job.get("name").asText();
                String applicationState = job.get("state").asText();
                String user = job.get("user").asText();
                String queue = job.get("queue").asText();
                long submitTime = job.get("submitTime").getLongValue();
                long startTime = job.get("startTime").getLongValue();
                long finishTime = job.get("finishTime").getLongValue();
                long avgMapTime = job.get("avgMapTime").getLongValue();
                long avgReduceTime = job.get("avgReduceTime").getLongValue();
                long avgShuffleTime = job.get("avgShuffleTime").getLongValue();
                long avgMergeTime = job.get("avgMergeTime").getLongValue();

                finishedJobsInfo.setName(applicationName);
                finishedJobsInfo.setId(jobId);
                finishedJobsInfo.setState(applicationState);
                finishedJobsInfo.setUser(user);
                finishedJobsInfo.setQueue(queue);
                finishedJobsInfo.setSubmitTime(submitTime);
                finishedJobsInfo.setStartTime(startTime);
                finishedJobsInfo.setFinishTime(finishTime);
                finishedJobsInfo.setAvgMapTime(avgMapTime);
                finishedJobsInfo.setAvgReduceTime(avgReduceTime);
                finishedJobsInfo.setAvgShuffleTime(avgShuffleTime);
                finishedJobsInfo.setAvgMergeTime(avgMergeTime);

                URL jobCountersURL = new URL("http://localhost:19888/ws/v1/history/mapreduce/jobs/"+jobId+"/counters");
                JsonNode counterGroups = readJsonNode(jobCountersURL).path("jobCounters").path("counterGroup");
                for(JsonNode counterGroup: counterGroups){
                    if(counterGroup.get("counterGroupName").asText().equalsIgnoreCase("org.apache.hadoop.mapreduce.TaskCounter")){
                        JsonNode counters = counterGroup.path("counter");
                        for(JsonNode counter: counters){
                            if(counter.get("name").asText().equalsIgnoreCase("GC_TIME_MILLIS")){
                                long gcTime = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setGcTime(gcTime);
                            }
                            else if(counter.get("name").asText().equalsIgnoreCase("PHYSICAL_MEMORY_BYTES")){
                                long usedMemory = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setUsedMemory(usedMemory);
                            }
                            else if(counter.get("name").asText().equalsIgnoreCase("CPU_MILLISECONDS")){
                                long timeSpentMaps  = counter.get("mapCounterValue").getLongValue();
                                long timeSpentReducers   = counter.get("reduceCounterValue").getLongValue();
                                long timeSpentTotal = counter.get("totalCounterValue").getLongValue();

                                finishedJobsInfo.setTimeSpentMaps(timeSpentMaps);
                                finishedJobsInfo.setTimeSpentReducers(timeSpentReducers);
                                finishedJobsInfo.setTimeSpentTotal(timeSpentTotal);
                            }
                        }


                    }
                }

                //write finishedjobInfo to file

                writer.write(finishedJobsInfo.toString()+"\n");

            }

            writer.close();
            System.out.println("counter = " + counter);
            if(counter == 5){
                counter = 0;
                aggregateCounter++;
                Files.copy(new File(finishedJobsFile).toPath(),new File(finishedJobsAggregatedFile+aggregateCounter).toPath(), StandardCopyOption.REPLACE_EXISTING);
                PrintWriter pw = new PrintWriter(finishedJobsFile);
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
                            FinishedJobsFetcher finishedJobsFetcher = new FinishedJobsFetcher();
                            finishedJobsFetcher.getHistoryAppsData();
                        }catch(Exception ex) {
                            ex.printStackTrace(); //or loggger would be better
                        }
                    }
                }, 0, 20, TimeUnit.SECONDS);
    }


}
