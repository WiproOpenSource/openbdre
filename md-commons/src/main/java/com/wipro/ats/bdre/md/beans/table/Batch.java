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

package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.*;

/**
 * Created by leela on 13-01-2015.
 */

/**
 * This class contains all the setter and getter methods for Batch fields.
 */
public class Batch {
    private Integer limitId;
    private Integer counter;
    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Long batchId;
    private Integer pageSize;
    @NotNull
    @Pattern(regexp = "([0-z][' '|-]?)+")
    @Size(max = 45)
    private String batchType;
    private Integer page;
    private Long sourceInstanceExecId;

    @Override
    public String toString() {
        return " limitId:" + limitId + " batchId:" + batchId + " sourceInstanceExecId:" + sourceInstanceExecId + " page:" + page + " batchType:" + batchType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }


    public Long getSourceInstanceExecId() {
        return sourceInstanceExecId;
    }

    public void setSourceInstanceExecId(Long sourceInstanceExecId) {
        this.sourceInstanceExecId = sourceInstanceExecId;
    }

    public Integer getLimitId() {
        return limitId;
    }

    public void setLimitId(Integer limitId) {
        this.limitId = limitId;
    }


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }


    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }
}
