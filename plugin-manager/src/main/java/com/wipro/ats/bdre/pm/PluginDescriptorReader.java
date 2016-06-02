package com.wipro.ats.bdre.pm;



import com.wipro.ats.bdre.pm.beans.Plugin;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by cloudera on 5/31/16.
 */
public class PluginDescriptorReader{

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginDescriptorReader.class);

    public Plugin jsonReader(String jsonFilePath) throws IOException {
        Plugin plugin = new Plugin();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String jsonfile = "";
            String jsonTemp;
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(jsonFilePath));
            while ((jsonTemp = br.readLine()) != null) {
                jsonfile = jsonfile + jsonTemp;
                LOGGER.info(jsonfile);
            }
            LOGGER.info("final string is" + jsonfile);
             plugin = mapper.readValue(jsonfile, Plugin.class);
        }
        catch (IOException ioException){

        }
        return plugin;
    }
}
