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

/**
 * Created by SU324335 on 09-Nov-15.
 */

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Transactional
@Service
public class BatchEnqueuerDAO {
    private static final Logger LOGGER = Logger.getLogger(BatchEnqueuerDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue> batchEnqueue(RegisterFileInfo registerFileInfo) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue> bcqs = new ArrayList<com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue>();
        try {
            Long finalBatchID;

            session.beginTransaction();

            if (registerFileInfo.getBatchId() == null) {
                Batch batch = new Batch();
                batch.setInstanceExec(null);
                batch.setBatchType("file");
                session.save(batch);
                finalBatchID = batch.getBatchId();
            } else {
                finalBatchID = registerFileInfo.getBatchId();

            }
            LOGGER.info("FinalBatchId is" + finalBatchID);
            //adding file to database
            File file = new File();
            Batch batch = new Batch();
            batch.setBatchId(finalBatchID);
            file.setBatch(batch);
            Servers servers = new Servers();
            servers.setServerId(registerFileInfo.getServerId());
            file.setServers(servers);
            FileId fileId = new FileId();
            fileId.setBatchId(finalBatchID);
            fileId.setServerId(registerFileInfo.getServerId());
            fileId.setCreationTs(registerFileInfo.getCreationTs());
            fileId.setFileHash(registerFileInfo.getFileHash());
            fileId.setFileSize(registerFileInfo.getFileSize());
            fileId.setPath(registerFileInfo.getPath());
            file.setId(fileId);
            session.save(file);
            Integer proId;
            List<Process> list = session.createCriteria(Process.class).add(Restrictions.eq("enqueuingProcessId", registerFileInfo.getParentProcessId())).list();
            Iterator<Process> iterator = list.iterator();
            LOGGER.info("Total number of processes is" + list.size());
            while (iterator.hasNext()) {
                proId = iterator.next().getProcessId();
                BatchConsumpQueue batchConsumpQueue = new BatchConsumpQueue();
                Batch sourceBatch = (Batch) session.get(Batch.class, finalBatchID);
                batchConsumpQueue.setBatchBySourceBatchId(sourceBatch);

                batchConsumpQueue.setSourceProcessId(registerFileInfo.getParentProcessId());
                batchConsumpQueue.setInsertTs(new Date());
                batchConsumpQueue.setBatchStatus((BatchStatus) session.get(BatchStatus.class, 0));
                batchConsumpQueue.setBatchMarking(registerFileInfo.getBatchMarking());
                batchConsumpQueue.setProcess((Process) session.get(Process.class, proId));
                session.save(batchConsumpQueue);
            }


            Batch batch1 = (Batch) session.get(Batch.class, finalBatchID);
            List<BatchConsumpQueue> list1 = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("batchBySourceBatchId", batch1)).list();
            LOGGER.info("Total size of Batches in queue is" + list1.size());
            Iterator iterator1 = list1.iterator();
            while (iterator1.hasNext()) {
                BatchConsumpQueue batchConsumpQueue = (BatchConsumpQueue) iterator1.next();
                com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue batchConsumpQueue1 = new com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue();
                batchConsumpQueue1.setSourceBatchId(batchConsumpQueue.getBatchBySourceBatchId().getBatchId());
                if (batchConsumpQueue.getBatchByTargetBatchId() != null)
                    batchConsumpQueue1.setTargetBatchId(batchConsumpQueue.getBatchByTargetBatchId().getBatchId());
                batchConsumpQueue1.setQueueId(batchConsumpQueue.getQueueId());
                batchConsumpQueue1.setInsertTs(batchConsumpQueue.getInsertTs());
                batchConsumpQueue1.setSourceProcessId(batchConsumpQueue.getSourceProcessId());
                batchConsumpQueue1.setStartTs(batchConsumpQueue.getStartTs());

                bcqs.add(batchConsumpQueue1);


            }
            session.getTransaction().commit();
            //list of batch consumption queues returned
            return bcqs;

        } catch (MetadataException e) {
            LOGGER.error("Error occurred", e);
            session.getTransaction().rollback();
            return bcqs;
        } finally {
            session.close();
        }
    }
}
