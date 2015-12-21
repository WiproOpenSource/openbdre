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

import com.wipro.ats.bdre.annotation.PersistableCollectionBean;
import com.wipro.ats.bdre.annotation.PersistableParam;

/**
 * This class is used to store properties from properties table.
 * And make them persistable.
 */

/**
 * This class contains all the setter and getter methods for GetProperties variables.
 */
@PersistableCollectionBean(qualifierField = "key")
public class GetPropertiesInfo {
    private Integer processId;
    private String configGroup;
    private String key;
    //Since this is simple key value pair thing, we don't need a qualifier. Making it empty.
    @PersistableParam(keyName = "")
    private String value;


    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
