package com.wipro.analytics.beans;

import com.wipro.analytics.fetchers.DataFetcherMain;

import java.sql.Timestamp;

/**
 * Created by cloudera on 4/3/17.
 */
public class HDFSQuotaInfo {

    private long quota;
    private long spaceQuota;
    private long numFiles;
    private long spaceConsumed;
    private String hdfsPath;
    private boolean isMonitorDirectory;
    private String parentMonitorDirectory;
    private Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMonitorDirectory() {
        return isMonitorDirectory;
    }

    public void setMonitorDirectory(boolean monitorDirectory) {
        isMonitorDirectory = monitorDirectory;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public String getParentMonitorDirectory() {
        return parentMonitorDirectory;
    }

    public void setParentMonitorDirectory(String parentMonitorDirectory) {
        this.parentMonitorDirectory = parentMonitorDirectory;
    }
    public long getQuota() {
        return quota;
    }

    public void setQuota(long quota) {
        this.quota = quota;
    }

    public long getSpaceQuota() {
        return spaceQuota;
    }

    public void setSpaceQuota(long spaceQuota) {
        this.spaceQuota = spaceQuota;
    }

    public long getNumFiles() {
        return numFiles;
    }

    public void setNumFiles(long numFiles) {
        this.numFiles = numFiles;
    }

    public long getSpaceConsumed() {
        return spaceConsumed;
    }

    public void setSpaceConsumed(long spaceConsumed) {
        this.spaceConsumed = spaceConsumed;
    }

    @Override
    public String toString(){
        String fieldDelimiter= DataFetcherMain.FILE_FIELD_SEPERATOR;

        return    hdfsPath + fieldDelimiter
                + quota + fieldDelimiter
                + numFiles + fieldDelimiter
                + spaceQuota + fieldDelimiter
                + spaceConsumed + fieldDelimiter
                + isMonitorDirectory + fieldDelimiter
                + parentMonitorDirectory+ fieldDelimiter
                + timestamp;

    }

}
