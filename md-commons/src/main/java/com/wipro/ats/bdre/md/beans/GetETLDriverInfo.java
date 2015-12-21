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

import com.wipro.ats.bdre.annotation.PersistableParam;

/**
 * Created by leela on 11-12-2014.
 */

/**
 * This class contains all the setter and getter methods for GetETLDriver variables.
 */
public class GetETLDriverInfo {
    @PersistableParam(keyName = "min-batch-id")
    private Long minBatch;
    @PersistableParam(keyName = "max-batch-id")
    private Long maxBatch;
    @PersistableParam(keyName = "file-list")
    private String fileList;

    @Override
    public String toString() {
        return " minBatch:" + minBatch + " maxBatch:" + maxBatch + " fileList:" + fileList;
    }

    public Long getMinBatch() {
        return minBatch;
    }

    public void setMinBatch(Long minBatch) {
        this.minBatch = minBatch;
    }

    public Long getMaxBatch() {
        return maxBatch;
    }

    public void setMaxBatch(Long maxBatch) {
        this.maxBatch = maxBatch;
    }

    public String getFileList() {
        return fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }
}
