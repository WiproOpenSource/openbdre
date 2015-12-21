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
import com.wipro.ats.bdre.md.dao.jpa.DeployStatus;
import com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MI294210 on 11/2/2015.
 */
@Transactional
@Service
public class DeployDAO {
    private static final Logger LOGGER = Logger.getLogger(DeployDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public void initDeploy(Long deployId) {
        Session session = sessionFactory.openSession();
        try {

            session.beginTransaction();

            // Running deploy status object
            DeployStatus runningDeployStatus = new DeployStatus();
            runningDeployStatus.setDeployStatusId((short) 2);
            // Succeeded deploy status object
            DeployStatus successDeployStatus = new DeployStatus();
            successDeployStatus.setDeployStatusId((short) 3);
            // Failed deploy status object
            DeployStatus failDeployStatus = new DeployStatus();
            failDeployStatus.setDeployStatusId((short) 4);
            // Picked deploy status object
            DeployStatus pickedDeployStatus = new DeployStatus();
            pickedDeployStatus.setDeployStatusId((short) 5);

            // querying deploymentId present or not
            Criteria checkDeploymentId = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deploymentId", deployId));
            Integer deploymentIdCount = checkDeploymentId.list().size();
            LOGGER.info("Deployment Id count :" + deploymentIdCount);

            //querying running deployment process
            Criteria checkDeployingProcess = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.and(Restrictions.eq("deploymentId", deployId), Restrictions.eq("deployStatus", runningDeployStatus)));
            Integer deployingProcessCount = checkDeployingProcess.list().size();
            LOGGER.info("Deploying process count :" + deployingProcessCount);

            //querying deployed process
            Criteria checkDeployedProcess = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deploymentId", deployId)).add(Restrictions.or(Restrictions.eq("deployStatus", failDeployStatus), Restrictions.eq("deployStatus", successDeployStatus)));
            Integer deployedProcessCount = checkDeployedProcess.list().size();
            LOGGER.info("Deployed process count :" + deployedProcessCount);

            //picked deployment status process with passed deployment Id  from PDQ to update
            List<ProcessDeploymentQueue> processDeploymentQueueList = new ArrayList<ProcessDeploymentQueue>();
            Criteria checkPickedProcesses = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.and(Restrictions.eq("deploymentId", deployId), Restrictions.eq("deployStatus", pickedDeployStatus)));
            processDeploymentQueueList = checkPickedProcesses.list();
            Integer pickedCount = checkPickedProcesses.list().size();
            LOGGER.info("Picked process count :" + pickedCount);

            //check deployment Id is present or not
            if (deploymentIdCount == 0) {
                throw new MetadataException("The deployment Id is not present:" + deployId);
            }
            // check process is deploying
            else if (deployingProcessCount != 0) {
                throw new MetadataException("The process is already deploying:" + deployId);
            }
            //check process is already deployed
            else if (deployedProcessCount != 0) {
                throw new MetadataException("The process is already deployed:" + deployId);
            } else {
                //updating startTs and deploy status of process to running in ProcessDeploymentQueue table from deploy status as picked
                if (pickedCount == 0) {
                    LOGGER.info("The process is not yet picked for deployment");
                } else {
                    java.util.Date date = new java.util.Date();
                    processDeploymentQueueList.get(0).setStartTs(new Timestamp(date.getTime()));
                    processDeploymentQueueList.get(0).setDeployStatus(runningDeployStatus);
                    session.update(processDeploymentQueueList.get(0));
                }
                session.getTransaction().commit();
            }

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
        } finally {
            session.close();
        }

    }

    public void termDeploy(Long deployId) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            // Running deploy status object
            DeployStatus runningDeployStatus = new DeployStatus();
            runningDeployStatus.setDeployStatusId((short) 2);
            // Failed deploy status object
            DeployStatus failDeployStatus = new DeployStatus();
            failDeployStatus.setDeployStatusId((short) 4);
            // Succeeded deploy status object
            DeployStatus successDeployStatus = new DeployStatus();
            successDeployStatus.setDeployStatusId((short) 3);

            // querying deploymentId present or not
            Criteria checkDeploymentId = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deploymentId", deployId));
            Integer deploymentIdCount = checkDeploymentId.list().size();
            LOGGER.info("Deployment Id count :" + deploymentIdCount);

            //querying deployed process
            Criteria checkDeployedProcess = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deploymentId", deployId)).add(Restrictions.or(Restrictions.eq("deployStatus", failDeployStatus), Restrictions.eq("deployStatus", successDeployStatus)));
            Integer deployedProcessCount = checkDeployedProcess.list().size();
            LOGGER.info("Deployed process count :" + deployedProcessCount);


            //querying running deployment process
            List<ProcessDeploymentQueue> processDeploymentQueueList = new ArrayList<ProcessDeploymentQueue>();
            Criteria checkDeployingProcess = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.and(Restrictions.eq("deploymentId", deployId), Restrictions.eq("deployStatus", runningDeployStatus)));
            Integer deployingProcessCount = checkDeployingProcess.list().size();
            processDeploymentQueueList = checkDeployingProcess.list();
            LOGGER.info("Deploying process count :" + deployingProcessCount);


            //check deployment Id is present or not
            if (deploymentIdCount == 0) {
                throw new MetadataException("The deployment Id is not present:" + deployId);
            }
            //check process is already deployed
            else if (deployedProcessCount != 0) {
                throw new MetadataException("The process is already deployed:" + deployId);
            }
            // check process is deploying
            else if (deployingProcessCount == 0) {
                throw new MetadataException("The process is not deploying:" + deployId);
            } else {
                //updating endTs and deploy status of process to failed in ProcessDeploymentQueue table from deploy status as running
                java.util.Date date = new java.util.Date();
                processDeploymentQueueList.get(0).setEndTs(new Timestamp(date.getTime()));
                processDeploymentQueueList.get(0).setDeployStatus(failDeployStatus);
                session.update(processDeploymentQueueList.get(0));

                session.getTransaction().commit();
            }

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
        } finally {
            session.close();
        }

    }

    public void haltDeploy(Long deployId) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            // Running deploy status object
            DeployStatus runningDeployStatus = new DeployStatus();
            runningDeployStatus.setDeployStatusId((short) 2);

            // Succeeded deploy status object
            DeployStatus successDeployStatus = new DeployStatus();
            successDeployStatus.setDeployStatusId((short) 3);

            // Failed deploy status object
            DeployStatus failDeployStatus = new DeployStatus();
            failDeployStatus.setDeployStatusId((short) 4);

            // querying deploymentId present or not
            Criteria checkDeploymentId = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deploymentId", deployId));
            Integer deploymentIdCount = checkDeploymentId.list().size();
            LOGGER.info("Deployment Id count :" + deploymentIdCount);

            //querying deployed process
            Criteria checkDeployedProcess = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deploymentId", deployId)).add(Restrictions.or(Restrictions.eq("deployStatus", failDeployStatus), Restrictions.eq("deployStatus", successDeployStatus)));
            Integer deployedProcessCount = checkDeployedProcess.list().size();
            LOGGER.info("Deployed process count :" + deployedProcessCount);


            //querying running deployment process
            List<ProcessDeploymentQueue> processDeploymentQueueList = new ArrayList<ProcessDeploymentQueue>();
            Criteria checkDeployingProcess = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.and(Restrictions.eq("deploymentId", deployId), Restrictions.eq("deployStatus", runningDeployStatus)));
            Integer deployingProcessCount = checkDeployingProcess.list().size();
            processDeploymentQueueList = checkDeployingProcess.list();
            LOGGER.info("Deploying process count :" + deployingProcessCount);

            //check deployment Id is present or not
            if (deploymentIdCount == 0) {
                throw new MetadataException("The deployment Id is not present:" + deployId);
            }
            //check process is already deployed
            else if (deployedProcessCount != 0) {
                throw new MetadataException("The process is already deployed:" + deployId);
            }
            // check process is deploying
            else if (deployingProcessCount == 0) {
                throw new MetadataException("The process is not deploying:" + deployId);
            } else {
                //updating endTs and deploy status of process to success in ProcessDeploymentQueue table from deploy status as running
                java.util.Date date = new java.util.Date();
                processDeploymentQueueList.get(0).setEndTs(new Timestamp(date.getTime()));
                processDeploymentQueueList.get(0).setDeployStatus(successDeployStatus);
                session.update(processDeploymentQueueList.get(0));

                session.getTransaction().commit();
            }

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
        } finally {
            session.close();
        }

    }
}
