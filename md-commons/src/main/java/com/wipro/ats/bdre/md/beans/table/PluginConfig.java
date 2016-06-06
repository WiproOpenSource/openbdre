package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.*;
/**
 * Created by cloudera on 5/27/16.
 */
public class PluginConfig {
    @NotNull
    private
    String pluginUniqueId;
    private String configGroup;
    private Integer pluginKey;
    private String pluginValue;
    private Integer page;
    private Integer counter;

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public String toString() {
    return " pluginUniqueId:" + pluginUniqueId + " configGroup:" + configGroup + " pluginKey:" + pluginKey + " pluginValue:" + pluginValue;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public Integer getPluginKey() {
        return pluginKey;
    }

    public void setPluginKey(Integer pluginKey) {
        this.pluginKey = pluginKey;
    }

    public String getPluginUniqueId() {
        return pluginUniqueId;
    }

    public void setPluginUniqueId(String pluginUniqueId) {
        this.pluginUniqueId = pluginUniqueId;
    }

    public String getPluginValue() {
        return pluginValue;
    }

    public void setPluginValue(String pluginValue) {
        this.pluginValue = pluginValue;
    }
}
