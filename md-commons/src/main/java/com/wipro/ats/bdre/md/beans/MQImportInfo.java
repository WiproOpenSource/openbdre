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

/**
 * Created by MI294210 on 22-05-2015.
 */

/**
 * This class contains all the setter and getter methods for MQImport variables.
 */
public class MQImportInfo {

    @NotNull
    private String brokerUrlValue;
    private static String brokerUrl = "broker.url";

    private String queueNameValue;
    private Integer numSpoutsValue;
    private Integer numBoltsValue;
    private static String queueName = "queue.name";
    private static String configGroup = "mqimport";
    private static String numSpouts = "num.spouts";
    private static String numBolts = "num.bolts";
    private String description;
    private Integer busDomainId;
    private Boolean canRecover;
    private Integer page;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    private Integer counter;
    private Integer subProcessId;
    private Integer parentProcessId;

    @Override
    public String toString() {
        return " brokerUrlValue:" + brokerUrlValue + " brokerUrl:" + brokerUrl + " queueNameValue:" + queueNameValue + " numSpoutsValue:" + numSpoutsValue +
                " numBoltsValue:" + numBoltsValue + " queueName:" + queueName + " configGroup:" + configGroup + " numSpouts:" + numSpouts +
                " numBolts:" + numBolts + " description:" + description.substring(0, Math.min(description.length(), 45)) +
                " busDomainId:" + busDomainId + " canRecover:" + canRecover + " page:" + page + " subProcessId:" + subProcessId + " parentProcessId:" + parentProcessId;
    }

    public static String getNumBolts() {
        return numBolts;
    }

    public static void setNumBolts(String numBolts) {
        MQImportInfo.numBolts = numBolts;
    }

    public static String getNumSpouts() {
        return numSpouts;
    }

    public static void setNumSpouts(String numSpouts) {
        MQImportInfo.numSpouts = numSpouts;
    }


    public String getBrokerUrlValue() {
        return brokerUrlValue;
    }

    public void setBrokerUrlValue(String brokerUrlValue) {
        this.brokerUrlValue = brokerUrlValue;
    }

    public static String getBrokerUrl() {
        return brokerUrl;
    }

    public static void setBrokerUrl(String brokerUrl) {
        MQImportInfo.brokerUrl = brokerUrl;
    }


    public String getQueueNameValue() {
        return queueNameValue;
    }

    public void setQueueNameValue(String queueNameValue) {
        this.queueNameValue = queueNameValue;
    }

    public static String getQueueName() {
        return queueName;
    }

    public static void setQueueName(String queueName) {
        MQImportInfo.queueName = queueName;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getSubProcessId() {
        return subProcessId;
    }

    public void setSubProcessId(Integer subProcessId) {
        this.subProcessId = subProcessId;
    }

    public Integer getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public Integer getNumSpoutsValue() {
        return numSpoutsValue;
    }

    public void setNumSpoutsValue(Integer numSpoutsValue) {
        this.numSpoutsValue = numSpoutsValue;
    }

    public Integer getNumBoltsValue() {
        return numBoltsValue;
    }

    public void setNumBoltsValue(Integer numBoltsValue) {
        this.numBoltsValue = numBoltsValue;
    }
}
