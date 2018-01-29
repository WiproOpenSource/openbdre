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
import com.wipro.ats.bdre.md.beans.ExecutionInfo;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by SH387936 on 12-28-2017.
 */
@Transactional
@Service

public class JobTriggerDAO {
    private static final Logger LOGGER = Logger.getLogger(JobTriggerDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    @Autowired
    ProcessDeploymentQueueDAO processDeploymentQueueDAO;
    @Autowired
    static ProcessDAO processDAO;
    private static final String ENQUEUINGPROCESSID="enqueuingProcessId";
    public String checkDownStream(Integer parentProcessId){
        LOGGER.info("inside checkDownStream");
        int flag=1;
        Session session=sessionFactory.openSession();

        List<Process> subProcessList=new ArrayList<>();
        List<Integer> downStreamProcessList=new ArrayList<>();
        Long batchId=new Long(0);
        String flag_batchId=null;
        try {
            session.beginTransaction();

            // Fetching the file monitoring parent process
            Criteria parentProcessCriteria=session.createCriteria(Process.class).add(Restrictions.eq("processId",parentProcessId));
            Process parentProcess=(Process) parentProcessCriteria.list().get(0);

            //Creating a batch for the file monitoring process.
            InstanceExec instanceExec = new InstanceExec();
            instanceExec.setProcess(parentProcess);
            instanceExec.setStartTs(new Date());
            ExecStatus execStatus=new ExecStatus();
            execStatus.setExecStateId(3);   // why 2 and not 3
            instanceExec.setExecStatus(execStatus);
            session.save(instanceExec);

            Batch batch = new Batch();
            batch.setInstanceExec(instanceExec);
            batch.setBatchType("Type 1");
             batchId=(Long)session.save(batch);
             LOGGER.info("The batch id generated is " + batchId);
            //Fetching the list of all the sub-processes which enqueue the file monitoring process

            Criteria criteria=session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq(ENQUEUINGPROCESSID, parentProcessId.toString()),Restrictions.like(ENQUEUINGPROCESSID,"%,"+parentProcessId.toString()),Restrictions.like(ENQUEUINGPROCESSID,parentProcessId.toString()+",%"),Restrictions.like(ENQUEUINGPROCESSID,"%,"+parentProcessId.toString()+",%"))).add(Restrictions.eq("deleteFlag",false));
            subProcessList=(List<Process>) criteria.list();

            Criteria deleteBCQCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("sourceProcessId", parentProcessId));
            List<BatchConsumpQueue> deleteBCQList=deleteBCQCriteria.list();
            for(int i=0;i<deleteBCQList.size();i++)
            {
                BatchConsumpQueue batchConsumpQueue=deleteBCQList.get(i);
                session.delete(batchConsumpQueue);
            }

            for(Process process: subProcessList){
                LOGGER.info(process.getProcessId() + " is the downstream sub process for the file monitoring job");
                LOGGER.info("adding a batch for the downstream process");
                BatchStatus batchStatus=new BatchStatus();
                batchStatus.setBatchStateId(0);
                BatchConsumpQueue batchConsumpQueue=new BatchConsumpQueue();
                batchConsumpQueue.setInsertTs(new Date());
                batchConsumpQueue.setSourceProcessId(parentProcessId);
                batchConsumpQueue.setBatchStatus(batchStatus);
                batchConsumpQueue.setProcess(process);
                batchConsumpQueue.setBatchBySourceBatchId(batch);
                // Fetching the parent process id of the sub-process
                Integer ppid=process.getProcess().getProcessId();
                downStreamProcessList.add(ppid);
                LOGGER.info("batch consumption queue object is " + batchConsumpQueue.toString());
                Long bcqID=(Long) session.save(batchConsumpQueue);
                LOGGER.info("batch consumption queue id  after saving is " + bcqID);
            }
            if(downStreamProcessList.isEmpty()){
                LOGGER.info("There is no downstream for the file monitoring job");
            }
            else {
                Integer ppid = downStreamProcessList.get(0);
                LOGGER.info("The parent process id of downstream process is : " + ppid);
                Process pProcess = (Process) session.get(Process.class, ppid);
                Criteria deploymentCriteria = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("process", pProcess));
                List<ProcessDeploymentQueue> downStreamDeployQueue = new ArrayList<>();
                downStreamDeployQueue = (List<ProcessDeploymentQueue>) deploymentCriteria.list();
                if (downStreamDeployQueue.isEmpty()) {
                    throw new MetadataException(ppid + " has not been deployed. Please deploy it");
                }
                // add the code so that latest entry from the process deployment queue is taken.
                else {
                    int length = downStreamDeployQueue.size();
                    long max = downStreamDeployQueue.get(0).getInsertTs().getTime();
                    int index = 0;
                    for (int i = 1; i < length; i++) {
                        if (max < downStreamDeployQueue.get(i).getInsertTs().getTime()) {
                            max = downStreamDeployQueue.get(i).getInsertTs().getTime();
                            index = i;
                        }
                    }
                    LOGGER.info("Last job deployment was carried out at : " + downStreamDeployQueue.get(index).getInsertTs());
                    //checking if the deployment is successful or not
                    DeployStatus deployStatus = downStreamDeployQueue.get(index).getDeployStatus();
                    Long deploymentId = downStreamDeployQueue.get(index).getDeploymentId();
                    Short deployStatusId = deployStatus.getDeployStatusId();
                    if (deployStatusId == 3) {
                        LOGGER.info("Downstream Process has been successfully deployed");
                        LOGGER.info("Deployment status id is : " + deployStatusId);
                        //directly execute the process
                        flag = 0;
                    } else {
                        throw new MetadataException(ppid + " has not been deployed successfully. Please deploy it again");

                    }
                }
            }
            flag_batchId=flag+","+ batchId;
            session.getTransaction().commit();
        }
        catch(MetadataException m){
            session.getTransaction().rollback();
            LOGGER.error(m.getMessage());
        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e);
            e.printStackTrace();
        }finally {
            session.close();
        }


        return flag_batchId;
    }
    public Process getDownStreamProcess(Integer upStreamId){
        Session session=sessionFactory.openSession();


        Process process = new Process();
        try {
            session.beginTransaction();
            List<Process> subProcessList = new ArrayList<>();
            List<Integer> downStreamProcessList = new ArrayList<>();
            Criteria criteria=session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq(ENQUEUINGPROCESSID, upStreamId.toString()),Restrictions.like(ENQUEUINGPROCESSID,"%,"+upStreamId.toString()),Restrictions.like(ENQUEUINGPROCESSID,upStreamId.toString()+",%"),Restrictions.like(ENQUEUINGPROCESSID,"%,"+upStreamId.toString()+",%")));
            subProcessList = (List<Process>) criteria.list();
            int ppid = subProcessList.get(0).getProcess().getProcessId();
            LOGGER.info("The parent process id of downstream process is : " + ppid);
            process = (Process) session.get(Process.class, ppid);
            session.getTransaction().commit();

        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e);
        }
        finally {
            session.close();
        }
        return process;
    }
    public List<Process> getOozieDownstream(Integer processId){
        LOGGER.info("inside get oozie downstream");
        List<Process> downStreamProcessList=new ArrayList<>();
        Session session=sessionFactory.openSession();

        try {
            session.beginTransaction();
            List<Process> listOfDownStreamSubProcessesWithEnqID = new ArrayList<Process>();
            Criteria listOfDownStreamSubProcessesWithEnqIDCriteria=session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq(ENQUEUINGPROCESSID, processId.toString()),Restrictions.like(ENQUEUINGPROCESSID,"%,"+processId.toString()),Restrictions.like(ENQUEUINGPROCESSID,processId.toString()+",%"),Restrictions.like(ENQUEUINGPROCESSID,"%,"+processId.toString()+",%")));
            listOfDownStreamSubProcessesWithEnqID=(List<Process>)listOfDownStreamSubProcessesWithEnqIDCriteria.list();
            Integer[] a=new Integer[100];
            int count=0;
            for(Process p: listOfDownStreamSubProcessesWithEnqID){
                int flag1=1;
                int flag2=1;
                Integer ppid=p.getProcess().getProcessId();
                LOGGER.info(ppid + " is downstream of " + processId);
                for(int i=0;i<a.length;i++){
                    if(ppid==a[i]){
                        flag1=0;
                        break;
                    }
                }
                if(flag1==1){
                    a[count]=ppid;
                    count++;
                }
                if(flag1==1){
                    Criteria deploymentCriteria=session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("process",p.getProcess()));
                    List<ProcessDeploymentQueue> downStreamDeployQueue=new ArrayList<>();
                    downStreamDeployQueue=(List<ProcessDeploymentQueue>)deploymentCriteria.list();
                    if(downStreamDeployQueue.isEmpty()){
                        throw new MetadataException(ppid + " has not been deployed. Please deploy it");
                    }
                    // add the code so that latest entry from the process deployment queue is taken.
                    else {
                        int length = downStreamDeployQueue.size();
                        long max = downStreamDeployQueue.get(0).getInsertTs().getTime();
                        int index = 0;
                        for (int i = 1; i < length; i++) {
                            if (max < downStreamDeployQueue.get(i).getInsertTs().getTime()) {
                                max = downStreamDeployQueue.get(i).getInsertTs().getTime();
                                index = i;
                            }
                        }
                        LOGGER.info("Last job deployment for " + ppid + " was carried out at : " + downStreamDeployQueue.get(index).getInsertTs());
                        //checking if the deployment is successful or not
                        DeployStatus deployStatus = downStreamDeployQueue.get(index).getDeployStatus();
                        Long deploymentId = downStreamDeployQueue.get(index).getDeploymentId();
                        Short deployStatusId = deployStatus.getDeployStatusId();
                        if (deployStatusId == 3) {
                            LOGGER.info("Downstream Process has been successfully deployed");
                            LOGGER.info("Deployment status id is : " + deployStatusId);
                            //directly execute the process

                        } else {
                            throw new MetadataException(ppid + " has not been deployed successfully. Please deploy it again");

                        }
                    }
                }
                if(flag1==1 && flag2==1){
                    downStreamProcessList.add(p.getProcess());
                    LOGGER.info(ppid + " added to down stream process list for execution");
                }
            }
            session.getTransaction().commit();
        }
        catch(MetadataException m){
            session.getTransaction().rollback();
            LOGGER.error(m.getMessage());
        }
        catch (Exception e){
            session.getTransaction().rollback();
            LOGGER.error(e.getMessage());
        }
        finally {
            session.close();
        }
        return downStreamProcessList;

    }

}
