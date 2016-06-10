package com.wipro.ats.bdre.md.pm.beans;

/**
 * Created by cloudera on 6/5/16.
 */
public class PluginConfig {
    private String configGroup;
    private String key;
    private String value;

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
