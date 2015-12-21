/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api.sftp;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetFiles;
import com.wipro.ats.bdre.md.beans.FileInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SFTP2HDFS extends BaseStructure {
    private static final Logger LOGGER = Logger.getLogger(SFTP2HDFS.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"minB", "min-batch-id", "minimum batch id"},
            {"maxB", "max-batch-id", "maximum batch id"}
    };

    private static void loadToHDFS(String localFile, String hdfsFile, String env) {
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name", env));
            FileSystem fs = FileSystem.get(conf);
            Path hdfsPath = new Path(hdfsFile);
            Path localPath = new Path(localFile);
            fs.copyFromLocalFile(false, true, localPath, hdfsPath);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public void execute(String[] params) {
        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String minBId = commandLine.getOptionValue("min-batch-id");
        LOGGER.debug("minimum-batch-id is " + minBId);
        String maxBId = commandLine.getOptionValue("max-batch-id");
        LOGGER.debug("maximum-batch-id is " + maxBId);
        String env = commandLine.getOptionValue("environment-id");
        LOGGER.debug("environment-id is " + env);
        String[] args = {"--maxB", maxBId, "--minB", minBId, "-env", "env1"};
        GetFiles getFiles = new GetFiles();
        List<FileInfo> fileInfos = getFiles.execute(args);
        SFTP sftp = new SFTP();
        LOGGER.info("total files to be downloaded " + fileInfos.size());
        for (FileInfo fileInfo : fileInfos) {
            LOGGER.info("Starting download of " + fileInfo.getFilePath());
            String localFile = IMConfig.getProperty("etl.local-download-directory", env) + fileInfo.getFilePath();
            File file = new File(localFile);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            } else if (!file.getParentFile().isDirectory()) {
                throw new ETLException(file.getParentFile() + " is a directory. This names matches with file to be download.");
            }
            sftp.download(fileInfo, localFile);

        }
        for (FileInfo fileInfo : fileInfos) {
            LOGGER.info("Uploading to HDFS: " + fileInfo.getFilePath());
            String localFile = IMConfig.getProperty("etl.local-download-directory", env) + fileInfo.getFilePath();
            String hdfsFile = IMConfig.getProperty("etl.hdfs-raw-directory", env) + fileInfo.getFilePath();
            loadToHDFS(localFile, hdfsFile, env);

        }

    }


}