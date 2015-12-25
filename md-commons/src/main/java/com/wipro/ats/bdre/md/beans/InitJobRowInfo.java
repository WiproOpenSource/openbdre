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
 * Created by leela on 17-12-2014.
 */

/**
 * This class contains all the setter and getter methods for InitJobRow variables.
 */
public class InitJobRowInfo {

    private Integer maxBatch;
    private Long sourceBatchId;
    private Integer lastRecoverableSpId;
    private Long targetBatchId;
    private Long instanceExecId;
    private String batchMarking;
    private Integer processId;
    private Long sourceInstanceExecId;
    private String fileList;

    public String getFileList() {
        return fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }

    public Long getSourceBatchId() {
        return sourceBatchId;
    }

    public void setSourceBatchId(Long sourceBatchId) {
        this.sourceBatchId = sourceBatchId;
    }

    public Integer getLastRecoverableSpId() {
        return lastRecoverableSpId;
    }

    public void setLastRecoverableSpId(Integer lastRecoverableSpId) {
        this.lastRecoverableSpId = lastRecoverableSpId;
    }


    public Long getTargetBatchId() {
        return targetBatchId;
    }

    public void setTargetBatchId(Long targetBatchId) {
        this.targetBatchId = targetBatchId;
    }


    public String getBatchMarking() {
        return batchMarking;
    }

    public Long getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(Long instanceExecId) {
        this.instanceExecId = instanceExecId;
    }

    public Long getSourceInstanceExecId() {
        return sourceInstanceExecId;
    }

    public void setSourceInstanceExecId(Long sourceInstanceExecId) {
        this.sourceInstanceExecId = sourceInstanceExecId;
    }

    public void setBatchMarking(String batchMarking) {
        this.batchMarking = batchMarking;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getMaxBatch() {
        return maxBatch;
    }

    public void setMaxBatch(Integer maxBatch) {
        this.maxBatch = maxBatch;
    }

}
