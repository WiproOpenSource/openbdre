package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by cloudera on 6/2/16.
 */
public class FSOpenrations {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginInstaller.class);
    public void copyAction(FS fs){
        Path path = FileSystems.getDefault().getPath(fs.getDestinationLocation());
        try{
            InputStream inputStram = new FileInputStream(fs.getSourceLocation());
            Files.copy(inputStram, path);
        }catch (IOException f){
            LOGGER.error("file not found" + f);
        }
    }

    public void deleteAction(FS fs){

    }

    public void moveAction(FS fs){

    }

    public void chmodAction(FS fs){

    }
}
