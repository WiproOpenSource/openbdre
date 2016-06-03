package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.FS;
import com.wipro.ats.bdre.md.pm.beans.DataList;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            }else if("FileDelete".equals(fs.getAction())){
                FSOpenrations fsOperations = new FSOpenrations();
                fsOperations.deleteAction(fs);
            }else if("FileMove".equals(fs.getAction())){
                FSOpenrations fsOperations = new FSOpenrations();
                fsOperations.moveAction(fs);
            }else if("FilePermission".equals(fs.getAction())){
                FSOpenrations fsOperations = new FSOpenrations();
                fsOperations.chmodAction(fs);
            }
        }

        for(DataList dataList : plugin.getInstall().getMetadata().getDataList()){
            MetadataActions metadataActions = new MetadataActions();
            metadataActions.insertAction(dataList);
        }


    }
}
