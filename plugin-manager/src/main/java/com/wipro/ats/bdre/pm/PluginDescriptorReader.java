package com.wipro.ats.bdre.pm;



import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by cloudera on 5/31/16.
 */
public class PluginDescriptorReader{

    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);

    public Plugin jsonReader(String jsonFilePath) throws IOException {
        Plugin plugin = new Plugin();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String jsonfile = "";
            String jsonTemp;
            BufferedReader br = null;
            LOGGER.info("reading json file : " + jsonFilePath);
            br = new BufferedReader(new FileReader(jsonFilePath));
            jsonTemp = br.readLine();
            while (jsonTemp != null) {
                jsonfile = jsonfile + jsonTemp;
                LOGGER.info(jsonfile);
                jsonTemp=br.readLine();
            }
            LOGGER.info("final string is" + jsonfile);
             plugin = mapper.readValue(jsonfile, Plugin.class);
        }
        catch (Exception ioException){
            LOGGER.error(ioException);
        }
        return plugin;
    }
}
