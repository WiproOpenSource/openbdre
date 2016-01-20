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

package com.wipro.ats.bdre.md.dao;


import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by SH324337 on 11/2/2015.
 */
@Transactional
@Service
public class RegisterFileDAO {
    private static final Logger LOGGER = Logger.getLogger(RegisterFileDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public RegisterFileInfo registerFile(RegisterFileInfo registerFileInfo) {
        Session session = sessionFactory.openSession();
        long autoGenBatchId;
        try {
            session.beginTransaction();
            Batch batchObj = new Batch();
            if (registerFileInfo.getBatchId() == null) {

                batchObj.setBatchType("Type1");
                batchObj.setInstanceExec(null);

                session.save(batchObj);


                autoGenBatchId = batchObj.getBatchId();
                LOGGER.info("Auto generated BatchId" + autoGenBatchId);
            } else {
                autoGenBatchId = registerFileInfo.getBatchId();
                batchObj.setBatchId(autoGenBatchId);
            }
            registerFileInfo.setBatchId(autoGenBatchId);
            Servers servers = new Servers();
            servers.setServerId(registerFileInfo.getServerId());
            FileId fileId = new FileId();
            fileId.setPath(registerFileInfo.getPath());
            fileId.setFileHash(registerFileInfo.getFileHash());
            fileId.setBatchId(registerFileInfo.getBatchId());
            fileId.setServerId(registerFileInfo.getServerId());
            fileId.setFileSize(registerFileInfo.getFileSize());
            fileId.setCreationTs(registerFileInfo.getCreationTs());

            Criteria fileCriteria = session.createCriteria(File.class).add(Restrictions.eq("servers", servers)).add(Restrictions.eq("id.fileHash", fileId.getFileHash())).add(Restrictions.eq("id.path", fileId.getPath()));
            LOGGER.info("matched file count" + fileCriteria.list().size());
            if (fileCriteria.list().size() > 0) {
                LOGGER.error("File Already exists exception");
                throw new MetadataException("File Already exists exception");
            } else {

                File newFile = new File();
                newFile.setId(fileId);
                newFile.setServers(servers);
                newFile.setBatch(batchObj);
                session.save(newFile);
                BatchStatus batchStatusObj = new BatchStatus();
                batchStatusObj.setBatchStateId(1);

                Process subProcess = (Process) session.get(Process.class, registerFileInfo.getSubProcessId());

                Process parentProcess = subProcess.getProcess();
                Criteria criteria = session.createCriteria(Process.class).add(Restrictions.eq("enqueuingProcessId",parentProcess.getProcessId()));
                List<Process> processList = criteria.list();
                for(Process process: processList) {
                    BatchConsumpQueue batchConsumpQueueObj = new BatchConsumpQueue();
                    batchConsumpQueueObj.setBatchBySourceBatchId(batchObj);
                    batchConsumpQueueObj.setSourceProcessId(registerFileInfo.getSubProcessId());
                    batchConsumpQueueObj.setInsertTs(registerFileInfo.getCreationTs());
                    batchConsumpQueueObj.setBatchStatus(batchStatusObj);
                    batchConsumpQueueObj.setBatchMarking(registerFileInfo.getBatchMarking());
                    batchConsumpQueueObj.setProcess(process);
                    session.save(batchConsumpQueueObj);
                }
            }
            session.getTransaction().commit();

            return registerFileInfo;

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }

    }

}

