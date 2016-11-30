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
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

/**
 * Created by MO335755 on 12/24/15.
 */
public class FileScan {

    private static final Logger LOGGER = Logger.getLogger(FileScan.class);

    private FileScan(){

    }

    public static void scanAndAddToQueue() {
        try {
            String scanDir = FileMonRunnableMain.getMonitoredDirName();
            LOGGER.debug("Scanning directory: " + scanDir);
            File dir = new File(scanDir);
            if (!dir.exists()) {
                LOGGER.info("Created monitoring dir " + dir + " success=" + dir.mkdirs());
            }
            File arcDir = new File(scanDir +"/"+ FileMonRunnableMain.ARCHIVE);
            if (!arcDir.exists()) {
                LOGGER.info("Created monitoring dir " + arcDir + " success=" + arcDir.mkdirs());
            }
            // Getting list of files recursively from directory except '_archive' directory
            Collection<File> listOfFiles = FileUtils.listFiles(dir, new RegexFileFilter(FileMonRunnableMain.getFilePattern()), new RegexFileFilter("^(?:(?!"+FileMonRunnableMain.ARCHIVE+").)*$"));
            String fileName = "";
            FileCopyInfo fileCopyInfo = null;
            for (File file : listOfFiles) {
                fileName = file.getName();
                LOGGER.debug("Matched File Pattern by " + fileName);
                fileCopyInfo = new FileCopyInfo();
                fileCopyInfo.setFileName(fileName);
                fileCopyInfo.setSubProcessId(FileMonRunnableMain.getSubProcessId());
                fileCopyInfo.setServerId(Integer.toString(123461));
                fileCopyInfo.setSrcLocation(file.getAbsolutePath());
                fileCopyInfo.setDstLocation(file.getParent().replace(FileMonRunnableMain.getMonitoredDirName(), FileMonRunnableMain.getHdfsUploadDir()));
                FileInputStream fis = new FileInputStream(file);
                fileCopyInfo.setFileHash(DigestUtils.md5Hex(fis));
                fis.close();
                fileCopyInfo.setFileSize(file.length());
                fileCopyInfo.setTimeStamp(file.lastModified());
                FileMonitor.addToQueue(fileName, fileCopyInfo);
            }
        } catch (Exception err) {
            LOGGER.error("Error in scan directory ", err);
            throw new BDREException(err);
        }
    }
}