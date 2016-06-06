package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.FS;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by cloudera on 6/2/16.
 */
public class FSOpenrations {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void copyAction(FS fs,String pluginDescriptorJSON){

        try{
            String homeDir = System.getProperty("user.home");
            File destFile = new File(homeDir + "/" + fs.getDestinationLocation());
            File srcFile = new File(pluginDescriptorJSON + "/" + fs.getSourceLocation());
            boolean fileCreated = destFile.createNewFile();
            Files.copy(srcFile.toPath(),destFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException f){
            LOGGER.error("file not found" + f);
        }
    }

    public void deleteAction(FS fs,String pluginDescriptorJSON){

    }

    public void moveAction(FS fs,String pluginDescriptorJSON){

    }

    public void chmodAction(FS fs,String pluginDescriptorJSON){

    }
}
