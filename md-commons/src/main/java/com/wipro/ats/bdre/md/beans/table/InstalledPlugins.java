package com.wipro.ats.bdre.md.beans.table;

/**
 * Created by cloudera on 5/27/16.
 */


import javax.validation.constraints.*;
import java.util.Date;


public class InstalledPlugins {
    @NotNull
    private String pluginUniqueId;
    private String pluginId;
    private String name;
    private String description;
    private String author;
    private Date addTs;
    private String plugin;
    private Boolean unInstallable;
    private Integer page;
    private Integer counter;
    private String tableAddTs;
    @NotNull
    private Integer version;

    public String getTableAddTs() {
        return tableAddTs;
    }

    public void setTableAddTs(String tableAddTs) {
        this.tableAddTs = tableAddTs;
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



    public String getPluginUniqueId() {
        return pluginUniqueId;
    }

    public void setPluginUniqueId(String pluginUniqueId) {
        this.pluginUniqueId = pluginUniqueId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getAddTs() {
        return addTs;
    }

    public void setAddTs(Date addTs) {
        this.addTs = addTs;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public Boolean getUnInstallable() {
        return unInstallable;
    }

    public void setUnInstallable(Boolean unInstallable) {
        this.unInstallable = unInstallable;
    }



    @Override
    public String toString() {
        return " pluginUniqueId:"+ pluginUniqueId + " pluginId:" + pluginId + " name:" + name + " description:" + description +
                " version:" + version + " author:" + author + " addTs:" + addTs + " plugin:" + plugin + " unInstallable:"
                + unInstallable;



    }



}



