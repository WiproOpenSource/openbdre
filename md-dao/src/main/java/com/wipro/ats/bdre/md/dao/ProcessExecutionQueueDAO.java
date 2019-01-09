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
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SH387936 on 01/17/2018.
 */
@Transactional
@Service
public class ProcessExecutionQueueDAO {
    private static final Logger LOGGER=Logger.getLogger(ProcessExecutionQueueDAO.class);

    @Autowired
    SessionFactory sessionFactory;

    public ProcessExecutionQueue insert(Integer processId,String userName){
        ProcessExecutionQueue processExecutionQueue = new ProcessExecutionQueue();
        LOGGER.info("Inside insert method of ProcessExecutionQueueDAO");
            Session session = sessionFactory.openSession();
            session.beginTransaction();
        try {
                LOGGER.info("creating criteria for checking whether process is present in process execution queue or not");
                Criteria checkProcessAlreadyInPEQ = session.createCriteria(ProcessExecutionQueue.class).add(Restrictions.eq("process.processId", processId));
                if (checkProcessAlreadyInPEQ.list().isEmpty()) {
                    LOGGER.info("inserting process with process id " + processId + " in process execution queue");
                    ExecStatus execStatus = new ExecStatus();
                    execStatus.setExecStateId(1);
                    Process process = (Process) session.get(Process.class, processId);
                    processExecutionQueue.setExecStatus(execStatus);
                    processExecutionQueue.setBusDomain(process.getBusDomain());
                    processExecutionQueue.setProcess(process);
                    processExecutionQueue.setUserName(userName);
                    processExecutionQueue.setProcessType(process.getProcessType());
                    processExecutionQueue.setInsertTs(new Date());
                    session.save(processExecutionQueue);
                    session.getTransaction().commit();
                    LOGGER.info("process successfully added in the process execution queue table");

                } else {
                    processExecutionQueue=(ProcessExecutionQueue) checkProcessAlreadyInPEQ.list().get(0);
                    LOGGER.info("current execution state of " + processId + " is "+ processExecutionQueue.getExecStatus().getExecStateId());
                    if(processExecutionQueue.getExecStatus().getExecStateId()==3 || processExecutionQueue.getExecStatus().getExecStateId()==6){
                        LOGGER.info("changing status of " + processId + " to not running");
                        ExecStatus execStatus=new ExecStatus();
                        execStatus.setExecStateId(1);
                        processExecutionQueue.setExecStatus(execStatus);
                        session.update(processExecutionQueue);
                        session.getTransaction().commit();
                    }
                }
            } catch (MetadataException m) {
                session.getTransaction().rollback();
                LOGGER.error(m.getMessage());
            } catch (Exception e) {
            session.getTransaction().rollback();
                LOGGER.error(e);
            }
            session.close();
        return processExecutionQueue;
    }
    public List<ProcessExecutionQueue> get(){
        List<ProcessExecutionQueue> processExecutionQueueList=new ArrayList<>();
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        try{
            LOGGER.info("picking processes from process deployment queue which have not been started");
            Criteria processExecutionCriteria = session.createCriteria(ProcessExecutionQueue.class).add(Restrictions.in("execStatus.execStateId", new Integer[]{1}));
            processExecutionQueueList=(List<ProcessExecutionQueue>)processExecutionCriteria.list();
            session.getTransaction().commit();
        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e);
        }

        session.close();
        return processExecutionQueueList;
    }
    public void updateStatusToStarted(Long executionId){
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        try {
            ProcessExecutionQueue processExecutionQueue=(ProcessExecutionQueue)session.get(ProcessExecutionQueue.class,executionId);
            ExecStatus execStatus=new ExecStatus();
            execStatus.setExecStateId(8);
            processExecutionQueue.setExecStatus(execStatus);
            session.update(processExecutionQueue);
            session.getTransaction().commit();
        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e);
        }

        session.close();
    }
    public void updateStatusToRunning(Integer processId){
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        try {
            Criteria criteria=session.createCriteria(ProcessExecutionQueue.class).add(Restrictions.eq("process.processId",processId));
            if(!criteria.list().isEmpty()){
                ExecStatus execStatus=new ExecStatus();
                execStatus.setExecStateId(2);
                ProcessExecutionQueue processExecutionQueue=(ProcessExecutionQueue)criteria.list().get(0);
                processExecutionQueue.setExecStatus(execStatus);
                session.update(processExecutionQueue);
            }
            session.getTransaction().commit();
        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e);
        }

        session.close();
    }

    public void updateStatusToSuccess(Integer processId){
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        try {
            Criteria criteria=session.createCriteria(ProcessExecutionQueue.class).add(Restrictions.eq("process.processId",processId));
            if(!criteria.list().isEmpty()){
                ExecStatus execStatus=new ExecStatus();
                execStatus.setExecStateId(3);
                ProcessExecutionQueue processExecutionQueue=(ProcessExecutionQueue)criteria.list().get(0);
                processExecutionQueue.setExecStatus(execStatus);
                session.update(processExecutionQueue);
            }
            session.getTransaction().commit();
        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e);
        }

        session.close();
    }

    public void updateStatusToFailed(Integer processId){
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        try {
            Criteria criteria=session.createCriteria(ProcessExecutionQueue.class).add(Restrictions.eq("process.processId",processId));
            if(!criteria.list().isEmpty()){
                ExecStatus execStatus=new ExecStatus();
                execStatus.setExecStateId(6);
                ProcessExecutionQueue processExecutionQueue=(ProcessExecutionQueue)criteria.list().get(0);
                processExecutionQueue.setExecStatus(execStatus);
                session.update(processExecutionQueue);
            }
            session.getTransaction().commit();
        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e);
        }

        session.close();
    }
}
