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
 * Created by leela on 10-12-2014.
 */

/**
 * This class contains all the setter and getter methods for HaltJob variables.
 */
public class HaltJobInfo {
    @PersistableParam(keyName = "process-id")
    private Integer processId;
    @PersistableParam(keyName = "batch-marking")
    private String batchMarking;

    public String getBatchMarking() {
        return batchMarking;
    }

    public void setBatchMarking(String batchMarking) {
        this.batchMarking = batchMarking;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }
}
