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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by leela on 10-12-2014.
 */

/**
 * This class contains all the setter and getter methods for InitJob variables.
 */
public class InitJobInfo implements java.io.Serializable{

    @PersistableParam(keyName = "instance-exec-id")
    private Long instanceExecId;
    @PersistableParam(keyName = "last-recoverable-sp-id")
    private Integer lastRecoverableSpId;
    @PersistableParam(keyName = "target-batch-id")
    private Long targetBatchId;
    @PersistableParam(keyName = "min-batch-id-map")
    private Map<String, String> minBatchIdMap = new HashMap<String, String>();
    @PersistableParam(keyName = "max-batch-id-map")
    private Map<String, String> maxBatchIdMap = new HashMap<String, String>();
    @PersistableParam(keyName = "min-batch-marking-map")
    private Map<String, String> minBatchMarkingMap = new HashMap<String, String>();
    @PersistableParam(keyName = "max-batch-marking-map")
    private Map<String, String> maxBatchMarkingMap = new HashMap<String, String>();
    @PersistableParam(keyName = "target-batch-marking")
    private Set<String> targetBatchMarkingSet = new HashSet<String>();
    @PersistableParam(keyName = "min-source-instance-exec-id-map")
    private Map<String, String> minSourceInstanceExecIdMap = new HashMap<String, String>();
    @PersistableParam(keyName = "max-source-instance-exec-id-map")
    private Map<String, String> maxSourceInstanceExecIdMap = new HashMap<String, String>();
    @PersistableParam(keyName = "file-list-map")
    private Map<String, String> fileListMap = new HashMap<String, String>();
    @PersistableParam(keyName = "batch-list-map")
    private Map<String, String> batchListMap = new HashMap<String, String>();

    public Long getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(Long instanceExecId) {
        this.instanceExecId = instanceExecId;
    }

    public Map<String, String> getMinSourceInstanceExecIdMap() {
        return minSourceInstanceExecIdMap;
    }

    public void setMinSourceInstanceExecIdMap(Map<String, String> minSourceInstanceExecIdMap) {
        this.minSourceInstanceExecIdMap = minSourceInstanceExecIdMap;
    }

    public Map<String, String> getMaxSourceInstanceExecIdMap() {
        return maxSourceInstanceExecIdMap;
    }

    public void setMaxSourceInstanceExecIdMap(Map<String, String> maxSourceInstanceExecIdMap) {
        this.maxSourceInstanceExecIdMap = maxSourceInstanceExecIdMap;
    }

    public Map<String, String> getFileListMap() {
        return fileListMap;
    }

    public void setFileListMap(Map<String, String> fileListMap) {
        this.fileListMap = fileListMap;
    }

    public Map<String, String> getMaxBatchMarkingMap() {
        return maxBatchMarkingMap;
    }

    public void setMaxBatchMarkingMap(Map<String, String> maxBatchMarkingMap) {
        this.maxBatchMarkingMap = maxBatchMarkingMap;
    }

    public Map<String, String> getMinBatchIdMap() {
        return minBatchIdMap;
    }

    public void setMinBatchIdMap(Map<String, String> minBatchIdMap) {
        this.minBatchIdMap = minBatchIdMap;
    }


    public Map<String, String> getMaxBatchIdMap() {
        return maxBatchIdMap;
    }

    public void setMaxBatchIdMap(Map<String, String> maxBatchIdMap) {
        this.maxBatchIdMap = maxBatchIdMap;
    }

    public Map<String, String> getMinBatchMarkingMap() {
        return minBatchMarkingMap;
    }

    public void setMinBatchMarkingMap(Map<String, String> minBatchMarkingMap) {
        this.minBatchMarkingMap = minBatchMarkingMap;
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


    public Set<String> getTargetBatchMarkingSet() {
        return targetBatchMarkingSet;
    }

    public void setTargetBatchMarkingSet(Set<String> targetBatchMarkingSet) {
        this.targetBatchMarkingSet = targetBatchMarkingSet;
    }

    public Map<String, String> getBatchListMap() {
        return batchListMap;
    }

    public void setBatchListMap(Map<String, String> batchListMap) {
        this.batchListMap = batchListMap;
    }
}



