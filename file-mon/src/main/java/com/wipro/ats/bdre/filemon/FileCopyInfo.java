package com.wipro.ats.bdre.filemon;

import org.apache.commons.vfs2.FileContent;

/**
 * Created by MO335755 on 12/22/2015.
 */
public class FileCopyInfo {
    private String fileName;
    private String subProcessId;
    private String serverId;
    private String srcLocation;
    private String dstLocation;
    private FileContent fileContent;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSubProcessId() {
        return subProcessId;
    }

    public void setSubProcessId(String subProcessId) {
        this.subProcessId = subProcessId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getSrcLocation() {
        return srcLocation;
    }

    public void setSrcLocation(String srcLocation) {
        this.srcLocation = srcLocation;
    }

    public String getDstLocation() {
        return dstLocation;
    }

    public void setDstLocation(String dstLocation) {
        this.dstLocation = dstLocation;
    }

    public FileContent getFileContent() {
        return fileContent;
    }

    public void setFileContent(FileContent fileContent) {
        this.fileContent = fileContent;
    }
}