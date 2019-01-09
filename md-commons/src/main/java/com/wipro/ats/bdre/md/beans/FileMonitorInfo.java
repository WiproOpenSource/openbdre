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

package com.wipro.ats.bdre.md.beans;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by PR324290 on 12/22/2015.
 */
public class FileMonitorInfo {
    @NotNull
    @Size(min = 1, max = 45)
    private String processName;
    @NotNull
    @Size(min = 1, max = 256)
    private String processDescription;
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    @NotNull
    private Integer busDomainId;
    @NotNull
    @Size(min = 1, max = 2048)
    private String monitoredDirName;
    @NotNull
    @Size(min = 1, max = 255)
    private String filePattern;
    @NotNull
    @Size(min = 1, max = 45)
    private String deleteCopiedSource;
    @NotNull
    @Size(min = 1, max = 2048)
    private String hdfsUploadDir;
    @NotNull
    @Min(value = 100)
    @Max(value = 10000)
    private int sleepTime;

    public String getKerberosEnabled() {
        return kerberosEnabled;
    }

    public void setKerberosEnabled(String kerberosEnabled) {
        this.kerberosEnabled = kerberosEnabled;
    }

    private String kerberosEnabled;
    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessDescription() {
        return processDescription;
    }

    public void setProcessDescription(String processDescription) {
        this.processDescription = processDescription;
    }

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
    }
    public String getMonitoredDirName() {
        return monitoredDirName;
    }

    public void setMonitoredDirName(String monitoredDirName) {
        this.monitoredDirName = monitoredDirName;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getDeleteCopiedSource() {
        return deleteCopiedSource;
    }

    public void setDeleteCopiedSource(String deleteCopiedSource) {
        this.deleteCopiedSource = deleteCopiedSource;
    }

    public String getHdfsUploadDir() {
        return hdfsUploadDir;
    }

    public void setHdfsUploadDir(String hdfsUploadDir) {
        this.hdfsUploadDir = hdfsUploadDir;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }


}
