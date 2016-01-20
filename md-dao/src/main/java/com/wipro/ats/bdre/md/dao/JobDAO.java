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
import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by KA294215 on 28-10-2015.
 */
@Transactional
@Service
public class JobDAO {
    private static final Logger LOGGER = Logger.getLogger(JobDAO.class);
    @Autowired
    SessionFactory sessionFactory;


    public List<InitJobRowInfo> initJob(Integer processId, Integer maxBatch) throws Exception {
        Integer lastRecoverableSPId = null;
        List<InitJobRowInfo> initJobRowInfos = new ArrayList<InitJobRowInfo>();

        BatchStatus accessedBatchStatus = new BatchStatus();
        accessedBatchStatus.setBatchStateId(2);
        Long targetBatchId = null;
        // Beginning sql session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            //  check if max_batch is null
            Criteria maxBatchNullCheckCriteria = session.createCriteria(Process.class);
            Process parentProcess = new Process();
            parentProcess.setProcessId(processId);
            maxBatchNullCheckCriteria.add(Restrictions.eq("process", parentProcess)).add(Restrictions.ne("enqueuingProcessId", 0)).add(Restrictions.eq("deleteFlag", false));
            Integer countOfProcWithBCP = maxBatchNullCheckCriteria.list().size();
            Criteria batchCutPatternCriteria= session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess)).add(Restrictions.ne("enqueuingProcessId", 0)).add(Restrictions.eq("deleteFlag", false))
                    .add(Restrictions.isNotNull("batchCutPattern"));
            Integer countOfProcWithOutBCP = batchCutPatternCriteria.list().size();
            if (countOfProcWithOutBCP < countOfProcWithBCP) {
                if (maxBatch == null) {
                    LOGGER.error("max_batch cannot be null");
                    throw new MetadataException("max_batch cannot be null");
                }
            }

            //check if the process is a valid parent_process
            Criteria validParentProcessCheckCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processId", processId))
                    .add(Restrictions.eq("deleteFlag", false));
            Integer validProcessCount = validParentProcessCheckCriteria.list().size();
            Process validParentProcessCheck = new Process();
            if (validProcessCount > 0) {
                validParentProcessCheck = (Process) validParentProcessCheckCriteria.list().get(0);
                LOGGER.info("parent process id is :::" + validParentProcessCheck.getProcess());
            }
            if (validProcessCount == 0) {
                LOGGER.error("Invalid parent-process. pid=)" + processId);
                throw new MetadataException("Invalid parent-process. pid=" + processId);
            } else if (validParentProcessCheck.getProcess() != null) {
                LOGGER.error("Invalid parent-process. pid=)" + processId);
                throw new MetadataException("Invalid parent-process. pid=" + processId);
            }

            //check if the process is not a deleted process
            Criteria deleteCheckCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processId", processId)).add(Restrictions.eq("deleteFlag", false));
            Process deleteCheck = (Process) deleteCheckCriteria.list().get(0);
            if (deleteCheck.getDeleteFlag()) {
                LOGGER.error("This is a deleted process. pid=" + processId);
                throw new MetadataException("This is a deleted process. pid=" + processId);
            }

            // check if the process is already running
            Process executionStatusCheckProcess = new Process();
            executionStatusCheckProcess.setProcessId(processId);
            Criteria executionStatusCheckCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", executionStatusCheckProcess)).add(Restrictions.isNull("endTs"));
            if (executionStatusCheckCriteria.list().size() > 0) {
                executionStatusCheckCriteria.setMaxResults(1);
                InstanceExec executionStatusCheck = (InstanceExec) executionStatusCheckCriteria.list().get(0);
                if (executionStatusCheck.getExecStatus().getExecStateId() == 2) {
                    LOGGER.error("Parent Process is already running. pid=" + processId);
                    throw new MetadataException("Parent Process is already running. pid=" + processId);
                }
            }

            // check if the sub processes are already running
            // steps contains
            Process parentProcessId = new Process();
            parentProcessId.setProcessId(processId);
            List<Process> listOfSubProcesses = new ArrayList<Process>();
            Criteria listOfSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcessId)).add(Restrictions.eq("deleteFlag", false));
            listOfSubProcesses = listOfSubProcessCriteria.list();

            ExecStatus runningExecState = new ExecStatus();
            runningExecState.setExecStateId(2);
            Criteria stepsExecutionCheckCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("execStatus", runningExecState));
            if (listOfSubProcesses.size() > 0) {
                for (Process stepsProcess : listOfSubProcesses) {
                    stepsExecutionCheckCriteria.add(Restrictions.eq("process", stepsProcess));
                    if (stepsExecutionCheckCriteria.list().size() > 0) {
                        LOGGER.error("Sub-processes already running! Cannot start this parent process " + processId);
                        throw new MetadataException("Sub-processes already running! Cannot start this parent process" + processId);
                    }
                }
            }
            //listOfSubProcessCriteria.list() gives list of all sub process objects.
            // getting last recoverable sub process id
            // Checking if the last run failed or not
            Process lastRunProcess = new Process();
            lastRunProcess.setProcessId(processId);
            ExecStatus successExecState = new ExecStatus();
            successExecState.setExecStateId(3);
            Criteria lastRunFailedCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", lastRunProcess)).addOrder(Order.desc("endTs"));
            if (lastRunFailedCriteria.list().size() > 0) {
                lastRunFailedCriteria.setMaxResults(1);
                InstanceExec lastRunExecStatus = (InstanceExec) lastRunFailedCriteria.list().get(0);
                if (lastRunExecStatus.getExecStatus().getExecStateId() == 6) {
                    Criteria getSuccessfulStepsCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("execStatus", successExecState));
                    for (Process stepsProcess : listOfSubProcesses) {
                        getSuccessfulStepsCriteria.add(Restrictions.eq("process", stepsProcess));
                    }
                    getSuccessfulStepsCriteria.addOrder(Order.desc("endTs"));

                    for (Object recoverableObject : getSuccessfulStepsCriteria.list()) {
                        InstanceExec successfulRuns = (InstanceExec) recoverableObject;
                        Criteria recoverableRuns = session.createCriteria(Process.class).add(Restrictions.eq("processId", successfulRuns.getProcess().getProcessId())).add(Restrictions.eq("deleteFlag", false));
                        Process recoverableProcess = (Process) recoverableRuns.list().get(0);
                        if (recoverableProcess.getCanRecover()) {
                            lastRecoverableSPId = recoverableProcess.getProcessId();
                            LOGGER.info("last recoverable SP ID is :" + lastRecoverableSPId);
                            break;
                        }
                    }
                }
            }

            // setting lastRecoverableSPId if next process contains ","
            if (lastRecoverableSPId != null) {
                Criteria nextProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processId", lastRecoverableSPId)).add(Restrictions.eq("deleteFlag", false));
                Process nextProcess = (Process) nextProcessCriteria.list().get(0);
                if (!nextProcess.getNextProcessId().contains(",")) {
                    lastRecoverableSPId = Integer.parseInt(nextProcess.getNextProcessId());
                }
            }

            // BatchCheck Proc Implementation
            Integer batchesPresent = null;
            for (Object batchCheckObject : batchCutPatternCriteria.list()) {
                Process batchCheckProcess = (Process) batchCheckObject;
                String batchCutPattern = batchCheckProcess.getBatchCutPattern();
                Criteria batchCheckCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.like("batchMarking", "%" + batchCutPattern + "%"));
                if (batchCheckCriteria.list().size() == 0) {
                    batchesPresent = 0;
                    LOGGER.error("No batch matching the batch-cut-pattern has been found for sub process yet, sub_pid=" + batchCheckProcess.getProcessId());
                    throw new MetadataException("No batch matching the batch-cut-pattern has been found for sub process yet, sub_pid=" + batchCheckProcess.getProcessId());
                }
            }

            int bcqEntries = 0;
            int processEntries = 0;
            Criteria bcqCriteria= session.createCriteria(BatchConsumpQueue.class).add(Restrictions.in("process", listOfSubProcesses));
            LOGGER.debug("bcqcriteria size= "+bcqCriteria.list().size());
            Criteria processCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess)).add(Restrictions.ne("enqueuingProcessId", 0)).add(Restrictions.eq("deleteFlag", false)).add(Restrictions.isNull("batchCutPattern"));
            processEntries=processCriteria.list().size();
            Set uniqueBatchEntries = new HashSet();
            for (Object batchCheckObjectBCQ : bcqCriteria.list()) {
                BatchConsumpQueue bcq = (BatchConsumpQueue) batchCheckObjectBCQ;
                uniqueBatchEntries.add(bcq.getProcess().getProcessId());
            }
            bcqEntries=uniqueBatchEntries.size();
            LOGGER.debug("no.of processes with non zero enq id and null bcp= "+processEntries);
            LOGGER.debug("size of unique processes set = "+bcqEntries);
            if (bcqEntries < processEntries) {
                LOGGER.error("No batches present for one of the sub processes");
                throw new MetadataException("No batches present for one of the sub processes");
            }

            // Checking if its first process of ( there is no upstram process of this process
            // And There is no entry for the subprocess of this process
            Integer sumOfEnqProcessId = 0;
            for (Process enqProcess : listOfSubProcesses) {
                sumOfEnqProcessId += enqProcess.getEnqueuingProcessId();
            }
            Boolean sumOfEnqQueueId = true;
            for (Process bcqEntryProcess : listOfSubProcesses) {
                Criteria bcqEntryCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("process", bcqEntryProcess));
                if (bcqEntryCriteria.list().size() > 0) {
                    sumOfEnqQueueId = false;
                    break;
                }
            }
            com.wipro.ats.bdre.md.dao.jpa.Batch initialBatch = new com.wipro.ats.bdre.md.dao.jpa.Batch();
            initialBatch.setBatchId(0L);
            BatchStatus newBatchStatus = new BatchStatus();
            newBatchStatus.setBatchStateId(0);
            if (sumOfEnqProcessId == 0 && sumOfEnqQueueId) {
                Criteria listOfSubProcessWithoutEnqCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess)).add(Restrictions.eq("enqueuingProcessId", 0)).add(Restrictions.eq("deleteFlag", false));
                for (Object withoutEnqObject : listOfSubProcessWithoutEnqCriteria.list()) {
                    Process withoutEnqProcess = (Process) withoutEnqObject;
                    BatchConsumpQueue batchConsumpqueue = new BatchConsumpQueue();
                    batchConsumpqueue.setBatchBySourceBatchId(initialBatch);
                    batchConsumpqueue.setInsertTs(new Date());
                    batchConsumpqueue.setSourceProcessId(null);
                    batchConsumpqueue.setBatchStatus(newBatchStatus);
                    batchConsumpqueue.setBatchMarking(null);
                    batchConsumpqueue.setProcess(withoutEnqProcess);
                    session.save(batchConsumpqueue);
                }

            }

            //check if there are any non null target_batch_ids and if there are none,
            // create a target_batch_id which is to be updated for all the enqueued batches of all sub processes
            Integer notNullTargetBatches = 0;
            for (Object subProcessObject : listOfSubProcessCriteria.list()) {
                Process subProcess = (Process) subProcessObject;
                Criteria notNullTargetBatchCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.isNotNull("batchByTargetBatchId")).add(Restrictions.eq("process", subProcess));
                if (notNullTargetBatchCriteria.list().size() > 0) {
                    notNullTargetBatches += notNullTargetBatchCriteria.list().size();
                }
            }

            if (notNullTargetBatches == 0) {
                InstanceExec instanceExec = new InstanceExec();
                instanceExec.setProcess(parentProcess);
                instanceExec.setStartTs(new Date());
                instanceExec.setExecStatus(runningExecState);
                session.save(instanceExec);

                LOGGER.info("insert instance exec is " + instanceExec.getInstanceExecId());
                Batch batch = new Batch();
                batch.setInstanceExec(instanceExec);
                batch.setBatchType("Type1");
                session.save(batch);

                targetBatchId = batch.getBatchId();

                //update the batch consump queue table by assigning the target batch id to the max_batch no.of batches
                // and updating the batch state
                Criterion nullBCQ = Restrictions.isNull("batchCutPattern");
                Criterion enptyBCQ = Restrictions.eq("batchCutPattern", "");
                LogicalExpression orExp = Restrictions.or(nullBCQ, enptyBCQ);

                Criteria blankBCPCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess))
                        .add(Restrictions.eq("deleteFlag", false));
                blankBCPCriteria.add(orExp);
                LOGGER.info("blankBCQCriteria size is" + blankBCPCriteria.list().size());
                //Map<Integer,List<BatchConsumpQueue>> queueIdMap = new HashMap<Integer,List<BatchConsumpQueue>>();
                for (Object blankBDPObject : blankBCPCriteria.list()) {
                    Process blankBCPProcess = (Process) blankBDPObject;
                    Criteria entriesForBlankBCPCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("process", blankBCPProcess));
                    // List<BatchConsumpQueue> BCQObjectList = new ArrayList<BatchConsumpQueue>();
                    if (entriesForBlankBCPCriteria.list().size() > 0) {
                        for (int i = 0; i < maxBatch; i++) {
                            BatchConsumpQueue batchConsumpQueueWithNullTBId = new BatchConsumpQueue();
                            batchConsumpQueueWithNullTBId = (BatchConsumpQueue) entriesForBlankBCPCriteria.list().get(i);
                            Criteria updateTargetBatchCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("queueId", batchConsumpQueueWithNullTBId.getQueueId()));
                            BatchConsumpQueue batchConsumpQueueUpdated = (BatchConsumpQueue) updateTargetBatchCriteria.list().get(0);
                            batchConsumpQueueUpdated.setBatchByTargetBatchId(batch);
                            session.update(batchConsumpQueueUpdated);
                            //BCQObjectList.add(batchConsumpQueue);
                        }
                        //queueIdMap.put(blankBCPProcess.getProcessId(),BCQObjectList);

                    }
                }

                Criteria nullBCPCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess))
                        .add(Restrictions.eq("deleteFlag", false)).add(Restrictions.ne("enqueuingProcessId", 0))
                        .add(Restrictions.isNull("batchCutPattern"));
                for (Object nullBDPObject : nullBCPCriteria.list()) {
                    Process blankBCPProcess = (Process) nullBDPObject;
                    Criteria entriesForNullBCPCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("process", blankBCPProcess));
                    // List<BatchConsumpQueue> BCQObjectList = new ArrayList<BatchConsumpQueue>();
                    if (entriesForNullBCPCriteria.list().size() > 0) {
                        for (int i = 0; i < maxBatch; i++) {
                            BatchConsumpQueue batchConsumpQueueWithNewBS = new BatchConsumpQueue();
                            batchConsumpQueueWithNewBS = (BatchConsumpQueue) entriesForNullBCPCriteria.list().get(i);
                            Criteria updateBatchStatusCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("queueId", batchConsumpQueueWithNewBS.getQueueId()));
                            BatchConsumpQueue batchConsumpQueueBSUpdated = (BatchConsumpQueue) updateBatchStatusCriteria.list().get(0);
                            batchConsumpQueueBSUpdated.setBatchStatus(accessedBatchStatus);
                            session.update(batchConsumpQueueBSUpdated);
                            //BCQObjectList.add(batchConsumpQueue);
                        }
                        //queueIdMap.put(blankBCPProcess.getProcessId(),BCQObjectList);

                    }
                }

                Criteria notNullBCPCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess))
                        .add(Restrictions.eq("deleteFlag", false)).add(Restrictions.ne("enqueuingProcessId", 0))
                        .add(Restrictions.isNotNull("batchCutPattern"));
                for (Object notNullBCPObject : notNullBCPCriteria.list()) {
                    Long sourceBatchId = null;
                    Process notNullBCPProcess = (Process) notNullBCPObject;
                    String batchCutPattern = notNullBCPProcess.getBatchCutPattern();
                    Criteria likeBCPCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.like("batchMarking", "%" + batchCutPattern + "%"))
                            .add(Restrictions.eq("process", notNullBCPProcess)).addOrder(Order.asc("batchBySourceBatchId")).setMaxResults(1);
                    if (likeBCPCriteria.list().size() > 0) {
                        BatchConsumpQueue batchConsumpQueue = (BatchConsumpQueue) likeBCPCriteria.list().get(0);
                        sourceBatchId = batchConsumpQueue.getBatchBySourceBatchId().getBatchId();
                    }

                    Criteria updateTargetBatchCriteriaWithBM = session.createCriteria(BatchConsumpQueue.class)
                            .add(Restrictions.le("batchBySourceBatchId", sourceBatchId)).add(Restrictions.eq("process", notNullBCPProcess));

                    for (Object updateBCQObject : updateTargetBatchCriteriaWithBM.list()) {
                        BatchConsumpQueue updateBCQ = (BatchConsumpQueue) updateBCQObject;
                        updateBCQ.setBatchByTargetBatchId(batch);
                        updateBCQ.setBatchStatus(accessedBatchStatus);
                        session.update(updateBCQ);
                    }


                }


            } else {
                //creating a new instance exec which corresponds to the re-run of previously failed run
                InstanceExec instanceExec = new InstanceExec();
                instanceExec.setProcess(parentProcess);
                instanceExec.setStartTs(new Date());
                instanceExec.setExecStatus(runningExecState);
                session.save(instanceExec);

                //updating the batch_state of the subprocesses to 2 in the case when the process failed previously.
                // Target batch id would be the same
                BatchStatus processedBatchStatus = new BatchStatus();
                processedBatchStatus.setBatchStateId(1);
                List<BatchStatus> batchStatuses = new ArrayList<BatchStatus>();
                batchStatuses.add(processedBatchStatus);
                batchStatuses.add(newBatchStatus);
                if (batchStatuses.size() != 0) {
                    Criteria updatingFailedBCQEntryCriteria = session.createCriteria(BatchConsumpQueue.class)
                            .add(Restrictions.in("batchStatus", batchStatuses)).add(Restrictions.isNotNull("batchByTargetBatchId"));
                    for (Object subProcessObject : listOfSubProcessCriteria.list()) {
                        Process subProcess = (Process) subProcessObject;
                        updatingFailedBCQEntryCriteria.add(Restrictions.eq("process", subProcess));
                        if (updatingFailedBCQEntryCriteria.list().size() > 0) {
                            BatchConsumpQueue batchConsumpQueueToUpdate = (BatchConsumpQueue) updatingFailedBCQEntryCriteria.list().get(0);
                            batchConsumpQueueToUpdate.setBatchStatus(accessedBatchStatus);
                        }
                    }
                }


                // updating source_instance_exec_id for batch


                List<Long> listOfTargetBatchId = new ArrayList<Long>();
                for (Object subProcessObject : listOfSubProcessCriteria.list()) {
                    Process subProcess = (Process) subProcessObject;
                    Criteria listOfTargetBatchIdCriteria = session.createCriteria(BatchConsumpQueue.class)
                            .add(Restrictions.eq("process", subProcess));
                    if (listOfTargetBatchIdCriteria.list().size() > 0) {
                        BatchConsumpQueue targetBatchOfBCQ = (BatchConsumpQueue) listOfTargetBatchIdCriteria.list().get(0);
                        listOfTargetBatchId.add(targetBatchOfBCQ.getBatchByTargetBatchId().getBatchId());
                    }
                }

                if (listOfTargetBatchId.size() != 0) {
                    Criteria batchIdCriteria = session.createCriteria(Batch.class).add(Restrictions.in("batchId", listOfTargetBatchId));
                    for (Object batchIdObject : batchIdCriteria.list()) {
                        Batch batchId = (Batch) batchIdObject;
                        batchId.setInstanceExec(instanceExec);
                        session.update(batchId);
                    }
                }
                Criteria newTargetBatchCriteria = session.createCriteria(Batch.class).add(Restrictions.eq("instanceExec", instanceExec));
                Batch newTargetBatch = (Batch) newTargetBatchCriteria.list().get(0);
                targetBatchId = newTargetBatch.getBatchId();


            }


            // returing values in InitJob
            Criteria instanceExecIdCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", parentProcess))
                    .add(Restrictions.eq("execStatus", runningExecState));
            InstanceExec instanceExec = (InstanceExec) instanceExecIdCriteria.list().get(0);
            Long instanceExecId = instanceExec.getInstanceExecId();
            if (listOfSubProcesses.size() != 0) {
                Criteria resultBCQCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.in("process", listOfSubProcesses))
                        .add(Restrictions.isNotNull("batchByTargetBatchId")).addOrder(Order.asc("process")).addOrder(Order.asc("batchBySourceBatchId"));

                for (Object resultBCQObject : resultBCQCriteria.list()) {
                    BatchConsumpQueue resultBCQ = (BatchConsumpQueue) resultBCQObject;
                    InitJobRowInfo initJobRowInfo = new InitJobRowInfo();
                    initJobRowInfo.setInstanceExecId(instanceExecId);
                    initJobRowInfo.setLastRecoverableSpId(lastRecoverableSPId);
                    initJobRowInfo.setSourceBatchId(resultBCQ.getBatchBySourceBatchId().getBatchId());
                    initJobRowInfo.setBatchMarking(resultBCQ.getBatchMarking());
                    initJobRowInfo.setProcessId(resultBCQ.getProcess().getProcessId());
                    initJobRowInfo.setTargetBatchId(resultBCQ.getBatchByTargetBatchId().getBatchId());
                    Criteria resultBatchCriteria = session.createCriteria(Batch.class).add(Restrictions.eq("batchId", resultBCQ.getBatchBySourceBatchId().getBatchId()));
                    Batch resultBatch = (Batch) resultBatchCriteria.list().get(0);
                    try {
                        initJobRowInfo.setSourceInstanceExecId(resultBatch.getInstanceExec().getInstanceExecId());
                    } catch (NullPointerException e) {
                        LOGGER.info("source Instance Exec Id is  NULL");
                        initJobRowInfo.setSourceInstanceExecId(null);
                    }
                    Criteria fileBatchCriteria = session.createCriteria(File.class).add(Restrictions.eq("id.batchId", resultBCQ.getBatchBySourceBatchId().getBatchId()));
                    if (fileBatchCriteria.list().size() > 0) {
                        StringBuilder fileList=new StringBuilder();
                        StringBuilder batchList=new StringBuilder();
                        for(Object fileBatch:fileBatchCriteria.list()) {
                            File file = (File)fileBatch;
                            fileList.append(file.getId().getPath()+",");
                            batchList.append(file.getId().getBatchId()+",");
                        }
                        initJobRowInfo.setFileList(fileList.substring(0,fileList.length()-1).toString());
                        initJobRowInfo.setBatchList(batchList.substring(0,batchList.length()-1).toString());
                    }
                    initJobRowInfos.add(initJobRowInfo);
                }
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return initJobRowInfos;
    }

    public void haltJob(Integer processId, String batchMarking) {
        // Beginning sql session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            Process parentProcessId = new Process();
            parentProcessId.setProcessId(processId);
            List<Process> listOfSubProcesses = new ArrayList<Process>();
            Criteria listOfSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcessId)).add(Restrictions.eq("deleteFlag", false));
            for (Object subProcessObject : listOfSubProcessCriteria.list()) {
                Process subProcess = (Process) subProcessObject;
                listOfSubProcesses.add(subProcess);
            }
            List<Process> listOfDownStreamSubProcessesWithEnqID = new ArrayList<Process>();
            Criteria listOfDownStreamSubProcessesWithEnqIDCriteria = session.createCriteria(Process.class).add(Restrictions.eq("enqueuingProcessId", parentProcessId.getProcessId())).add(Restrictions.eq("deleteFlag", false));
            for (Object subProcessObject : listOfDownStreamSubProcessesWithEnqIDCriteria.list()) {
                Process subProcess = (Process) subProcessObject;
                listOfDownStreamSubProcessesWithEnqID.add(subProcess);
            }
            ExecStatus runningExecStatus = new ExecStatus();
            runningExecStatus.setExecStateId(2);
            ExecStatus failedExecStatus = new ExecStatus();
            failedExecStatus.setExecStateId(6);
            ExecStatus successExecStatus = new ExecStatus();
            successExecStatus.setExecStateId(3);
            BatchStatus newBatchStatus = new BatchStatus();
            newBatchStatus.setBatchStateId(0);
            BatchStatus processedBatchStatus = new BatchStatus();
            processedBatchStatus.setBatchStateId(1);
            if ("null,".equals(batchMarking)) {
                batchMarking = null;
            }

            // checking valid parent process
            Criteria validProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processId", processId)).add(Restrictions.eq("deleteFlag", false));
            if (validProcessCriteria.list().size() == 0) {
                LOGGER.error("Invalid parent-process. pid=" + processId);
                throw new MetadataException("Invalid parent-process. pid=" + processId);
            } else {
                Process validProcess = (Process) validProcessCriteria.list().get(0);
                if (validProcess.getProcess() != null) {
                    LOGGER.error("Invalid parent-process. pid=" + processId);
                    throw new MetadataException("Invalid parent-process. pid=" + processId);
                }
            }

            // checking whether process is running or not

            Criteria runningProcessCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", parentProcessId))
                    .add(Restrictions.eq("execStatus", runningExecStatus));
            if (runningProcessCriteria.list().size() != 1) {
                LOGGER.error("The process is not under execution, pid=" + processId);
                throw new MetadataException("The process is not under execution, pid=" + processId);
            }

            // checking whether sub processes are failed or not
            if (listOfSubProcesses.size() != 0) {
                Criteria runningSubProcessCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.in("process", listOfSubProcesses))
                        .add(Restrictions.eq("execStatus", runningExecStatus));
                if (runningSubProcessCriteria.list().size() > 0) {
                    LOGGER.error("sub process in running state");
                    throw new MetadataException("sub process in running state");
                }
            }
            List<Long> maxInstanceExecIds = new ArrayList<Long>();
            for (Process subProcess : listOfSubProcesses) {
                Criteria maxInstanceExecCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", subProcess))
                        .addOrder(Order.desc("instanceExecId")).setMaxResults(1);

                if (maxInstanceExecCriteria.list().size() == 1) {
                    InstanceExec maxInstanceExecId = (InstanceExec) maxInstanceExecCriteria.list().get(0);
                    maxInstanceExecIds.add(maxInstanceExecId.getInstanceExecId());
                }
            }
            if (maxInstanceExecIds.size() > 0) {
                Criteria failedSubProcessCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.in("instanceExecId", maxInstanceExecIds))
                        .add(Restrictions.eq("execStatus", failedExecStatus));
                if (failedSubProcessCriteria.list().size() > 0) {
                    LOGGER.error("sub process in failed state");
                    throw new MetadataException("sub process in failed state");
                }
            }

            Criteria updateInstanceExecCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", parentProcessId))
                    .add(Restrictions.eq("execStatus", runningExecStatus));
            InstanceExec updateInstanceExec = (InstanceExec) updateInstanceExecCriteria.list().get(0);
            updateInstanceExec.setEndTs(new Date());
            updateInstanceExec.setExecStatus(successExecStatus);
            session.update(updateInstanceExec);

            String batchMarkingPassed;
            if (batchMarking == null) {
                batchMarking = "0";
                batchMarkingPassed = null;
            } else {
                batchMarkingPassed = batchMarking;
            }

            Batch targetBatchId = null;
            if (listOfSubProcesses.size() != 0) {
                Criteria targetBatchIdCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.in("process", listOfSubProcesses))
                        .setMaxResults(1);
                if (targetBatchIdCriteria.list().size() == 1) {
                    BatchConsumpQueue batchConsumpQueue = (BatchConsumpQueue) targetBatchIdCriteria.list().get(0);
                    targetBatchId = batchConsumpQueue.getBatchByTargetBatchId();
                }
            }

            for (Process subProcess : listOfDownStreamSubProcessesWithEnqID) {
                if (batchMarkingPassed == null) {
                    BatchConsumpQueue insertBatchConsumpQueue = new BatchConsumpQueue();
                    insertBatchConsumpQueue.setBatchBySourceBatchId(targetBatchId);
                    insertBatchConsumpQueue.setInsertTs(new Date());
                    insertBatchConsumpQueue.setSourceProcessId(parentProcessId.getProcessId());
                    insertBatchConsumpQueue.setBatchStatus(processedBatchStatus);
                    insertBatchConsumpQueue.setBatchMarking(null);
                    insertBatchConsumpQueue.setProcess(subProcess);
                    session.save(insertBatchConsumpQueue);
                } else {
                    BatchConsumpQueue insertBatchConsumpQueue = new BatchConsumpQueue();
                    insertBatchConsumpQueue.setBatchBySourceBatchId(targetBatchId);
                    insertBatchConsumpQueue.setInsertTs(new Date());
                    insertBatchConsumpQueue.setSourceProcessId(parentProcessId.getProcessId());
                    insertBatchConsumpQueue.setBatchStatus(processedBatchStatus);
                    insertBatchConsumpQueue.setBatchMarking(batchMarking);
                    insertBatchConsumpQueue.setProcess(subProcess);
                    session.save(insertBatchConsumpQueue);
                }
            }

            if (listOfSubProcesses.size() != 0) {
                Criteria insertACQCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.in("process", listOfSubProcesses))
                        .add(Restrictions.eq("batchStatus", processedBatchStatus));
                for (Object batchConsumpQueueObject : insertACQCriteria.list()) {
                    BatchConsumpQueue batchConsumpQueue = (BatchConsumpQueue) batchConsumpQueueObject;
                    ArchiveConsumpQueue archiveConsumpQueue = new ArchiveConsumpQueue();
                    archiveConsumpQueue.setProcess(batchConsumpQueue.getProcess());
                    archiveConsumpQueue.setBatchByTargetBatchId(batchConsumpQueue.getBatchByTargetBatchId());
                    archiveConsumpQueue.setBatchBySourceBatchId(batchConsumpQueue.getBatchBySourceBatchId());
                    archiveConsumpQueue.setBatchStatus(batchConsumpQueue.getBatchStatus());
                    archiveConsumpQueue.setInsertTs(batchConsumpQueue.getInsertTs());
                    archiveConsumpQueue.setSourceProcessId(batchConsumpQueue.getSourceProcessId());
                    archiveConsumpQueue.setStartTs(batchConsumpQueue.getStartTs());
                    archiveConsumpQueue.setEndTs(batchConsumpQueue.getEndTs());
                    archiveConsumpQueue.setBatchMarking(batchConsumpQueue.getBatchMarking());
                    session.save(archiveConsumpQueue);
                    session.delete(batchConsumpQueue);
                }
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void termJob(Integer processId) {
        // Beginning sql session
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            Process parentProcessId = new Process();
            parentProcessId.setProcessId(processId);
            List<Process> listOfSubProcesses = new ArrayList<Process>();
            Criteria listOfSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcessId)).add(Restrictions.eq("deleteFlag", false));
            for (Object subProcessObject : listOfSubProcessCriteria.list()) {
                Process subProcess = (Process) subProcessObject;
                listOfSubProcesses.add(subProcess);
            }
            ExecStatus runningExecStatus = new ExecStatus();
            runningExecStatus.setExecStateId(2);
            ExecStatus failedExecStatus = new ExecStatus();
            failedExecStatus.setExecStateId(6);
            ExecStatus successExecStatus = new ExecStatus();
            successExecStatus.setExecStateId(3);
            BatchStatus newBatchStatus = new BatchStatus();
            newBatchStatus.setBatchStateId(0);
            BatchStatus processedBatchStatus = new BatchStatus();
            processedBatchStatus.setBatchStateId(1);

            // checking valid parent process
            Criteria validProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processId", processId)).add(Restrictions.eq("deleteFlag", false));
            if (validProcessCriteria.list().size() == 0) {
                LOGGER.error("Invalid parent-process. pid=" + processId);
                throw new MetadataException("Invalid parent-process. pid=" + processId);
            } else {
                Process validProcess = (Process) validProcessCriteria.list().get(0);
                if (validProcess.getProcess() != null) {
                    LOGGER.error("Invalid parent-process. pid=" + processId);
                    throw new MetadataException("Invalid parent-process. pid=" + processId);
                }
            }

            // checking whether process is running or not

            Criteria runningProcessCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", parentProcessId))
                    .addOrder(Order.desc("startTs")).setMaxResults(1);

            if (runningProcessCriteria.list().size() != 0) {
                InstanceExec runningInstanceExec = (InstanceExec) runningProcessCriteria.list().get(0);

                if (runningInstanceExec.getExecStatus().getExecStateId() != 2) {
                    LOGGER.error("The process is not under execution, pid=" + processId);
                    throw new MetadataException("The process is not under execution, pid=" + processId);
                }
            }
            if (listOfSubProcesses.size() != 0) {
                Criteria runningSubProcessCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.in("process", listOfSubProcesses))
                        .add(Restrictions.eq("execStatus", runningExecStatus));
                if (runningSubProcessCriteria.list().size() > 0) {
                    LOGGER.error("sub process in running state");
                    throw new MetadataException("sub process in running state");
                }
            }

            Criteria updateInstanceExecCriteria = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process", parentProcessId))
                    .add(Restrictions.eq("execStatus", runningExecStatus));
            InstanceExec updateInstanceExec = (InstanceExec) updateInstanceExecCriteria.list().get(0);
            updateInstanceExec.setEndTs(new Date());
            updateInstanceExec.setExecStatus(failedExecStatus);
            session.update(updateInstanceExec);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

}
