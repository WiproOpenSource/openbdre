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
 * This class is used to store information regarding tables present in hive.
 * And make the variables persistable.
 */

/**
 * This class contains all the setter and getter methods for GetHiveTables variables.
 */
@PersistableCollectionBean(qualifierField = "tableId")
public class GetHiveTablesInfo {
    private Integer processId;
    @PersistableParam(keyName = "table_id")
    private Integer tableId;
    @PersistableParam(keyName = "comment")
    private String comment;
    @PersistableParam(keyName = "location_type")
    private String locationType;
    @PersistableParam(keyName = "dbname")
    private String dbName;
    @PersistableParam(keyName = "batch_id_partition_col")
    private String batchIdPartitionCol;
    @PersistableParam(keyName = "table_name")
    private String tableName;
    @PersistableParam(keyName = "type")
    private String type;
    @PersistableParam(keyName = "ddl")
    private String ddl;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getComments() {
        return comment;
    }

    public void setComments(String comments) {
        this.comment = comments;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getBatchIdPartitionCol() {
        return batchIdPartitionCol;
    }

    public void setBatchIdPartitionCol(String batchIdPartitionCol) {
        this.batchIdPartitionCol = batchIdPartitionCol;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
