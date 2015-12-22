package com.wipro.ats.bdre.md.beans;

import javax.validation.constraints.NotNull;

/**
 * Created by PR324290 on 12/22/2015.
 */
public class FileMonitorInfo {
    @NotNull
    private String monitoredDirName;
    @NotNull
    private String filePattern;
    @NotNull
    private String deleteCopiedSource;
    @NotNull
    private String hdfsUploadDir;
    @NotNull
    private int sleepTime;
    public String getMonitoredDirName() {
        return monitoredDirName;
    }

    public void setMonitoredDirName(String monitoredDirName) {
        this.monitoredDirName = monitoredDirName;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getDeleteCopiedSource() {
        return deleteCopiedSource;
    }

    public void setDeleteCopiedSource(String deleteCopiedSource) {
        this.deleteCopiedSource = deleteCopiedSource;
    }

    public String getHdfsUploadDir() {
        return hdfsUploadDir;
    }

    public void setHdfsUploadDir(String hdfsUploadDir) {
        this.hdfsUploadDir = hdfsUploadDir;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }


}
