/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.beans;

/**
 * Created by arijit on 12/30/14.
 */

/**
 * This class contains all the setter and getter methods for File variables.
 */
public class FileInfo {
    private String serverName;
    private String serverIP;
    private String filePath;
    private String fileHash;
    private Long fileSize;
    private String serverType;
    private String username;
    private String password;
    private String sshPrivateKey;
    private Long batchId;
    private Long minBatch;
    private Long maxBatch;
    private String serverMetaInfo;

    @Override
    public String toString() {
        return "serverName=" + serverName + "\n" +
                "serverIP=" + serverIP + "\n" +
                "filePath=" + filePath + "\n" +
                "fileHash=" + fileHash + "\n" +
                "fileSize=" + fileSize + "\n" +
                "serverType=" + serverType + "\n" +
                "username=" + username + "\n" +
                "password=" + "****" + "\n" +
                "sshPrivateKey=" + "****" + "\n" +
                "batch_id=" + batchId + "\n" +
                "minBatch=" + minBatch + "\n" +
                "maxBatch=" + maxBatch;
    }

    public Long getMinBatch() {
        return minBatch;
    }

    public void setMinBatch(Long minBatch) {
        this.minBatch = minBatch;
    }

    public Long getMaxBatch() {
        return maxBatch;
    }

    public void setMaxBatch(Long maxBatch) {
        this.maxBatch = maxBatch;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSshPrivateKey() {
        return sshPrivateKey;
    }

    public void setSshPrivateKey(String sshPrivateKey) {
        this.sshPrivateKey = sshPrivateKey;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getServerMetaInfo() {
        return serverMetaInfo;
    }

    public void setServerMetaInfo(String serverMetaInfo) {
        this.serverMetaInfo = serverMetaInfo;
    }
}
