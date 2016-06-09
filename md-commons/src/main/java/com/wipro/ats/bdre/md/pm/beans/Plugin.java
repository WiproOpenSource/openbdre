package com.wipro.ats.bdre.md.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class Plugin {
    private PluginDetails pluginDetails;
    private List<PluginDependency> pluginDependency;

    private List<PluginConfig> pluginConfig;

    public List<PluginConfig> getPluginConfig() {
        return pluginConfig;
    }

    public void setPluginConfig(List<PluginConfig> pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    private Install install;


    public Install getInstall() {
        return install;
    }

    public void setInstall(Install install) {
        this.install = install;
    }

    public List<PluginDependency> getPluginDependency() {
        return pluginDependency;
    }

    public void setPluginDependency(List<PluginDependency> pluginDependency) {
        this.pluginDependency = pluginDependency;
    }

    public PluginDetails getPluginDetails() {
        return pluginDetails;
    }

    public void setPluginDetails(PluginDetails pluginDetails) {
        this.pluginDetails = pluginDetails;
    }


}
