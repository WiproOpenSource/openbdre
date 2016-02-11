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
 * Created by MI294210 on 10/28/2015.
 */

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Transactional
@Service
public class StepDAO {
    private static final Logger LOGGER = Logger.getLogger(StepDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public Long initStep(Integer subPid) {
        Session session = sessionFactory.openSession();

        Long sub_instance_exec_id = null;
        try {
            session.beginTransaction();

            //fetching the sub process and parent process
            Process subProcess = (Process) session.get(Process.class, subPid);
            Process parentProcess = null;
            if (subProcess != null) {
                parentProcess = subProcess.getProcess();
            }

            //object of running execution state
            ExecStatus runningExecState = new ExecStatus();
            runningExecState.setExecStateId(2);

            // querying deleted  sub process
            Criteria checkDeletedProcess = session.createCriteria(Process.class).add(Restrictions.eq("processId", subPid)).add(Restrictions.eq("deleteFlag", true));
            Integer deletedProcessCount = checkDeletedProcess.list().size();
            LOGGER.info("Deleted process count :" + deletedProcessCount);

            //querying running parent process
            Criteria checkParentProcessExec = session.createCriteria(InstanceExec.class).add(Restrictions.and(Restrictions.eq("process", parentProcess), Restrictions.eq("execStatus", runningExecState)));
            Integer runningProcessCount = checkParentProcessExec.list().size();
            LOGGER.info("Running parent process count :" + runningProcessCount);

            //querying running sub process process
            Criteria checkSubProcessExec = session.createCriteria(InstanceExec.class).add(Restrictions.and(Restrictions.eq("process", subProcess), Restrictions.eq("execStatus", runningExecState)));
            Integer runningSubProcessCount = checkSubProcessExec.list().size();
            LOGGER.info("Running sub process count :" + runningSubProcessCount);

            //list of  sub process entries in BCQ with target_batch_id not null
            List<BatchConsumpQueue> batchConsumpQueueList = new ArrayList<BatchConsumpQueue>();
            Criteria checkSubProcessBCQ = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.and(Restrictions.eq("process", subProcess), Restrictions.isNotNull("batchByTargetBatchId")));
            batchConsumpQueueList = checkSubProcessBCQ.list();

            //check valid sub process
            if (parentProcess == null) {
                throw new MetadataException("Invalid sub-process. sub_pid=" + subPid);
            }
            // check process is deleted
            else if (deletedProcessCount != 0) {
                throw new MetadataException("This is a deleted sub-process. pid =" + subPid);
            }
            //check parent process is not running
            else if (runningProcessCount == 0) {
                throw new MetadataException("The parent process is not under execution, sub_pid=" + subPid);
            }
            //check sub process is running already
            else if (runningSubProcessCount != 0) {
                throw new MetadataException("This sub process is already under execution ! sub_pid=" + subPid);
            } else {
                // entry of sub process in InstanceExec table with running status
                InstanceExec subProcessExec = new InstanceExec();

                subProcessExec.setProcess(subProcess);
                java.util.Date date = new java.util.Date();
                subProcessExec.setStartTs(new Timestamp(date.getTime()));
                subProcessExec.setExecStatus(runningExecState);
                sub_instance_exec_id = (Long) session.save(subProcessExec);

                LOGGER.info("The instance-exec-id of sub process inserted :" + sub_instance_exec_id);

                //updating startTs of sub process in BatchComsumpQueue table with target batch id not null
                for (BatchConsumpQueue bcq : batchConsumpQueueList) {
                    bcq.setStartTs(new Timestamp(date.getTime()));
                    session.update(bcq);
                }
                session.getTransaction().commit();
            }

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
            throw e;
        } finally {
            session.close();
        }
        return sub_instance_exec_id;
    }


    public void termStep(Integer subPid) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            //fetching the sub process and parent process
            Process subProcess = (Process) session.get(Process.class, subPid);
            Process parentProcess = null;
            if (subProcess != null) {
                parentProcess = subProcess.getProcess();
            }
            //object of running execution state
            ExecStatus runningExecState = new ExecStatus();
            runningExecState.setExecStateId(2);

            //object of failed execution state
            ExecStatus failedExecState = new ExecStatus();
            failedExecState.setExecStateId(6);

            //processed batch status
            BatchStatus processedBatchState = new BatchStatus();
            processedBatchState.setBatchStateId(1);

            //querying running parent process
            Criteria checkParentProcessExec = session.createCriteria(InstanceExec.class).add(Restrictions.and(Restrictions.eq("process", parentProcess), Restrictions.eq("execStatus", runningExecState)));
            Integer runningProcessCount = checkParentProcessExec.list().size();
            LOGGER.info("Running parent process count :" + runningProcessCount);

            //querying running sub process
            Criteria checkSubProcessExec = session.createCriteria(InstanceExec.class).add(Restrictions.and(Restrictions.eq("process", subProcess), Restrictions.eq("execStatus", runningExecState)));
            Integer runningSubProcessCount = checkSubProcessExec.list().size();
            LOGGER.info("Running sub process count :" + runningSubProcessCount);

            //Fetch the instance_exec of the sub process
            List<InstanceExec> subProcessExec = new ArrayList<InstanceExec>();
            subProcessExec = checkSubProcessExec.list();

            //list of sub process entries in BCQ with target_batch_id not null
            List<BatchConsumpQueue> batchConsumpQueueList = new ArrayList<BatchConsumpQueue>();
            Criteria checkSubProcessBCQ = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.and(Restrictions.eq("process", subProcess), Restrictions.isNotNull("batchByTargetBatchId")));
            batchConsumpQueueList = checkSubProcessBCQ.list();

            //check valid sub process
            if (parentProcess == null) {
                throw new MetadataException("Invalid sub-process. sub_pid=" + subPid);
            }
            //check parent process is not running
            else if (runningProcessCount == 0) {
                throw new MetadataException("The parent process is not under execution, sub_pid=" + subPid);
            }
            //check sub process is not running
            else if (runningSubProcessCount == 0) {
                throw new MetadataException("This sub process is not running ! sub_pid=" + subPid);
            } else {
                // updating endTs and  exec status of sub process in InstanceExec table to failed
                subProcessExec.get(0).setProcess(subProcess);
                java.util.Date date = new java.util.Date();
                subProcessExec.get(0).setEndTs(new Timestamp(date.getTime()));
                subProcessExec.get(0).setExecStatus(failedExecState);
                session.update(subProcessExec.get(0));

                //updating endTs and batch status of sub process  to processed in BatchComsumpQueue table
                for (BatchConsumpQueue bcq : batchConsumpQueueList) {
                    bcq.setEndTs(new Timestamp(date.getTime()));
                    bcq.setBatchStatus(processedBatchState);
                    session.update(bcq);
                }
                session.getTransaction().commit();
            }

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
            throw e;
        } finally {
            session.close();
        }

    }


    public void haltStep(Integer subPid) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            //fetching the sub process and parent process
            Process subProcess = (Process) session.get(Process.class, subPid);
            Process parentProcess = null;
            if (subProcess != null) {
                parentProcess = subProcess.getProcess();
            }

            //object of running execution state
            ExecStatus runningExecState = new ExecStatus();
            runningExecState.setExecStateId(2);

            //object of succeeded execution state
            ExecStatus successExecState = new ExecStatus();
            successExecState.setExecStateId(3);

            //processed batch status
            BatchStatus processedBatchState = new BatchStatus();
            processedBatchState.setBatchStateId(1);

            //querying running parent process
            Criteria checkParentProcessExec = session.createCriteria(InstanceExec.class).add(Restrictions.and(Restrictions.eq("process", parentProcess), Restrictions.eq("execStatus", runningExecState)));
            Integer runningProcessCount = checkParentProcessExec.list().size();
            LOGGER.info("Running parent process count :" + runningProcessCount);

            //querying running sub process
            Criteria checkSubProcessExec = session.createCriteria(InstanceExec.class).add(Restrictions.and(Restrictions.eq("process", subProcess), Restrictions.eq("execStatus", runningExecState)));
            Integer runningSubProcessCount = checkSubProcessExec.list().size();
            LOGGER.info("Running sub process count :" + runningSubProcessCount);

            //Fetch the instance_exec of the sub process
            List<InstanceExec> subProcessExec = new ArrayList<InstanceExec>();
            subProcessExec = checkSubProcessExec.list();

            // querying existence of enqueuing processes to update the BCQ entry
            Criteria checkEnqProcessId = session.createCriteria(Process.class).add(Restrictions.eq("processId", subPid)).setProjection(Projections.sum("enqueuingProcessId"));
            Long enqProcessIdSum = (Long) checkEnqProcessId.uniqueResult();
            LOGGER.info("The sum of enqueuing ids of the sub process in BCQ :" + enqProcessIdSum);

            List<BatchConsumpQueue> batchConsumpQueueList = new ArrayList<BatchConsumpQueue>();
            if (enqProcessIdSum == 0) {

                //list of sub process entries in BCQ
                Criteria checkSubProcessBCQ = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("process", subProcess));
                batchConsumpQueueList = checkSubProcessBCQ.list();

            } else {
                //list of sub process entries in BCQ with target_batch_id not null
                Criteria checkSubProcessBCQ = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.and(Restrictions.eq("process", subProcess), Restrictions.isNotNull("batchByTargetBatchId")));
                batchConsumpQueueList = checkSubProcessBCQ.list();
            }
            //check valid sub process
            if (parentProcess == null) {
                throw new MetadataException("Invalid sub-process. sub_pid=" + subPid);
            }
            //check parent process is not running
            else if (runningProcessCount == 0) {
                throw new MetadataException("The parent process is not under execution, sub_pid=" + subPid);
            }
            //check sub process is not running
            else if (runningSubProcessCount == 0) {
                throw new MetadataException("This sub process is not running ! sub_pid=" + subPid);
            } else {

                // updating endTs and exec status of sub process in InstanceExec table to success
                subProcessExec.get(0).setProcess(subProcess);
                java.util.Date date = new java.util.Date();
                subProcessExec.get(0).setEndTs(new Timestamp(date.getTime()));
                subProcessExec.get(0).setExecStatus(successExecState);
                session.update(subProcessExec.get(0));

                //updating endTs and batch status of sub process to processed in BatchComsumpQueue table
                for (BatchConsumpQueue bcq : batchConsumpQueueList) {
                    bcq.setEndTs(new Timestamp(date.getTime()));
                    bcq.setBatchStatus(processedBatchState);
                    session.update(bcq);
                }

                session.getTransaction().commit();
            }

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
            throw e;
        } finally {
            session.close();
        }

    }
}