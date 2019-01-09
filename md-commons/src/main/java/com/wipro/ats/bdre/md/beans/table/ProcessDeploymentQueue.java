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
package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by MI294210 on 8/31/2015.
 */

/**
 * This class contains all the setter and getter methods for ProcessDeploymentQueue fields.
 */
public class ProcessDeploymentQueue {
    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Long deploymentId;
    private Integer pageSize;
    @NotNull
    @Digits(fraction = 0, integer = 11)
    private Integer processId;
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer deployStatusId;
    private Integer busDomainId;
    private Integer processTypeId;
    private String userName;
    private String deployScriptLocation;
    private Date insertTs;
    private Date startTs;
    private Date endTs;
    private String tableInsertTs;
    private String tableStartTs;
    private String tableEndTs;
    private Integer fetchNum;
    private Integer page;
    private Integer counter;

    @Override
    public String toString() {
        return "deploymentId:" + deploymentId + " deployStatusId:" + deployStatusId + " processId:" + processId + " startTs:" + startTs +
                " endTs:" + endTs + " insertTs" + insertTs + " busDomainId:" + busDomainId + " userName:" + userName +
                " processTypeId:" + processTypeId + " deployScriptLocation:" + deployScriptLocation + " page:" + page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(Long deploymentId) {
        this.deploymentId = deploymentId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getDeployStatusId() {
        return deployStatusId;
    }

    public void setDeployStatusId(Integer deployStatusId) {
        this.deployStatusId = deployStatusId;
    }

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(Integer processTypeId) {
        this.processTypeId = processTypeId;
    }

    public String getDeployScriptLocation() {
        return deployScriptLocation;
    }

    public void setDeployScriptLocation(String deployScriptLocation) {
        this.deployScriptLocation = deployScriptLocation;
    }

    public Date getInsertTs() {
        return insertTs;
    }

    public void setInsertTs(Date insertTs) {
        this.insertTs = insertTs;
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

    public String getTableInsertTs() {
        return tableInsertTs;
    }

    public void setTableInsertTs(String tableInsertTs) {
        this.tableInsertTs = tableInsertTs;
    }

    public String getTableEndTs() {
        return tableEndTs;
    }

    public void setTableEndTs(String tableEndTs) {
        this.tableEndTs = tableEndTs;
    }

    public String getTableStartTs() {
        return tableStartTs;
    }

    public void setTableStartTs(String tableStartTs) {
        this.tableStartTs = tableStartTs;
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

    public Integer getFetchNum() {
        return fetchNum;
    }

    public void setFetchNum(Integer fetchNum) {
        this.fetchNum = fetchNum;
    }

}
