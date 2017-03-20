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
    private long usedMemory;
    private long timeSpentMaps;
    private long timeSpentReducers;
    private long timeSpentTotal;
    private Timestamp timestamp;

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

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long used_memory) {
        this.usedMemory = used_memory;
    }

    public long getTimeSpentMaps() {
        return timeSpentMaps;
    }

    public void setTimeSpentMaps(long timeSpentMaps) {
        this.timeSpentMaps = timeSpentMaps;
    }

    public long getTimeSpentReducers() {
        return timeSpentReducers;
    }

    public void setTimeSpentReducers(long timeSpentReducers) {
        this.timeSpentReducers = timeSpentReducers;
    }

    public long getTimeSpentTotal() {
        return timeSpentTotal;
    }

    public void setTimeSpentTotal(long timeSpentTotal) {
        this.timeSpentTotal = timeSpentTotal;
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
                +usedMemory+ fieldDelimiter
                +timeSpentMaps+ fieldDelimiter
                +timeSpentReducers+ fieldDelimiter
                +timeSpentTotal+ fieldDelimiter
                + timestamp;
    }
}
