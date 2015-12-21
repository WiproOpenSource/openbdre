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

package com.wipro.ats.bdre.md.beans;

import java.util.Date;

/**
 * Created by MI294210 on 13-05-2015.
 */

/**
 * This class contains all the setter and getter methods for LineageQuery variables.
 */
public class LineageQueryInfo {
    private Integer page;
    private Integer counter;

    private String queryId;
    private Date createTs;
    private String tableCreateTs;
    private Integer queryTypeId;
    private String queryString;
    private Integer processId;
    private Long instanceExecId;

    @Override
    public String toString() {
        return " page:" + page + " queryId:" + queryId + " createTs:" + createTs + " tableCreateTs:" + tableCreateTs + " queryTypeId:" + queryTypeId +
                " queryString:" + queryString + " processId:" + processId + " instanceExecId:" + instanceExecId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public String getTableCreateTs() {
        return tableCreateTs;
    }

    public void setTableCreateTs(String tableCreateTs) {
        this.tableCreateTs = tableCreateTs;
    }

    public Integer getQueryTypeId() {
        return queryTypeId;
    }

    public void setQueryTypeId(Integer queryTypeId) {
        this.queryTypeId = queryTypeId;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Long getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(Long instanceExecId) {
        this.instanceExecId = instanceExecId;
    }
}
