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
 * Created by kapil on 29-01-2015.
 */

/**
 * This class contains all the setter and getter methods for InstanceExec fields.
 */
public class InstanceExec {

    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Integer instanceExecId;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer processId;
    @NotNull
    private Date startTs;
    private Date endTs;

    @Override
    public String toString() {
        return " instanceExecId:" + instanceExecId + " processId:" + processId + " startTs:" + startTs + " endTs:" + endTs +
                " tableStartTs:" + tableStartTs + " tableEndTs:" + tableEndTs +
                " execState:" + execState + " page:" + page;
    }

    public String getTableStartTs() {
        return tableStartTs;
    }

    public void setTableStartTs(String tableStartTs) {
        this.tableStartTs = tableStartTs;
    }

    public String getTableEndTs() {
        return tableEndTs;
    }

    public void setTableEndTs(String tableEndTs) {
        this.tableEndTs = tableEndTs;
    }

    @NotNull
    private String tableStartTs;
    private String tableEndTs;
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private Integer execState;

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
    private Integer page;

    public Integer getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(Integer instanceExecId) {
        this.instanceExecId = instanceExecId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
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

    public Integer getExecState() {
        return execState;
    }

    public void setExecState(Integer execState) {
        this.execState = execState;
    }
}
