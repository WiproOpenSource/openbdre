/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by vishnu on 1/11/15.
 */
public class FileMonRunnableMain extends ETLBase {
    private static final Logger LOGGER = Logger.getLogger(FileMonRunnableMain.class);
    private static String monitoredDirName = "";
    private static String filePattern = "";
    private static boolean deleteCopiedSrc = false;
    private static String hdfsUploadDir = "";
    private static String subProcessId = "";

    public static String getFilePattern() {
        return filePattern;
    }

    public static void setFilePattern(String filePattern) {
        FileMonRunnableMain.filePattern = filePattern;
    }

    public static String getMonitoredDirName() {
        return monitoredDirName;
    }

    public static void setMonitoredDirName(String monitoredDirName) {
        FileMonRunnableMain.monitoredDirName = monitoredDirName;
    }

    public static boolean isDeleteCopiedSrc() {
        return deleteCopiedSrc;
    }

    public static void setDeleteCopiedSrc(boolean deleteCopiedSrc) {
        FileMonRunnableMain.deleteCopiedSrc = deleteCopiedSrc;
    }

    public static String getHdfsUploadDir() {
        return hdfsUploadDir;
    }

    public static void setHdfsUploadDir(String hdfsUploadDir) {
        FileMonRunnableMain.hdfsUploadDir = hdfsUploadDir;
    }


    //Created a dummy PARAMS_STRUCTURE variable to get the environment value.
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", "Process Id of the process to begin"}

    };

    public static void main(String[] args) {


        FileMonRunnableMain f2SFileMonitorMain = new FileMonRunnableMain();
        f2SFileMonitorMain.execute(args);
    }

    public static String getSubProcessId() {
        return subProcessId;
    }

    private void execute(String[] params) {
        try {
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            GetProperties getProperties = new GetProperties();
            //LOGGER.info("property is "+commandLine.getOptionValue("p"));
            Properties properties=getProperties.getProperties(commandLine.getOptionValue("p"), "fileMon");
            LOGGER.info("property is "+properties);

            //mrt.setEnv(env);

            monitoredDirName=properties.getProperty("monitoredDirName");
            filePattern=properties.getProperty("filePattern");
            hdfsUploadDir=properties.getProperty("hdfsUploadDir");
            subProcessId=properties.getProperty("subProcessId");
            deleteCopiedSrc=Boolean.parseBoolean(properties.getProperty("deleteCopiedSrc"));
            FileMonRunnable fileMonRunnable = new FileMonRunnable();
            Thread t = new Thread(fileMonRunnable);
            t.start();
            long sleepTime = Long.parseLong(properties.getProperty("sleepTime"));

            while (FileMonRunnable.runnableCount <= 10) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException iex) {
                    LOGGER.error(iex.getMessage());
                    throw new ETLException(iex);
                }
            }
        } catch (Exception err) {
            LOGGER.error(err.getMessage());
            throw new ETLException(err);
        }
    }
}
