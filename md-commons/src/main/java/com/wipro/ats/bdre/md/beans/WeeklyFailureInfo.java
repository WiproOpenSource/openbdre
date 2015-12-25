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

import com.wipro.ats.bdre.annotation.PersistableParam;

/**
 * Created by IshitaParekh on 06-02-2015.
 */

/**
 * This class contains all the setter and getter methods for WeeklyFailure variables.
 */
public class WeeklyFailureInfo {
    private Integer counter;

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getInstanceExecId() {
        return instanceExecId;
    }

    public void setInstanceExecId(Long instanceExecId) {
        this.instanceExecId = instanceExecId;
    }

    @Override
    public String toString() {
        return " processId:" + processId + " instanceExecId:" + instanceExecId;
    }

    @PersistableParam(keyName = "process-id")
    private Long processId = 0L;
    @PersistableParam(keyName = "instance-exec-id")
    private Long instanceExecId = 0L;


}
