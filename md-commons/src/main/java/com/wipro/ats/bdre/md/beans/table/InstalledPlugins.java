package com.wipro.ats.bdre.md.beans.table;

/**
 * Created by cloudera on 5/27/16.
 */


import javax.validation.constraints.*;
import java.util.Date;


public class InstalledPlugins {
    @NotNull
    @Pattern(regexp = "([0-z]+(-)[0-9]+")
    private String pluginUniqueId;
    private String pluginId;
    private String name;
    private String description;
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    @NotNull
    private Integer version;

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

    private String author;
    @NotNull
    private Date addTs;
    private String plugin;
    private Boolean unInstallable;

    @Override
    public String toString() {
        return " pluginUniqueId:"+ pluginUniqueId + " pluginId:" + pluginId + " name:" + name + " description:" + description +
                " version:" + version + " author:" + author + " addTs:" + addTs + " plugin:" + plugin + " unInstallable:"
                + unInstallable;



    }



}



