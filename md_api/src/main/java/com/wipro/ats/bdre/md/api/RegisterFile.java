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

package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.md.dao.RegisterFileDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Timestamp;

/**
 * Created by arijit on 12/8/14.
 */
public class RegisterFile extends MetadataAPIBase {
    public RegisterFile() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(RegisterFile.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "sub-process-id", "Process Id of the sub process"},
            {"sId", "server-id", "id of server"},
            {"path", "file-path", "path of the file"},
            {"fs", "file-size", "size of the file"},
            {"fh", "file-hash", "file hash"},
            {"cTS", "creation-timestamp", "creation timestamp"},
            {"bid", "batch-id", "Batch id(use null for auto-generated batchid)"}
    };

    /**
     * This method runs RegisterFileProc proc in mysql and returns the input data back.
     *
     * @param params String array containing sub-process-id,server-id, path, file-size, file-hash, creation-timestamp,
     * batch-id with their respective notation on command line.
     * @return This method returns same input data as instance of RegisterFIleInfo class.
     */
    @Autowired
    private RegisterFileDAO registerFileDAO;

    public RegisterFileInfo execute(String[] params) {

        try {
            RegisterFileInfo registerFileInfo = new RegisterFileInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pid = commandLine.getOptionValue("sub-process-id");
            LOGGER.debug("subProcessId is " + pid);
            String sId = commandLine.getOptionValue("server-id");
            LOGGER.debug("serverId is " + sId);
            String path = commandLine.getOptionValue("path");
            LOGGER.debug("path is " + path);
            String fSize = commandLine.getOptionValue("file-size");
            LOGGER.debug("file size is " + fSize);
            String fHash = commandLine.getOptionValue("file-hash");
            LOGGER.debug("file-hash " + fHash);
            String creationTs = commandLine.getOptionValue("creation-timestamp");
            LOGGER.debug("creation Ts " + creationTs);
            String batchId = commandLine.getOptionValue("batch-id");
            LOGGER.debug("batchId " + batchId);
            //Setting the default batch with batch id as null
            if ("0".equalsIgnoreCase(batchId) || "NULL".equalsIgnoreCase(batchId)) {
                registerFileInfo.setBatchId(null);
            } else {
                registerFileInfo.setBatchId(new Long(batchId));
            }

            registerFileInfo.setSubProcessId(Integer.parseInt(pid));
            registerFileInfo.setPath(path);
            registerFileInfo.setServerId(Integer.parseInt(sId));
            registerFileInfo.setFileSize(Long.parseLong(fSize));
            registerFileInfo.setFileHash(fHash);
            registerFileInfo.setCreationTs(Timestamp.valueOf(creationTs));
            //Calling proc RegisterFile
//            registerFileInfo = s.selectOne("call_procedures.RegisterFile", registerFileInfo);
            registerFileInfo = registerFileDAO.registerFile(registerFileInfo);
            LOGGER.debug("registerFileInfo " + registerFileInfo.getPath() + " " + registerFileInfo.getBatchId());
            LOGGER.debug("registerFileInfo " + registerFileInfo.getSubProcessId() + " " + registerFileInfo.getCreationTs());
            return registerFileInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}