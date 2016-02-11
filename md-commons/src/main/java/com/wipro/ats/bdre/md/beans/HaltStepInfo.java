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
 * Created by leela on 10-12-2014.
 */

/**
 * This class contains all the setter and getter methods for HaltStep variables.
 */
public class HaltStepInfo {
    @PersistableParam(keyName = "sub-process-id")
    private Integer subProcessId;

    public int getSubProcessId() {
        return subProcessId;
    }

    public void setSubProcessId(Integer subPid) {
        this.subProcessId = subPid;
    }
}
