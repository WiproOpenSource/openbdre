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
 * Created by MI294210 on 13-05-2015.
 */

/**
 * This class contains all the setter and getter methods for LineageQueryType variables.
 */
public class LineageQueryTypeInfo {
    private Integer page;
    private Integer counter;
    private Integer queryTypeId;
    private String queryTypeName;

    @Override
    public String toString() {
        return " page:" + page + " queryTypeId:" + queryTypeId + " queryTypeName:" + queryTypeName;
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

    public Integer getQueryTypeId() {
        return queryTypeId;
    }

    public void setQueryTypeId(Integer queryTypeId) {
        this.queryTypeId = queryTypeId;
    }

    public String getQueryTypeName() {
        return queryTypeName;
    }

    public void setQueryTypeName(String queryTypeName) {
        this.queryTypeName = queryTypeName;
    }
}
