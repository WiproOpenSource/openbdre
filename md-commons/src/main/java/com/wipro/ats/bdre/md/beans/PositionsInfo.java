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

import java.util.List;

/**
 * Created by SH324337 on 12/17/2015.
 */
public class PositionsInfo {

    private Integer processId;
    private Integer parentProcessId;
    private Integer xPos;
    private Integer yPos;
    private Integer level;
    private transient List<PositionsInfo> children;

    public List<PositionsInfo> kids() {
        return children;
    }

    public void setChildren(List<PositionsInfo> children) {
        this.children = children;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }


    public Integer getProcessId() {
        return processId;
    }


    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }

    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

}

