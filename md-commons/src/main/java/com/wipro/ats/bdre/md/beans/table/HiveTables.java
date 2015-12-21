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

import javax.validation.constraints.*;

/**
 * Created by kapil on 12-01-2015.
 */

/**
 * This class contains all the setter and getter methods for HiveTables fields.
 */
public class HiveTables {

    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Integer tableId;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @NotNull
    @Size(max = 256)
    private String comments;
    @NotNull
    @Size(max = 45)
    private String locationType;
    @NotNull
    @Pattern(regexp = "([0-z][_]?)+")
    @Size(max = 45)

    private String dbName;
    @NotNull
    @Pattern(regexp = "([0-z][_]?)+")
    @Size(max = 45)

    private String batchIdPartitionCol;
    @Pattern(regexp = "([0-z][_]?)+")
    @NotNull
    @Size(max = 45)

    private String tableName;
    @NotNull
    @Pattern(regexp = "([0-z][' ']?)+")
    @Size(max = 45)
    private String type;
    @NotNull
    @Size(max = 2048)

    private String ddl;
    private Integer page;
    private Integer counter;

    @Override
    public String toString() {
        return " tableId:" + tableId + " comments:" + comments + " locationType:" + locationType +
                " dbName:" + dbName + " batchIdPartitionCol:" + batchIdPartitionCol +
                " tableName:" + tableName + " type:" + type +
                " ddl:" + ddl + " page:" + page;
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

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBatchIdPartitionCol() {
        return batchIdPartitionCol;
    }

    public void setBatchIdPartitionCol(String batchIdPartitionCol) {
        this.batchIdPartitionCol = batchIdPartitionCol;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
