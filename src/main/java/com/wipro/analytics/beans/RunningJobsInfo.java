package com.wipro.analytics.beans;

import com.wipro.analytics.fetchers.DataFetcherMain;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by cloudera on 3/19/17.
 */
public class RunningJobsInfo implements Serializable {
    private String applicationId;
    private String applicationName;
    private String applicationState;
    private String applicationType;
    private String finalState;
    private String progress;
    private String username;
    private String queueName;
    private long startTime;
    private long elapsedTime;
    private long finishTime;
    private String trackingUrl;
    private int numContainers;
    private int allocatedMB;
    private int allocatedVCores;
    private long memorySeconds;
    private long vcoreSeconds;
    private Timestamp timestamp;


    public long getVcoreSeconds() {
        return vcoreSeconds;
    }

    public void setVcoreSeconds(long vcoreSeconds) {
        this.vcoreSeconds = vcoreSeconds;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(String applicationState) {
        this.applicationState = applicationState;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getFinalState() {
        return finalState;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public int getNumContainers() {
        return numContainers;
    }

    public void setNumContainers(int numContainers) {
        this.numContainers = numContainers;
    }

    public int getAllocatedMB() {
        return allocatedMB;
    }

    public void setAllocatedMB(int allocatedMB) {
        this.allocatedMB = allocatedMB;
    }

    public int getAllocatedVCores() {
        return allocatedVCores;
    }

    public void setAllocatedVCores(int allocatedVCores) {
        this.allocatedVCores = allocatedVCores;
    }

    public long getMemorySeconds() {
        return memorySeconds;
    }

    public void setMemorySeconds(long memorySeconds) {
        this.memorySeconds = memorySeconds;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    
    @Override
    public String toString() {
        String fieldDelimiter= DataFetcherMain.FILE_FIELD_SEPERATOR;
        return    applicationId + fieldDelimiter
                + applicationName + fieldDelimiter
                + applicationState + fieldDelimiter
                + applicationType + fieldDelimiter
                + finalState + fieldDelimiter
                + progress + fieldDelimiter
                + username + fieldDelimiter
                + queueName + fieldDelimiter
                + startTime + fieldDelimiter
                + elapsedTime + fieldDelimiter
                + finishTime + fieldDelimiter
                + trackingUrl + fieldDelimiter
                + numContainers + fieldDelimiter
                + allocatedMB + fieldDelimiter
                + allocatedVCores + fieldDelimiter
                + memorySeconds + fieldDelimiter
                + vcoreSeconds + fieldDelimiter
                + timestamp;

    }
}
