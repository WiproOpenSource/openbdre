package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.pm.beans.FS;
import com.wipro.ats.bdre.md.pm.beans.DataList;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.apache.log4j.Logger;

/**
 * Created by cloudera on 6/2/16.
 */
public class PluginInstaller {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void install(Plugin plugin,String pluginDescriptorJSON){

        for(FS fs : plugin.getInstall().getFs()){

            if("FILECOPY".equals(fs.getAction())){
                FSOperations fsOperations = new FSOperations();
                fsOperations.copyAction(fs,pluginDescriptorJSON);
            }else if("FILEDELETE".equals(fs.getAction())){
                FSOperations fsOperations = new FSOperations();
                fsOperations.deleteAction(fs,pluginDescriptorJSON);
            }else if("FILEMOVE".equals(fs.getAction())){
                FSOperations fsOperations = new FSOperations();
                fsOperations.moveAction(fs,pluginDescriptorJSON);
            }else if("FILEPERMISSION".equals(fs.getAction())){

                FSOperations fsOperations = new FSOperations();
                fsOperations.chmodAction(fs,pluginDescriptorJSON);
            }
        }

        for(DataList dataList : plugin.getInstall().getMetadata().getInsert()){
            MetadataActions metadataActions = new MetadataActions();
            metadataActions.insertAction(dataList);
        }

        for(DataList dataList : plugin.getInstall().getMetadata().getUpdate()){
            MetadataActions metadataActions = new MetadataActions();
            metadataActions.updateAction(dataList);
        }
        for(DataList dataList : plugin.getInstall().getMetadata().getDelete()){
            MetadataActions metadataActions = new MetadataActions();
            metadataActions.deleteAction(dataList);
        }


    }
}
