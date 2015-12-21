/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.RegisterFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by vishnu on 1/11/15.
 */
public class FileMonitor implements FileListener {
    private static final Logger LOGGER = Logger.getLogger(FileMonRunnableMain.class);
    private static FileMonitor fileMonitor = null;
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    //HashTable Contains key as Directory Path and values as List of FileMonInfo Objects
    private Hashtable<String, List<FileMonInfo>> fileSet = new Hashtable<String, List<FileMonInfo>>();

    private FileMonitor() {
        init();
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
        List<String> dirList = IMConfig.getPropertyList("file-mon.dirs");
        List<String> filterList = IMConfig.getPropertyList("file-mon.filter");
        List<String> subProcessList = IMConfig.getPropertyList("file-mon.sub-processIds");
        List<String> serverIdList = IMConfig.getPropertyList("file-mon.serverIds");
        List<FileMonInfo> fmInfoList = new ArrayList<FileMonInfo>();
        for (int i = 0; i < dirList.size(); i++) {
            if (!fileSet.containsKey(dirList.get(i))) {
                FileMonInfo fmInfo = new FileMonInfo();
                fmInfo.setFilePattern(filterList.get(i));
                fmInfo.setSubProcessId(subProcessList.get(i));
                fmInfo.setServerId(serverIdList.get(i));
                fmInfoList.add(fmInfo);
                fileSet.put(dirList.get(i), fmInfoList);
            } else {
                fmInfoList = fileSet.get(dirList.get(i));
                FileMonInfo fmInfo = new FileMonInfo();
                fmInfo.setFilePattern(filterList.get(i));
                fmInfo.setSubProcessId(subProcessList.get(i));
                fmInfo.setServerId(serverIdList.get(i));
                fmInfoList.add(fmInfo);
                fileSet.put(dirList.get(i), fmInfoList);
            }
        }
    }

    //This method will get invoked when a file created in the directory.
    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {

        FileObject obj = fileChangeEvent.getFile();
        LOGGER.debug("File Created " + obj.getURL());
        String dirPath = obj.getParent().getName().getPath();
        String fileName = obj.getName().getBaseName();

        List<FileMonInfo> fileMonInfoList = fileSet.get(dirPath);

        for (FileMonInfo fileMonInfo : fileMonInfoList) {
            //Checking if the file name matches with the given pattern
            if (fileName.matches(fileMonInfo.getFilePattern())) {
                String subProcessId = fileMonInfo.getSubProcessId();
                FileContent fc = obj.getContent();
                LOGGER.debug("Matched File Pattern");
                executeRegisterFiles(fc, subProcessId, fileMonInfo.getServerId(), dirPath + "/" + fileName);
                break;
            }
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
        //nothing to do
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
        //nothing to do
    }

    //Calling the RegisterFile method in metadata API on file Creation.
    private void executeRegisterFiles(FileContent fc, String subProcessId, String serverId, String path) {
        try {
            //getting the hashcode of the file
            String fileHash =DigestUtils.md5Hex(fc.getInputStream());
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
