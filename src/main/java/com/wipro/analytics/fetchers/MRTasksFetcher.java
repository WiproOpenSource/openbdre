package com.wipro.analytics.fetchers;

import com.wipro.analytics.HiveConnection;
import com.wipro.analytics.beans.MRTaskInfo;
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
 * Created by cloudera on 4/4/17.
 */
public class MRTasksFetcher {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String MRTasksFile = DataFetcherMain.MR_TASKS_FILE;
    private final static String MRTasksAggregatedDir = DataFetcherMain.MR_TASKS_AGGREGATED_DIR;
    private final static String resourceManagerHost = DataFetcherMain.RESOURCE_MANAGER_HOST;
    private final static String resourceManagerPort = DataFetcherMain.RESOURCE_MANAGER_PORT;
    private static final long scheduleInterval = DataFetcherMain.SCHEDULE_INTERVAL;
    private static final long aggregationInterval = DataFetcherMain.AGGREGATION_INTERVAL;
    private static final String lineSeparator = DataFetcherMain.FILE_LINE_SEPERATOR;
    private static final String MRTasksTable = DataFetcherMain.MR_TASKS_TABLE;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static int counter = 0;
    static int aggregateCounter =0;

    public JsonNode readJsonNode(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return objectMapper.readTree(conn.getInputStream());
    }

    public void getTasks(){
        try {
            counter++;
            BufferedWriter writer = new BufferedWriter( new FileWriter(MRTasksFile,true));

            URL runningAppsUrl = new URL("http://"+resourceManagerHost+":"+resourceManagerPort+"/ws/v1/cluster/apps?states=running&applicationTypes=MAPREDUCE");
            JsonNode rootNode = readJsonNode(runningAppsUrl);
            JsonNode appsArray = rootNode.path("apps").path("app");
            String[] applicationIdsArray = new String[appsArray.size()];
            int jobCounter=0;

            for(JsonNode app: appsArray){
                String applicationId = app.get("id").asText();
                applicationIdsArray[jobCounter++] = applicationId;
            }

            for(String applicationId : applicationIdsArray){
                URL tasksUrl = new URL("http://"+resourceManagerHost+":"+resourceManagerPort+"/proxy/"+applicationId+"/ws/v1/mapreduce/jobs/"+applicationId.replace("application","job")+"/tasks");
                JsonNode taskArray = readJsonNode(tasksUrl).path("tasks").path("task");
                for(JsonNode task : taskArray){
                    MRTaskInfo mrTaskInfo = new MRTaskInfo();
                    mrTaskInfo.setApplicationId(applicationId);
                    String taskId = task.get("id").asText();
                    double taskProgress = task.get("progress").getDoubleValue();
                    String taskState = task.get("state").asText();
                    String taskType = task.get("type").asText();
                    long taskStartTime = task.get("startTime").getLongValue();
                    long taskFinishTime = task.get("finishTime").getLongValue();
                    long taskElapsedTime = task.get("elapsedTime").getLongValue();

                    mrTaskInfo.setTaskId(taskId);
                    mrTaskInfo.setTaskProgress(taskProgress);
                    mrTaskInfo.setTaskState(taskState);
                    mrTaskInfo.setTaskType(taskType);
                    mrTaskInfo.setTaskStartTime(taskStartTime);
                    mrTaskInfo.setTaskFinishTime(taskFinishTime);
                    mrTaskInfo.setTaskElapsedTime(taskElapsedTime);

                    try{
                    URL taskAttemptURl = new URL("http://"+resourceManagerHost+":"+resourceManagerPort+"/proxy/"+applicationId+"/ws/v1/mapreduce/jobs/"+applicationId.replace("application","job")+"/tasks/"+taskId+"/attempts");
                    JsonNode taskAttemptsArray = readJsonNode(taskAttemptURl).path("taskAttempts").path("taskAttempt");
                    for(JsonNode taskAttempt : taskAttemptsArray){
                        if(taskAttempt.get("state").asText().equalsIgnoreCase("RUNNING")) {
                            String runningTaskAttemptId = taskAttempt.get("id").asText();
                            String taskAttemptState = taskAttempt.get("state").asText();
                            String assignedContainerId = taskAttempt.get("assignedContainerId").asText();
                            String nodeHttpAddress = taskAttempt.get("nodeHttpAddress").asText();

                            mrTaskInfo.setTaskAttemptState(taskAttemptState);
                            mrTaskInfo.setRunningTaskAttemptId(runningTaskAttemptId);
                            mrTaskInfo.setAssignedContainerId(assignedContainerId);
                            mrTaskInfo.setNodeHttpAddress(nodeHttpAddress);


                            URL containerDetailsURL = new URL("http://" + nodeHttpAddress + "/ws/v1/node/containers/" + assignedContainerId);
                            JsonNode container = readJsonNode(containerDetailsURL).path("container");
                            String nodeId = container.get("nodeId").asText();
                            String containerState = container.get("state").asText();
                            String containerUsername = container.get("user").asText();
                            long containerTotalMemoryNeededMB = container.get("totalMemoryNeededMB").getLongValue();
                            long containerTotalVCoresNeeded = container.get("totalVCoresNeeded").getLongValue();


                            mrTaskInfo.setNodeId(nodeId);
                            mrTaskInfo.setContainerState(containerState);
                            mrTaskInfo.setContainerUsername(containerUsername);
                            mrTaskInfo.setContainerTotalMemoryNeededMB(containerTotalMemoryNeededMB);
                            mrTaskInfo.setContainerTotalVCoresNeeded(containerTotalVCoresNeeded);
                            mrTaskInfo.setTimestamp(new Timestamp(Calendar.getInstance().getTime().getTime()));

                            System.out.println("mrTaskInfo.toString() = " + mrTaskInfo.toString());

                            writer.write(mrTaskInfo.toString() + lineSeparator);
                           }
                        }
                       }catch (Exception e){
                        System.out.println("Unable to fetch taskattempts and container details as task is not in running state");
                    }


                }
            }

            writer.close();
            System.out.println("mr tasks counter = " + counter);
            if(counter == aggregationInterval/scheduleInterval){
                counter = 0;
                if(new File(MRTasksFile).length() !=0) {
                    aggregateCounter++;
                    String fileName=MRTasksAggregatedDir +"mr-tasks-"+ System.currentTimeMillis();
                    Files.copy(new File(MRTasksFile).toPath(), new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    PrintWriter pw = new PrintWriter(MRTasksFile);
                    pw.close();
                    HiveConnection hiveConnection = new HiveConnection();
                    hiveConnection.loadIntoHive(fileName, MRTasksTable);
                }
            }

        }catch (Exception e){
            System.out.println("e = " + e);
            e.printStackTrace();
        }

    }

    public static void schedule(long startDelay, long scheduleInterval, TimeUnit timeUnitForSchedule) {


        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                            MRTasksFetcher mrTasksFetcher = new MRTasksFetcher();
                            mrTasksFetcher.getTasks();
                        }catch(Exception ex) {
                            ex.printStackTrace(); //or loggger would be better
                        }
                    }
                }, startDelay, scheduleInterval, timeUnitForSchedule);
    }



}
