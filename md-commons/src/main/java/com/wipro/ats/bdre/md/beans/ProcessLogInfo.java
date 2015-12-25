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

import java.util.Date;

/**
 * Created by IshitaParekh on 10-03-2015.
 */

/**
 * This class contains all the setter and getter methods for ProcessLog variables.
 */
public class ProcessLogInfo {


    private Integer logId;
    private Integer processId;
    private Integer counter;
    private Integer parentProcessId;
    private String logCategory;
    private String message;
    private String messageId;
    private Long instanceRef;
    private Date addTs;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return " parentProcessId:" + parentProcessId + " logCategory:" + logCategory + " message:" + message + " messageId:" + messageId +
                " instanceRef:" + instanceRef + " addTs:" + addTs + " logId:" + logId + " processId:" + processId + " page:" + page;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    private Integer page;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getLogCategory() {
        return logCategory;
    }

    public void setLogCategory(String logCategory) {
        this.logCategory = logCategory;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Date getAddTs() {
        return addTs;
    }

    public void setAddTs(Date addTs) {
        this.addTs = addTs;
    }


    public Long getInstanceRef() {
        return instanceRef;
    }

    public void setInstanceRef(Long instanceRef) {
        this.instanceRef = instanceRef;
    }


    public ProcessLogInfo() {
        this.logId = logId;
        this.processId = processId;
        this.logCategory = logCategory;
        this.message = message;
        this.messageId = messageId;
        this.instanceRef = instanceRef;
        this.addTs = addTs;
    }
}
