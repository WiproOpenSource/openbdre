package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.RegisterFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.File;

/**
 * Created by MO335755 on 12/23/2015.
 */
public class QueuedFileUploader {

    private static final Logger LOGGER = Logger.getLogger(QueuedFileUploader.class);

    /* this variable is used to keep details of file being currently copying */
    private static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    //private
    private static Configuration config = new Configuration();
    static{
        /*config.addResource("/etc/hadoop/conf/core-site.xml");
        config.addResource("/etc/hadoop/conf/hdfs-site.xml");*/
    }
    private static void hdfsCopy(FileCopyInfo fileCopying) throws IOException{

        try {

            // Copying file from local to HDFS overriding, if file already exists
            config.set("fs.defaultFS",FileMonRunnableMain.getDefaultFSName());
            FileSystem fs = FileSystem.get(config);
            fs.copyFromLocalFile(FileMonRunnableMain.isDeleteCopiedSrc(), true, new Path(fileCopying.getSrcLocation()),
                    new Path(fileCopying.getDstLocation()));

        } catch (Exception e) {
            FileMonitor.fileToCopyMap.put(fileCopying.getFileName(), fileCopying);
            LOGGER.error("Error in executeCopyProcess method. Requeuing file " + fileCopying.getFileName() , e);
            throw new IOException(e);

        }

    }

    public static void executeCopyProcess() {
        FileCopyInfo fileCopying=null;
            if (FileMonitor.fileToCopyMap.size()>0) {
                String key = FileMonitor.fileToCopyMap.firstKey();
                int index = FileMonitor.fileToCopyMap.indexOf(key);
                fileCopying = FileMonitor.fileToCopyMap.getValue(index);
                FileMonitor.fileToCopyMap.remove(index);
                try {
                    hdfsCopy(fileCopying);

                    //TODO: Enable it
                    //executeRegisterFiles(fileCopying.getFileContent(), fileCopying.getSubProcessId(), fileCopying.getServerId(), fileCopying.getDstLocation() + "/" + key);
                } catch (IOException e) {
                    //TODO: Write log
                }
                // calling register file

            }

    }

    //Calling the RegisterFile method in metadata API on file Creation.
    private static void executeRegisterFiles(FileContent fc, String subProcessId, String serverId, String path) {
        try {
            //getting the hashcode of the file
            String fileHash = DigestUtils.md5Hex(fc.getInputStream());
            String fileSize = String.valueOf(fc.getSize());
            long timeStamp = fc.getLastModifiedTime();
            Date dt=new Date(timeStamp);
            String strDate=sdf.format(dt);
            RegisterFile registerFile = new RegisterFile();
            String[] params = {"-p", subProcessId, "-sId", serverId, "-path", path, "-fs", fileSize, "-fh", fileHash, "-cTS", strDate, "-bid", "0"};
            LOGGER.debug("executeRegisterFiles Invoked for "+path);
            registerFile.execute(params);
        } catch (Exception err) {
            LOGGER.error(err.getMessage());
            throw new ETLException(err);
        }
    }

}
