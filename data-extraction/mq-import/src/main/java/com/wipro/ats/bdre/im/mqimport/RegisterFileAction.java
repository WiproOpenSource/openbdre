/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.mqimport;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.md.api.BatchEnqueuer;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.storm.hdfs.common.rotation.RotationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by arijit on 4/25/15.
 */
public class RegisterFileAction implements RotationAction  {

    private String[] beargs;


    private static final Logger LOG = LoggerFactory.getLogger(RegisterFileAction.class);
    public RegisterFileAction registerAndEnqueue(String[] be) {

        beargs=be;

        return this;
    }

    @Override
    public void execute(FileSystem fileSystem, Path filePath) throws IOException {
        String path=filePath.getName();
        String fileHash=fileSystem.getFileChecksum(filePath).toString();
        LOG.debug("the path of current file is " + path);
        String folderPath= IMConfig.getProperty("mq-import.target-directory", MQTopology.environment);
        String fPath;
        if(folderPath.charAt(folderPath.length()-1)=='/')
            fPath=folderPath;
        else
            fPath=folderPath+"/";
        String fSUnits= IMConfig.getProperty("mq-import.file-size-units", MQTopology.environment);
        String fileSize= IMConfig.getProperty("mq-import.rotation-file-size", MQTopology.environment);
        if(fSUnits.toUpperCase().equals("KB")) {
            fileSize=(Long.parseLong(fileSize)*1024)+""; }
        else if (fSUnits.toUpperCase().equals("MB")) {
            fileSize=(Long.parseLong(fileSize)*1024*1024)+""; }
        else if (fSUnits.toUpperCase().equals("GB")) {
            fileSize=(Long.parseLong(fileSize)*1024*1024*1024)+""; }
        else if (fSUnits.toUpperCase().equals("TB")) {
            fileSize=(Long.parseLong(fileSize)*1024*1024*1024*1024)+""; }
        beargs[5]=fPath+path;
        beargs[7]=fileSize;
        beargs[9]=fileHash;
        BatchEnqueuer be= new BatchEnqueuer();
        be.execute(beargs);

        return;
    }

}
