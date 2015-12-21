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

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by kapil on 12-01-2015.
 */

/**
 * This class contains all the setter and getter methods for ExecStatus fields.
 */
public class ExecStatus {

    private Integer page;
    private Integer counter;
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer execStateId;
    @NotNull
    @Size(max = 45)
    private String description;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    @Override
    public String toString() {
        return " page:" + page + " execStateId:" + execStateId + " description:" + description.substring(0, Math.min(description.length(), 45));
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getExecStateId() {
        return execStateId;
    }

    public void setExecStateId(Integer execStateId) {
        this.execStateId = execStateId;
    }


}
