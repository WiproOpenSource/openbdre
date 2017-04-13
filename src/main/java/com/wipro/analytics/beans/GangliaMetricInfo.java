package com.wipro.analytics.beans;

import java.sql.Timestamp;

/**
 * Created by cloudera on 4/12/17.
 */
public class GangliaMetricInfo {
    private String clusterName;
    private String nodeHostName;
    private String metric;
    private double metricValue;
    private long metricValueTime;
    private Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNodeHostName() {
        return nodeHostName;
    }

    public void setNodeHostName(String nodeHostName) {
        this.nodeHostName = nodeHostName;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    public long getMetricValueTime() {
        return metricValueTime;
    }

    public void setMetricValueTime(long metricValueTime) {
        this.metricValueTime = metricValueTime;
    }

    @Override
    public String toString() {
        //String fieldDelimiter = DataFetcherMain.FILE_FIELD_SEPERATOR;
        String fieldDelimiter = "\t";
        return clusterName + fieldDelimiter
                + nodeHostName + fieldDelimiter
                + metric + fieldDelimiter
                + metricValue + fieldDelimiter
                + metricValueTime + fieldDelimiter
                +timestamp;
    }


}
