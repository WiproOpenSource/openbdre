package com.wipro.ats.bdre.md.beans;

/**
 * Created by PR324290 on 3/28/16.
 */
public class SLAMonitoringBean {
    private Integer processId;
    private long currentExecutionTime;
    private long averageExecutionTime;
    private long sLATime;
    private boolean processRunning;
    private int stateOfProcess;

    public int getStateOfProcess() {
        return stateOfProcess;
    }

    public void setStateOfProcess(int stateOfProcess) {
        this.stateOfProcess = stateOfProcess;
    }

    public boolean isProcessRunning() {
        return processRunning;
    }

    public void setProcessRunning(boolean processRunning) {
        this.processRunning = processRunning;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }


    public long getAverageExecutionTime() {
        return averageExecutionTime;
    }

    public void setAverageExecutionTime(long averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
    }

    public long getCurrentExecutionTime() {
        return currentExecutionTime;
    }

    public void setCurrentExecutionTime(long currentExecutionTime) {
        this.currentExecutionTime = currentExecutionTime;
    }

    public long getsLATime() {
        return sLATime;
    }

    public void setsLATime(long sLATime) {
        this.sLATime = sLATime;
    }


}
