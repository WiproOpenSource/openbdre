package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by cloudera on 12/24/15.
 */
public class FileScan {

    private static final Logger LOGGER = Logger.getLogger(FileScan.class);

    public static void scanAndAddToQueue() {
        try {
            String monitorDir = FileMonRunnableMain.getMonitoredDirName();


            File dir = new File(monitorDir);
            File[] listOfFiles = dir.listFiles();
            String fhash = "";
            for (File file : listOfFiles) {
                if (file.isFile()) {//Checking if the file name matches with the given pattern
                    String fileName = file.getName();
                    if (fileName.matches(FileMonRunnableMain.getFilePattern())) {
                        LOGGER.debug("Matched File Pattern by " + fileName);
                        fhash = DigestUtils.md5Hex(FileUtils.readFileToByteArray(file));
                        FileCopyInfo fileCopyInfo = new FileCopyInfo();
                        fileCopyInfo.setFileName(fileName);
                        fileCopyInfo.setSubProcessId(FileMonRunnableMain.getSubProcessId());
                        fileCopyInfo.setServerId(new Integer(123461).toString());
                        fileCopyInfo.setSrcLocation(file.getAbsolutePath());
                        fileCopyInfo.setDstLocation(FileMonRunnableMain.getHdfsUploadDir());
                        fileCopyInfo.setFileHash(fhash);
                        fileCopyInfo.setFileSize(file.length());
                        fileCopyInfo.setTimeStamp(file.lastModified());
                        FileMonitor.addToQueue(fileName,fileCopyInfo);
                    }
                }
            }

        } catch (Exception err) {
            LOGGER.error(err.toString(), err);
            throw new ETLException(err);
        }
    }
}
