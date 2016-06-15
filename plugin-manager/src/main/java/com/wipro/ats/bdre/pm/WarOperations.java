package com.wipro.ats.bdre.pm;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by cloudera on 6/14/16.
 */
public class WarOperations {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void listOfFiles(File file,File parent) throws IOException{
        LOGGER.info("entering directory name : " + file.getName() + " The absolute location of dir is : " + file.getAbsolutePath());
        for(File file1 : file.listFiles()){
            if(file1.isDirectory()){
                String relativePath = file1.getAbsolutePath().replace(parent.getAbsolutePath() ,"");
                String webappPath = System.getProperty("user.home") + "/bdre/lib/webapps/md-ui-1.1-SNAPSHOT" + relativePath;
                if (!new File(webappPath).exists()){
                    new File(webappPath).mkdir();
                }
                listOfFiles(file1,parent);
            }else{
                String relativePath = file1.getAbsolutePath().replace(parent.getAbsolutePath() ,"");
                String webappPath = System.getProperty("user.home") + "/bdre/lib/webapps/mdui" + relativePath;
                if (new File(webappPath).exists() && ! Files.isSymbolicLink(new File(webappPath).toPath())){
                       continue;
                }else {
                    Path linkPath = new File(webappPath).toPath();
                    if(new File(webappPath).exists()){
                        new File(webappPath).delete();
                    }
                    Path targetPath = new File(file1.getAbsolutePath()).toPath();
                    try {
                        Files.createSymbolicLink(linkPath, targetPath);
                    } catch (IOException io) {
                        LOGGER.error(io + " : " + io.getMessage());
                        throw io;
                    }
                }
            }
        }
    }
}
