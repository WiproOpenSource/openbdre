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

/**
 * Created by MI294210 on 9/10/2015.
 */

import com.wipro.ats.bdre.md.beans.table.Process;

import java.util.Date;
import java.util.List;

/**
 * This class contains all the setter and getter methods for ProcessAncestors variables.
 */
public class ProcessAncestorsInfo {
    private Integer processId;
    private Date editTs;
    private Date deployInsertTs;
    private Date deploySuccessTs;
    private Date deployFailTs;
    private String tableEditTs;
    private String tableDeployInsertTs;
    private String tableDeploySuccessTs;
    private String tableDeployFailTs;
    private Integer deployId;
    private List<Process> upstreamProcess;
    private List<ProcessAncestorsInfo> processAncestorsInfoList;

    @Override
    public String toString() {
        return " processId:" + processId + " editTS:" + editTs + " deployInsertTs:" + deployInsertTs +
                " deploySuccessTs:" + deploySuccessTs + " deployFailTs:" + deployFailTs + " deployId:" + deployId + " upstreamProcess:" + upstreamProcess +
                " processAncestorsInfoList:" + processAncestorsInfoList;
    }

    public String getTableEditTs() {
        return tableEditTs;
    }

    public void setTableEditTs(String tableEditTs) {
        this.tableEditTs = tableEditTs;
    }

    public String getTableDeployInsertTs() {
        return tableDeployInsertTs;
    }

    public void setTableDeployInsertTs(String tableDeployInsertTs) {
        this.tableDeployInsertTs = tableDeployInsertTs;
    }

    public String getTableDeploySuccessTs() {
        return tableDeploySuccessTs;
    }

    public void setTableDeploySuccessTs(String tableDeploySuccessTs) {
        this.tableDeploySuccessTs = tableDeploySuccessTs;
    }

    public List<Process> getUpstreamProcess() {
        return upstreamProcess;
    }

    public void setUpstreamProcess(List<Process> upstreamProcess) {
        this.upstreamProcess = upstreamProcess;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Date getEditTs() {
        return editTs;
    }

    public void setEditTs(Date editTs) {
        this.editTs = editTs;
    }

    public Date getDeployInsertTs() {
        return deployInsertTs;
    }

    public void setDeployInsertTs(Date deployInsertTs) {
        this.deployInsertTs = deployInsertTs;
    }

    public Date getDeploySuccessTs() {
        return deploySuccessTs;
    }

    public void setDeploySuccessTs(Date deploySuccessTs) {
        this.deploySuccessTs = deploySuccessTs;
    }

    public Integer getDeployId() {
        return deployId;
    }

    public void setDeployId(Integer deployId) {
        this.deployId = deployId;
    }

    public Date getDeployFailTs() {
        return deployFailTs;
    }

    public void setDeployFailTs(Date deployFailTs) {
        this.deployFailTs = deployFailTs;
    }

    public String getTableDeployFailTs() {
        return tableDeployFailTs;
    }

    public void setTableDeployFailTs(String tableDeployFailTs) {
        this.tableDeployFailTs = tableDeployFailTs;
    }

    public List<ProcessAncestorsInfo> getProcessAncestorsInfoList() {
        return processAncestorsInfoList;
    }

    public void setProcessAncestorsInfoList(List<ProcessAncestorsInfo> processAncestorsInfoList) {
        this.processAncestorsInfoList = processAncestorsInfoList;
    }
}
