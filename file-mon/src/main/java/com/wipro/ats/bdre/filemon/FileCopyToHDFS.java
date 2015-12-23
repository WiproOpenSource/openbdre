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

/**
 * Created by MO335755 on 12/23/2015.
 */
public class FileCopyToHDFS {

    private static final Logger LOGGER = Logger.getLogger(FileCopyToHDFS.class);

    /* this variable is used to keep details of file being currently copying */
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private FileCopyInfo fileCopying;
    private static Configuration config = new Configuration();

    public static void main(String args[]){
        config.addResource("/etc/hadoop/conf/core-site.xml");
        config.addResource("/etc/hadoop/conf/hdfs-site.xml");
    }

    private void executeCopyProcess() throws IOException{
        String key = FileMonitor.fileToCopyMap.firstKey();
        int index = FileMonitor.fileToCopyMap.indexOf(key);
        fileCopying = FileMonitor.fileToCopyMap.getValue(index);
        FileMonitor.fileToCopyMap.remove(index);
        try{
            // Copying file from local to HDFS overriding, if file already exists
            FileSystem fs = FileSystem.get(config);
            fs.copyFromLocalFile(false, true, new Path(fileCopying.getSrcLocation()),
                    new Path(fileCopying.getDstLocation()));

            // calling register file
            executeRegisterFiles(fileCopying.getFileContent(), fileCopying.getSubProcessId(), fileCopying.getServerId(), fileCopying.getDstLocation() + "/" + key);
        }catch(IOException ioex){
            FileMonitor.fileToCopyMap.put(key, fileCopying);
            LOGGER.error("Error in executeCopyProcess method " + ioex);
            throw new IOException("Error in executeCopyProcess method " + ioex);
        }
    }

    //Calling the RegisterFile method in metadata API on file Creation.
    private void executeRegisterFiles(FileContent fc, String subProcessId, String serverId, String path) {
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
