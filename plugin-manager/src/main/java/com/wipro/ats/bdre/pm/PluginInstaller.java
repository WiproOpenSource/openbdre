package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.FS;
import com.wipro.ats.bdre.md.pm.beans.Insert;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by cloudera on 6/2/16.
 */
public class PluginInstaller {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginInstaller.class);
    public void install(Plugin plugin){

        for(FS fs : plugin.getInstall().getFs()){
            if("FileCopy".equals(fs.getAction())){
                FSOpenrations fsOperations = new FSOpenrations();
                fsOperations.copyAction(fs);
            }

            //TODO: add if cases if other actions exists
        }

        for(Insert insert : plugin.getInstall().getMetadata().getInsert()){

        }


    }
}
