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

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by leela on 13-01-2015.
 */

/**
 * This class contains all the setter and getter methods for File fields.
 */
public class File {
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Long batchId;
    private Integer pageSize;
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer serverId;
    @NotNull
    @Size(max = 256)
    private String path;
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 20)
    private Long fileSize;
    private String tableCreationTS;
    private Date creationTS;
    private String fileHash;
    private Integer page;
    private Integer counter;

    @Override
    public String toString() {
        return " batchId:" + batchId + " serverId:" + serverId + " path:" + path + " fileHash:" + fileHash + " tableCreationTS:" + tableCreationTS +
                " fileSize:" + fileSize + " creationTS:" + creationTS + " page:" + page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTableCreationTS() {
        return tableCreationTS;
    }

    public void setTableCreationTS(String tableCreationTS) {
        this.tableCreationTS = tableCreationTS;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Date getCreationTS() {
        return creationTS;
    }

    public void setCreationTS(Date creationTS) {

        this.creationTS = creationTS;
    }
}
