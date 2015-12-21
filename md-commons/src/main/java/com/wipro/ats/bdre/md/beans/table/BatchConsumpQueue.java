/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Created by leela on 13-01-2015.
 */

/**
 * This class contains all the setter and getter methods for BatchConsumpQueue fields.
 */
public class BatchConsumpQueue {
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Long sourceBatchId;
    private Long targetBatchId;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Long queueId;
    private Date insertTs;
    private String tableInsertTS;
    private Integer sourceProcessId;
    private Date startTs;

    @Override
    public String toString() {
        return " sourceBatchId:" + sourceBatchId + " targetBatchId:" + targetBatchId + " sourceProcessId:" + sourceProcessId + " queueId:" + queueId + " insertTs:" + insertTs + " processId:" + processId + " page:" + page +
                " batchState:" + batchState + " batchMarking:" + batchMarking +
                " tableStartTS:" + tableStartTS + " tableEndTS:" + tableEndTS + " startTs:" + startTs + " endTs:" + endTs;
    }

    public String getTableStartTS() {
        return tableStartTS;
    }

    public void setTableStartTS(String tableStartTS) {
        this.tableStartTS = tableStartTS;
    }

    public String getTableInsertTS() {
        return tableInsertTS;
    }

    public void setTableInsertTS(String tableInsertTS) {
        this.tableInsertTS = tableInsertTS;
    }

    public String getTableEndTS() {
        return tableEndTS;
    }

    public void setTableEndTS(String tableEndTS) {
        this.tableEndTS = tableEndTS;
    }

    private String tableStartTS;
    private Date endTs;
    private String tableEndTS;
    @NotNull
    @Min(value = 0)
    @Digits(fraction = 0, integer = 11)
    private Integer batchState;
    private String batchMarking;
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer processId;
    private Integer page;


    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    private Integer counter;

    public Long getSourceBatchId() {
        return sourceBatchId;
    }

    public void setSourceBatchId(Long sourceBatchId) {
        this.sourceBatchId = sourceBatchId;
    }

    public Long getTargetBatchId() {
        return targetBatchId;
    }

    public void setTargetBatchId(Long targetBatchId) {
        this.targetBatchId = targetBatchId;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public Date getInsertTs() {
        return insertTs;
    }

    public void setInsertTs(Date insertTs) {
        this.insertTs = insertTs;
    }

    public Integer getSourceProcessId() {
        return sourceProcessId;
    }

    public void setSourceProcessId(Integer sourceProcessId) {
        this.sourceProcessId = sourceProcessId;
    }

    public Date getStartTs() {
        return startTs;
    }

    public void setStartTs(Date startTs) {
        this.startTs = startTs;
    }

    public Date getEndTs() {
        return endTs;
    }

    public void setEndTs(Date endTs) {
        this.endTs = endTs;
    }

    public Integer getBatchState() {
        return batchState;
    }

    public void setBatchState(Integer batchState) {
        this.batchState = batchState;
    }

    public String getBatchMarking() {
        return batchMarking;
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
}
