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

import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
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

    public static String getSubProcessId() {
        return subProcessId;
    }

    public static String getDefaultFSName() {
        return defaultFSName;
    }

    //Created a dummy PARAMS_STRUCTURE variable to get the environment value.
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", "Process Id of the process to begin"}

    };

    public static void main(String[] args) {
        FileMonRunnableMain f2SFileMonitorMain = new FileMonRunnableMain();
        f2SFileMonitorMain.execute(args);
    }

    private void execute(String[] params) {
        try {
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);

            GetProcess getProcess = new GetProcess();
            List<ProcessInfo> subProcessList = getProcess.execute(params);
            subProcessId = subProcessList.get(1).getProcessId().toString();

            GetProperties getProperties = new GetProperties();
            Properties properties = getProperties.getProperties(subProcessId, "fileMon");
            LOGGER.info("property is " + properties);
            GetGeneralConfig generalConfig = new GetGeneralConfig();
            GeneralConfig gc = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name");

            defaultFSName = gc.getDefaultVal();
            monitoredDirName = properties.getProperty("monitoredDirName");
            filePattern = properties.getProperty("filePattern");
            hdfsUploadDir = properties.getProperty("hdfsUploadDir");

            deleteCopiedSrc = Boolean.parseBoolean(properties.getProperty("deleteCopiedSrc"));
            sleepTime = Long.parseLong(properties.getProperty("sleepTime"));
            if (sleepTime < 100) {
                sleepTime=100;
            }

            //Now run the monitoring thread
            //This is a daemon thread
            FileSystemManager fsManager = VFS.getManager();
            //Reading directory paths and adding to the DefaultFileMonitor
            String dir = FileMonRunnableMain.getMonitoredDirName();
            DefaultFileMonitor fm = new DefaultFileMonitor(FileMonitor.getInstance());
            FileObject listenDir = fsManager.resolveFile(dir);
            LOGGER.debug("Monitoring directories " + dir);
            fm.setRecursive(false);
            fm.addFile(listenDir);
            fm.start();
            //Now scan the mondir for existing files and add to queue
            FileScan.scanAndAddToQueue();
            //Now starting the consumer thread
            Thread consumerThread1 = new Thread(new QueueConsumerRunnable());
            consumerThread1.start();

            Thread consumerThread2 = new Thread(new QueueConsumerRunnable());
            consumerThread2.start();
        } catch (Exception err) {
            LOGGER.error(err);
            throw new ETLException(err);
        }
    }
}
