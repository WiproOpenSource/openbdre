package com.wipro.analytics.beans;

import com.wipro.analytics.fetchers.DataFetcherMain;

import java.sql.Timestamp;

/**
 * Created by cloudera on 4/4/17.
 */
public class MRTaskInfo {
    private String applicationId;
    private String taskId;
    private double taskProgress;
    private String taskState;
    private String taskType;
    private long taskStartTime;
    private long taskFinishTime;
    private long taskElapsedTime;
    private String runningTaskAttemptId;
    private String taskAttemptState;
    private String assignedContainerId;
    private String nodeHttpAddress;
    private String nodeId;
    private String containerState;
    private String containerUsername;
    private long containerTotalMemoryNeededMB;
    private long containerTotalVCoresNeeded;
    private Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getContainerTotalVCoresNeeded() {
        return containerTotalVCoresNeeded;
    }

    public void setContainerTotalVCoresNeeded(long containerTotalVCoresNeeded) {
        this.containerTotalVCoresNeeded = containerTotalVCoresNeeded;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public double getTaskProgress() {
        return taskProgress;
    }

    public void setTaskProgress(double taskProgress) {
        this.taskProgress = taskProgress;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public long getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(long taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public long getTaskFinishTime() {
        return taskFinishTime;
    }

    public void setTaskFinishTime(long taskFinishTime) {
        this.taskFinishTime = taskFinishTime;
    }

    public long getTaskElapsedTime() {
        return taskElapsedTime;
    }

    public void setTaskElapsedTime(long taskElapsedTime) {
        this.taskElapsedTime = taskElapsedTime;
    }

    public String getRunningTaskAttemptId() {
        return runningTaskAttemptId;
    }

    public void setRunningTaskAttemptId(String runningTaskAttemptId) {
        this.runningTaskAttemptId = runningTaskAttemptId;
    }

    public String getTaskAttemptState() {
        return taskAttemptState;
    }

    public void setTaskAttemptState(String taskAttemptState) {
        this.taskAttemptState = taskAttemptState;
    }

    public String getAssignedContainerId() {
        return assignedContainerId;
    }

    public void setAssignedContainerId(String assignedContainerId) {
        this.assignedContainerId = assignedContainerId;
    }

    public String getNodeHttpAddress() {
        return nodeHttpAddress;
    }

    public void setNodeHttpAddress(String nodeHttpAddress) {
        this.nodeHttpAddress = nodeHttpAddress;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getContainerState() {
        return containerState;
    }

    public void setContainerState(String containerState) {
        this.containerState = containerState;
    }

    public String getContainerUsername() {
        return containerUsername;
    }

    public void setContainerUsername(String containerUsername) {
        this.containerUsername = containerUsername;
    }

    public long getContainerTotalMemoryNeededMB() {
        return containerTotalMemoryNeededMB;
    }

    public void setContainerTotalMemoryNeededMB(long containerTotalMemoryNeededMB) {
        this.containerTotalMemoryNeededMB = containerTotalMemoryNeededMB;
    }

    @Override
    public String toString() {
        String fieldDelimiter= DataFetcherMain.FILE_FIELD_SEPERATOR;

        return  applicationId + fieldDelimiter
                +  taskId + fieldDelimiter
                + taskProgress + fieldDelimiter
                + taskState + fieldDelimiter
                + taskType + fieldDelimiter
                +  taskStartTime + fieldDelimiter
                + taskFinishTime + fieldDelimiter
                +  taskElapsedTime + fieldDelimiter
                +  runningTaskAttemptId + fieldDelimiter
                + taskAttemptState  + fieldDelimiter
                + assignedContainerId + fieldDelimiter
                + nodeHttpAddress + fieldDelimiter
                + nodeId + fieldDelimiter
                +  containerState + fieldDelimiter
                +  containerUsername + fieldDelimiter
                +  containerTotalMemoryNeededMB + fieldDelimiter
                + containerTotalVCoresNeeded + fieldDelimiter
                +timestamp;

    }
}
