package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.md.api.Import;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * Created by cloudera on 5/31/16.
 */
public class PluginExploder extends BaseStructure {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "zip-path", "Path for plugin zip"}
    };

    protected String explode (String[] params){
        // getting path of zipped file containing plugin files
        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String path = commandLine.getOptionValue("zip-path");
        LOGGER.info("Path of zipped file is :" + path);
        // creating BDRE temp directory to explod zipped file
        String homeDir = System.getProperty("user.home");
        String uuid = UUID.randomUUID().toString();
        String outputDir = homeDir + "/bdre-plugins/" + uuid;
        LOGGER.info("unzipped plugin zip at "+ outputDir);
        // unzipping zip file
        Import pimport = new Import();
        pimport.unZipIt(path, outputDir);
        String fileName = path.split("/")[path.split("/").length-1];
        LOGGER.info("name of zip file is : " + fileName);
        LOGGER.info("output directory is : " + fileName.substring(0,fileName.length()-4));
        return outputDir + "/" + fileName.substring(0,fileName.length()-4);

    }
}
