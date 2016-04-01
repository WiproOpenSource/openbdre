/*
 *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.wipro.ats.bdre.clustermigration.beans;

import com.wipro.ats.bdre.annotation.PersistableParam;

/**
 * Created by cloudera on 4/1/16.
 */
public class MigrationPreprocessorInfo {
    public String getSrcStgDb() {
        return srcStgDb;
    }

    public void setSrcStgDb(String srcStgDb) {
        this.srcStgDb = srcStgDb;
    }

    public String getSrcStgTable() {
        return srcStgTable;
    }

    public void setSrcStgTable(String srcStgTable) {
        this.srcStgTable = srcStgTable;
    }

    public String getStgAllPartCols() {
        return stgAllPartCols;
    }

    public void setStgAllPartCols(String stgAllPartCols) {
        this.stgAllPartCols = stgAllPartCols;
    }

    public String getSrcRegularCols() {
        return srcRegularCols;
    }

    public void setSrcRegularCols(String srcRegularCols) {
        this.srcRegularCols = srcRegularCols;
    }

    public String getSrcBPCols() {
        return srcBPCols;
    }

    public void setSrcBPCols(String srcBPCols) {
        this.srcBPCols = srcBPCols;
    }

    public String getSrcDb() {
        return srcDb;
    }

    public void setSrcDb(String srcDb) {
        this.srcDb = srcDb;
    }

    public String getSrcTable() {
        return srcTable;
    }

    public void setSrcTable(String srcTable) {
        this.srcTable = srcTable;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    public String getJtAddress() {
        return jtAddress;
    }

    public void setJtAddress(String jtAddress) {
        this.jtAddress = jtAddress;
    }

    public String getNnAddress() {
        return nnAddress;
    }

    public void setNnAddress(String nnAddress) {
        this.nnAddress = nnAddress;
    }

    public String getSrcStgTablePath() {
        return srcStgTablePath;
    }

    public void setSrcStgTablePath(String srcStgTablePath) {
        this.srcStgTablePath = srcStgTablePath;
    }

    public String getDestStgFolderPath() {
        return destStgFolderPath;
    }

    public void setDestStgFolderPath(String destStgFolderPath) {
        this.destStgFolderPath = destStgFolderPath;
    }

    public String getDestStgFolderContentPath() {
        return destStgFolderContentPath;
    }

    public void setDestStgFolderContentPath(String destStgFolderContentPath) {
        this.destStgFolderContentPath = destStgFolderContentPath;
    }

    public String getDestTablePath() {
        return destTablePath;
    }

    public void setDestTablePath(String destTablePath) {
        this.destTablePath = destTablePath;
    }

    public String getDestTable() {
        return destTable;
    }

    public void setDestTable(String destTable) {
        this.destTable = destTable;
    }

    public String getDestDb() {
        return destDb;
    }

    public void setDestDb(String destDb) {
        this.destDb = destDb;
    }

    @PersistableParam(keyName = "source-stg-db")
    private String srcStgDb;
    @PersistableParam(keyName = "source-stg-table")
    private String srcStgTable;
    @PersistableParam(keyName = "stg-all-part-cols")
    private String stgAllPartCols;
    @PersistableParam(keyName = "source-reg-cols")
    private String srcRegularCols;
    @PersistableParam(keyName = "source-bp-cols")
    private String srcBPCols;
    @PersistableParam(keyName = "source-db")
    private String srcDb;
    @PersistableParam(keyName = "source-table")
    private String srcTable;
    @PersistableParam(keyName = "filter-condition")
    private String filterCondition;
    @PersistableParam(keyName = "job-tracker-address")
    private String jtAddress;
    @PersistableParam(keyName = "name-node-address")
    private String nnAddress;
    @PersistableParam(keyName = "src-stg-tbl-path")
    private String srcStgTablePath;
    @PersistableParam(keyName = "dest-stg-folder-path")
    private String destStgFolderPath;
    @PersistableParam(keyName = "dest-stg-folder-content-path")
    private String destStgFolderContentPath;
    @PersistableParam(keyName = "dest-table-path")
    private String destTablePath;
    @PersistableParam(keyName = "dest-table")
    private String destTable;
    @PersistableParam(keyName = "dest-db")
    private String destDb;
}
