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

import com.wipro.ats.bdre.ResolvePath;
import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.RegisterFile;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;
import org.apache.commons.io.FilenameUtils;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MO335755 on 12/23/2015.
 */
public class QueuedFileUploader {

    private static final Logger LOGGER = Logger.getLogger(QueuedFileUploader.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static Configuration config = new Configuration();

    private QueuedFileUploader(){

    }

    private static void hdfsCopy(FileCopyInfo fileCopying) throws IOException {
        try {
            LOGGER.info("in the hdfs copy method");
            // Copying file from local to HDFS overriding, if file already exists
            config.set("fs.defaultFS", FileMonRunnableMain.getDefaultFSName());
            if("true".equals(FileMonRunnableMain.getKerberosEnabled() )) {
                File f = new File(FileMonRunnableMain.getHadoopConfDir());
                URL u = f.toURL();
                URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class urlClass = URLClassLoader.class;
                Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
                method.setAccessible(true);
                method.invoke(urlClassLoader, new Object[]{u});
                config.set("hadoop.security.authentication", "Kerberos");
                UserGroupInformation.setConfiguration(config);
                UserGroupInformation.loginUserFromKeytab(FileMonRunnableMain.getKerberosUserName(), FileMonRunnableMain.getKerberosKeytabFileLocation());
            }
            FileSystem fs = FileSystem.get(config);
            String destDir = fileCopying.getDstLocation();
            Path destPath = new Path(ResolvePath.replaceVars(destDir));
            if(!fs.exists(destPath)){
                LOGGER.info("Creating HDFS dest dir "+destPath+ " Success="+fs.mkdirs(destPath));
            }
            if(FileMonRunnableMain.isDeleteCopiedSrc()) {
                fs.copyFromLocalFile(true, true, new Path(fileCopying.getSrcLocation()),
                        destPath);
            }else{
                fs.copyFromLocalFile(false, true, new Path(fileCopying.getSrcLocation()),
                        destPath);

                File sourceFile = new File(fileCopying.getSrcLocation());
                String arcDir = destDir.replace(FileMonRunnableMain.getHdfsUploadDir(),FileMonRunnableMain.getMonitoredDirName()+"/"+FileMonRunnableMain.ARCHIVE);
                File arcDirFile = new File(arcDir);
                FileUtils.moveFileToDirectory(sourceFile, arcDirFile, true);
            }
        } catch (Exception e) {
            FileMonitor.addToQueue(fileCopying.getFileName(), fileCopying);
            LOGGER.error("Error in executeCopyProcess method. Requeuing file " + fileCopying.getFileName(), e);
            throw new IOException(e);
        }
    }

    public static void executeCopyProcess() {
        // this variable is used to keep details of file being currently copying
        if (FileMonitor.getQueueSize() > 0) {
            FileCopyInfo fileCopying = FileMonitor.getFileInfoFromQueue();
            try {
                hdfsCopy(fileCopying);
                // calling register file
                executeRegisterFiles(fileCopying);
            } catch (Exception err) {
                LOGGER.error("Error in execute copy process ", err);
                throw new BDREException(err);
            }
        }
    }

    private static void executeRegisterFiles(FileCopyInfo fileCopying) {
        try {
            String subProcessId = fileCopying.getSubProcessId();
            String serverId = fileCopying.getServerId();
            String path = fileCopying.getDstLocation()+"/"+FilenameUtils.getName(fileCopying.getFileName());
            String fileHash = fileCopying.getFileHash();
            String fileSize = String.valueOf(fileCopying.getFileSize());
            long timeStamp = fileCopying.getTimeStamp();
            Date dt = new Date(timeStamp);
            String strDate = sdf.format(dt);
            String[] params = {"-p", subProcessId, "-sId", serverId, "-path", path, "-fs", fileSize, "-fh", fileHash, "-cTS", strDate, "-bid", "0"};
            LOGGER.debug("executeRegisterFiles Invoked for " + path);
            new RegisterFile().execute(params);
        } catch (Exception err) {
            LOGGER.error("Error execute register files ", err);
            throw new BDREException(err);
        }
    }

}
