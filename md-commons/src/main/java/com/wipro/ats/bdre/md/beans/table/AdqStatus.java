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

/**
 * Created by SU324335 on 3/8/2016.
 */
public class AdqStatus {
    private Short adqState;
    private String description;
    private Integer pageSize;
    private Integer page;
    private Integer counter;

    public AdqStatus() {
    }

    public AdqStatus(Short adqState, String description) {
        this.adqState = adqState;
        this.description = description;
    }

    @Override
    public String toString() {
        return "AdqStatus{" +
                "adqState=" + adqState +
                ", description='" + description + '\'' +
                ", pageSize=" + pageSize +
                ", page=" + page +
                ", counter=" + counter +
                '}';
    }

    public Short getAdqState() {
        return adqState;
    }

    public void setAdqState(Short adqState) {
        this.adqState = adqState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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


}
