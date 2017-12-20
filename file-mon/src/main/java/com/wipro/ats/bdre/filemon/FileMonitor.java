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
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.vfs2.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;


/**
 * Created by vishnu on 1/11/15.
 */
public class FileMonitor implements FileListener {
    private static final Logger LOGGER = Logger.getLogger(FileMonitor.class);
    private static FileMonitor fileMonitor = null;

    private String monDir = null;
    private String archiveDirName = null;
    private String filePattern = null;
    /* this data structure is used to maintain order and getting eldest element
   * Map contain Filename as key and FileCopyInfo as value    * */
    private static LinkedMap<String, FileCopyInfo> fileToCopyMap =
            new LinkedMap<String, FileCopyInfo>();

    private FileMonitor() {
        init();
    }
    public static synchronized FileCopyInfo getFileInfoFromQueue() {
        String key = FileMonitor.fileToCopyMap.firstKey();
        return fileToCopyMap.remove(key);
    }

    public static synchronized void addToQueue(String fileName, FileCopyInfo fileCopyInfo) {
        fileToCopyMap.put(fileName, fileCopyInfo);
    }
    public static synchronized int getQueueSize(){
        return fileToCopyMap.size();
    }



    //Singleton pattern
    public static FileMonitor getInstance() {
        if (fileMonitor == null) {
            fileMonitor = new FileMonitor();
        }
        return fileMonitor;
    }

    //Read the monitored directories, file patterns, subprocessIds and serverIds and build the HashTable
    private void init() {
        monDir = FileMonRunnableMain.getMonitoredDirName();
        filePattern = FileMonRunnableMain.getFilePattern();
        archiveDirName = FileMonRunnableMain.ARCHIVE;
    }

    //This method will get invoked when a file created in the directory.
    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
        FileObject obj = fileChangeEvent.getFile();
        LOGGER.info("File Created " + obj.getURL());
        String dirPath = obj.getParent().getName().getPath();
        LOGGER.info("Full path "+obj.getName().getPath());

        //Don't process anything with _archive
        if(dirPath.startsWith(monDir+"/"+archiveDirName)){
            return;
        }
        //Don't process directory
        if(obj.getType() == FileType.FOLDER){
            return;
        }

        String fileName = obj.getName().getPath();

        //Checking if the file name matches with the given pattern
        if (fileName.matches(filePattern)) {
            LOGGER.info("file object content is open :"+obj.isContentOpen());
            FileContent fc = obj.getContent();
            LOGGER.info("Matched File Pattern by " + fileName);
            putEligibleFileInfoInMap(fileName, fc);
        }
    }

    private static void putEligibleFileInfoInMap(String fileName, FileContent fc) {
        // *Start*   Eligible files moved to data structure for ingestion to HDFS
        FileCopyInfo fileCopyInfo = new FileCopyInfo();
        LOGGER.info("in putEligibleFileInfoInMap function");
        try {
            fileCopyInfo.setFileName(fileName);
            fileCopyInfo.setSubProcessId(FileMonRunnableMain.getSubProcessId());
            fileCopyInfo.setServerId(Integer.toString(123461));
            fileCopyInfo.setSrcLocation(fc.getFile().getName().getPath());
            fileCopyInfo.setDstLocation(new java.io.File(fileName).getParent().replace(FileMonRunnableMain.getMonitoredDirName(), FileMonRunnableMain.getHdfsUploadDir()));
            //fileCopyInfo.setFileHash(DigestUtils.md5Hex(fc.getInputStream()));
            fileCopyInfo.setFileHash(fileName+fc.getLastModifiedTime());
            fileCopyInfo.setFileSize(fc.getSize());
            Long prevSize=0L;
            Long currentSize=0L;
            while (true)
            {
                try {
                    currentSize=new File(fileName).length();
                    LOGGER.info("currentSize of file: "+fileName+" is "+currentSize);

                } catch (Exception e) {
                    LOGGER.info(e);

                }
                if(currentSize.equals(prevSize))
                {
                    break;

                }
                else
                {
                    prevSize=currentSize;
                    Thread.sleep(1000);
                    continue;
                }

            }


            fileCopyInfo.setTimeStamp(fc.getLastModifiedTime());
            // putting element to structure
           addToQueue(fileName,fileCopyInfo);
        } catch (Exception err) {
            LOGGER.error("Error adding file to queue ", err);
            throw new BDREException(err);
        }
        // *End*   Eligible files moved to data structure for ingestion to HDFS
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
        //nothing to do
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
        LOGGER.info("file is changing");
        //nothing to do
    }


}
