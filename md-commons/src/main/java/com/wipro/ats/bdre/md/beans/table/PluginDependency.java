package com.wipro.ats.bdre.md.beans.table;



import javax.validation.constraints.*;

/**
 * Created by cloudera on 5/27/16.
 */
public class PluginDependency {
    @NotNull
    @Min(value = 1)
    private Integer dependencyId;
    @NotNull
    private String pluginUniqueId;
    private String dependentPluginUniqueId;
    private Integer counter;
    private Integer page;
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


    public Integer getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(Integer dependencyId) {
        this.dependencyId = dependencyId;
    }

    public String getDependentPluginUniqueId() {
        return dependentPluginUniqueId;
    }

    public void setDependentPluginUniqueId(String dependentPluginUniqueId) {
        this.dependentPluginUniqueId = dependentPluginUniqueId;
    }

    public String getPluginUniqueId() {
        return pluginUniqueId;
    }

    public void setPluginUniqueId(String pluginUniqueId) {
        this.pluginUniqueId = pluginUniqueId;
    }

    @Override
    public String toString() {
    return " dependencyId:"+ dependencyId + " pluginUniqueId:" + pluginUniqueId + " dependentPluginUniqueId:" + dependentPluginUniqueId;
    }


}
