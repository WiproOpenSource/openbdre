/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.exception.BDREException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by MO335755 on 12/24/15.
 */
public class FileScan {

    private static final Logger LOGGER = Logger.getLogger(FileScan.class);

    public static void scanAndAddToQueue() {
        try {
            String scanDir = FileMonRunnableMain.getMonitoredDirName();
            LOGGER.debug("Scanning directory: "+ scanDir);
            File dir = new File(scanDir);
            if(!dir.exists()){
                LOGGER.info("Created monitoring dir "+dir+ " success="+dir.mkdirs());
            }
            File arcDir = new File(scanDir+"/_archive");
            if(!arcDir.exists()){
                LOGGER.info("Created monitoring dir " + arcDir + " success=" + arcDir.mkdirs());
            }
            File[] listOfFiles = dir.listFiles();
            String fhash = "";
            for (File file : listOfFiles) {
                if (file.isFile()) {//Checking if the file name matches with the given pattern
                    String fileName = file.getName();
                    if (fileName.matches(FileMonRunnableMain.getFilePattern())) {
                        LOGGER.debug("Matched File Pattern by :" + fileName);
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
            LOGGER.error("Error in scan directory ", err);
            throw new BDREException(err);
        }
    }
}
