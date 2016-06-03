package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.apache.log4j.Logger;


/**
 * Created by cloudera on 5/31/16.
 */
public class PluginManagerMain {

    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public static void main(String[] args) throws Exception {
        String pluginDescriptorJSON = "";
        if(args.length == 0){
            LOGGER.error("Zip Path is not provided. Aborting...");
        } else {
            // unzipping the zip
            PluginExploder pluginExploder = new PluginExploder();
            pluginDescriptorJSON = pluginExploder.explode(args);
            PluginDescriptorReader pluginDescriptorReader = new PluginDescriptorReader();
            Plugin plugin = pluginDescriptorReader.jsonReader(pluginDescriptorJSON + "/plugin.json");
            PluginDependencyResolver pluginDependencyResolver = new PluginDependencyResolver();
            if(pluginDependencyResolver.dependencyCheck(plugin)){
                PluginInstaller pluginInstaller = new PluginInstaller();
                pluginInstaller.install(plugin,pluginDescriptorJSON);
            }else{
                LOGGER.error("plugin dependency not met can't install your plugin");
                throw new Exception();
            }
        }


    }
}

