package com.wipro.analytics.fetchers;

import com.wipro.analytics.HiveConnection;
import com.wipro.analytics.beans.GangliaMetricInfo;
import org.codehaus.jackson.JsonNode;

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
 * Created by cloudera on 4/12/17.
 */
public class GangliaMetricsFetcher {
    private final static org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();
    private final static String CLUSTER_NAME = DataFetcherMain.CLUSTER_NAME;
    private final static String HOST_NAMES_LIST = DataFetcherMain.HOST_NAMES_LIST;
    private final static String GANGLIA_WEBSERVER_HOSTNAME = DataFetcherMain.GANGLIA_WEBSERVER_HOSTNAME;
    private final static long STARTING_FETCH_INTERVAL_FROM_GANGLIA = DataFetcherMain.STARTING_FETCH_INTERVAL_FROM_GANGLIA;
    private final static String gangliaMetricFile = DataFetcherMain.GANGLIA_METRIC_FILE;
    private static final String lineSeparator = DataFetcherMain.FILE_LINE_SEPERATOR;
    private static final String gangliaMetricsTable = DataFetcherMain.GANGLIA_METRICS_TABLE;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static long currentEpochTimeInSeconds = System.currentTimeMillis() / 1000;
    static long fetchStartTime = currentEpochTimeInSeconds - STARTING_FETCH_INTERVAL_FROM_GANGLIA;
    static long gangliaScheduleCount = 0;

    public static JsonNode readJsonNode(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return objectMapper.readTree(conn.getInputStream());
    }

    public static void getGangliaMetrics() {
     /*   ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            NodeBean nodeBean = mapper.readValue(new URL("http://localhost/ganglia/api/rundeck.php"), NodeBean.class);
            System.out.println(ReflectionToStringBuilder.toString(nodeBean, ToStringStyle.MULTI_LINE_STYLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        try {
            gangliaScheduleCount++;
            System.out.println("gangliaScheduleCount = " + gangliaScheduleCount);
            //todo loop for hostNames of Nodes
            String[] listOfMetrics = { "cpu_aidle","cpu_idle", "cpu_nice","cpu_steal","cpu_system","cpu_user","cpu_wio",
                                       "disk_free","disk_total","part_max_used",
                                        "load_fifteen","load_five","load_one",
                                        "mem_buffers","mem_cached", "mem_free", "mem_shared","swap_free",
                                       "bytes_in", "bytes_out", "pkts_in", "pkts_out",
                                        "proc_run", "proc_total"};

            BufferedWriter writer = new BufferedWriter( new FileWriter(gangliaMetricFile,true));
            for(String hostName : HOST_NAMES_LIST.split(",")) {
                for (String metric : listOfMetrics) {
                    currentEpochTimeInSeconds = System.currentTimeMillis() / 1000;

                    URL metricUrl = new URL("http://" + GANGLIA_WEBSERVER_HOSTNAME + "/ganglia/graph.php?h=" + hostName + "&m=" + metric + "&cs=" + fetchStartTime + "&ce=" + currentEpochTimeInSeconds + "&r=hour&c=" + CLUSTER_NAME + "&json=1");
                    JsonNode rootNodeArray = readJsonNode(metricUrl);
                    JsonNode rootNode = rootNodeArray.get(0);
                    JsonNode datapointsArray = rootNode.path("datapoints");
                    for (JsonNode datapoint : datapointsArray) {
                        if (datapoint.get(0).asText().equalsIgnoreCase("NaN")) {
                            continue;
                        } else {
                            double metricValue = datapoint.get(0).getDoubleValue();
                            long metricValueTime = datapoint.get(1).getLongValue();

                            GangliaMetricInfo gangliaMetricInfo = new GangliaMetricInfo();
                            gangliaMetricInfo.setClusterName(CLUSTER_NAME);
                            gangliaMetricInfo.setNodeHostName(hostName);
                            gangliaMetricInfo.setMetric(metric);
                            gangliaMetricInfo.setMetricValue(metricValue);
                            gangliaMetricInfo.setMetricValueTime(metricValueTime);

                            gangliaMetricInfo.setTimestamp(new Timestamp(Calendar.getInstance().getTime().getTime()));
                            writer.write(gangliaMetricInfo.toString() + lineSeparator);
                        }
                    }
                }
            }
            fetchStartTime = currentEpochTimeInSeconds;

            writer.close();
            if(new File(gangliaMetricFile).length() !=0) {
                HiveConnection hiveConnection = new HiveConnection();
                hiveConnection.loadIntoHive(gangliaMetricFile, gangliaMetricsTable);
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
                            GangliaMetricsFetcher gangliaMetricsFetcher = new GangliaMetricsFetcher();
                            gangliaMetricsFetcher.getGangliaMetrics();
                        }catch(Exception ex) {
                            ex.printStackTrace(); //or loggger would be better
                        }
                    }
                }, startDelay, scheduleInterval, timeUnitForSchedule);
    }
}
