package com.wipro.ats.bdre.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class PluginDependency {
    private Integer pluginId;
    private String version;
    private String versionLevel;

    public Integer getPluginId() {
        return pluginId;
    }

    public void setPluginId(Integer pluginId) {
        this.pluginId = pluginId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionLevel() {
        return versionLevel;
    }

    public void setVersionLevel(String versionLevel) {
        this.versionLevel = versionLevel;
    }
}
