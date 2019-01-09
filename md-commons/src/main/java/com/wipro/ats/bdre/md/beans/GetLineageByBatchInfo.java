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

import java.sql.Timestamp;

/**
 * Created by arijit on 1/27/15.
 */

/**
 * This class contains all the setter and getter methods for GetLineageByBatch variables.
 */
public class GetLineageByBatchInfo {
    private long targetBatchId;
    private long sourceBatchId;
    private long instanceExecId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String processName;
    private String processDesc;
    private long processId;
    private Integer execState;

    public Integer getExecState() {
        return execState;
    }

    public void setExecState(Integer execState) {
        this.execState = execState;
    }

    @Override
    public String toString() {
        return " targetBatchId:" + targetBatchId + " sourceBatchId:" + sourceBatchId + " instanceExecId:" + instanceExecId + " startTime:" + startTime +
                " endTime:" + endTime + " processName:" + processName + " processDesc:" + processDesc + " processId:" + processId;
    }

    public long getProcessId() {
        return processId;
    }

    public void setProcessId(long processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessDesc() {
        return processDesc;
    }

    public void setProcessDesc(String processDesc) {
        this.processDesc = processDesc;
    }

    public long getTargetBatchId() {
        return targetBatchId;
    }


    public void setTargetBatchId(long targetBatchId) {
        this.targetBatchId = targetBatchId;
    }

    public long getSourceBatchId() {
        return sourceBatchId;
    }

    public void setSourceBatchId(long sourceBatchId) {
        this.sourceBatchId = sourceBatchId;
    }

    public long getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(long instanceExecId) {
        this.instanceExecId = instanceExecId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
