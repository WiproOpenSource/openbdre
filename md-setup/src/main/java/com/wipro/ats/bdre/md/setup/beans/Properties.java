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
package com.wipro.ats.bdre.md.setup.beans;


public class Properties {


     private PropertiesId id;
     private String configGroup;
     private String propValue;
     private String description;

    public Properties() {
    }

    public Properties(PropertiesId id, String configGroup, String propValue, String description) {
       this.id = id;
       this.configGroup = configGroup;
       this.propValue = propValue;
       this.description = description;
    }

    public PropertiesId getId() {
        return this.id;
    }

    public void setId(PropertiesId id) {
        this.id = id;
    }

    public String getConfigGroup() {
        return this.configGroup;
    }
    
    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }
    
    public String getPropValue() {
        return this.propValue;
    }
    
    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

}


