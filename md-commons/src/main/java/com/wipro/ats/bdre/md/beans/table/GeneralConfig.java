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


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
/**
 * Created by MI294210 on 9/9/2015.
 */

/**
 * This class contains all the setter and getter methods for GeneralConfig fields.
 */
public class GeneralConfig {
    @NotNull
    @Size(max = 128)
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
    @Size(max = 2048)
    private String value;
    @NotNull
    @Size(max = 1028)
    private String description;
    private Integer required;
    private String defaultVal;
    private Integer counter;
    private Integer page;
    @NotNull
    @Size(max = 20)
    private String type;
    private Boolean enabled;


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

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getDefaultVal() {

        if (defaultVal == null) {
            return "";
        } else {
            return defaultVal;
        }
    }

    public void setDefaultVal(String defaultVal) {

        if (defaultVal == null) {
            this.defaultVal = "";
        } else {
            this.defaultVal = defaultVal;
        }
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return " configGroup:" + configGroup + " key:" + key + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                " value:" + value + " type:" + type + " defaultVal:" + defaultVal;
    }


}
