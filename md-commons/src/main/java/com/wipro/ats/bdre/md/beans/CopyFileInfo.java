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
 * Created by arijit on 12/30/14.
 */

/**
 * This class contains all the setter and getter methods for CopyFile variables.
 */
public class CopyFileInfo {
    private Long sourceBatchId;
    private Long destBatchId;
    private Integer destServerId;
    private String destPrefix;

    public Long getDestBatchId() {
        return destBatchId;
    }

    public void setDestBatchId(Long destBatchId) {
        this.destBatchId = destBatchId;
    }

    public Long getSourceBatchId() {
        return sourceBatchId;
    }

    public void setSourceBatchId(Long sourceBatchId) {
        this.sourceBatchId = sourceBatchId;
    }

    public Integer getDestServerId() {
        return destServerId;
    }

    public void setDestServerId(Integer destServerId) {
        this.destServerId = destServerId;
    }

    public String getDestPrefix() {
        return destPrefix;
    }

    public void setDestPrefix(String destPrefix) {
        this.destPrefix = destPrefix;
    }
}
