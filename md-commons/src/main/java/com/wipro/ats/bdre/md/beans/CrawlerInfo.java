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

import javax.validation.constraints.*;

/**
 * Created by AS294216 on 05-10-2015.
 */
public class CrawlerInfo {
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
    @Min(value = 0)
    @Max(value = 10000)
    @NotNull
    private Integer politenessDelay;
    @Min(value = 1)
    @Max(value = 10000)
    @NotNull
    private Integer maxDepthOfCrawling;
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    @NotNull
    private Integer maxPagesToFetch;
    @NotNull
    private Integer includeBinaryContentInCrawling = 0;
    @NotNull
    private Integer resumableCrawling = 0;
    @NotNull
    private String userAgentString;
    private String proxyHost;
    private Integer proxyPort;
    private String proxyUserName;
    private String proxyPassword;
    @NotNull
    private String url;
    @NotNull
    private String urlsToSearch;
    @NotNull
    private String urlsNotToSearch;
    private String instanceExecOozieVal = "${wf:actionData(\"init-job\")[\"instance-exec-id\"]}";
    @Digits(fraction = 0, integer = 11)
    private Integer processId;
    @Min(value = 2)
    @Max(value = 10000)
    @NotNull
    private Integer numMappers;

    public Integer getNumMappers() {
        return numMappers;
    }

    public void setNumMappers(Integer numMappers) {
        this.numMappers = numMappers;
    }

    public Integer getPolitenessDelay() {
        return politenessDelay;
    }

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

    public String getInstanceExecOozieVal() {
        return instanceExecOozieVal;
    }

    public void setPolitenessDelay(Integer politenessDelay) {
        this.politenessDelay = politenessDelay;
    }

    public Integer getMaxDepthOfCrawling() {
        return maxDepthOfCrawling;
    }

    public void setMaxDepthOfCrawling(Integer maxDepthOfCrawling) {
        this.maxDepthOfCrawling = maxDepthOfCrawling;
    }

    public Integer getMaxPagesToFetch() {
        return maxPagesToFetch;
    }

    public void setMaxPagesToFetch(Integer maxPagesToFetch) {
        this.maxPagesToFetch = maxPagesToFetch;
    }

    public Integer getIncludeBinaryContentInCrawling() {
        return includeBinaryContentInCrawling;
    }

    public void setIncludeBinaryContentInCrawling(Integer includeBinaryContentInCrawling) {
        this.includeBinaryContentInCrawling = includeBinaryContentInCrawling;
    }

    public Integer getResumableCrawling() {
        return resumableCrawling;
    }

    public void setResumableCrawling(Integer resumableCrawling) {
        this.resumableCrawling = resumableCrawling;
    }

    public String getUserAgentString() {
        return userAgentString;
    }

    public void setUserAgentString(String userAgentString) {
        this.userAgentString = userAgentString;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlsToSearch() {
        return urlsToSearch;
    }

    public void setUrlsToSearch(String urlsToSearch) {
        this.urlsToSearch = urlsToSearch;
    }

    public String getUrlsNotToSearch() {
        return urlsNotToSearch;
    }

    public void setUrlsNotToSearch(String urlsNotToSearch) {
        this.urlsNotToSearch = urlsNotToSearch;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }
}
