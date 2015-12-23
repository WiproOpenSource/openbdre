package com.wipro.ats.bdre.md.beans;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by PR324290 on 12/22/2015.
 */
public class FileMonitorInfo {
    @NotNull
    @Size(min = 1, max = 45)
    private String monitoredDirName;
    @NotNull
    @Size(min = 1, max = 45)
    private String filePattern;
    @NotNull
    @Size(min = 1, max = 45)
    private String deleteCopiedSource;
    @NotNull
    @Size(min = 1, max = 45)
    private String hdfsUploadDir;
    @NotNull
    @Min(value = 100)
    @Max(value = 10000)
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
