/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.GetLineageByBatchInfo;
import com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by PR324290 on 11/3/2015.
 */
@Transactional
@Service
public class LineageByBatchDAO {

    private static final Logger LOGGER = Logger.getLogger(LineageByBatchDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<GetLineageByBatchInfo> lineageByBatch(GetLineageByBatchInfo getLineageByBatchInfo) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Batch batch = (Batch) session.get(Batch.class, getLineageByBatchInfo.getTargetBatchId());
        List<GetLineageByBatchInfo> outputList = new ArrayList<GetLineageByBatchInfo>();

        try {
            if (batch != null) {
                Process process = new Process();
                if (batch.getInstanceExec() != null) {
                    Long sourceInstanceExecutionId = batch.getInstanceExec().getInstanceExecId();
                    LOGGER.info("source instance execution id is" + sourceInstanceExecutionId);

                    process = (Process) session.get(Process.class, batch.getInstanceExec().getProcess().getProcessId());
                    LOGGER.info("processid is " + batch.getInstanceExec().getProcess().getProcessId());
                }
                //obtaining the batches which is in consumption queue
                List<BatchConsumpQueue> list = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("batchByTargetBatchId", batch)).list();
                //obtaining the batches which is in archive queue i.e completed processes
                LOGGER.info("size of baches in the queue" + list.size());
                List<ArchiveConsumpQueue> list1 = session.createCriteria(ArchiveConsumpQueue.class).add(Restrictions.eq("batchByTargetBatchId", batch)).list();
                LOGGER.info("size of baches  completed in the queue" + list1.size());
                //putting all those objects in api bean

                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    BatchConsumpQueue batchConsumpQueue = (BatchConsumpQueue) iterator.next();
                    GetLineageByBatchInfo getLineageByBatchInfo1 = new GetLineageByBatchInfo();
                    getLineageByBatchInfo1.setExecState(batch.getInstanceExec().getExecStatus().getExecStateId());
                    getLineageByBatchInfo1.setTargetBatchId(getLineageByBatchInfo.getTargetBatchId());
                    getLineageByBatchInfo1.setSourceBatchId(batchConsumpQueue.getBatchBySourceBatchId().getBatchId());
                    getLineageByBatchInfo1.setInstanceExecId(batch.getInstanceExec().getInstanceExecId());
                    getLineageByBatchInfo1.setStartTime(new java.sql.Timestamp(batch.getInstanceExec().getStartTs().getTime()));
                    if (batch.getInstanceExec().getEndTs() != null) {
                        getLineageByBatchInfo1.setEndTime(new java.sql.Timestamp(batch.getInstanceExec().getEndTs().getTime()));
                    }

                    getLineageByBatchInfo1.setProcessDesc(process.getDescription());
                    getLineageByBatchInfo1.setProcessName(process.getProcessName());
                    getLineageByBatchInfo1.setProcessId(process.getProcessId());

                    outputList.add(getLineageByBatchInfo1);
                }
                Iterator iterator1 = list1.iterator();
                while (iterator1.hasNext()) {
                    ArchiveConsumpQueue archiveConsumpQueue = (ArchiveConsumpQueue) iterator1.next();
                    GetLineageByBatchInfo getLineageByBatchInfo1 = new GetLineageByBatchInfo();
                    getLineageByBatchInfo1.setExecState(batch.getInstanceExec().getExecStatus().getExecStateId());
                    getLineageByBatchInfo1.setTargetBatchId(getLineageByBatchInfo.getTargetBatchId());
                    getLineageByBatchInfo1.setSourceBatchId(archiveConsumpQueue.getBatchBySourceBatchId().getBatchId());
                    getLineageByBatchInfo1.setInstanceExecId(batch.getInstanceExec().getInstanceExecId());
                    getLineageByBatchInfo1.setStartTime(new java.sql.Timestamp(batch.getInstanceExec().getStartTs().getTime()));
                    if (batch.getInstanceExec().getEndTs() != null)
                        getLineageByBatchInfo1.setEndTime(new java.sql.Timestamp(batch.getInstanceExec().getEndTs().getTime()));
                    getLineageByBatchInfo1.setProcessDesc(process.getDescription());
                    getLineageByBatchInfo1.setProcessName(process.getProcessName());
                    getLineageByBatchInfo1.setProcessId(process.getProcessId());

                    outputList.add(getLineageByBatchInfo1);
                }
                LOGGER.info("total no of objects to be returned is" + outputList.size());
                session.getTransaction().commit();
            } else {
                throw new MetadataException("Invalid Target BatchId");
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new MetadataException(e);
        } finally {
            session.close();
        }

        return outputList;

    }


}

