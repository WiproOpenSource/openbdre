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

import java.util.Date;

/**
 * Created by MI294210 on 12-05-2015.
 */

/**
 * This class contains all the setter and getter methods for LineageNode variables.
 */
public class LineageNodeInfo {
    private Integer page;
    private Integer counter;

    private String nodeId;
    private Integer nodeTypeId;
    private String containerNodeId;
    private Integer nodeOrder;
    private Date insertTs;
    private String tableInsertTs;
    private String tableUpdateTs;
    private Date updateTs;
    private String dotString;
    private String dotLabel;
    private String displayName;

    @Override
    public String toString() {
        return " page:" + page + " nodeId:" + nodeId + " nodeTypeId:" + nodeTypeId + " containerNodeId:" + containerNodeId + " nodeOrder:" + nodeOrder +
                " insertTs:" + insertTs + " tableInsertTs:" + tableInsertTs + " tableUpdateTs:" + tableUpdateTs +
                " updateTs:" + updateTs + " dotString:" + dotString + " dotLabel:" + dotLabel + " displayName:" + displayName;
    }

    public Date getInsertTs() {
        return insertTs;
    }

    public void setInsertTs(Date insertTs) {
        this.insertTs = insertTs;
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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getNodeTypeId() {
        return nodeTypeId;
    }

    public void setNodeTypeId(Integer nodeTypeId) {
        this.nodeTypeId = nodeTypeId;
    }

    public String getContainerNodeId() {
        return containerNodeId;
    }

    public void setContainerNodeId(String containerNodeId) {
        this.containerNodeId = containerNodeId;
    }

    public Integer getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(Integer nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public String getTableInsertTs() {
        return tableInsertTs;
    }

    public void setTableInsertTs(String tableInsertTs) {
        this.tableInsertTs = tableInsertTs;
    }

    public String getTableUpdateTs() {
        return tableUpdateTs;
    }

    public void setTableUpdateTs(String tableUpdateTs) {
        this.tableUpdateTs = tableUpdateTs;
    }

    public Date getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    public String getDotString() {
        return dotString;
    }

    public void setDotString(String dotString) {
        this.dotString = dotString;
    }

    public String getDotLabel() {
        return dotLabel;
    }

    public void setDotLabel(String dotLabel) {
        this.dotLabel = dotLabel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
