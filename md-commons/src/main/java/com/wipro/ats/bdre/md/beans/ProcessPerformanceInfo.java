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

import com.wipro.ats.bdre.annotation.PersistableParam;

/**
 * Created by IshitaParekh on 29-01-2015.
 */

/**
 * This class contains all the setter and getter methods for ProcessPerformance variables.
 */
public class ProcessPerformanceInfo {


    public Long getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(Long instanceExecId) {
        this.instanceExecId = instanceExecId;
    }

    public Integer getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(Integer durationInSec) {
        this.durationInSec = durationInSec;
    }


    public Integer getTimeInMin() {
        return timeInMin;
    }

    public void setTimeInMin(Integer timeInMin) {
        this.timeInMin = timeInMin;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    @Override
    public String toString() {
        return " timeInMin:" + timeInMin + " processId:" + processId + " durationInSec:" + durationInSec + " instanceExecId:" + instanceExecId;
    }

    @PersistableParam(keyName = "input-minutes")
    private Integer timeInMin = 0;
    @PersistableParam(keyName = "process-id")
    private Long processId = 0L;
    @PersistableParam(keyName = "process-duration")
    private Integer durationInSec = 0;
    @PersistableParam(keyName = "instance-exec-id")
    private Long instanceExecId = 0L;


}
