package com.wipro.analytics.fetchers;

import com.wipro.analytics.HiveConnection;
import com.wipro.analytics.beans.QueueInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by cloudera on 3/18/17.
 */
public class QueueFetcher {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String queuesFile = DataFetcherMain.QUEUES_FILE;
    private final String queuesAggregatedDir = DataFetcherMain.QUEUES_AGGREGATED_DIR;
    private final static String resourceManagerHost = DataFetcherMain.RESOURCE_MANAGER_HOST;
    private final static String resourceManagerPort = DataFetcherMain.RESOURCE_MANAGER_PORT;
    private static final long scheduleInterval = DataFetcherMain.SCHEDULE_INTERVAL;
    private static final long aggregationInterval = DataFetcherMain.AGGREGATION_INTERVAL;
    private static final String lineSeparator = DataFetcherMain.FILE_LINE_SEPERATOR;
    private static final String queueTable = DataFetcherMain.QUEUE_TABLE;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static List<QueueInfo> queueInfoList = new ArrayList<QueueInfo>();

    static int counter = 0;
    static int aggregateCounter =0;


    public JsonNode readJsonNode(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return objectMapper.readTree(conn.getInputStream());
     /*   return objectMapper.readTree("{\n" +
                "    \"scheduler\": {\n" +
                "        \"schedulerInfo\": {\n" +
                "            \"capacity\": 100.0, \n" +
                "            \"maxCapacity\": 100.0, \n" +
                "            \"queueName\": \"root\", \n" +
                "            \"queues\": {\n" +
                "                \"queue\": [\n" +
                "                    {\n" +
                "                        \"absoluteCapacity\": 10.5, \n" +
                "                        \"absoluteMaxCapacity\": 50.0, \n" +
                "                        \"absoluteUsedCapacity\": 0.0, \n" +
                "                        \"capacity\": 10.5, \n" +
                "                        \"maxCapacity\": 50.0, \n" +
                "                        \"numApplications\": 0, \n" +
                "                        \"queueName\": \"a\", \n" +
                "                        \"queues\": {\n" +
                "                            \"queue\": [\n" +
                "                                {\n" +
                "                                    \"absoluteCapacity\": 3.15, \n" +
                "                                    \"absoluteMaxCapacity\": 25.0, \n" +
                "                                    \"absoluteUsedCapacity\": 0.0, \n" +
                "                                    \"capacity\": 30.000002, \n" +
                "                                    \"maxCapacity\": 50.0, \n" +
                "                                    \"numApplications\": 0, \n" +
                "                                    \"queueName\": \"a1\", \n" +
                "                                    \"queues\": {\n" +
                "                                        \"queue\": [\n" +
                "                                            {\n" +
                "                                                \"absoluteCapacity\": 2.6775, \n" +
                "                                                \"absoluteMaxCapacity\": 25.0, \n" +
                "                                                \"absoluteUsedCapacity\": 0.0, \n" +
                "                                                \"capacity\": 85.0, \n" +
                "                                                \"maxActiveApplications\": 1, \n" +
                "                                                \"maxActiveApplicationsPerUser\": 1, \n" +
                "                                                \"maxApplications\": 267, \n" +
                "                                                \"maxApplicationsPerUser\": 267, \n" +
                "                                                \"maxCapacity\": 100.0, \n" +
                "                                                \"numActiveApplications\": 0, \n" +
                "                                                \"numApplications\": 0, \n" +
                "                                                \"numContainers\": 0, \n" +
                "                                                \"numPendingApplications\": 0, \n" +
                "                                                \"queueName\": \"a1a\", \n" +
                "                                                \"resourcesUsed\": {\n" +
                "                                                    \"memory\": 0, \n" +
                "                                                    \"vCores\": 0\n" +
                "                                                }, \n" +
                "                                                \"state\": \"RUNNING\", \n" +
                "                                                \"type\": \"capacitySchedulerLeafQueueInfo\", \n" +
                "                                                \"usedCapacity\": 0.0, \n" +
                "                                                \"usedResources\": \"<memory:0, vCores:0>\", \n" +
                "                                                \"userLimit\": 100, \n" +
                "                                                \"userLimitFactor\": 1.0, \n" +
                "                                                \"users\": null\n" +
                "                                            }, \n" +
                "                                            {\n" +
                "                                                \"absoluteCapacity\": 0.47250003, \n" +
                "                                                \"absoluteMaxCapacity\": 25.0, \n" +
                "                                                \"absoluteUsedCapacity\": 0.0, \n" +
                "                                                \"capacity\": 15.000001, \n" +
                "                                                \"maxActiveApplications\": 1, \n" +
                "                                                \"maxActiveApplicationsPerUser\": 1, \n" +
                "                                                \"maxApplications\": 47, \n" +
                "                                                \"maxApplicationsPerUser\": 47, \n" +
                "                                                \"maxCapacity\": 100.0, \n" +
                "                                                \"numActiveApplications\": 0, \n" +
                "                                                \"numApplications\": 0, \n" +
                "                                                \"numContainers\": 0, \n" +
                "                                                \"numPendingApplications\": 0, \n" +
                "                                                \"queueName\": \"a1b\", \n" +
                "                                                \"resourcesUsed\": {\n" +
                "                                                    \"memory\": 0, \n" +
                "                                                    \"vCores\": 0\n" +
                "                                                }, \n" +
                "                                                \"state\": \"RUNNING\", \n" +
                "                                                \"type\": \"capacitySchedulerLeafQueueInfo\", \n" +
                "                                                \"usedCapacity\": 0.0, \n" +
                "                                                \"usedResources\": \"<memory:0, vCores:0>\", \n" +
                "                                                \"userLimit\": 100, \n" +
                "                                                \"userLimitFactor\": 1.0, \n" +
                "                                                \"users\": null\n" +
                "                                            }\n" +
                "                                        ]\n" +
                "                                    }, \n" +
                "                                    \"resourcesUsed\": {\n" +
                "                                        \"memory\": 0, \n" +
                "                                        \"vCores\": 0\n" +
                "                                    }, \n" +
                "                                    \"state\": \"RUNNING\", \n" +
                "                                    \"usedCapacity\": 0.0, \n" +
                "                                    \"usedResources\": \"<memory:0, vCores:0>\"\n" +
                "                                }, \n" +
                "                                {\n" +
                "                                    \"absoluteCapacity\": 7.35, \n" +
                "                                    \"absoluteMaxCapacity\": 50.0, \n" +
                "                                    \"absoluteUsedCapacity\": 0.0, \n" +
                "                                    \"capacity\": 70.0, \n" +
                "                                    \"maxActiveApplications\": 1, \n" +
                "                                    \"maxActiveApplicationsPerUser\": 100, \n" +
                "                                    \"maxApplications\": 735, \n" +
                "                                    \"maxApplicationsPerUser\": 73500, \n" +
                "                                    \"maxCapacity\": 100.0, \n" +
                "                                    \"numActiveApplications\": 0, \n" +
                "                                    \"numApplications\": 0, \n" +
                "                                    \"numContainers\": 0, \n" +
                "                                    \"numPendingApplications\": 0, \n" +
                "                                    \"queueName\": \"a2\", \n" +
                "                                    \"resourcesUsed\": {\n" +
                "                                        \"memory\": 0, \n" +
                "                                        \"vCores\": 0\n" +
                "                                    }, \n" +
                "                                    \"state\": \"RUNNING\", \n" +
                "                                    \"type\": \"capacitySchedulerLeafQueueInfo\", \n" +
                "                                    \"usedCapacity\": 0.0, \n" +
                "                                    \"usedResources\": \"<memory:0, vCores:0>\", \n" +
                "                                    \"userLimit\": 100, \n" +
                "                                    \"userLimitFactor\": 100.0, \n" +
                "                                    \"users\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }, \n" +
                "                        \"resourcesUsed\": {\n" +
                "                            \"memory\": 0, \n" +
                "                            \"vCores\": 0\n" +
                "                        }, \n" +
                "                        \"state\": \"RUNNING\", \n" +
                "                        \"usedCapacity\": 0.0, \n" +
                "                        \"usedResources\": \"<memory:0, vCores:0>\"\n" +
                "                    }, \n" +
                "                    {\n" +
                "                        \"absoluteCapacity\": 89.5, \n" +
                "                        \"absoluteMaxCapacity\": 100.0, \n" +
                "                        \"absoluteUsedCapacity\": 0.0, \n" +
                "                        \"capacity\": 89.5, \n" +
                "                        \"maxCapacity\": 100.0, \n" +
                "                        \"numApplications\": 2, \n" +
                "                        \"queueName\": \"b\", \n" +
                "                        \"queues\": {\n" +
                "                            \"queue\": [\n" +
                "                                {\n" +
                "                                    \"absoluteCapacity\": 53.7, \n" +
                "                                    \"absoluteMaxCapacity\": 100.0, \n" +
                "                                    \"absoluteUsedCapacity\": 0.0, \n" +
                "                                    \"capacity\": 60.000004, \n" +
                "                                    \"maxActiveApplications\": 1, \n" +
                "                                    \"maxActiveApplicationsPerUser\": 100, \n" +
                "                                    \"maxApplications\": 5370, \n" +
                "                                    \"maxApplicationsPerUser\": 537000, \n" +
                "                                    \"maxCapacity\": 100.0, \n" +
                "                                    \"numActiveApplications\": 1, \n" +
                "                                    \"numApplications\": 2, \n" +
                "                                    \"numContainers\": 0, \n" +
                "                                    \"numPendingApplications\": 1, \n" +
                "                                    \"queueName\": \"b1\", \n" +
                "                                    \"resourcesUsed\": {\n" +
                "                                        \"memory\": 0, \n" +
                "                                        \"vCores\": 0\n" +
                "                                    }, \n" +
                "                                    \"state\": \"RUNNING\", \n" +
                "                                    \"type\": \"capacitySchedulerLeafQueueInfo\", \n" +
                "                                    \"usedCapacity\": 0.0, \n" +
                "                                    \"usedResources\": \"<memory:0, vCores:0>\", \n" +
                "                                    \"userLimit\": 100, \n" +
                "                                    \"userLimitFactor\": 100.0, \n" +
                "                                    \"users\": {\n" +
                "                                        \"user\": [\n" +
                "                                            {\n" +
                "                                                \"numActiveApplications\": 0, \n" +
                "                                                \"numPendingApplications\": 1, \n" +
                "                                                \"resourcesUsed\": {\n" +
                "                                                    \"memory\": 0, \n" +
                "                                                    \"vCores\": 0\n" +
                "                                                }, \n" +
                "                                                \"username\": \"user2\"\n" +
                "                                            }, \n" +
                "                                            {\n" +
                "                                                \"numActiveApplications\": 1, \n" +
                "                                                \"numPendingApplications\": 0, \n" +
                "                                                \"resourcesUsed\": {\n" +
                "                                                    \"memory\": 0, \n" +
                "                                                    \"vCores\": 0\n" +
                "                                                }, \n" +
                "                                                \"username\": \"user1\"\n" +
                "                                            }\n" +
                "                                        ]\n" +
                "                                    }\n" +
                "                                }, \n" +
                "                                {\n" +
                "                                    \"absoluteCapacity\": 35.3525, \n" +
                "                                    \"absoluteMaxCapacity\": 100.0, \n" +
                "                                    \"absoluteUsedCapacity\": 0.0, \n" +
                "                                    \"capacity\": 39.5, \n" +
                "                                    \"maxActiveApplications\": 1, \n" +
                "                                    \"maxActiveApplicationsPerUser\": 100, \n" +
                "                                    \"maxApplications\": 3535, \n" +
                "                                    \"maxApplicationsPerUser\": 353500, \n" +
                "                                    \"maxCapacity\": 100.0, \n" +
                "                                    \"numActiveApplications\": 0, \n" +
                "                                    \"numApplications\": 0, \n" +
                "                                    \"numContainers\": 0, \n" +
                "                                    \"numPendingApplications\": 0, \n" +
                "                                    \"queueName\": \"b2\", \n" +
                "                                    \"resourcesUsed\": {\n" +
                "                                        \"memory\": 0, \n" +
                "                                        \"vCores\": 0\n" +
                "                                    }, \n" +
                "                                    \"state\": \"RUNNING\", \n" +
                "                                    \"type\": \"capacitySchedulerLeafQueueInfo\", \n" +
                "                                    \"usedCapacity\": 0.0, \n" +
                "                                    \"usedResources\": \"<memory:0, vCores:0>\", \n" +
                "                                    \"userLimit\": 100, \n" +
                "                                    \"userLimitFactor\": 100.0, \n" +
                "                                    \"users\": null\n" +
                "                                }, \n" +
                "                                {\n" +
                "                                    \"absoluteCapacity\": 0.4475, \n" +
                "                                    \"absoluteMaxCapacity\": 100.0, \n" +
                "                                    \"absoluteUsedCapacity\": 0.0, \n" +
                "                                    \"capacity\": 0.5, \n" +
                "                                    \"maxActiveApplications\": 1, \n" +
                "                                    \"maxActiveApplicationsPerUser\": 100, \n" +
                "                                    \"maxApplications\": 44, \n" +
                "                                    \"maxApplicationsPerUser\": 4400, \n" +
                "                                    \"maxCapacity\": 100.0, \n" +
                "                                    \"numActiveApplications\": 0, \n" +
                "                                    \"numApplications\": 0, \n" +
                "                                    \"numContainers\": 0, \n" +
                "                                    \"numPendingApplications\": 0, \n" +
                "                                    \"queueName\": \"b3\", \n" +
                "                                    \"resourcesUsed\": {\n" +
                "                                        \"memory\": 0, \n" +
                "                                        \"vCores\": 0\n" +
                "                                    }, \n" +
                "                                    \"state\": \"RUNNING\", \n" +
                "                                    \"type\": \"capacitySchedulerLeafQueueInfo\", \n" +
                "                                    \"usedCapacity\": 0.0, \n" +
                "                                    \"usedResources\": \"<memory:0, vCores:0>\", \n" +
                "                                    \"userLimit\": 100, \n" +
                "                                    \"userLimitFactor\": 100.0, \n" +
                "                                    \"users\": null\n" +
                "                                }\n" +
                "                            ]\n" +
                "                        }, \n" +
                "                        \"resourcesUsed\": {\n" +
                "                            \"memory\": 0, \n" +
                "                            \"vCores\": 0\n" +
                "                        }, \n" +
                "                        \"state\": \"RUNNING\", \n" +
                "                        \"usedCapacity\": 0.0, \n" +
                "                        \"usedResources\": \"<memory:0, vCores:0>\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }, \n" +
                "            \"type\": \"capacityScheduler\", \n" +
                "            \"usedCapacity\": 0.0\n" +
                "        }\n" +
                "    }\n" +
                "}");
*/

    }

    public void getQueuesData(){
        try {
            URL schedulerUrl = new URL("http://"+resourceManagerHost+":"+resourceManagerPort+"/ws/v1/cluster/scheduler");
            JsonNode rootNode = readJsonNode(schedulerUrl);
            JsonNode schedulerInfo = rootNode.path("scheduler").path("schedulerInfo");
            String schedulerType = schedulerInfo.get("type").asText();
         //   System.out.println("schedulerType = " + schedulerType);

            if(schedulerType.equalsIgnoreCase("fairScheduler")){

            }

            else if(schedulerType.equalsIgnoreCase("capacityScheduler")){
                queueInfoList.clear();
                getCapacitySchedulerQueue(schedulerInfo);
                BufferedWriter writer = new BufferedWriter( new FileWriter(queuesFile,true));
                //System.out.println("queuinfo list size = " + queueInfoList.size());
                for(QueueInfo queueInfo: queueInfoList){
                    queueInfo.setTimestamp(new Timestamp(Calendar.getInstance().getTime().getTime()));
                    writer.write(queueInfo.toString()+lineSeparator);
                }
                writer.close();
                counter++;
                System.out.println("queue counter =" + counter);
                if(counter == aggregationInterval/scheduleInterval){
                    counter = 0;
                    if(new File(queuesFile).length() !=0) {
                        aggregateCounter++;
                        String fileName=queuesAggregatedDir +"queue-"+ System.currentTimeMillis();
                        Files.copy(new File(queuesFile).toPath(), new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        PrintWriter pw = new PrintWriter(queuesFile);
                        pw.close();
                        HiveConnection hiveConnection = new HiveConnection();
                        hiveConnection.loadIntoHive(fileName,queueTable);
                    }
                }

            }

        } catch (Exception e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }
    }

    public void getCapacitySchedulerQueue(final JsonNode node) throws IOException {
        JsonNode queueArray = node.path("queues").path("queue");
        for(JsonNode queue: queueArray) {

            //queues fetched here contain all queues
           // String queueName = queue.get("queueName").asText();
           // System.out.println("queuname is " + queueName);
            if (queue.has("queues")) {
                getCapacitySchedulerQueue(queue);
            }
            else {

                //queues fetched here are only leaf queues
                String queueName = queue.get("queueName").asText();
                double absoluteAllocatedCapacity = queue.get("absoluteCapacity").getDoubleValue();
                double absoluteUsedCapacity = queue.get("absoluteUsedCapacity").getDoubleValue();

                //// TODO: 3/20/2017 know diff between resourcesUsed & usedResources both in same json
                JsonNode resourcesUsed = queue.path("resourcesUsed");
                int usedMemory = resourcesUsed.get("memory").getIntValue();
                int usedCores = resourcesUsed.get("vCores").getIntValue();



                int numContainers = queue.get("numContainers").getIntValue();
                String queueState = queue.get("state").asText();

                //Below elements of queue are present only in leaf queues but not in parent queue
                int maxApplications = queue.get("maxApplications").getIntValue();
                int numApplications = queue.get("numApplications").getIntValue();
                int numActiveApplications = queue.get("numActiveApplications").getIntValue();
                int numPendingApplications = queue.get("numPendingApplications").getIntValue();
                String queueType = queue.get("type").asText();

                //Iterate through users json array
                String users = "";
                JsonNode usersArray = queue.path("users").path("user");
                if(!usersArray.isMissingNode()){
                    for(JsonNode user: usersArray){
                        users = users + (user.get("username").asText())+",";
                    }
                    if(users.charAt(users.length()-1)==',') {
                        users = users.substring(0,users.length()-1);
                    }
                }

                QueueInfo queueInfo = new QueueInfo();
                queueInfo.setQueueName(queueName);
                queueInfo.setAbsoluteUsedCapacity(absoluteUsedCapacity);
                queueInfo.setAbsoluteAllocatedCapacity(absoluteAllocatedCapacity);
                queueInfo.setUsedMemory(usedMemory);
                queueInfo.setUsedCores(usedCores);
                queueInfo.setNumContainers(numContainers);
                queueInfo.setQueueState(queueState);
                queueInfo.setNumApplications(numApplications);
                queueInfo.setNumActiveApplications(numActiveApplications);
                queueInfo.setNumPendingApplications(numPendingApplications);
                queueInfo.setQueueType(queueType);
                queueInfo.setUsers(users);
                queueInfoList.add(queueInfo);

            }
        }
    }


    public static void schedule(long startDelay, long scheduleInterval, TimeUnit timeUnitForSchedule) {


        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                            QueueFetcher queueFetcher = new QueueFetcher();
                            queueFetcher.getQueuesData();
                        }catch(Exception ex) {
                            ex.printStackTrace(); //or loggger would be better
                        }
                    }
                }, startDelay, scheduleInterval, timeUnitForSchedule);
    }


}
