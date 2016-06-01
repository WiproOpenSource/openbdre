package com.wipro.ats.bdre.pm.beans;

/**
 * Created by cloudera on 6/1/16.
 */
public class PluginDetails {
    private Integer pluginId;
    private String name;
    private String descritpion;
    private String author;
    private String pluginWebsite;

    public Integer getPluginId() {
        return pluginId;
    }

    public void setPluginId(Integer pluginId) {
        this.pluginId = pluginId;
    }

    public String getDescritpion() {
        return descritpion;
    }

    public void setDescritpion(String descritpion) {
        this.descritpion = descritpion;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPluginWebsite() {
        return pluginWebsite;
    }

    public void setPluginWebsite(String pluginWebsite) {
        this.pluginWebsite = pluginWebsite;
    }

    private String version;

    public boolean isUninstallable() {
        return uninstallable;
    }

    public void setUninstallable(boolean uninstallable) {
        this.uninstallable = uninstallable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean uninstallable;

}
