package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.FS;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by cloudera on 6/2/16.
 */
public class FSOperations {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void copyAction(FS fs,String pluginDescriptorJSON){
        Path path = FileSystems.getDefault().getPath(pluginDescriptorJSON + "/" + fs.getDestinationLocation());
        try{
            InputStream inputStram = new FileInputStream(fs.getSourceLocation());
            Files.copy(inputStram, path);
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
