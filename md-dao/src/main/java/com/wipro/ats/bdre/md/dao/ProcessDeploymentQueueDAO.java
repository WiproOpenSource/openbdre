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

import java.util.Date;
import java.util.List;

/**
 * Created by PR324290 on 10/28/2015.
 */
@Transactional
@Service
public class ProcessDeploymentQueueDAO {

    private static final Logger LOGGER = Logger.getLogger(ProcessDeploymentQueueDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<ProcessDeploymentQueue> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProcessDeploymentQueue.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<ProcessDeploymentQueue> processDeploymentQueues = criteria.list();
        session.getTransaction().commit();
        session.close();
        return processDeploymentQueues;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProcessDeploymentQueue.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public ProcessDeploymentQueue get(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ProcessDeploymentQueue processDeploymentQueue = (ProcessDeploymentQueue) session.get(ProcessDeploymentQueue.class, id);
        session.getTransaction().commit();
        session.close();
        return processDeploymentQueue;
    }

    public Long insert(ProcessDeploymentQueue processDeploymentQueue) {
        Session session = sessionFactory.openSession();
        Long id = null;
        try {
            session.beginTransaction();

            id = (Long) session.save(processDeploymentQueue);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(ProcessDeploymentQueue processDeploymentQueue) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(processDeploymentQueue);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public ProcessDeploymentQueue insertProcessDeploymentQueue(Integer processId, String userName) {
        Session session = sessionFactory.openSession();
        ProcessDeploymentQueue returnJpaPdq = new ProcessDeploymentQueue();
        try {
            session.beginTransaction();
            ProcessDeploymentQueue jpaPdq = new ProcessDeploymentQueue();

            //check if the script path is present
            Criteria fetchScriptPath = session.createCriteria(Properties.class).add(Restrictions.eq("id.processId", processId)).add(Restrictions.eq("configGroup", "deploy")).setProjection(Projections.property("propValue"));

            String scriptPath = null;
            if (fetchScriptPath.list().size() != 0)
                scriptPath = (String) fetchScriptPath.uniqueResult();
            ;

            //if (select deploy_status_id from process_deployment_queue where process_id=p_id and deploy_status_id in (1,2)) is null

            Criteria checkProcessAlreadyInPDQ = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("process.processId", processId)).add(Restrictions.in("deployStatus.deployStatusId", new Short[]{1, 2}));
            if (checkProcessAlreadyInPDQ.list().size() == 0) {
                Process process = (Process) session.get(Process.class, processId);
                jpaPdq.setProcess(process);
                LOGGER.info(process);
                //select process_type_id from process where process_id=p_id
                Criteria fetchProcessType = session.createCriteria(Process.class).add(Restrictions.eq("processId", processId)).setProjection(Projections.property("processType"));
                ProcessType processType = (ProcessType) fetchProcessType.uniqueResult();
                LOGGER.info(processType.getProcessTypeId());
                jpaPdq.setProcessType(processType);
                //select bus_domain_id from process where process_id=p_id
                Criteria fetchBusDomain = session.createCriteria(Process.class).add(Restrictions.eq("processId", processId)).setProjection(Projections.property("busDomain"));
                BusDomain busDomain = (BusDomain) fetchBusDomain.uniqueResult();
                jpaPdq.setBusDomain(busDomain);
                LOGGER.info(busDomain.getBusDomainId());
                jpaPdq.setDeployScriptLocation(scriptPath);
                jpaPdq.setUserName(userName);

                DeployStatus deployStatus = (DeployStatus) session.get(DeployStatus.class, (short) 1);
                jpaPdq.setDeployStatus(deployStatus);
                jpaPdq.setInsertTs(new Date());
                Long deploymentId = (Long) session.save(jpaPdq);


                returnJpaPdq = (ProcessDeploymentQueue) session.get(ProcessDeploymentQueue.class, deploymentId);
                //select deployment_id as deploymentId,process_id as processId,start_ts as startTs,insert_ts as insertTs,end_ts as endTs,deploy_status_id as deployStatusId,user_name as userName,bus_domain_id as busDomainId,process_type_id as processTypeId, deploy_script_location as deployScriptLocation from process_deployment_queue where deployment_id = LAST_INSERT_ID()  ;


            } else {
                throw new MetadataException("The process is already in deploy queue");
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return returnJpaPdq;
    }

    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            ProcessDeploymentQueue processDeploymentQueue = (ProcessDeploymentQueue) session.get(ProcessDeploymentQueue.class, id);
            session.delete(processDeploymentQueue);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
