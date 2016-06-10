package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.InstalledPluginsDAO;
import com.wipro.ats.bdre.md.pm.beans.PluginDetails;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 6/2/16.
 */
public class InstalledPlugins extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(InstalledPlugins.class);


    @Autowired
    InstalledPluginsDAO installedPluginsDAO;


    public InstalledPlugins() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Override
    public Object execute (String[] params){
        return null;
    }

    public boolean dependencyCheck(String pluginId){
        boolean dependencyMet = true;
        com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins installedPlugins = installedPluginsDAO.get(pluginId);
        if (installedPlugins == null){
            dependencyMet = false;
        }
        return dependencyMet;
    }

    public List<com.wipro.ats.bdre.md.pm.beans.PluginDependency> getDependencies(String pluginUniqueId){
        List<com.wipro.ats.bdre.md.pm.beans.PluginDependency> pluginDependencies = new ArrayList<com.wipro.ats.bdre.md.pm.beans.PluginDependency>();
        return pluginDependencies;
    }

    public String insert(PluginDetails pluginDetails){
        com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins installedPlugins = new com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins();
        installedPlugins.setPluginUniqueId(pluginDetails.getPluginId() + "-" + pluginDetails.getVersion());
        installedPlugins.setPluginId(pluginDetails.getPluginId());
        installedPlugins.setName(pluginDetails.getName());
        installedPlugins.setDescription(pluginDetails.getDescription());
        installedPlugins.setPluginVersion(pluginDetails.getVersion());
        installedPlugins.setAuthor(pluginDetails.getAuthor());
        //TODO: don't knwo what does plugin column means in INSTALLED_PLUGIN table so add it accordingly
        installedPlugins.setUninstallable(pluginDetails.isUninstallable());
        return installedPluginsDAO.insert(installedPlugins);

    }

}
