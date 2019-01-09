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
 * This class is used to store properties from properties table.
 * And make them persistable.
 */

/**
 * This class contains all the setter and getter methods for ProcessDependency variables.
 */
public class ProcessDependencyInfo {
    private Integer processId;
    private String description;
    private Timestamp addTS;
    private String processName;
    private Integer busDomainId;
    private Integer processTypeId;
    private Integer parentProcessId = new Integer(0); //To avoid NPE during parent row processing
    private Boolean canRecover;
    private String enqProcessId;
    private String nextProcessIds;
    private String batchPattern;
    private String rowType;

    @Override
    public String toString() {
        return " processName:" + processName + " addTS:" + addTS + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                " busDomainId:" + busDomainId + " processTypeId:" + processTypeId + " parentProcessId:" + parentProcessId +
                " canRecover:" + canRecover + " enqProcessId:" + enqProcessId + " nextProcessIds:" + nextProcessIds + " batchPattern:" + batchPattern +
                " rowType:" + rowType;
    }

    public Timestamp getAddTS() {
        return addTS;
    }

    public void setAddTS(Timestamp addTS) {
        this.addTS = addTS;
    }

    public String getRowType() {
        return rowType;
    }

    public void setRowType(String rowType) {
        this.rowType = rowType;
    }

    public String getBatchPattern() {
        return batchPattern;
    }

    public void setBatchPattern(String batchPattern) {
        this.batchPattern = batchPattern;
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

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
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

}
