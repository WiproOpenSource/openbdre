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
 * Created by arijit on 12/20/14.
 */

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * This class contains all the setter and getter methods for Process variables.
 */
public class ProcessInfo {
    private Integer processId;
    private String description;
    private String processName;
    private Integer busDomainId;
    private Integer processTypeId;
    //To avoid NPE during parent row processing
    private Integer parentProcessId = new Integer(0);
    private Boolean canRecover;
    private String enqProcessId;
    private String nextProcessIds;
    private String batchCutPattern;
    private Boolean deleteFlag;
    List<ProcessInfo> processInfos;
    List<LineageNodeInfo> lineageNodeInfo;
    private Long instanceExecId;
    private Date startTs;
    private Date endTs;
    @NotNull
    private String tableStartTs;
    private String tableEndTs;
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private Integer execState;
    private Integer counter;
    private Integer page;

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
    }

    public String getBatchCutPattern() {
        return batchCutPattern;
    }

    public void setBatchCutPattern(String batchCutPattern) {
        this.batchCutPattern = batchCutPattern;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public List<ProcessInfo> getProcessInfo() {
        return processInfos;
    }

    public void setProcessInfo(List<ProcessInfo> processInfos) {
        this.processInfos = processInfos;
    }

    public Integer getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }

    private Integer workflowId;

    /**
     * default constructor
     */
    public ProcessInfo() {
    }

    /**
     * This constructor sets the value of variables
     *
     * @param processId       id of process
     * @param description     what this process do
     * @param processName     name of the process
     * @param busDomainId     instance execution for which application
     * @param processTypeId   type of process
     * @param parentProcessId id of parent process
     * @param canRecover      whether this process is recoverable or not
     * @param enqProcessId    Id of parent process whose batch this process is enquing
     * @param nextProcessIds  ids of next processes
     */
    public ProcessInfo(Integer processId, String description, String processName, Integer busDomainId, Integer processTypeId, Integer parentProcessId, Boolean canRecover, String enqProcessId, String nextProcessIds) {
        this.processId = processId;
        this.description = description;
        this.processName = processName;
        this.busDomainId = busDomainId;
        this.processTypeId = processTypeId;
        this.parentProcessId = parentProcessId;
        this.canRecover = canRecover;
        this.enqProcessId = enqProcessId;
        this.nextProcessIds = nextProcessIds;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
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

    public Integer getApplicationId() {
        return busDomainId;
    }

    public void setApplicationId(Integer applicationId) {
        this.busDomainId = applicationId;
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

    public Boolean isCanRecover() {
        return canRecover;
    }

    public void setCanRecover(Boolean canRecover) {
        this.canRecover = canRecover;
    }

    public String getEnqProcessId() {
        return enqProcessId;
    }

    public void setEnqProcessId(String enqProcessId) {
        this.enqProcessId = enqProcessId;
    }


    public String getNextProcessIds() {
        return nextProcessIds;
    }

    public void setNextProcessIds(String nextProcessIds) {
        this.nextProcessIds = nextProcessIds;
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

    public Integer getExecState() {
        return execState;
    }

    public void setExecState(Integer execState) {
        this.execState = execState;
    }

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

    public Long getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(Long instanceExecId) {
        this.instanceExecId = instanceExecId;
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

    @Override
    public String toString() {
        return " processId:" + processId + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                " processName:" + processName + " busDomainId:" + busDomainId + " processTypeId:" + processTypeId +
                " parentProcessId:" + parentProcessId + " canRecover:" + canRecover + " enqProcessId:" + enqProcessId +
                " nextProcessIds:" + nextProcessIds + " workflowId:" + workflowId + "instanceExecId:" + instanceExecId +
                " execState:" + execState + " startTs:" + startTs + " endTs" + endTs;
    }
}
