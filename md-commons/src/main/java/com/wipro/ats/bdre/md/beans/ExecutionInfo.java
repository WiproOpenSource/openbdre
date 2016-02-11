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
 * Created by KA294215 on 06-10-2015.
 */
public class ExecutionInfo {

    private int OSProcessId;
    private int processId;

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public int getOSProcessId() {
        return OSProcessId;
    }

    public void setOSProcessId(int OSProcessId) {
        this.OSProcessId = OSProcessId;
    }

    @Override
    public String toString() {
        return " processId:" + processId + " OSProcessId:" + OSProcessId;
    }

}
