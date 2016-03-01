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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by KA294215 on 12-03-2015.
 */

/**
 * This class contains all the setter and getter methods for DQSetup variables.
 */
public class DQSetupInfo {
    @NotNull
    private String rulesUserNameValue;
    private static String rulesUserName = "rules.username";
    @NotNull
    private String rulesPasswordValue;
    private static String rulesPassword = "rules.password";
    @NotNull
    private String rulesPackageValue;
    private static String rulesPackage = "rules.package";
    @NotNull
    private String fileDelimiterRegexValue;
    private static String fileDelimiterRegex = "file.delimiter.regex";
    @NotNull
    private String minPassThresholdPercentValue;
    private static String minPassThresholdPercent = "min.pass.threshold.percent";
    private static String configGroup = "dq";
    @NotNull
    private String description;
    @NotNull
    @Size(min = 1, max = 45)
    private String processName;
    private Integer busDomainId;
    private Boolean canRecover;
    private Boolean deleteFlag;
    private Integer enqId;
    private Integer page;
    private Integer counter;
    private Integer pageSize;
    private Integer subProcessId;
    private Integer parentProcessId;

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return " rulesUserNameValue:" + rulesUserNameValue + " rulesUserName:" + rulesUserName + " rulesPasswordValue:" + rulesPasswordValue +
                " rulesPassword:" + rulesPassword + " rulesPackageValue:" + rulesPackageValue + " rulesPackage:" + rulesPackage +
                " fileDelimiterRegexValue:" + fileDelimiterRegexValue + " fileDelimiterRegex:" + fileDelimiterRegex +
                " minPassThresholdPercentValue:" + minPassThresholdPercentValue + " minPassThresholdPercent:" + minPassThresholdPercent +
                " configGroup:" + configGroup + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                " busDomainId:" + busDomainId + " canRecover:" + canRecover + " enqId:" + enqId + " page:" + page + " subProcessId:" + subProcessId +
                " parentProcessId:" + parentProcessId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Integer getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
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


    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getRulesUserName() {
        return rulesUserName;
    }

    public static void setRulesUserName(String rulesUserName) {
        DQSetupInfo.rulesUserName = rulesUserName;
    }

    public static void setRulesPassword(String rulesPassword) {
        DQSetupInfo.rulesPassword = rulesPassword;
    }

    public static void setRulesPackage(String rulesPackage) {
        DQSetupInfo.rulesPackage = rulesPackage;
    }

    public static void setFileDelimiterRegex(String fileDelimiterRegex) {
        DQSetupInfo.fileDelimiterRegex = fileDelimiterRegex;
    }

    public static void setMinPassThresholdPercent(String minPassThresholdPercent) {
        DQSetupInfo.minPassThresholdPercent = minPassThresholdPercent;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public String getRulesPassword() {
        return rulesPassword;
    }

    public String getRulesPackage() {
        return rulesPackage;
    }

    public String getFileDelimiterRegex() {
        return fileDelimiterRegex;
    }

    public String getMinPassThresholdPercent() {
        return minPassThresholdPercent;
    }

    public String getRulesUserNameValue() {

        return rulesUserNameValue;
    }

    public void setRulesUserNameValue(String rulesUserNameValue) {
        this.rulesUserNameValue = rulesUserNameValue;
    }

    public String getRulesPasswordValue() {
        return rulesPasswordValue;
    }

    public void setRulesPasswordValue(String rulesPasswordValue) {
        this.rulesPasswordValue = rulesPasswordValue;
    }

    public String getRulesPackageValue() {
        return rulesPackageValue;
    }

    public void setRulesPackageValue(String rulesPackageValue) {
        this.rulesPackageValue = rulesPackageValue;
    }

    public String getFileDelimiterRegexValue() {
        return fileDelimiterRegexValue;
    }

    public void setFileDelimiterRegexValue(String fileDelimiterRegexValue) {
        this.fileDelimiterRegexValue = fileDelimiterRegexValue;
    }

    public String getMinPassThresholdPercentValue() {
        return minPassThresholdPercentValue;
    }

    public void setMinPassThresholdPercentValue(String minPassThresholdPercentValue) {
        this.minPassThresholdPercentValue = minPassThresholdPercentValue;
    }

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
    }

    public Boolean getCanRecover() {
        return canRecover;
    }

    public void setCanRecover(Boolean canRecover) {
        this.canRecover = canRecover;
    }


    public Integer getEnqId() {
        return enqId;
    }

    public void setEnqId(Integer enqId) {
        this.enqId = enqId;
    }

    public Integer getSubProcessId() {
        return subProcessId;
    }

    public void setSubProcessId(Integer subProcessId) {
        this.subProcessId = subProcessId;
    }
}
