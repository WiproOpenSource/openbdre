package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.api.InstalledPlugins;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import com.wipro.ats.bdre.md.pm.beans.PluginDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cloudera on 6/1/16.
 */
public class PluginDependencyResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginDependencyResolver.class);
    public boolean dependencyCheck(Plugin plugin){
        for (PluginDependency pluginDependency : plugin.getPluginDependency()){
            InstalledPlugins installedPlugins = new InstalledPlugins();
            if (installedPlugins.dependencyCheck(pluginDependency.getPluginId()) == false){
                return false;
            }
        }
        return true;
    }
}
