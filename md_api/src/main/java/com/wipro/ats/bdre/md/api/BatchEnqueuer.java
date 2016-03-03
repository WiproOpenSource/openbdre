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
import com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue;
import com.wipro.ats.bdre.md.dao.BatchEnqueuerDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by SR294224 on 5/29/2015.
 */
public class BatchEnqueuer extends MetadataAPIBase {

    @Autowired
    BatchEnqueuerDAO batchEnqueuerDAO;
    private static final Logger LOGGER = Logger.getLogger(BatchEnqueuer.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "parent-process-id", "Process Id of the parent"},
            {"sId", "server-id", "id of server"},
            {"path", "file-path", "path of the file"},
            {"fs", "file-size", "size of the file"},
            {"fh", "file-hash", "file hash"},
            {"cTS", "creation-timestamp", "creation timestamp"},
            {"bid", "batch-id", "Batch id(use null for auto-generated batchid)"},
            {"bm", "batch-marking", "Batch marking"}
    };

    public BatchEnqueuer() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }
    /**
     * This method runs BatchEnqueuer proc for a process id and registers the file and
     * enqueues the batch to the downstream process.
     *
     * @param params String array containing parentProcessId,serverId,path,fileSize,fileHash,
     *               creationTs,batchId,batchMarking,env with their respective notation on command line.
     * @return This method return output of BatchEnqueuer proc having information regarding
     * batch queued in batch_consump_queue.
     */
    public List<BatchConsumpQueue> execute(String[] params) {

        try {
            RegisterFileInfo registerFileInfo = new RegisterFileInfo();
            List<BatchConsumpQueue> bcqs;
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pid = commandLine.getOptionValue("parent-process-id");
            LOGGER.debug("parentProcessId is " + pid);
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
            if ("0".equalsIgnoreCase(batchId) || "NULL".equalsIgnoreCase(batchId)) {
                registerFileInfo.setBatchId(null);
            } else {
                registerFileInfo.setBatchId(new Long(batchId));
            }
            String batchMarking = commandLine.getOptionValue("batch-marking");
            LOGGER.debug("batch-marking " + batchMarking);


            //Populating the registerFileInfo bean
            registerFileInfo.setParentProcessId(Integer.parseInt(pid));
            registerFileInfo.setPath(path);
            registerFileInfo.setServerId(Integer.parseInt(sId));
            registerFileInfo.setFileSize(Long.parseLong(fSize));
            registerFileInfo.setFileHash(fHash);
            registerFileInfo.setCreationTs(Timestamp.valueOf(creationTs));
            registerFileInfo.setBatchMarking(batchMarking);
            //Calling the BatchEnqueuerDAO with registerFileInfo as parameter

            bcqs = batchEnqueuerDAO.batchEnqueue(registerFileInfo);
            LOGGER.debug("registerFileInfo " + registerFileInfo.getPath() + " " + registerFileInfo.getBatchId());
            LOGGER.debug("registerFileInfo " + registerFileInfo.getParentProcessId() + " " + registerFileInfo.getCreationTs());
            return bcqs;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
