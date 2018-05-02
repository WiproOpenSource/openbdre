package com.wipro.analytics.beans;

import com.wipro.analytics.fetchers.DataFetcherMain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by cloudera on 3/19/17.
 */
public class FinishedJobsInfo implements Serializable {

    private String id;
    private String name;
    private String queue;
    private String user;
    private String state;
    private long submitTime;
    private long startTime;
    private long finishTime;
    private long avgMapTime ;
    private long avgReduceTime;
    private long avgShuffleTime;
    private long avgMergeTime;
    private long gcTime;
    private long usedPhysicalMemory;
    private long cpuTimeSpentMaps;
    private long cpuTimeSpentReducers;
    private long cpuTimeSpentTotal;
    private long vCoreSecondsMaps;
    private long vCoreSecondsReducers;
    private long memorySecondsMaps;
    private long memorySecondsReducers;
    private long slotsTimeMaps;
    private long slotsTimeReducers;
    private long timeMaps;
    private long timeReducers;
    private int noOfMaps;
    private int noOfReducers;
    private double occupiedMemory;
    private double allocatedMemory;
    private double usedPerAllocatedMemory;
    private double usedPerAllocatedCPU;
    private long totalFileBytesRead;
    private long totalFileBytesWritten;
    private long totalFileReadOps;
    private long totalFileLargeReadOps;
    private long totalFileWriteOps;
    private long totalHDFSBytesRead;
    private long totalHDFSBytesWritten;
    private long totalHDFSReadOps;
    private long totalHDFSLargeReadOps;
    private long totalHDFSWriteOps;
    private String actionId;
    private String workflowId;
    private Timestamp timestamp;


    public double getUsedPerAllocatedCPU() {
        return usedPerAllocatedCPU;
    }

    public void setUsedPerAllocatedCPU(double usedPerAllocatedCPU) {
        this.usedPerAllocatedCPU = usedPerAllocatedCPU;
    }

    public double getOccupiedMemory() {
        return occupiedMemory;
    }

    public void setOccupiedMemory(double occupiedMemory) {
        this.occupiedMemory = occupiedMemory;
    }

    public double getAllocatedMemory() {
        return allocatedMemory;
    }

    public void setAllocatedMemory(double allocatedMemory) {
        this.allocatedMemory = allocatedMemory;
    }

    public double getUsedPerAllocatedMemory() {
        return usedPerAllocatedMemory;
    }

    public void setUsedPerAllocatedMemory(double usedPerAllocatedMemory) {
        this.usedPerAllocatedMemory = usedPerAllocatedMemory;
    }

    public int getNoOfReducers() {
        return noOfReducers;
    }

    public void setNoOfReducers(int noOfReducers) {
        this.noOfReducers = noOfReducers;
    }

    public long getTimeMaps() {
        return timeMaps;
    }

    public void setTimeMaps(long timeMaps) {
        this.timeMaps = timeMaps;
    }

    public long getTimeReducers() {
        return timeReducers;
    }

    public void setTimeReducers(long timeReducers) {
        this.timeReducers = timeReducers;
    }

    public int getNoOfMaps() {
        return noOfMaps;
    }

    public void setNoOfMaps(int noOfMaps) {
        this.noOfMaps = noOfMaps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public long getAvgMapTime() {
        return avgMapTime;
    }

    public void setAvgMapTime(long avgMapTime) {
        this.avgMapTime = avgMapTime;
    }

    public long getAvgReduceTime() {
        return avgReduceTime;
    }

    public void setAvgReduceTime(long avgReduceTime) {
        this.avgReduceTime = avgReduceTime;
    }

    public long getAvgShuffleTime() {
        return avgShuffleTime;
    }

    public void setAvgShuffleTime(long avgShuffleTime) {
        this.avgShuffleTime = avgShuffleTime;
    }

    public long getAvgMergeTime() {
        return avgMergeTime;
    }

    public void setAvgMergeTime(long avgMergeTime) {
        this.avgMergeTime = avgMergeTime;
    }

    public long getGcTime() {
        return gcTime;
    }

    public void setGcTime(long gcTime) {
        this.gcTime = gcTime;
    }

    public long getUsedPhysicalMemory() {
        return usedPhysicalMemory;
    }

    public void setUsedPhysicalMemory(long used_memory) {
        this.usedPhysicalMemory = used_memory;
    }

    public long getcpuTimeSpentMaps() {
        return cpuTimeSpentMaps;
    }

    public void setcpuTimeSpentMaps(long cpuTimeSpentMaps) {
        this.cpuTimeSpentMaps = cpuTimeSpentMaps;
    }

    public long getcpuTimeSpentReducers() {
        return cpuTimeSpentReducers;
    }

    public void setcpuTimeSpentReducers(long cpuTimeSpentReducers) {
        this.cpuTimeSpentReducers = cpuTimeSpentReducers;
    }

    public long getcpuTimeSpentTotal() {
        return cpuTimeSpentTotal;
    }

    public void setcpuTimeSpentTotal(long cpuTimeSpentTotal) {
        this.cpuTimeSpentTotal = cpuTimeSpentTotal;
    }

    public long getvCoreSecondsMaps() {
        return vCoreSecondsMaps;
    }

    public void setvCoreSecondsMaps(long vCoreSecondsMaps) {
        this.vCoreSecondsMaps = vCoreSecondsMaps;
    }

    public long getvCoreSecondsReducers() {
        return vCoreSecondsReducers;
    }

    public void setvCoreSecondsReducers(long vCoreSecondsReducers) {
        this.vCoreSecondsReducers = vCoreSecondsReducers;
    }

    public long getMemorySecondsMaps() {
        return memorySecondsMaps;
    }

    public void setMemorySecondsMaps(long memorySecondsMaps) {
        this.memorySecondsMaps = memorySecondsMaps;
    }

    public long getMemorySecondsReducers() {
        return memorySecondsReducers;
    }

    public void setMemorySecondsReducers(long memorySecondsReducers) {
        this.memorySecondsReducers = memorySecondsReducers;
    }

    public long getSlotsTimeMaps() {
        return slotsTimeMaps;
    }

    public void setSlotsTimeMaps(long slotsTimeMaps) {
        this.slotsTimeMaps = slotsTimeMaps;
    }

    public long getSlotsTimeReducers() {
        return slotsTimeReducers;
    }

    public void setSlotsTimeReducers(long slotsTimeReducers) {
        this.slotsTimeReducers = slotsTimeReducers;
    }

    public long getTotalHDFSWriteOps() {
        return totalHDFSWriteOps;
    }

    public void setTotalHDFSWriteOps(long totalHDFSWriteOps) {
        this.totalHDFSWriteOps = totalHDFSWriteOps;
    }

    public long getTotalFileBytesRead() {
        return totalFileBytesRead;
    }

    public void setTotalFileBytesRead(long totalFileBytesRead) {
        this.totalFileBytesRead = totalFileBytesRead;
    }

    public long getTotalFileBytesWritten() {
        return totalFileBytesWritten;
    }

    public void setTotalFileBytesWritten(long totalFileBytesWritten) {
        this.totalFileBytesWritten = totalFileBytesWritten;
    }

    public long getTotalFileReadOps() {
        return totalFileReadOps;
    }

    public void setTotalFileReadOps(long totalFileReadOps) {
        this.totalFileReadOps = totalFileReadOps;
    }

    public long getTotalFileLargeReadOps() {
        return totalFileLargeReadOps;
    }

    public void setTotalFileLargeReadOps(long totalFileLargeReadOps) {
        this.totalFileLargeReadOps = totalFileLargeReadOps;
    }

    public long getTotalFileWriteOps() {
        return totalFileWriteOps;
    }

    public void setTotalFileWriteOps(long totalFileWriteOps) {
        this.totalFileWriteOps = totalFileWriteOps;
    }

    public long getTotalHDFSBytesRead() {
        return totalHDFSBytesRead;
    }

    public void setTotalHDFSBytesRead(long totalHDFSBytesRead) {
        this.totalHDFSBytesRead = totalHDFSBytesRead;
    }

    public long getTotalHDFSBytesWritten() {
        return totalHDFSBytesWritten;
    }

    public void setTotalHDFSBytesWritten(long totalHDFSBytesWritten) {
        this.totalHDFSBytesWritten = totalHDFSBytesWritten;
    }

    public long getTotalHDFSReadOps() {
        return totalHDFSReadOps;
    }

    public void setTotalHDFSReadOps(long totalHDFSReadOps) {
        this.totalHDFSReadOps = totalHDFSReadOps;
    }

    public long getTotalHDFSLargeReadOps() {
        return totalHDFSLargeReadOps;
    }

    public void setTotalHDFSLargeReadOps(long totalHDFSLargeReadOps) {
        this.totalHDFSLargeReadOps = totalHDFSLargeReadOps;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }


    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        String fieldDelimiter= DataFetcherMain.FILE_FIELD_SEPERATOR;
        return  id+ fieldDelimiter
                +name+ fieldDelimiter
                +queue+ fieldDelimiter
                + user+ fieldDelimiter
                +state+ fieldDelimiter
                +submitTime+ fieldDelimiter
                +startTime+ fieldDelimiter
                +finishTime+ fieldDelimiter
                +avgMapTime + fieldDelimiter
                +avgReduceTime+ fieldDelimiter
                +avgShuffleTime+ fieldDelimiter
                +avgMergeTime+ fieldDelimiter
                +gcTime+ fieldDelimiter
                +usedPhysicalMemory+ fieldDelimiter
                +cpuTimeSpentMaps+ fieldDelimiter
                +cpuTimeSpentReducers+ fieldDelimiter
                +cpuTimeSpentTotal+ fieldDelimiter
                +vCoreSecondsMaps+ fieldDelimiter
                +vCoreSecondsReducers+ fieldDelimiter
                +memorySecondsMaps+ fieldDelimiter
                +memorySecondsReducers+ fieldDelimiter
                +slotsTimeMaps+ fieldDelimiter
                +slotsTimeReducers+ fieldDelimiter
                + timeMaps+ fieldDelimiter
                + timeReducers+ fieldDelimiter
                + noOfMaps+ fieldDelimiter
                + noOfReducers+ fieldDelimiter
                +occupiedMemory+ fieldDelimiter
                +allocatedMemory+fieldDelimiter
                + usedPerAllocatedMemory +fieldDelimiter
                + usedPerAllocatedCPU +fieldDelimiter
                +totalFileBytesRead+ fieldDelimiter
                +totalFileBytesWritten+ fieldDelimiter
                +totalFileReadOps+ fieldDelimiter
                +totalFileLargeReadOps+ fieldDelimiter
                +totalFileWriteOps+ fieldDelimiter
                +totalHDFSBytesRead+ fieldDelimiter
                +totalHDFSBytesWritten+ fieldDelimiter
                +totalHDFSReadOps+ fieldDelimiter
                +totalHDFSLargeReadOps+ fieldDelimiter
                +totalHDFSWriteOps+ fieldDelimiter
                +actionId+ fieldDelimiter
                +workflowId+ fieldDelimiter
                +timestamp;
    }
}
