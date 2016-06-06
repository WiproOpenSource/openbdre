package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.api.InstalledPlugins;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import com.wipro.ats.bdre.md.pm.beans.PluginDependency;
import org.apache.log4j.Logger;

/**
 * Created by cloudera on 6/1/16.
 */
public class PluginDependencyResolver {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public boolean dependencyCheck(Plugin plugin){
        for (PluginDependency pluginDependency : plugin.getPluginDependency()){
            InstalledPlugins installedPlugins = new InstalledPlugins();
            String uniquePluginId = pluginDependency.getPluginId() + "-" + pluginDependency.getVersion();
            if (!installedPlugins.dependencyCheck(uniquePluginId)){
                return false;
            }
        }
        return true;
    }
}
