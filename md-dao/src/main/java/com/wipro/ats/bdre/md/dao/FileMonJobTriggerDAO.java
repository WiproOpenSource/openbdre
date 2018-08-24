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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by SH387936 on 28-12-2017.
 */
@Transactional
@Service

public class FileMonJobTriggerDAO {
    private static final Logger LOGGER = Logger.getLogger(FileMonJobTriggerDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    ProcessDeploymentQueueDAO processDeploymentQueueDAO;
    public void runDownStream(Integer parentProcessId){
        Session session=sessionFactory.openSession();
        List<Process> subProcessList=new ArrayList<>();
        List<Process> parentProcessList=new ArrayList<>();
        try {
            session.beginTransaction();
            Criteria parentProcessCriteria=session.createCriteria(Process.class).add(Restrictions.eq("processId",parentProcessId));
            parentProcessList=(List<Process>) parentProcessCriteria.list().get(0);
            Process parentProcess=parentProcessList.get(0);
            InstanceExec instanceExec = new InstanceExec();
            instanceExec.setProcess(parentProcess);
            instanceExec.setStartTs(new Date());
            ExecStatus execStatus=new ExecStatus();
            execStatus.setExecStateId(2);
            instanceExec.setExecStatus(execStatus);
            session.save(instanceExec);

            Batch batch = new Batch();
            batch.setInstanceExec(instanceExec);
            batch.setBatchType("Type 1");

            Criteria criteria=session.createCriteria(Process.class).add(Restrictions.eq("enqueuingProcessId",parentProcessId));
            subProcessList=(List<Process>) criteria.list();
            for(Process process: subProcessList){
                BatchStatus batchStatus=new BatchStatus();
                batchStatus.setBatchStateId(0);
                BatchConsumpQueue batchConsumpQueue=new BatchConsumpQueue();
                batchConsumpQueue.setInsertTs(new Date());
                batchConsumpQueue.setSourceProcessId(parentProcessId);
                batchConsumpQueue.setBatchStatus(batchStatus);
                batchConsumpQueue.setProcess(process);
                batchConsumpQueue.setBatchBySourceBatchId(batch);
            }
            for(Process process:subProcessList){
                Integer processId = process.getProcessId();
                int flag=0;
                Criteria upstreamProcessCriteria=session.createCriteria(Process.class).add(Restrictions.eq("enqueuingProcessId",processId));
                List<Process> upstreamProcessList = (List<Process>) upstreamProcessCriteria.list();
                if(!upstreamProcessList.isEmpty()){
                    ExecStatus successStatus = (ExecStatus) session.get(ExecStatus.class, 3);
                    for(Process upstreamProcess: upstreamProcessList){
                    Criteria successProcessCriteria=session.createCriteria(InstanceExec.class).add(Restrictions.eq("execStatus",successStatus)).add(Restrictions.eq("process",upstreamProcess));
                    if(successProcessCriteria.list().isEmpty()){
                        flag=1;
                    }
                }
                }
                if(flag==0){
                    com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue jpaPdq = processDeploymentQueueDAO.insertProcessDeploymentQueue(process.getProcessId(), "admin");
                }
            }

        }
        catch (Exception e){
            LOGGER.error(e);
        }
    }
}
