package com.wipro.ats.bdre.md.api.util;

import com.wipro.ats.bdre.md.rest.beans.ProcessExport;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by cloudera on 3/9/16.
 */
public class AddJson {
    private static final Logger LOGGER = Logger.getLogger(AddJson.class);
    public  String addJsonToProcessId(String processId,ProcessExport processExport)
    {


        String homeDir = System.getProperty("user.home");

        String source_dir=homeDir+"/bdre-wfd/"+processId;

        ObjectMapper mapper = new ObjectMapper();
        File creatingDir = new File(source_dir);
        if (!creatingDir.exists()) {
            creatingDir.mkdir();
        }

        if (creatingDir.exists()) {
            // convert user object to json string,
            try {
                mapper.writeValue(new File(homeDir + "/bdre-wfd/" + processId + "/" +"process.json"), processExport);
            } catch (IOException e) {
               LOGGER.info(e);
                return "failed";

            }

        }
       return "success";
}}
