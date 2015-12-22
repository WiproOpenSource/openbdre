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
import com.wipro.ats.bdre.md.beans.MQImportInfo;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.triggers.ProcessValidateInsert;
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
 * Created by SU324335 on 26-Nov-15.
 */

@Transactional
@Service
public class MQImportSetupDAO {
    private static final Logger LOGGER = Logger.getLogger(MQImportSetupDAO.class);

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    ProcessDAO processDAO;

    @Autowired
    PropertiesDAO propertiesDAO;

    // @Autowired
    // ProcessValidateInsert processValidateInsert;
    public List<Properties> list(int page, int numResults) {
        Session session = sessionFactory.openSession();
        List<Properties> propertiesList = new ArrayList<Properties>();

        try {
            session.beginTransaction();
            Criteria uniquePropertiesListCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("configGroup", "mqimport")).setProjection(Projections.distinct(Projections.property("id.processId")));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> jpaUniquePropertiesList = uniquePropertiesListCriteria.list();
            int jpaUniquePropertiesListSize = jpaUniquePropertiesList.size();
            LOGGER.info("Distinct Properties Count: " + jpaUniquePropertiesListSize);

            Criteria propertiesListCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("configGroup", "mqimport")).addOrder(Order.asc("id.processId"));
            propertiesListCriteria.setFirstResult(page);
            propertiesListCriteria.setMaxResults(numResults);
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> jpaPropertiesList = propertiesListCriteria.list();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperty : jpaPropertiesList) {
                Properties properties = new Properties();
                properties.setCounter(jpaUniquePropertiesListSize);
                if (jpaProperty.getProcess() != null) {
                    properties.setProcessId(jpaProperty.getProcess().getProcessId());
                }
                if (jpaProperty.getProcess().getProcess() != null) {
                    properties.setParentProcessId(jpaProperty.getProcess().getProcess().getProcessId());
                }
                properties.setConfigGroup(jpaProperty.getConfigGroup());
                properties.setKey(jpaProperty.getId().getPropKey());
                properties.setValue(jpaProperty.getPropValue());
                properties.setDescription(jpaProperty.getDescription());

                propertiesList.add(properties);
                LOGGER.info("Process ID of Properties is" + properties.getProcessId());
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesList;

    }

    public List<Properties> insert(MQImportInfo mqImportInfo) {
        Session session = sessionFactory.openSession();
        List<Properties> propertiesList = new ArrayList<Properties>();

        try {

            session.beginTransaction();
            Process process = new Process();
            //('MQ Import Job',current_timestamp, 'MQ Import Job',busdomainid,20,null,0,0,null,'',2);
            process.setDescription("MQ Import Job");
            process.setAddTs(new Date());
            process.setEditTs(new Date());
            process.setProcessName("MQ Import Job");

            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(mqImportInfo.getBusDomainId());
            process.setBusDomain(busDomain);
            ProcessType processType = new ProcessType();
            processType.setProcessTypeId(20);
            process.setProcessType(processType);
            WorkflowType workflowType = new WorkflowType();
            workflowType.setWorkflowId(2);
            process.setWorkflowType(workflowType);
            process.setCanRecover(false);
            process.setDeleteFlag(false);
            process.setEnqueuingProcessId(0);
            process.setNextProcessId(" ");
            ProcessValidateInsert processValidateInsert=new ProcessValidateInsert();
            Integer parentProcessId;
            if(process.getProcess()!=null) {
                boolean triggerCheck=processValidateInsert.ProcessTypeValidator(process);
                if(triggerCheck==true)
                {
                    parentProcessId = (Integer) session.save(process);
                }
                else
                {
                    parentProcessId=null;
                    throw new MetadataException("error occured");
                }
            }
            else {
                parentProcessId = (Integer) session.save(process);
            }
            LOGGER.info("ProcessID of Process inserted is " + process.getProcessId());

            // ('MQ Import Step',current_timestamp, 'MQ Import Step',busdomainid,21,ppid,canrecover,0,null,ppid,0);
            Process subProcess = new Process();
            subProcess.setDescription("MQ Import Step");
            subProcess.setAddTs(new Date());
            subProcess.setEditTs(new Date());
            subProcess.setProcessName("MQ Import Step");

            BusDomain subProcessBusDomain = new BusDomain();
            subProcessBusDomain.setBusDomainId(mqImportInfo.getBusDomainId());
            subProcess.setBusDomain(subProcessBusDomain);

            ProcessType subProcessType = new ProcessType();
            subProcessType.setProcessTypeId(21);
            subProcess.setProcessType(subProcessType);

            Process parentSubProcess = new Process();
            parentSubProcess.setProcessId(parentProcessId);
            subProcess.setProcess(parentSubProcess);

            WorkflowType subWorkflowType = new WorkflowType();
            subWorkflowType.setWorkflowId(0);
            subProcess.setWorkflowType(subWorkflowType);

            subProcess.setCanRecover(mqImportInfo.getCanRecover());
            subProcess.setEnqueuingProcessId(0);
            subProcess.setNextProcessId(parentProcessId.toString());
            subProcess.setDeleteFlag(false);
            Integer subProcessId = (Integer) session.save(subProcess);
            LOGGER.info("sub process id" + subProcessId);
            LOGGER.info("ProcessID of SubProcess inserted is " + subProcess.getProcessId());

            //update process set next_process_id= spid where process_id=ppid;
            process.setNextProcessId(subProcessId.toString());
            session.update(process);

        /*insert into properties (process_id,config_group,prop_key,prop_value,description) values (spid,cnfg_group,broker_url,broker_url_value,'Broker URL of Active MQ ');
        insert into properties (process_id,config_group,prop_key,prop_value,description) values (spid,cnfg_group,queue_name,queue_name_value,'Name of the Queue of ActiveMQ');
        insert into properties (process_id,config_group,prop_key,prop_value,description) values (spid,cnfg_group,num_spouts,num_spouts_value,'Number of spouts/parallel message consumers');
        insert into properties (process_id,config_group,prop_key,prop_value,description) values (spid,cnfg_group,num_bolts,num_bolts_value,'Number of bolts/parallel message processors');

*/
            com.wipro.ats.bdre.md.dao.jpa.Properties propertiesBrokerURL = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesIdBrokerURL = new PropertiesId();

            propertiesIdBrokerURL.setProcessId(subProcessId);
            propertiesIdBrokerURL.setPropKey(MQImportInfo.getBrokerUrl());

            propertiesBrokerURL.setConfigGroup(mqImportInfo.getConfigGroup());
            propertiesBrokerURL.setPropValue(mqImportInfo.getBrokerUrlValue());
            propertiesBrokerURL.setId(propertiesIdBrokerURL);
            propertiesBrokerURL.setDescription("Broker URL of Active MQ ");

            //inserting Broker URL
            session.save(propertiesBrokerURL);
            LOGGER.info("Property key of inserted Broker URL: " + propertiesBrokerURL.getId().getPropKey());

            com.wipro.ats.bdre.md.dao.jpa.Properties propertiesNameQueue = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesIdNameQueue = new PropertiesId();
            propertiesIdNameQueue.setProcessId(subProcessId);
            propertiesNameQueue.setConfigGroup(mqImportInfo.getConfigGroup());
            propertiesIdNameQueue.setPropKey(MQImportInfo.getQueueName());
            propertiesNameQueue.setId(propertiesIdNameQueue);
            propertiesNameQueue.setPropValue(mqImportInfo.getQueueNameValue());
            propertiesNameQueue.setDescription("Name of the Queue of ActiveMQ ");
            //inserting Name of the Queue
            session.save(propertiesNameQueue);
            LOGGER.info("Property key of inserted NameQueueL: " + propertiesNameQueue.getId().getPropKey());


            com.wipro.ats.bdre.md.dao.jpa.Properties propertiesSpouts = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesIdSpouts = new PropertiesId();
            propertiesIdSpouts.setProcessId(subProcessId);
            propertiesSpouts.setConfigGroup(mqImportInfo.getConfigGroup());
            propertiesIdSpouts.setPropKey(MQImportInfo.getNumSpouts());
            propertiesSpouts.setId(propertiesIdSpouts);
            propertiesSpouts.setPropValue(String.valueOf(mqImportInfo.getNumSpoutsValue()));
            propertiesSpouts.setDescription("Number of spouts/parallel message consumers ");
            //inserting Number of spouts
            session.save(propertiesSpouts);
            LOGGER.info("Property key of inserted Spouts: " + propertiesSpouts.getId().getPropKey());

            com.wipro.ats.bdre.md.dao.jpa.Properties propertiesBolts = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesIdBolts = new PropertiesId();
            propertiesIdBolts.setProcessId(subProcessId);
            propertiesBolts.setConfigGroup(mqImportInfo.getConfigGroup());
            propertiesIdBolts.setPropKey(MQImportInfo.getNumBolts());
            propertiesBolts.setId(propertiesIdBolts);
            propertiesBolts.setPropValue(String.valueOf(mqImportInfo.getNumBoltsValue()));
            propertiesBolts.setDescription("Number of bolts/parallel message processors ");
            //inserting Number of bolts
            session.save(propertiesBolts);
            LOGGER.info("Property key of inserted Bolts: " + propertiesBolts.getId().getPropKey());

            Criteria counterCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("id.processId", subProcessId)).add(Restrictions.eq("configGroup", mqImportInfo.getConfigGroup()));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> jpaPropertiesList = counterCriteria.list();
            int counter = jpaPropertiesList.size();

            for (com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties : jpaPropertiesList) {
                Properties newProperty = new Properties();
                newProperty.setSubProcessId(subProcessId);
                newProperty.setParentProcessId(parentProcessId);
                newProperty.setConfigGroup(jpaProperties.getConfigGroup());
                newProperty.setDescription(jpaProperties.getDescription());
                newProperty.setCounter(counter);
                newProperty.setKey(jpaProperties.getId().getPropKey());
                newProperty.setValue(jpaProperties.getPropValue());
                LOGGER.info("Config group of Returning Property is " + newProperty.getConfigGroup());
                propertiesList.add(newProperty);
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesList;

    }

    public void delete(int processId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            //delete from properties where process_id = pid and config_group= 'mqimport'  ;
            Criteria deletingPropertiesCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("id.processId", processId)).add(Restrictions.eq("configGroup", "mqimport"));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> deletingPropertiesList = deletingPropertiesCriteria.list();

            for (com.wipro.ats.bdre.md.dao.jpa.Properties properties : deletingPropertiesList) {
                session.delete(properties);
                LOGGER.info("ProcessID of Properties deleted:" + properties.getId().getProcessId());

            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


    public List<Properties> update(MQImportInfo mqImportInfo) {
        Session session = sessionFactory.openSession();
        List<Properties> propertiesList = new ArrayList<Properties>();

        try {
            session.beginTransaction();
        /*update properties set prop_value=broker_url_value,description='Broker URL of Active MQ ' where process_id = p_id && config_group = cnfg_group && prop_key = broker_url ;
        update properties set prop_value=queue_name_value,description='Name of the Queue of ActiveMQ' where process_id = p_id && config_group = cnfg_group &&  prop_key = queue_name ;
        update properties set prop_value=num_spouts_value,description='Number of spouts/parallel message consumer' where process_id = p_id && config_group = cnfg_group && prop_key = num_spouts ;
        update properties set prop_value=num_bolts_value,description='Number of bolts/parallel message processors' where process_id = p_id && config_group = cnfg_group &&  prop_key = num_bolts ;
*/
            Criteria brokerUrlUpdatingPropertiesCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("id.processId", mqImportInfo.getSubProcessId())).add(Restrictions.eq("configGroup", mqImportInfo.getConfigGroup())).add(Restrictions.eq("id.propKey", MQImportInfo.getBrokerUrl()));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> brokerUrlUpdatingPropertiesList = brokerUrlUpdatingPropertiesCriteria.list();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties brokerUrlUpdatingProperties : brokerUrlUpdatingPropertiesList) {
                brokerUrlUpdatingProperties.setPropValue(mqImportInfo.getBrokerUrlValue());
                brokerUrlUpdatingProperties.setDescription("Broker URL of Active MQ ");

                //updating broker url
                session.update(brokerUrlUpdatingProperties);
                LOGGER.info("Value of Updated Broker Url property" + brokerUrlUpdatingProperties.getPropValue());
            }

            Criteria queueUpdatingPropertiesCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("id.processId", mqImportInfo.getSubProcessId())).add(Restrictions.eq("configGroup", mqImportInfo.getConfigGroup())).add(Restrictions.eq("id.propKey", MQImportInfo.getQueueName()));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> queueUpdatingPropertiesList = queueUpdatingPropertiesCriteria.list();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties queueUpdatingProperties : queueUpdatingPropertiesList) {
                queueUpdatingProperties.setPropValue(mqImportInfo.getQueueNameValue());
                queueUpdatingProperties.setDescription("Name of the Queue of ActiveMQ");
                //updating Name of Queue
                session.update(queueUpdatingProperties);
                LOGGER.info("Value of Updated Broker Url property" + queueUpdatingProperties.getPropValue());
            }

            Criteria spoutsUpdatingPropertiesCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("id.processId", mqImportInfo.getSubProcessId())).add(Restrictions.eq("configGroup", mqImportInfo.getConfigGroup())).add(Restrictions.eq("id.propKey", MQImportInfo.getNumSpouts()));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> spoutsUpdatingPropertiesList = spoutsUpdatingPropertiesCriteria.list();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties spoutsUpdatingProperties : spoutsUpdatingPropertiesList) {
                spoutsUpdatingProperties.setPropValue(String.valueOf(mqImportInfo.getNumSpoutsValue()));
                spoutsUpdatingProperties.setDescription("Number of spouts/parallel message consumer");
                //updating spouts
                session.update(spoutsUpdatingProperties);
                LOGGER.info("Value of Updated Broker Url property" + spoutsUpdatingProperties.getPropValue());
            }

            Criteria boltsUpdatingPropertiesCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("id.processId", mqImportInfo.getSubProcessId())).add(Restrictions.eq("configGroup", mqImportInfo.getConfigGroup())).add(Restrictions.eq("id.propKey", MQImportInfo.getNumBolts()));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> boltsUpdatingPropertiesList = boltsUpdatingPropertiesCriteria.list();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties boltsUpdatingProperties : boltsUpdatingPropertiesList) {
                boltsUpdatingProperties.setPropValue(String.valueOf(mqImportInfo.getNumBoltsValue()));
                boltsUpdatingProperties.setDescription("Number of bolts/parallel message processors");
                //updating Bolts
                session.update(boltsUpdatingProperties);
                LOGGER.info("Value of Updated Broker Url property" + boltsUpdatingProperties.getPropValue());
            }

            Criteria fetchMQPropertiesCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq("id.processId", mqImportInfo.getSubProcessId())).add(Restrictions.eq("configGroup", mqImportInfo.getConfigGroup()));
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> jpaPropertiesList = fetchMQPropertiesCriteria.list();
            int counter = jpaPropertiesList.size();

            for (com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties : jpaPropertiesList) {
                Properties newProperty = new Properties();
                newProperty.setProcessId(jpaProperties.getId().getProcessId());
                newProperty.setConfigGroup(jpaProperties.getConfigGroup());
                newProperty.setDescription(jpaProperties.getDescription());
                newProperty.setCounter(counter);
                newProperty.setKey(jpaProperties.getId().getPropKey());
                newProperty.setValue(jpaProperties.getPropValue());

                propertiesList.add(newProperty);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesList;

    }

}
