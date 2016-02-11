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

import com.wipro.ats.bdre.annotation.PersistableParam;

import java.sql.Timestamp;

/**
 * Created by leela on 10-12-2014.
 */

/**
 * This class contains all the setter and getter methods for RegisterFile variables.
 */
public class RegisterFileInfo {
    @PersistableParam(keyName = "sub-process-id")
    private Integer subProcessId;
    @PersistableParam(keyName = "server-id")
    private Integer serverId;
    @PersistableParam(keyName = "path")
    private String path;
    @PersistableParam(keyName = "file-size")
    private Long fileSize;
    @PersistableParam(keyName = "file-hash")
    private String fileHash;
    @PersistableParam(keyName = "creation-ts")
    private Timestamp creationTs;
    @PersistableParam(keyName = "batch-id")
    private Long batchId;
    @PersistableParam(keyName = "parent-process-id")
    private Integer parentProcessId;
    @PersistableParam(keyName = "batch-marking")
    private String batchMarking;

    @Override
    public String toString() {
        return " subProcessId:" + subProcessId + "serverId:" + serverId +
                " path:" + path + " fileSize:" + fileSize +
                " parentProcessId:" + parentProcessId + " fileHash:" + fileHash + " creationTs:" + creationTs +
                " batchId:" + batchId + " batchMarking:" + batchMarking;
    }

    public Integer getSubProcessId() {
        return subProcessId;
    }

    public void setSubProcessId(Integer subProcessId) {
        this.subProcessId = subProcessId;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }


    public Timestamp getCreationTs() {
        return creationTs;
    }

    public void setCreationTs(Timestamp creationTs) {
        this.creationTs = creationTs;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Integer getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public String getBatchMarking() {
        return batchMarking;
    }

    public void setBatchMarking(String batchMarking) {
        this.batchMarking = batchMarking;
    }
}
