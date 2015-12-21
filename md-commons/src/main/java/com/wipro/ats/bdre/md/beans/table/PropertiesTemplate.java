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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by sr294224 on 7/14/2015.
 */
public class PropertiesTemplate {
    @NotNull
    @Digits(fraction = 0, integer = 11)
    private Integer processTemplateId;
    private Integer processId;
    @NotNull
    @Size(max = 10)
    private String configGroup;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @NotNull
    @Size(max = 128)
    @Pattern(regexp = "([0-z][\\.|_|-]?)+")
    private String key;
    @NotNull
    @Size(max = 2048)
    private String value;
    @NotNull
    @Size(max = 1028)
    private String description;
    private Integer counter;
    private Integer page;
    private Integer parentProcessTemplateId;
    private Integer subProcessTemplateId;

    @Override
    public String toString() {
        return " processId:" + processId + " processTemplateId:" + processTemplateId + " configGroup:" + configGroup + " key:" + key + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                " page:" + page + " parentProcessTemplateId:" + parentProcessTemplateId + " subProcessTemplateId:" + subProcessTemplateId;
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

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProcessTemplateId() {
        return processTemplateId;
    }

    public void setProcessTemplateId(Integer processTemplateId) {
        this.processTemplateId = processTemplateId;
    }

    public Integer getParentProcessTemplateId() {
        return parentProcessTemplateId;
    }

    public void setParentProcessTemplateId(Integer parentProcessTemplateId) {
        this.parentProcessTemplateId = parentProcessTemplateId;
    }

    public Integer getSubProcessTemplateId() {
        return subProcessTemplateId;
    }

    public void setSubProcessTemplateId(Integer subProcessTemplateId) {
        this.subProcessTemplateId = subProcessTemplateId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }
}
