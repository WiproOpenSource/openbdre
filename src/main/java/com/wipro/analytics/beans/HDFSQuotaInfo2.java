package com.wipro.analytics.beans;

import com.wipro.analytics.fetchers.DataFetcherMain;

import java.sql.Timestamp;

/**
 * Created by cloudera on 4/8/17.
 */
public class HDFSQuotaInfo2 {
    private String parentDir;
    private String subDir;
    private long parentNameQuota;
    private long parentDirNumFiles;
    private long subdirNumfiles;
    private double parentSpaceQuota;
    private double parentDirSize;
    private double subdirSize;
    private Timestamp timestamp;


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    public String getSubDir() {
        return subDir;
    }

    public void setSubDir(String subDir) {
        this.subDir = subDir;
    }

    public long getParentNameQuota() {
        return parentNameQuota;
    }

    public void setParentNameQuota(long parentNameQuota) {
        this.parentNameQuota = parentNameQuota;
    }

    public long getParentDirNumFiles() {
        return parentDirNumFiles;
    }

    public void setParentDirNumFiles(long parentDirNumFiles) {
        this.parentDirNumFiles = parentDirNumFiles;
    }

    public long getSubdirNumfiles() {
        return subdirNumfiles;
    }

    public void setSubdirNumfiles(long subdirNumfiles) {
        this.subdirNumfiles = subdirNumfiles;
    }

    public double getParentSpaceQuota() {
        return parentSpaceQuota;
    }

    public void setParentSpaceQuota(double parentSpaceQuota) {
        this.parentSpaceQuota = parentSpaceQuota;
    }

    public double getParentDirSize() {
        return parentDirSize;
    }

    public void setParentDirSize(double parentDirSize) {
        this.parentDirSize = parentDirSize;
    }

    public double getSubdirSize() {
        return subdirSize;
    }

    public void setSubdirSize(double subdirSize) {
        this.subdirSize = subdirSize;
    }

    @Override
    public String toString(){
        String fieldDelimiter= DataFetcherMain.FILE_FIELD_SEPERATOR;

        return    parentDir + fieldDelimiter
                + subDir + fieldDelimiter
                + parentNameQuota + fieldDelimiter
                + parentDirNumFiles + fieldDelimiter
                + subdirNumfiles + fieldDelimiter
                + parentSpaceQuota + fieldDelimiter
                + parentDirSize+ fieldDelimiter
                + subdirSize+ fieldDelimiter
                + timestamp;

    }

}
