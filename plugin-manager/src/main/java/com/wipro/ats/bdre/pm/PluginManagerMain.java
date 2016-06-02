package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.pm.beans.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cloudera on 5/31/16.
 */
public class PluginManagerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManagerMain.class);
    public static void main(String[] args) throws Exception {
        String pluginDescriptorJSON = "";
        if(args.length==0){
            LOGGER.info("Zip Path is not provided. Aborting...");
        } else {
            // unzipping the zip
            PluginExploder pluginExploder = new PluginExploder();
            pluginDescriptorJSON = pluginExploder.explode(args) + "/plugin.json";
            PluginDescriptorReader pluginDescriptorReader = new PluginDescriptorReader();
            Plugin plugin = pluginDescriptorReader.jsonReader(pluginDescriptorJSON);

        }


    }
}

