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
public class Adq {

    private Long adqId;
    private Integer processId;

    public Adq(){
    }

    public Adq(Long adqId, Integer processId, String username, String appDomain, String appName, Short adqState, Integer pageSize, Integer page, Integer counter) {
        this.adqId = adqId;
        this.processId = processId;
        this.username = username;
        this.appDomain = appDomain;
        this.appName = appName;
        this.adqState = adqState;
        this.pageSize = pageSize;
        this.page = page;
        this.counter = counter;
    }

    @Override
    public String toString() {
        return "Adq{" +
                "adqId=" + adqId +
                ", processId=" + processId +
                ", username='" + username + '\'' +
                ", appDomain='" + appDomain + '\'' +
                ", appName='" + appName + '\'' +
                ", adqState=" + adqState +
                ", pageSize=" + pageSize +
                ", page=" + page +
                ", counter=" + counter +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAdqId() {
        return adqId;
    }

    public void setAdqId(Long adqId) {
        this.adqId = adqId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getAppDomain() {
        return appDomain;
    }

    public void setAppDomain(String appDomain) {
        this.appDomain = appDomain;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Short getAdqState() {
        return adqState;
    }

    public void setAdqState(Short adqState) {
        this.adqState = adqState;
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

    private String username;
    private String appDomain;
    private String appName;
    private Short adqState;
    private Integer pageSize;
    private Integer page;
    private Integer counter;
}
