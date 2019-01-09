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

import javax.validation.constraints.*;

/**
 * Created by leela on 13-01-2015.
 */

/**
 * This class contains all the setter and getter methods for Servers fields.
 */
public class Servers {
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private Integer serverId;
    @NotNull
    @Size(max = 45)
    @Pattern(regexp = "([0-z][-]?)+")
    private String serverType;
    private Integer pageSize;
    @NotNull
    @Size(max = 45)
    @Pattern(regexp = "([0-z][-]?[\\.]?)+")
    private String serverName;
    @Size(max = 45)
    private String serverMetaInfo;
    @Size(max = 45)
    @Pattern(regexp = "[0-z]+")
    private String loginUser;
    @Size(max = 45)
    private String loginPassword;
    @Size(max = 512)
    private String sshPrivateKey;
    @Size(max = 45)
    @Pattern(regexp = "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b")
    private String serverIp;
    private Integer page;
    private Integer counter;

    @Override
    public String toString() {
        return " serverId:" + serverId + " serverType:" + serverType + " serverName:" + serverName +
                " serverMetaInfo:" + serverMetaInfo + " loginUser:" + loginUser + " loginPassword:" + loginPassword +
                " sshPrivateKey:" + sshPrivateKey + " serverIp:" + serverIp +
                " page:" + page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerMetaInfo() {
        return serverMetaInfo;
    }

    public void setServerMetaInfo(String serverMetaInfo) {
        this.serverMetaInfo = serverMetaInfo;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getSshPrivateKey() {
        return sshPrivateKey;
    }

    public void setSshPrivateKey(String sshPrivateKey) {
        this.sshPrivateKey = sshPrivateKey;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }


}
