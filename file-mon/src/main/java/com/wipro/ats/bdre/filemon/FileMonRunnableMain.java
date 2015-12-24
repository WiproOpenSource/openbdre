/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.log4j.Logger;

import java.util.List;
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
    private static long sleepTime;
    private static String defaultFSName;

    public static long getSleepTime() {
        return sleepTime;
    }

    public static void setSleepTime(long sleepTime) {
        FileMonRunnableMain.sleepTime = sleepTime;
    }



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

    public static String getDefaultFSName() {
        return defaultFSName;
    }

    private void execute(String[] params) {
        try {
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            GetProperties getProperties = new GetProperties();
            //LOGGER.info("property is "+commandLine.getOptionValue("p"));
            String pid=commandLine.getOptionValue("p");
            // getting subpid that comes under pid
            ProcessDAO processDAO = new ProcessDAO();
            List<Process> subProcessList = processDAO.subProcesslist(Integer.parseInt(pid));
            subProcessId = subProcessList.get(0).getProcessId().toString();

            Properties properties=getProperties.getProperties(subProcessId, "fileMon");
            LOGGER.info("property is "+properties);
            GetGeneralConfig generalConfig = new GetGeneralConfig();
            GeneralConfig gc=generalConfig.byConigGroupAndKey("imconfig","common.default-fs-name");

            defaultFSName=gc.getDefaultVal();
            monitoredDirName=properties.getProperty("monitoredDirName");
            filePattern=properties.getProperty("filePattern");
            hdfsUploadDir=properties.getProperty("hdfsUploadDir");

            deleteCopiedSrc=Boolean.parseBoolean(properties.getProperty("deleteCopiedSrc"));
            sleepTime = Long.parseLong(properties.getProperty("sleepTime"));

            //Now run the monitoring thread
            //This is a daemon thread
            FileSystemManager fsManager = VFS.getManager();
            //Reading directory paths and adding to the DefaultFileMonitor
            String dir = FileMonRunnableMain.getMonitoredDirName();
            DefaultFileMonitor fm = new DefaultFileMonitor(FileMonitor.getInstance());
            FileObject listendir = fsManager.resolveFile(dir);;
            LOGGER.debug("Monitoring directories " + dir);
            LOGGER.info("Dir value"+ listendir);
            fm.setRecursive(false);
            fm.addFile(listendir);
            fm.start();

            //Now starting the consumer thread
            Thread consumerThread1 = new Thread(new QueueConsumerRunnable());
            consumerThread1.start();

            Thread consumerThread2 = new Thread(new QueueConsumerRunnable());
            consumerThread2.start();
        } catch (Exception err) {
            LOGGER.error(err.getMessage());
            throw new ETLException(err);
        }
    }
}
