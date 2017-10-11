package com.wipro.ats.bdre.md.beans;

/**
 * Created by cloudera on 6/19/17.
 */
public class ExecutionBean {
    int processId;
    String batchDuration;
    String master;
    String deploymentMode;
    int driverCores;
    int driverMemory;
    int driverPermgen;
    int executorCores;
    int executorInstances;
    int executorMemory;
    String hdfsUser;
    String inputRateController;
    boolean isCheckPoint;
    boolean isDynamicAllocation;
    boolean isEventLogging;
    String logLevel;
    int receiverMaxRate;
    int taskMaxFailures;
    String yarnQueue;
    public boolean isDynamicAllocation() {
        return isDynamicAllocation;
    }

    public void setDynamicAllocation(boolean dynamicAllocation) {
        isDynamicAllocation = dynamicAllocation;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getBatchDuration() {
        return batchDuration;
    }

    public void setBatchDuration(String batchDuration) {
        this.batchDuration = batchDuration;
    }

    public String getComment() {
        return master;
    }

    public void setComment(String comment) {
        this.master = comment;
    }

    public String getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(String deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public int getDriverCores() {
        return driverCores;
    }

    public void setDriverCores(int driverCores) {
        this.driverCores = driverCores;
    }

    public int getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(int driverMemory) {
        this.driverMemory = driverMemory;
    }

    public int getDriverPermgen() {
        return driverPermgen;
    }

    public void setDriverPermgen(int driverPermgen) {
        this.driverPermgen = driverPermgen;
    }

    public int getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(int executorCores) {
        this.executorCores = executorCores;
    }

    public int getExecutorInstances() {
        return executorInstances;
    }

    public void setExecutorInstances(int executorInstances) {
        this.executorInstances = executorInstances;
    }

    public int getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(int executorMemory) {
        this.executorMemory = executorMemory;
    }

    public String getHdfsUser() {
        return hdfsUser;
    }

    public void setHdfsUser(String hdfsUser) {
        this.hdfsUser = hdfsUser;
    }

    public String getInputRateController() {
        return inputRateController;
    }

    public void setInputRateController(String inputRateController) {
        this.inputRateController = inputRateController;
    }

    public boolean isCheckPoint() {
        return isCheckPoint;
    }

    public void setCheckPoint(boolean checkPoint) {
        isCheckPoint = checkPoint;
    }

    public boolean isEventLogging() {
        return isEventLogging;
    }

    public void setEventLogging(boolean eventLogging) {
        isEventLogging = eventLogging;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public int getReceiverMaxRate() {
        return receiverMaxRate;
    }

    public void setReceiverMaxRate(int receiverMaxRate) {
        this.receiverMaxRate = receiverMaxRate;
    }

    public int getTaskMaxFailures() {
        return taskMaxFailures;
    }

    public void setTaskMaxFailures(int taskMaxFailures) {
        this.taskMaxFailures = taskMaxFailures;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public void setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
    }


    @Override
    public String toString() {
        return "ExecutionBean{" +
                "processId=" + processId +
                ", batchDuration='" + batchDuration + '\'' +
                ", master='" + master + '\'' +
                ", deploymentMode='" + deploymentMode + '\'' +
                ", driverCores=" + driverCores +
                ", driverMemory=" + driverMemory +
                ", driverPermgen=" + driverPermgen +
                ", executorCores=" + executorCores +
                ", executorInstances=" + executorInstances +
                ", executorMemory=" + executorMemory +
                ", hdfsUser='" + hdfsUser + '\'' +
                ", inputRateController='" + inputRateController + '\'' +
                ", isCheckPoint=" + isCheckPoint +
                ", isDynamicAllocation=" + isDynamicAllocation +
                ", isEventLogging=" + isEventLogging +
                ", logLevel='" + logLevel + '\'' +
                ", receiverMaxRate=" + receiverMaxRate +
                ", taskMaxFailures=" + taskMaxFailures +
                ", yarnQueue='" + yarnQueue + '\'' +
                '}';
    }


}
