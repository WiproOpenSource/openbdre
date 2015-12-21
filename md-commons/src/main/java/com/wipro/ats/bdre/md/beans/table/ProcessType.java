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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by kapil on 12-01-2015.
 */

/**
 * This class contains all the setter and getter methods for ProcessType fields.
 */
public class ProcessType {
    @NotNull
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private Integer processTypeId;

    public Integer getParentProcessTypeId() {
        return parentProcessTypeId;
    }

    public void setParentProcessTypeId(Integer parentProcessTypeId) {
        this.parentProcessTypeId = parentProcessTypeId;
    }

    private Integer parentProcessTypeId;
    private Integer page;
    private Integer counter;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    @Override
    public String toString() {
        return " processTypeId:" + processTypeId + " parentProcessTypeId:" + parentProcessTypeId + " page:" + page + " processTypeName:" + processTypeName;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(Integer processTypeId) {
        this.processTypeId = processTypeId;
    }

    public String getProcessTypeName() {
        return processTypeName;
    }

    public void setProcessTypeName(String processTypeName) {
        this.processTypeName = processTypeName;
    }

    @NotNull
    @Pattern(regexp = "([0-z][-]?[' ']?)+")
    private String processTypeName;
}
