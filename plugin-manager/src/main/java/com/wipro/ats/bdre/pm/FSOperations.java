package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.FS;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by cloudera on 6/2/16.
 */
public class FSOperations {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void copyAction(FS fs,String pluginDescriptorJSON) throws FileNotFoundException{

        try{
            String homeDir = System.getProperty("user.home");
            File destFile = new File(homeDir + "/" + fs.getDestinationLocation());
            File destDir = new File((homeDir + "/" + fs.getDestinationLocation()).substring(0,(homeDir + "/" + fs.getDestinationLocation()).lastIndexOf("/")));
            destDir.mkdirs();
            File srcFile = new File(pluginDescriptorJSON + "/" + fs.getSourceLocation());
            if (srcFile.exists()) {
                boolean fileCreated = destFile.createNewFile();
                Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new FileNotFoundException("file not found");
            }

        }catch (IOException f){
            LOGGER.error("file not found" + f);
            throw new FileNotFoundException("file not found");
        }
    }

    public void deleteAction(FS fs,String pluginDescriptorJSON){

    }

    public void moveAction(FS fs,String pluginDescriptorJSON){

    }

    public void chmodAction(FS fs,String pluginDescriptorJSON){

    }
}
