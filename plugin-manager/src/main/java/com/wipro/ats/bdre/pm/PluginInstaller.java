package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.api.Import;
import com.wipro.ats.bdre.md.pm.beans.FS;
import com.wipro.ats.bdre.md.pm.beans.DataList;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import com.wipro.ats.bdre.md.pm.beans.UIWAR;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by cloudera on 6/2/16.
 */
public class PluginInstaller {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void install(Plugin plugin,String pluginDescriptorJSON) throws IOException{

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

        String warLocation = pluginDescriptorJSON + "/" + plugin.getInstall().getUiWar().getLocation();
        Import impotObject = new Import();
        File folder = new File(warLocation.substring(0, warLocation.lastIndexOf(".")));
        if(!folder.exists()){
            folder.mkdir();
        }
        impotObject.unZipIt(warLocation, warLocation.substring(0, warLocation.lastIndexOf(".")));
        WarOperations warOperations = new WarOperations();
        warOperations.listOfFiles(folder,folder);



        String restWarLocation = pluginDescriptorJSON + "/" + plugin.getInstall().getUiWar().getLocation();
        folder = new File(restWarLocation.substring(0, restWarLocation.lastIndexOf(".")));
        if(!folder.exists()){
            folder.mkdir();
        }
        impotObject.unZipIt(restWarLocation, restWarLocation.substring(0, restWarLocation.lastIndexOf(".")));
        warOperations.listOfFiles(folder,folder);

    }
}
