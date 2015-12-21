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

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Created by arijit on 1/9/15.
 */

/**
 * This class contains all the setter and getter methods for Process fields .
 */
public class ProcessTemplate {

    @Digits(fraction = 0, integer = 11)
    private Integer processTemplateId;

    public Date getAddTS() {
        return addTS;
    }

    public void setAddTS(Date addTS) {
        this.addTS = addTS;
    }

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
    }

    public String getTableAddTS() {
        return tableAddTS;
    }

    public void setTableAddTS(String tableAddTS) {
        this.tableAddTS = tableAddTS;
    }

    private String tableAddTS;

    private Integer workflowId;
    @NotNull
    @Size(min = 1, max = 256)
    private String description;

    private Date addTS;
    @NotNull
    @Size(max = 45)
    private String processName;
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    @NotNull
    private Integer busDomainId;
    @Min(value = 0)
    @Max(value = Integer.MAX_VALUE)
    @NotNull
    private Integer processTypeId;

    private Integer parentProcessId; // new Integer(0); //To avoid NPE during parent row processing
    @NotNull
    private Boolean canRecover;

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }


    private Integer processId;
    private String batchPattern;
    private String nextProcessTemplateId;
    private Integer page;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    private Integer counter;
    private String nextProcessIds;
    private Integer enqProcessId;

    private Boolean deleteFlag;

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    @Override
    public String toString() {
        return " processId:" + processId + " tableAddTS:" + tableAddTS +
                " workflowId:" + workflowId + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                "addTS:" + addTS + " processName:" + processName + " busDomainId:" + busDomainId +
                " processTypeId:" + processTypeId + " parentProcessId:" + parentProcessId + " processTemplateId:" + processTemplateId +
                " enqProcessId:" + enqProcessId + " nextProcessIds:" + nextProcessIds + " page:" + page;
    }

    public String getBatchPattern() {
        return batchPattern;
    }

    public void setBatchPattern(String batchPattern) {
        this.batchPattern = batchPattern;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Integer getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(Integer processTypeId) {
        this.processTypeId = processTypeId;
    }

    public Integer getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public Boolean getCanRecover() {
        return canRecover;
    }

    public void setCanRecover(Boolean canRecover) {
        this.canRecover = canRecover;
    }


    public Integer getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }


    public Integer getProcessTemplateId() {
        return processTemplateId;
    }

    public void setProcessTemplateId(Integer processTemplateId) {
        this.processTemplateId = processTemplateId;
    }

    public String getNextProcessTemplateId() {
        return nextProcessTemplateId;
    }

    public void setNextProcessTemplateId(String nextProcessTemplateId) {
        this.nextProcessTemplateId = nextProcessTemplateId;
    }

    public String getNextProcessIds() {
        return nextProcessIds;
    }

    public void setNextProcessIds(String nextProcessIds) {
        this.nextProcessIds = nextProcessIds;
    }

    public Integer getEnqProcessId() {
        return enqProcessId;
    }

    public void setEnqProcessId(Integer enqProcessId) {
        this.enqProcessId = enqProcessId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }
}
