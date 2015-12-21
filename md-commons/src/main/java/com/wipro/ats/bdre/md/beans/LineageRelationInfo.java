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

/**
 * Created by MI294210 on 12-05-2015.
 */

/**
 * This class contains all the setter and getter methods for LineageRelation variables.
 */
public class LineageRelationInfo {
    private Integer page;
    private Integer counter;
    private String relationId;
    private String srcNodeId;
    private String targetNodeId;
    private String queryId;
    private String dotString;

    @Override
    public String toString() {
        return " page:" + page + " relationId:" + relationId + " srcNodeId:" + srcNodeId + " targetNodeId:" + targetNodeId +
                " queryId:" + queryId +
                " dotString:" + dotString;
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

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getSrcNodeId() {
        return srcNodeId;
    }

    public void setSrcNodeId(String srcNodeId) {
        this.srcNodeId = srcNodeId;
    }

    public String getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(String targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getDotString() {
        return dotString;
    }

    public void setDotString(String dotString) {
        this.dotString = dotString;
    }
}
