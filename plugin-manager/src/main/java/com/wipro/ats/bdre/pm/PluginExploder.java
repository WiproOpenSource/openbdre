package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.md.api.Import;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by cloudera on 5/31/16.
 */
public class PluginExploder extends BaseStructure {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginExploder.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "zip-path", "Path for plugin zip"}
    };

    protected String explode (String[] params){
        // getting path of zipped file containing plugin files
        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String path = commandLine.getOptionValue("zip-path");
        // creating BDRE temp directory to explod zipped file
        String homeDir = System.getProperty("user.home");
        String uuid = UUID.randomUUID().toString();
        String outputDir = homeDir + "/bdre-plugins/" + uuid;
        // unzipping zip file
        Import pimport = new Import();
        pimport.unZipIt(path, outputDir);
        return outputDir;

    }
}
