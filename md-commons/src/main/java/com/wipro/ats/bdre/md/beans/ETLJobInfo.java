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
 * Created by arijit on 1/21/15.
 */

/**
 * This class contains all the setter and getter methods for ETLJob variables.
 */
public class ETLJobInfo {

    private Integer page;
    private String uuid;
    private Integer busDomainId;
    private String processName;
    private String description;
    private Long serialNumber;
    private String baseTableName;
    private String rawTableName;
    private String rawViewName;
    private String baseDBName;
    private String rawDBName;
    private String baseTableDDL;
    private String columnInfo;
    private String serdeProperties;
    private String tableProperties;
    private Integer inputFormat;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return " page:" + page + " uuid:" + uuid + " busDomainId:" + busDomainId + " processName:" + processName + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                " serialNumber:" + serialNumber + " baseTableName:" + baseTableName + " rawTableName:" + rawTableName + " rawViewName:" + rawViewName +
                " baseDBName:" + baseDBName + " rawDBName:" + rawDBName + " baseTableDDL:" + baseTableDDL + " columnInfo:" + columnInfo +
                " serdeProperties:" + serdeProperties + " tableProperties:" + tableProperties + " inputFormat:" + inputFormat +
                " processId:" + processId + " rawTableDDL:" + rawTableDDL + " rawViewDDL:" + rawViewDDL + " rawPartitionCol:" + rawPartitionCol
                + " dropRaw:" + dropRaw + " enqId:" + enqId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    private Integer processId;


    public boolean isDropRaw() {
        return dropRaw;
    }

    public void setDropRaw(boolean dropRaw) {
        this.dropRaw = dropRaw;
    }

    public void setEnqId(Integer enqId) {
        this.enqId = enqId;
    }

    public Integer getEnqId() {
        return enqId;
    }

    private String rawTableDDL;
    private String rawViewDDL;
    private String rawPartitionCol;
    private boolean dropRaw = false;
    private Integer enqId;

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    private Integer counter;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBaseTableName() {
        return baseTableName;
    }

    public void setBaseTableName(String baseTableName) {
        this.baseTableName = baseTableName;
    }

    public String getRawTableName() {
        return rawTableName;
    }

    public void setRawTableName(String rawTableName) {
        this.rawTableName = rawTableName;
    }

    public String getRawViewName() {
        return rawViewName;
    }

    public void setRawViewName(String rawViewName) {
        this.rawViewName = rawViewName;
    }

    public String getBaseDBName() {
        return baseDBName;
    }

    public void setBaseDBName(String baseDBName) {
        this.baseDBName = baseDBName;
    }

    public String getRawDBName() {
        return rawDBName;
    }

    public void setRawDBName(String rawDBName) {
        this.rawDBName = rawDBName;
    }

    public String getBaseTableDDL() {
        return baseTableDDL;
    }

    public void setBaseTableDDL(String baseTableDDL) {
        this.baseTableDDL = baseTableDDL;
    }

    public String getRawTableDDL() {
        return rawTableDDL;
    }

    public void setRawTableDDL(String rawTableDDL) {
        this.rawTableDDL = rawTableDDL;
    }

    public String getRawViewDDL() {
        return rawViewDDL;
    }

    public void setRawViewDDL(String rawViewDDL) {
        this.rawViewDDL = rawViewDDL;
    }

    public String getRawPartitionCol() {
        return rawPartitionCol;
    }

    public void setRawPartitionCol(String rawPartitionCol) {
        this.rawPartitionCol = rawPartitionCol;
    }


    public String getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(String columnInfo) {
        this.columnInfo = columnInfo;
    }

    public String getSerdeProperties() {
        return serdeProperties;
    }

    public void setSerdeProperties(String serdeProperties) {
        this.serdeProperties = serdeProperties;
    }

    public String getTableProperties() {
        return tableProperties;
    }

    public void setTableProperties(String tableProperties) {
        this.tableProperties = tableProperties;
    }

    public Integer getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(Integer inputFormat) {
        this.inputFormat = inputFormat;
    }
}

