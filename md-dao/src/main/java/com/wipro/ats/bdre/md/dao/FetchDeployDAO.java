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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MI294210 on 11/3/2015.
 */
@Transactional
@Service
public class FetchDeployDAO {
    private static final Logger LOGGER = Logger.getLogger(FetchDeployDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue> fetchDeploy(Integer deployNumber) {
        List<ProcessDeploymentQueue> notStartedPDQList = new ArrayList<ProcessDeploymentQueue>();
        List<com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue> returnPDQList = new ArrayList<com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue>();
        Session session = sessionFactory.openSession();
        try {

            session.beginTransaction();

            // Not started deploy status object
            DeployStatus notStartedDeployStatus = new DeployStatus();
            notStartedDeployStatus.setDeployStatusId((short) 1);

            // Picked deploy status object
            DeployStatus pickedDeployStatus = new DeployStatus();
            pickedDeployStatus.setDeployStatusId((short) 5);

            //Listing passed number of PDQ rows with not started status
            Criteria checkNotStartedProcesses = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deployStatus", notStartedDeployStatus));
            LOGGER.info("Deploy number passed :" + deployNumber);
            Integer notStartedProcessCount = checkNotStartedProcesses.list().size();
            LOGGER.info("Not started process count :" + notStartedProcessCount);
            if (notStartedProcessCount >= deployNumber) {
                checkNotStartedProcesses.setMaxResults(deployNumber);
            }
            notStartedPDQList = checkNotStartedProcesses.list();

            //updating  deploy status of process to picked in ProcessDeploymentQueue table from deploy status as not started
            if (notStartedProcessCount != 0) {
                for (ProcessDeploymentQueue pdq : notStartedPDQList) {
                    pdq.setDeployStatus(pickedDeployStatus);
                    session.update(pdq);
                }

            } else {
                LOGGER.info("Nothing fetched from PDQ to update");
            }
            //Listing passed number of PDQ rows with  picked status
            Criteria checkPickedProcesses = session.createCriteria(ProcessDeploymentQueue.class).add(Restrictions.eq("deployStatus", pickedDeployStatus));
            List<ProcessDeploymentQueue> pickedPDQList = new ArrayList<ProcessDeploymentQueue>();
            Integer pickedProcessCount = checkPickedProcesses.list().size();
            LOGGER.info("Picked process count :" + pickedProcessCount);
            if (pickedProcessCount >= deployNumber) {
                checkPickedProcesses.setMaxResults(deployNumber);
            }
            pickedPDQList = checkPickedProcesses.list();

            //Mapping hibernate PDQ result beans into normal PDQ beans
            for (ProcessDeploymentQueue pickedPDQ : pickedPDQList) {
                com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue returnPDQ = new com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue();
                returnPDQ.setDeploymentId(pickedPDQ.getDeploymentId());
                returnPDQ.setProcessId(pickedPDQ.getProcess().getProcessId());
                returnPDQ.setDeployStatusId((int) pickedPDQ.getDeployStatus().getDeployStatusId());
                returnPDQ.setBusDomainId(pickedPDQ.getBusDomain().getBusDomainId());
                returnPDQ.setProcessTypeId(pickedPDQ.getProcessType().getProcessTypeId());
                returnPDQ.setInsertTs(pickedPDQ.getInsertTs());
                returnPDQ.setStartTs(pickedPDQ.getStartTs());
                returnPDQ.setEndTs(pickedPDQ.getEndTs());
                returnPDQ.setDeployScriptLocation(pickedPDQ.getDeployScriptLocation());
                returnPDQ.setFetchNum(deployNumber);
                returnPDQ.setCounter(notStartedProcessCount);
                returnPDQ.setUserName(pickedPDQ.getUserName());

                returnPDQList.add(returnPDQ);
            }

            session.getTransaction().commit();

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
        } finally {
            session.close();
        }
        return returnPDQList;
    }
}
