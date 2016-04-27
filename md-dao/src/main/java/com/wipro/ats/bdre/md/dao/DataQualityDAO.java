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
import com.wipro.ats.bdre.md.beans.DQSetupInfo;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by PR324290 on 11/26/2015.
 */
@Transactional
@Service
public class DataQualityDAO {
    private static final Logger LOGGER = Logger.getLogger(DataQualityDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    private static final String PROCESSID="process.processId";
    private static final String CONFIGGROUP="configGroup";
    public void deleteDQSetup(int pid) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Criteria deletePropertiesCriteria = session.createCriteria(Properties.class).add(Restrictions.eq(PROCESSID, pid));
            deletePropertiesCriteria.add(Restrictions.eq(CONFIGGROUP, "dq"));
            List<Properties> deletingProperties = deletePropertiesCriteria.list();
            Iterator<Properties> iterator = deletingProperties.iterator();
            while (iterator.hasNext()) {
                Properties properties = iterator.next();
                session.delete(properties);
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

    }

    public List<com.wipro.ats.bdre.md.beans.table.Properties> listDQSetup(int startPage, int pageSize) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.Properties> tablePropertiesList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Properties>();

        try {
            session.beginTransaction();
            Criteria propertiesCriteria = session.createCriteria(Properties.class);
            propertiesCriteria.add(Restrictions.eq(CONFIGGROUP, "dq"));
            propertiesCriteria.setProjection(Projections.distinct(Projections.property("process")));
            int counter = propertiesCriteria.list().size();
            LOGGER.info("Distinct process id count: " + counter);
            Criteria listPropertiesCriteria = session.createCriteria(Properties.class);
            listPropertiesCriteria.add(Restrictions.eq(CONFIGGROUP, "dq"));
            listPropertiesCriteria.addOrder(Order.desc(PROCESSID));
            listPropertiesCriteria.setFirstResult(startPage).setMaxResults(pageSize);
            List<Properties> propertiesList = listPropertiesCriteria.list();

            Iterator<Properties> iterator = propertiesList.iterator();
            while (iterator.hasNext()) {
                Properties properties = iterator.next();
                com.wipro.ats.bdre.md.beans.table.Properties tableProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                if (properties.getProcess() != null)
                    tableProperties.setProcessId(properties.getProcess().getProcessId());
                if (properties.getProcess() != null && properties.getProcess().getProcess() != null)
                    tableProperties.setParentProcessId(properties.getProcess().getProcess().getProcessId());
                tableProperties.setConfigGroup(properties.getConfigGroup());
                if (properties.getId() != null)
                    tableProperties.setKey(properties.getId().getPropKey());
                tableProperties.setValue(properties.getPropValue());
                tableProperties.setDescription(properties.getDescription());
                tableProperties.setSubProcessId(properties.getProcess().getProcessId());
                tableProperties.setCounter(counter);
                tablePropertiesList.add(tableProperties);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return tablePropertiesList;
    }


    public List<com.wipro.ats.bdre.md.beans.table.Properties> insertDQSetup(DQSetupInfo dqSetupInfo,String username) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.Properties> tablePropertiesList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Properties>();

        try {
            session.beginTransaction();
            UserRoles userRoles=new UserRoles();
            Criteria criteria = session.createCriteria(UserRoles.class).add(Restrictions.eq("users.username", username)).addOrder(Order.asc("userRoleId"));
            if (criteria.list()!=null){
                userRoles = (UserRoles) criteria.list().get(0);
            }
            LOGGER.info("process came in insertDQ");
            com.wipro.ats.bdre.md.dao.jpa.Process jpaProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            jpaProcess.setAddTs(new Date());
            jpaProcess.setEditTs(new Date());
            jpaProcess.setDescription( dqSetupInfo.getDescription());
            jpaProcess.setProcessName( dqSetupInfo.getProcessName());
            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(dqSetupInfo.getBusDomainId());
            jpaProcess.setBusDomain(busDomain);
            ProcessType processType = new ProcessType();
            processType.setProcessTypeId(19);
            jpaProcess.setProcessType(processType);
            jpaProcess.setCanRecover(false);
            jpaProcess.setDeleteFlag(false);
            jpaProcess.setEnqueuingProcessId(dqSetupInfo.getEnqId());
            jpaProcess.setNextProcessId(" ");
            WorkflowType workflowType = (WorkflowType) session.get(WorkflowType.class, 1);
            jpaProcess.setWorkflowType(workflowType);
            PermissionType permissionType=new PermissionType();
            permissionType.setPermissionTypeId(7);
            jpaProcess.setPermissionTypeByUserAccessId(permissionType);
            PermissionType permissionType1=new PermissionType();
            permissionType1.setPermissionTypeId(4);
            jpaProcess.setPermissionTypeByGroupAccessId(permissionType1);
            PermissionType permissionType2=new PermissionType();
            permissionType2.setPermissionTypeId(0);
            jpaProcess.setPermissionTypeByOthersAccessId(permissionType2);
            jpaProcess.setUserRoles(userRoles);
            jpaProcess.setUserName(username);
            Integer parentProcessId = (Integer) session.save(jpaProcess);

            LOGGER.info("inserted ppid is " + parentProcessId);

            com.wipro.ats.bdre.md.dao.jpa.Process jpaProcessStep = new com.wipro.ats.bdre.md.dao.jpa.Process();
            jpaProcessStep.setAddTs(new Date());
            jpaProcessStep.setEditTs(new Date());
            jpaProcessStep.setDescription(dqSetupInfo.getDescription() );
            jpaProcessStep.setProcessName("SubProcess of "+dqSetupInfo.getProcessName());
            BusDomain busDomainStep = new BusDomain();
            busDomainStep.setBusDomainId(dqSetupInfo.getBusDomainId());
            jpaProcessStep.setBusDomain(busDomainStep);
            ProcessType processTypeStep = new ProcessType();
            processTypeStep.setProcessTypeId(16);
            jpaProcessStep.setProcessType(processTypeStep);
            Process parentProcessStep = (Process)session.get(Process.class,parentProcessId);

            jpaProcessStep.setProcess(parentProcessStep);
            jpaProcessStep.setDeleteFlag(false);

            if (dqSetupInfo.getCanRecover() == null)
                jpaProcessStep.setCanRecover(true);
            else
                jpaProcessStep.setCanRecover(dqSetupInfo.getCanRecover());
            jpaProcessStep.setEnqueuingProcessId(dqSetupInfo.getEnqId());
            jpaProcessStep.setNextProcessId(parentProcessId.toString());
            WorkflowType workflowTypeStep = (WorkflowType) session.get(WorkflowType.class, 1);
            jpaProcessStep.setWorkflowType(workflowTypeStep);
            Integer subProcessId = (Integer) session.save(jpaProcessStep);
            LOGGER.info("inserted subProcessId is " + subProcessId);

            //Parent process updated
            jpaProcess.setNextProcessId(subProcessId.toString());
            session.update(jpaProcess);
            Properties userName = new Properties();

            userName.setProcess(jpaProcessStep);
            userName.setConfigGroup(dqSetupInfo.getConfigGroup());
            userName.setDescription(dqSetupInfo.getDescription());
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setProcessId(subProcessId);
            propertiesId.setPropKey(dqSetupInfo.getRulesUserName());

            userName.setId(propertiesId);
            userName.setPropValue(dqSetupInfo.getRulesUserNameValue());


            //inserting rules username
            session.save(userName);
            LOGGER.info("user name properties inserted");
            Properties password = new Properties();

            password.setProcess(jpaProcessStep);
            password.setConfigGroup(dqSetupInfo.getConfigGroup());
            password.setDescription(dqSetupInfo.getDescription());
            PropertiesId propertiesId1 = new PropertiesId();
            propertiesId1.setProcessId(subProcessId);
            propertiesId1.setPropKey(dqSetupInfo.getRulesPassword());
            password.setId(propertiesId1);
            password.setPropValue(dqSetupInfo.getRulesPasswordValue());


            //inserting rules password
            session.save(password);
            LOGGER.info("password properties inserted");

            Properties rulesPackage = new Properties();

            rulesPackage.setProcess(jpaProcessStep);
            rulesPackage.setConfigGroup(dqSetupInfo.getConfigGroup());
            rulesPackage.setDescription(dqSetupInfo.getDescription());
            PropertiesId propertiesId2 = new PropertiesId();
            propertiesId2.setProcessId(subProcessId);
            propertiesId2.setPropKey(dqSetupInfo.getRulesPackage());
            rulesPackage.setId(propertiesId2);
            rulesPackage.setPropValue(dqSetupInfo.getRulesPackageValue());
            //inserting rules package
            session.save(rulesPackage);

            Properties delimiter = new Properties();

            delimiter.setProcess(jpaProcessStep);
            delimiter.setConfigGroup(dqSetupInfo.getConfigGroup());
            delimiter.setDescription(dqSetupInfo.getDescription());
            PropertiesId propertiesId3 = new PropertiesId();
            propertiesId3.setProcessId(subProcessId);
            propertiesId3.setPropKey(dqSetupInfo.getFileDelimiterRegex());
            delimiter.setId(propertiesId3);
            delimiter.setPropValue(dqSetupInfo.getFileDelimiterRegexValue());
            // inserting file delimiter property
            session.save(delimiter);

            Properties threshold = new Properties();

            threshold.setProcess(jpaProcessStep);
            threshold.setConfigGroup(dqSetupInfo.getConfigGroup());
            threshold.setDescription(dqSetupInfo.getDescription());
            PropertiesId propertiesId4 = new PropertiesId();
            propertiesId4.setProcessId(subProcessId);
            propertiesId4.setPropKey(dqSetupInfo.getMinPassThresholdPercent());
            threshold.setId(propertiesId4);
            threshold.setPropValue(dqSetupInfo.getMinPassThresholdPercentValue());
            // inserting threshold property
            session.save(threshold);


            Criteria countCriteria = session.createCriteria(Properties.class).add(Restrictions.eq("id.processId", subProcessId));
            countCriteria.add(Restrictions.eq(CONFIGGROUP, dqSetupInfo.getConfigGroup()));

            List<Properties> jpaPropertiesList = countCriteria.list();
            int counter = jpaPropertiesList.size();

            Iterator<Properties> iterator = jpaPropertiesList.iterator();
            while (iterator.hasNext()) {
                Properties returnProperties = iterator.next();
                com.wipro.ats.bdre.md.beans.table.Properties tableProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                tableProperties.setSubProcessId(subProcessId);
                tableProperties.setParentProcessId(parentProcessId);
                tableProperties.setConfigGroup(returnProperties.getConfigGroup());
                tableProperties.setValue(returnProperties.getPropValue());
                if (returnProperties.getId() != null)
                    tableProperties.setKey(returnProperties.getId().getPropKey());
                tableProperties.setDescription(returnProperties.getDescription());
                tableProperties.setCounter(counter);
                tablePropertiesList.add(tableProperties);

            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return tablePropertiesList;

    }

    public List<com.wipro.ats.bdre.md.beans.table.Properties> updateDQSetup(DQSetupInfo dqSetupInfo) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.Properties> tablePropertiesList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Properties>();

        try {
            session.beginTransaction();
            PropertiesId propertiesId1 = new PropertiesId();
            propertiesId1.setProcessId(dqSetupInfo.getSubProcessId());
            propertiesId1.setPropKey(dqSetupInfo.getRulesUserName());
            LOGGER.info(" propertiesId1 processId" + propertiesId1.getProcessId());
            LOGGER.info(" propertiesId1 setPropKey" + propertiesId1.getPropKey());
            LOGGER.info("dqSetupInfo.getConfigGroup() " + dqSetupInfo.getConfigGroup());
            Criteria updatePropertiesCriteria1 = session.createCriteria(Properties.class).add(Restrictions.eq("id", propertiesId1));
            updatePropertiesCriteria1.add(Restrictions.eq(CONFIGGROUP, dqSetupInfo.getConfigGroup()));
            Properties updatingProperties1 = (Properties) updatePropertiesCriteria1.uniqueResult();
            LOGGER.info("updating propertiy is " + updatingProperties1);
            updatingProperties1.setPropValue(dqSetupInfo.getRulesUserNameValue());
            updatingProperties1.setDescription(dqSetupInfo.getDescription());
            session.update(updatingProperties1);

            PropertiesId propertiesId2 = new PropertiesId();
            propertiesId2.setProcessId(dqSetupInfo.getSubProcessId());
            propertiesId2.setPropKey(dqSetupInfo.getRulesPassword());
            Criteria updatePropertiesCriteria2 = session.createCriteria(Properties.class).add(Restrictions.eq("id", propertiesId2));
            updatePropertiesCriteria2.add(Restrictions.eq(CONFIGGROUP, dqSetupInfo.getConfigGroup()));
            Properties updatingProperties2 = (Properties) updatePropertiesCriteria2.uniqueResult();
            updatingProperties2.setPropValue(dqSetupInfo.getRulesPasswordValue());
            updatingProperties2.setDescription(dqSetupInfo.getDescription());
            session.update(updatingProperties2);

            PropertiesId propertiesId3 = new PropertiesId();
            propertiesId3.setProcessId(dqSetupInfo.getSubProcessId());
            propertiesId3.setPropKey(dqSetupInfo.getRulesPackage());
            Criteria updatePropertiesCriteria3 = session.createCriteria(Properties.class).add(Restrictions.eq("id", propertiesId3));
            updatePropertiesCriteria3.add(Restrictions.eq(CONFIGGROUP, dqSetupInfo.getConfigGroup()));
            Properties updatingProperties3 = (Properties) updatePropertiesCriteria3.uniqueResult();
            updatingProperties3.setPropValue(dqSetupInfo.getRulesPackageValue());
            updatingProperties3.setDescription(dqSetupInfo.getDescription());
            session.update(updatingProperties3);

            PropertiesId propertiesId4 = new PropertiesId();
            propertiesId4.setProcessId(dqSetupInfo.getSubProcessId());
            propertiesId4.setPropKey(dqSetupInfo.getFileDelimiterRegex());
            Criteria updatePropertiesCriteria4 = session.createCriteria(Properties.class).add(Restrictions.eq("id", propertiesId4));
            updatePropertiesCriteria4.add(Restrictions.eq(CONFIGGROUP, dqSetupInfo.getConfigGroup()));
            Properties updatingProperties4 = (Properties) updatePropertiesCriteria4.uniqueResult();
            updatingProperties4.setPropValue(dqSetupInfo.getFileDelimiterRegexValue());
            updatingProperties4.setDescription(dqSetupInfo.getDescription());
            session.update(updatingProperties4);

            PropertiesId propertiesId5 = new PropertiesId();
            propertiesId5.setProcessId(dqSetupInfo.getSubProcessId());
            propertiesId5.setPropKey(dqSetupInfo.getMinPassThresholdPercent());
            Criteria updatePropertiesCriteria5 = session.createCriteria(Properties.class).add(Restrictions.eq("id", propertiesId5));
            updatePropertiesCriteria5.add(Restrictions.eq(CONFIGGROUP, dqSetupInfo.getConfigGroup()));
            Properties updatingProperties5 = (Properties) updatePropertiesCriteria5.uniqueResult();
            updatingProperties5.setPropValue(dqSetupInfo.getMinPassThresholdPercentValue());
            updatingProperties5.setDescription(dqSetupInfo.getDescription());
            session.update(updatingProperties5);

            Criteria countCriteria = session.createCriteria(Properties.class).add(Restrictions.eq(PROCESSID, dqSetupInfo.getSubProcessId()));
            countCriteria.add(Restrictions.eq(CONFIGGROUP, dqSetupInfo.getConfigGroup()));
            List<Properties> jpaPropertiesList = countCriteria.list();
            int counter = jpaPropertiesList.size();
            Iterator<Properties> iterator = jpaPropertiesList.iterator();
            while (iterator.hasNext()) {
                Properties properties = iterator.next();
                com.wipro.ats.bdre.md.beans.table.Properties tableProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                tableProperties.setProcessId(properties.getProcess().getProcessId());
                tableProperties.setConfigGroup(properties.getConfigGroup());
                tableProperties.setValue(properties.getPropValue());
                if (properties.getId() != null)
                    tableProperties.setKey(properties.getId().getPropKey());
                tableProperties.setDescription(properties.getDescription());
                tableProperties.setCounter(counter);
                tablePropertiesList.add(tableProperties);

            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return tablePropertiesList;

    }
}
