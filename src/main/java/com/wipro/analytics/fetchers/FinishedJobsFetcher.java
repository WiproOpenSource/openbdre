package com.wipro.analytics.fetchers;

import com.wipro.analytics.HiveConnection;
import com.wipro.analytics.beans.FinishedJobsInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by cloudera on 3/18/17.
 */
public class FinishedJobsFetcher {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String finishedJobsFile = DataFetcherMain.FINISHED_JOBS_FILE;
    private final String finishedJobsAggregatedDir = DataFetcherMain.FINISHED_JOBS_AGGREGATED_DIR;
    private final String jobHistoryServerHost = DataFetcherMain.JOBHISTORY_SERVER_HOST;
    private final String jobHistoryServerPort = DataFetcherMain.JOBHISTORY_SERVER_PORT;
    private static final long scheduleInterval = DataFetcherMain.SCHEDULE_INTERVAL;
    private static final long aggregationInterval = DataFetcherMain.AGGREGATION_INTERVAL;
    private static final String lineSeparator = DataFetcherMain.FILE_LINE_SEPERATOR;
    private static final String finishedJobsTable = DataFetcherMain.FINISHED_JOBS_TABLE;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static int counter = 0;
    static int aggregateCounter =0;
    static long finishedTimeBegin ;


    public JsonNode readJsonNode(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return objectMapper.readTree(conn.getInputStream());
    }

    public void getHistoryAppsData(){
        try {
            long currentTime = System.currentTimeMillis();
         //   System.out.println("currentTime = " + currentTime);
         //   System.out.println("finishedTimeBegin = " + finishedTimeBegin);
            URL historyAppsUrl = new URL("http://"+jobHistoryServerHost+":"+jobHistoryServerPort+"/ws/v1/history/mapreduce/jobs?finishedTimeBegin="+finishedTimeBegin+"&finishedTimeEnd="+currentTime+"&state=SUCCEEDED");
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

                URL jobURL = new URL("http://"+jobHistoryServerHost+":"+jobHistoryServerPort+"/ws/v1/history/mapreduce/jobs/"+jobId);
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

                URL jobCountersURL = new URL("http://"+jobHistoryServerHost+":"+jobHistoryServerPort+"/ws/v1/history/mapreduce/jobs/"+jobId+"/counters");
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
                                finishedJobsInfo.setUsedPhysicalMemory(usedMemory);
                            }
                            else if(counter.get("name").asText().equalsIgnoreCase("CPU_MILLISECONDS")){
                                long cpuTimeSpentMaps  = counter.get("mapCounterValue").getLongValue();
                                long cpuTimeSpentReducers   = counter.get("reduceCounterValue").getLongValue();
                                long cpuTimeSpentTotal = counter.get("totalCounterValue").getLongValue();

                                finishedJobsInfo.setcpuTimeSpentMaps(cpuTimeSpentMaps);
                                finishedJobsInfo.setcpuTimeSpentReducers(cpuTimeSpentReducers);
                                finishedJobsInfo.setcpuTimeSpentTotal(cpuTimeSpentTotal);
                            }
                        }

                    }

                    else if(counterGroup.get("counterGroupName").asText().equalsIgnoreCase("org.apache.hadoop.mapreduce.FileSystemCounter")){
                        JsonNode counters = counterGroup.path("counter");
                        for(JsonNode counter: counters) {
                            if (counter.get("name").asText().equalsIgnoreCase("FILE_BYTES_READ")) {
                                long totalFileBytesRead = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalFileBytesRead(totalFileBytesRead);
                            } else if (counter.get("name").asText().equalsIgnoreCase("FILE_BYTES_WRITTEN")) {
                                long totalFileBytesWritten = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalFileBytesWritten(totalFileBytesWritten);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("FILE_READ_OPS")) {
                                long totalFileReadOps = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalFileReadOps(totalFileReadOps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("FILE_LARGE_READ_OPS")) {
                                long totalFileLargeReadOps = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalFileLargeReadOps(totalFileLargeReadOps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("FILE_WRITE_OPS")) {
                                long totalFileWriteOps = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalFileWriteOps(totalFileWriteOps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("HDFS_BYTES_READ")) {
                                long totalHDFSBytesRead = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalHDFSBytesRead(totalHDFSBytesRead);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("HDFS_BYTES_WRITTEN")) {
                                long totalHDFSBytesWritten = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalHDFSBytesWritten(totalHDFSBytesWritten);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("HDFS_READ_OPS")) {
                                long totalHDFSReadOps = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalHDFSReadOps(totalHDFSReadOps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("HDFS_LARGE_READ_OPS")) {
                                long totalHDFSLargeReadOps = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalHDFSLargeReadOps(totalHDFSLargeReadOps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("HDFS_WRITE_OPS")) {
                                long totalHDFSWriteOps = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTotalHDFSWriteOps(totalHDFSWriteOps);
                            }

                        }
                    }

                    else if(counterGroup.get("counterGroupName").asText().equalsIgnoreCase("org.apache.hadoop.mapreduce.JobCounter")){
                        JsonNode counters = counterGroup.path("counter");
                        for(JsonNode counter: counters) {
                            if (counter.get("name").asText().equalsIgnoreCase("SLOTS_MILLIS_MAPS")) {
                                long slotsTimeMaps  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setSlotsTimeMaps(slotsTimeMaps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("SLOTS_MILLIS_REDUCES")) {
                                long slotsTimeReducers  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setSlotsTimeReducers(slotsTimeReducers);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("MB_MILLIS_MAPS")) {
                                long memorySecondsMaps  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setMemorySecondsMaps(memorySecondsMaps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("MB_MILLIS_REDUCES")) {
                                long memorySecondsReducers  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setMemorySecondsReducers(memorySecondsReducers);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("VCORES_MILLIS_MAPS")) {
                                long vCoreSecondsMaps  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setvCoreSecondsMaps(vCoreSecondsMaps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("VCORES_MILLIS_REDUCES")) {
                                long vCoreSecondsReducers  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setvCoreSecondsReducers(vCoreSecondsReducers);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("MILLIS_MAPS")) {
                                long timeMaps  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTimeMaps(timeMaps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("MILLIS_REDUCES")) {
                                long timeReducers  = counter.get("totalCounterValue").getLongValue();
                                finishedJobsInfo.setTimeReducers(timeReducers);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("TOTAL_LAUNCHED_MAPS")) {
                                int noOfMaps  = counter.get("totalCounterValue").getIntValue();
                                finishedJobsInfo.setNoOfMaps(noOfMaps);
                            }
                            else if (counter.get("name").asText().equalsIgnoreCase("TOTAL_LAUNCHED_REDUCES")) {
                                int noOfReducers  = counter.get("totalCounterValue").getIntValue();
                                finishedJobsInfo.setNoOfReducers(noOfReducers);
                            }

                        }
                    }

                }

                URL jobConfURL = new URL("http://"+jobHistoryServerHost+":"+jobHistoryServerPort+"/ws/v1/history/mapreduce/jobs/"+jobId+"/conf");
                JsonNode properties = readJsonNode(jobConfURL).path("conf").path("property");
                for(JsonNode property : properties){
                    if(property.get("name").asText().equalsIgnoreCase("oozie.action.id")){
                        String actionId = property.get("value").asText();
                        finishedJobsInfo.setActionId(actionId);
                    }
                    else  if(property.get("name").asText().equalsIgnoreCase("oozie.job.id")){
                        String workflowId = property.get("value").asText();
                        finishedJobsInfo.setWorkflowId(workflowId);
                    }
                }

                long totalTasks = finishedJobsInfo.getNoOfMaps() + finishedJobsInfo.getNoOfReducers();

                double averageTaskMemory = finishedJobsInfo.getUsedPhysicalMemory() / (totalTasks * 1024.0*1024.0);
                double occupiedMemory = (finishedJobsInfo.getTimeMaps() + finishedJobsInfo.getTimeReducers()) * averageTaskMemory;
                double allocatedMemory = (finishedJobsInfo.getMemorySecondsMaps() + finishedJobsInfo.getMemorySecondsReducers());
                double usedPerAllocatedMemory = occupiedMemory/allocatedMemory;
                finishedJobsInfo.setOccupiedMemory(occupiedMemory);
                finishedJobsInfo.setAllocatedMemory(allocatedMemory);
                finishedJobsInfo.setUsedPerAllocatedMemory(usedPerAllocatedMemory);
                double allocatedCPU = finishedJobsInfo.getvCoreSecondsMaps()+finishedJobsInfo.getvCoreSecondsReducers();
                double usedPerAllocatedCPU = finishedJobsInfo.getcpuTimeSpentTotal()/allocatedCPU;
                finishedJobsInfo.setUsedPerAllocatedCPU(usedPerAllocatedCPU);


                //write finishedjobInfo to file
                finishedJobsInfo.setTimestamp(new Timestamp(Calendar.getInstance().getTime().getTime()));
                writer.write(finishedJobsInfo.toString()+lineSeparator);

            }

            writer.close();
            System.out.println("finished counter = " + counter);
            if(counter == aggregationInterval/scheduleInterval){
                counter = 0;
                if(new File(finishedJobsFile).length() !=0) {
                    aggregateCounter++;
                    String fileName=finishedJobsAggregatedDir +"finished-"+ System.currentTimeMillis();
                    Files.copy(new File(finishedJobsFile).toPath(), new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    PrintWriter pw = new PrintWriter(finishedJobsFile);
                    pw.close();
                    //TODO: change logic to monitor dir and load automatically
                    HiveConnection hiveConnection = new HiveConnection();
                    hiveConnection.loadIntoHive(fileName, finishedJobsTable);
                }
            }

        } catch (Exception e) {
            System.out.println("e = " + e);
            e.printStackTrace();

        }
    }

    public static void schedule(long startDelay, long scheduleInterval, TimeUnit timeUnitForSchedule) {


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
                }, startDelay, scheduleInterval, timeUnitForSchedule);
    }


}
