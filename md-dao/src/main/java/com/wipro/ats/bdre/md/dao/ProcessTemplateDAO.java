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
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplate;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by PR324290 on 10/29/2015.
 */
@Transactional
@Service
public class ProcessTemplateDAO {


    private static final Logger LOGGER = Logger.getLogger(ProcessTemplateDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    private static final String PROCESSTEMPLATEID="processTemplateId";
    private static final String PARENTPROCESSTEMPLATEID="processTemplate.processTemplateId";
    private static final String DELETE_FLAG="deleteFlag";
    private static final String CONFIG_GROUP="configGroup";
    private static final String CONFIG_GROUPID="id.processId";

    public List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> list(Integer pageNum, Integer numResults, Integer processId) {
        Session session = sessionFactory.openSession();

        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> processTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.ProcessTemplate>();
        try {
            session.beginTransaction();
            Criteria fetchProcessTPPId = session.createCriteria(ProcessTemplate.class).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processId));
            Criteria fetchProcessTPId = session.createCriteria(ProcessTemplate.class).add(Restrictions.eq(PROCESSTEMPLATEID, processId));

            if (processId == null) {

                //(parent_process_id is null and delete_flag != 1 and process_template_id !=0) order by process_template_id
                Criteria fetchProcessTemplate = session.createCriteria(ProcessTemplate.class).add(Restrictions.isNull(PARENTPROCESSTEMPLATEID)).add(Restrictions.eq(DELETE_FLAG, false)).add(Restrictions.not(Restrictions.eq(PROCESSTEMPLATEID, 0))).addOrder(Order.asc(PROCESSTEMPLATEID));
                fetchProcessTemplate.setFirstResult(pageNum);
                fetchProcessTemplate.setMaxResults(numResults);
                List<ProcessTemplate> jpaPTList = fetchProcessTemplate.list();

                LOGGER.info("List size of process template:" + fetchProcessTemplate.list().size());
                for (ProcessTemplate jpaProcessTemplate : jpaPTList) {
                    com.wipro.ats.bdre.md.beans.table.ProcessTemplate processTemplate = new com.wipro.ats.bdre.md.beans.table.ProcessTemplate();

                    processTemplate.setProcessTemplateId(jpaProcessTemplate.getProcessTemplateId());
                    processTemplate.setDescription(jpaProcessTemplate.getDescription());
                    processTemplate.setAddTS(jpaProcessTemplate.getAddTs());
                    processTemplate.setProcessName(jpaProcessTemplate.getProcessName());
                    processTemplate.setBusDomainId(jpaProcessTemplate.getBusDomain().getBusDomainId());
                    processTemplate.setProcessTypeId(jpaProcessTemplate.getProcessType().getProcessTypeId());
                    if (jpaProcessTemplate.getProcessTemplate() != null)
                        processTemplate.setParentProcessId(jpaProcessTemplate.getProcessTemplate().getProcessTemplateId());
                    processTemplate.setCanRecover(jpaProcessTemplate.getCanRecover());
                    processTemplate.setBatchPattern(jpaProcessTemplate.getBatchCutPattern());
                    processTemplate.setNextProcessTemplateId(jpaProcessTemplate.getNextProcessTemplateId());
                    processTemplateList.add(processTemplate);
                }

            } else if (fetchProcessTPPId.list().isEmpty() && fetchProcessTPId.list().size() == 1) {
                ProcessTemplate processTemplatePid = (ProcessTemplate) fetchProcessTPId.uniqueResult();
                if (processTemplatePid.getProcessTemplate() != null && processTemplatePid != null) {
                    Integer parentProcessId = processTemplatePid.getProcessTemplate().getProcessTemplateId();

                    Criteria fetchProcessTemplate = session.createCriteria(ProcessTemplate.class).add(Restrictions.eq(PROCESSTEMPLATEID, parentProcessId)).add(Restrictions.eq(DELETE_FLAG, false)).add(Restrictions.not(Restrictions.eq(PROCESSTEMPLATEID, 0))).addOrder(Order.asc(PROCESSTEMPLATEID));
                    fetchProcessTemplate.setFirstResult(pageNum);
                    fetchProcessTemplate.setMaxResults(numResults);
                    List<ProcessTemplate> jpaPTList = fetchProcessTemplate.list();

                    LOGGER.info("List size of process template : " + fetchProcessTemplate.list().size());
                    for (ProcessTemplate jpaProcessTemplate : jpaPTList) {
                        com.wipro.ats.bdre.md.beans.table.ProcessTemplate processTemplate = new com.wipro.ats.bdre.md.beans.table.ProcessTemplate();

                        processTemplate.setProcessTemplateId(jpaProcessTemplate.getProcessTemplateId());
                        processTemplate.setDescription(jpaProcessTemplate.getDescription());
                        processTemplate.setAddTS(jpaProcessTemplate.getAddTs());
                        processTemplate.setProcessName(jpaProcessTemplate.getProcessName());
                        processTemplate.setBusDomainId(jpaProcessTemplate.getBusDomain().getBusDomainId());
                        processTemplate.setProcessTypeId(jpaProcessTemplate.getProcessType().getProcessTypeId());
                        if (jpaProcessTemplate.getProcessTemplate() != null)
                            processTemplate.setParentProcessId(jpaProcessTemplate.getProcessTemplate().getProcessTemplateId());
                        processTemplate.setCanRecover(jpaProcessTemplate.getCanRecover());
                        processTemplate.setBatchPattern(jpaProcessTemplate.getBatchCutPattern());
                        processTemplate.setNextProcessTemplateId(jpaProcessTemplate.getNextProcessTemplateId());
                        processTemplate.setCounter(jpaPTList.size());
                        processTemplateList.add(processTemplate);
                    }
                }

            } else {
                Criteria fetchProcessTemplate = session.createCriteria(ProcessTemplate.class).add(Restrictions.isNull(PARENTPROCESSTEMPLATEID)).add(Restrictions.eq(PROCESSTEMPLATEID, processId)).add(Restrictions.eq(DELETE_FLAG, false)).add(Restrictions.not(Restrictions.eq(PROCESSTEMPLATEID, 0))).addOrder(Order.asc(PROCESSTEMPLATEID));
                fetchProcessTemplate.setFirstResult(pageNum);
                fetchProcessTemplate.setMaxResults(numResults);

                List<ProcessTemplate> jpaPTList = fetchProcessTemplate.list();

                LOGGER.info("List size of process template :" + fetchProcessTemplate.list().size());
                for (ProcessTemplate jpaProcessTemplate : jpaPTList) {
                    com.wipro.ats.bdre.md.beans.table.ProcessTemplate processTemplate = new com.wipro.ats.bdre.md.beans.table.ProcessTemplate();

                    processTemplate.setProcessTemplateId(jpaProcessTemplate.getProcessTemplateId());
                    processTemplate.setDescription(jpaProcessTemplate.getDescription());
                    processTemplate.setAddTS(jpaProcessTemplate.getAddTs());
                    processTemplate.setProcessName(jpaProcessTemplate.getProcessName());
                    processTemplate.setBusDomainId(jpaProcessTemplate.getBusDomain().getBusDomainId());
                    processTemplate.setProcessTypeId(jpaProcessTemplate.getProcessType().getProcessTypeId());
                    if (jpaProcessTemplate.getProcessTemplate() != null)
                        processTemplate.setParentProcessId(jpaProcessTemplate.getProcessTemplate().getProcessTemplateId());
                    processTemplate.setCanRecover(jpaProcessTemplate.getCanRecover());
                    processTemplate.setBatchPattern(jpaProcessTemplate.getBatchCutPattern());
                    processTemplate.setNextProcessTemplateId(jpaProcessTemplate.getNextProcessTemplateId());
                    processTemplate.setCounter(jpaPTList.size());
                    processTemplateList.add(processTemplate);
                }
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processTemplateList;
    }


    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProcessTemplate.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public ProcessTemplate get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ProcessTemplate processTemplate = (ProcessTemplate) session.get(ProcessTemplate.class, id);
        session.getTransaction().commit();
        session.close();
        return processTemplate;
    }


    public Integer insert(ProcessTemplate processTemplate) {
        Session session = sessionFactory.openSession();
        Integer id = null;
        try {
            session.beginTransaction();
            LOGGER.info(processTemplate.toString());
            id = (Integer) session.save(processTemplate);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }


    public void update(ProcessTemplate processTemplate) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(processTemplate);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


    public void delete(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            ProcessTemplate processTemplate = (ProcessTemplate) session.get(ProcessTemplate.class, id);
            session.delete(processTemplate);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> selectPTList(int processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> returningList = new ArrayList<com.wipro.ats.bdre.md.beans.table.ProcessTemplate>();

        try {
            session.beginTransaction();
            Criteria criteria1 = session.createCriteria(ProcessTemplate.class);
            criteria1.add(Restrictions.eq(PROCESSTEMPLATEID, processTemplateId));
            criteria1.add(Restrictions.eq(DELETE_FLAG, false));
            List<ProcessTemplate> processTemplatesList1 = criteria1.list();

            Criteria criteria2 = session.createCriteria(ProcessTemplate.class);
            criteria2.add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId));
            criteria2.add(Restrictions.eq(DELETE_FLAG, false));
            List<ProcessTemplate> processTemplatesList2 = criteria2.list();

            List<ProcessTemplate> completeProcessTemplatesList = new ArrayList<ProcessTemplate>();
            completeProcessTemplatesList.addAll(processTemplatesList1);
            completeProcessTemplatesList.addAll(processTemplatesList2);
            Iterator<ProcessTemplate> iterator = completeProcessTemplatesList.iterator();
            while (iterator.hasNext()) {
                ProcessTemplate processTemplate = iterator.next();
                com.wipro.ats.bdre.md.beans.table.ProcessTemplate tableProcessTemplate = new com.wipro.ats.bdre.md.beans.table.ProcessTemplate();
                tableProcessTemplate.setProcessTemplateId(processTemplate.getProcessTemplateId());
                tableProcessTemplate.setDescription(processTemplate.getDescription());
                tableProcessTemplate.setProcessName(processTemplate.getProcessName());
                tableProcessTemplate.setBusDomainId(processTemplate.getBusDomain().getBusDomainId());
                tableProcessTemplate.setProcessTypeId(processTemplate.getProcessType().getProcessTypeId());
                if (processTemplate.getProcessTemplate() != null)
                    tableProcessTemplate.setParentProcessId(processTemplate.getProcessTemplate().getProcessTemplateId());
                tableProcessTemplate.setCanRecover(processTemplate.getCanRecover());
                tableProcessTemplate.setBatchPattern(processTemplate.getBatchCutPattern());
                tableProcessTemplate.setNextProcessTemplateId(processTemplate.getNextProcessTemplateId());
                tableProcessTemplate.setWorkflowId(processTemplate.getWorkflowType().getWorkflowId());
                tableProcessTemplate.setCounter(completeProcessTemplatesList.size());
                returningList.add(tableProcessTemplate);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return returningList;
    }

    public List<com.wipro.ats.bdre.md.beans.table.Process> selectPListForTemplate(int processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.Process> tableProcessList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();

        try {
            session.beginTransaction();
            ProcessTemplate processTemplate = (ProcessTemplate) session.get(ProcessTemplate.class, processTemplateId);
            if (processTemplate.getProcessTemplate().getNextProcessTemplateId() != null) {
                Criteria criteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class, "p1").createCriteria("process", "p2");
                criteria.add(Restrictions.eq("p1.process.processId", "p2.processId"));
                criteria.add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId));
                criteria.add(Restrictions.eq(DELETE_FLAG, 0));
                criteria.addOrder(Order.asc("p2.processId"));
                List<Process> jpaProcessList = criteria.list();
                Iterator<Process> iterator = jpaProcessList.iterator();
                while (iterator.hasNext()) {
                    Process jpaProcess = iterator.next();
                    com.wipro.ats.bdre.md.beans.table.Process tableProcess = new com.wipro.ats.bdre.md.beans.table.Process();
                    tableProcess.setProcessId(jpaProcess.getProcessId());
                    tableProcess.setDescription(jpaProcess.getDescription());
                    tableProcess.setProcessName(jpaProcess.getProcessName());
                    tableProcess.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
                    tableProcess.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
                    tableProcess.setCanRecover(jpaProcess.getCanRecover());
                    tableProcess.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
                    tableProcess.setParentProcessId(jpaProcess.getProcess().getProcessId());
                    tableProcess.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
                    tableProcess.setBatchPattern(jpaProcess.getBatchCutPattern());
                    tableProcess.setNextProcessIds(jpaProcess.getNextProcessId());
                    tableProcess.setProcessTemplateId(jpaProcess.getProcessTemplate().getProcessTemplateId());
                    tableProcess.setCounter(jpaProcessList.size());
                    tableProcessList.add(tableProcess);
                }


            } else {
                Criteria criteria = session.createCriteria(Process.class).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId));
                criteria.add(Restrictions.eq(DELETE_FLAG, 0));
                List<Process> jpaProcessList = criteria.list();
                Iterator<Process> iterator = jpaProcessList.iterator();
                while (iterator.hasNext()) {
                    Process jpaProcess = iterator.next();
                    com.wipro.ats.bdre.md.beans.table.Process tableProcess = new com.wipro.ats.bdre.md.beans.table.Process();
                    tableProcess.setProcessId(jpaProcess.getProcessId());
                    tableProcess.setDescription(jpaProcess.getDescription());
                    tableProcess.setProcessName(jpaProcess.getProcessName());
                    tableProcess.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
                    tableProcess.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
                    tableProcess.setCanRecover(jpaProcess.getCanRecover());
                    tableProcess.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
                    tableProcess.setParentProcessId(jpaProcess.getProcess().getProcessId());
                    tableProcess.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
                    tableProcess.setBatchPattern(jpaProcess.getBatchCutPattern());
                    tableProcess.setNextProcessIds(jpaProcess.getNextProcessId());
                    tableProcess.setProcessTemplateId(jpaProcess.getProcessTemplate().getProcessTemplateId());
                    tableProcess.setCounter(jpaProcessList.size());
                    tableProcessList.add(tableProcess);
                }

            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return tableProcessList;
    }

    public List<com.wipro.ats.bdre.md.beans.table.Process> selectPPListForTemplateId(int processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.Process> tableProcessList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();

        try {
            session.beginTransaction();
            Criteria criteriaProcessIds = session.createCriteria(Process.class).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId));
            criteriaProcessIds.setProjection(Projections.property("processId"));
            List<Integer> listProcessIds = criteriaProcessIds.list();
            LOGGER.info("number of process ids are " + listProcessIds.size());
            LOGGER.info("ids are  " + listProcessIds);
            Criteria criteriaProcess1 = session.createCriteria(Process.class).add(Restrictions.eq(DELETE_FLAG, false));
            criteriaProcess1.add(Restrictions.isNull("process.processId"));
            criteriaProcess1.add(Restrictions.in("processId", listProcessIds));
            List<Process> jpaProcessList1 = criteriaProcess1.list();
            LOGGER.info("size of first returned list " + jpaProcessList1.size());

            Criteria criteriaProcess2 = session.createCriteria(Process.class).add(Restrictions.eq(DELETE_FLAG, false));
            criteriaProcess2.add(Restrictions.isNull("process.processId"));
            criteriaProcess2.add(Restrictions.in("process.processId", listProcessIds));
            List<Process> jpaProcessList2 = criteriaProcess2.list();
            LOGGER.info("size of second returned list " + jpaProcessList2.size());
            List<Process> completeProcessList = new ArrayList<Process>();
            completeProcessList.addAll(jpaProcessList1);
            completeProcessList.addAll(jpaProcessList2);
            Iterator<Process> iterator = completeProcessList.iterator();
            while (iterator.hasNext()) {
                Process jpaProcess = iterator.next();
                com.wipro.ats.bdre.md.beans.table.Process tableProcess = new com.wipro.ats.bdre.md.beans.table.Process();
                tableProcess.setProcessId(jpaProcess.getProcessId());
                tableProcess.setDescription(jpaProcess.getDescription());
                tableProcess.setProcessName(jpaProcess.getProcessName());
                tableProcess.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
                tableProcess.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
                tableProcess.setCanRecover(jpaProcess.getCanRecover());
                tableProcess.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
                if (jpaProcess.getProcess() != null)
                    tableProcess.setParentProcessId(jpaProcess.getProcess().getProcessId());
                tableProcess.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
                tableProcess.setBatchPattern(jpaProcess.getBatchCutPattern());
                tableProcess.setNextProcessIds(jpaProcess.getNextProcessId());
                tableProcess.setProcessTemplateId(jpaProcess.getProcessTemplate().getProcessTemplateId());
                tableProcess.setCounter(completeProcessList.size());
                tableProcessList.add(tableProcess);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return tableProcessList;

    }

    public List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> listSubProcessTemplates(int processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> tableProcessTemplates = new ArrayList<com.wipro.ats.bdre.md.beans.table.ProcessTemplate>();

        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(ProcessTemplate.class).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId));
            criteria.add(Restrictions.ne(DELETE_FLAG, true));
            List<ProcessTemplate> processTemplateList = criteria.list();
            Iterator<ProcessTemplate> iterator = processTemplateList.iterator();

            while (iterator.hasNext()) {
                ProcessTemplate jpaProcessTemplate = iterator.next();
                com.wipro.ats.bdre.md.beans.table.ProcessTemplate tableProcessTemplate = new com.wipro.ats.bdre.md.beans.table.ProcessTemplate();
                tableProcessTemplate.setProcessTemplateId(jpaProcessTemplate.getProcessTemplateId());
                tableProcessTemplate.setDescription(jpaProcessTemplate.getDescription());
                tableProcessTemplate.setAddTS(jpaProcessTemplate.getAddTs());
                tableProcessTemplate.setProcessName(jpaProcessTemplate.getProcessName());
                tableProcessTemplate.setBusDomainId(jpaProcessTemplate.getBusDomain().getBusDomainId());
                tableProcessTemplate.setProcessTypeId(jpaProcessTemplate.getProcessType().getProcessTypeId());
                if (jpaProcessTemplate.getProcessTemplate() != null)
                    tableProcessTemplate.setParentProcessId(jpaProcessTemplate.getProcessTemplate().getProcessTemplateId());
                tableProcessTemplate.setCanRecover(jpaProcessTemplate.getCanRecover());
                tableProcessTemplate.setBatchPattern(jpaProcessTemplate.getBatchCutPattern());
                tableProcessTemplate.setCounter(processTemplateList.size());
                tableProcessTemplate.setNextProcessTemplateId(jpaProcessTemplate.getNextProcessTemplateId());
                tableProcessTemplates.add(tableProcessTemplate);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return tableProcessTemplates;
    }

    //fetch process with processId = processTemplateId
    public com.wipro.ats.bdre.md.beans.table.Process selectNextForPid(Integer processId, Integer parentProcessId) {
        Session session = sessionFactory.openSession();
        com.wipro.ats.bdre.md.beans.table.Process returnProcess = new com.wipro.ats.bdre.md.beans.table.Process();

        try {
            session.beginTransaction();
            Criteria fetchProcessList = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.eq(DELETE_FLAG, false)).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processId)).add(Restrictions.or(Restrictions.eq("process.processId", parentProcessId), Restrictions.eq("processId", parentProcessId)));
            Integer listSize = fetchProcessList.list().size();
            LOGGER.info("process list size:" + listSize);
            List<com.wipro.ats.bdre.md.dao.jpa.Process> jpaProcessList = fetchProcessList.list();

            for (com.wipro.ats.bdre.md.dao.jpa.Process process : jpaProcessList) {
                returnProcess.setProcessId(process.getProcessId());
                returnProcess.setProcessName(process.getProcessName());
                returnProcess.setBusDomainId(process.getBusDomain().getBusDomainId());
                returnProcess.setProcessTypeId(process.getProcessType().getProcessTypeId());
                returnProcess.setParentProcessId(process.getProcess().getProcessId());
                returnProcess.setCanRecover(process.getCanRecover());
                returnProcess.setEnqProcessId(process.getEnqueuingProcessId());
                returnProcess.setBatchPattern(process.getBatchCutPattern());
                returnProcess.setNextProcessIds(process.getNextProcessId());
                returnProcess.setWorkflowId(process.getWorkflowType().getWorkflowId());
                returnProcess.setDescription(process.getDescription());
                returnProcess.setCounter(jpaProcessList.size());
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return returnProcess;
    }

    public List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> selectMissingSubPList(Integer processId, Integer processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> tableProcessTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.ProcessTemplate>();

        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(ProcessTemplate.class);
            criteria.add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId));
            criteria.add(Restrictions.eq(DELETE_FLAG, false));
            criteria.setProjection(Projections.property(PROCESSTEMPLATEID));
            List<Integer> processTemplateIdList = criteria.list();
            LOGGER.info("List of processTemplateIdList is " + processTemplateIdList);
            Criteria processCriteria = session.createCriteria(Process.class);
            if (!processTemplateIdList.isEmpty())
                processCriteria.add(Restrictions.not(Restrictions.in(PARENTPROCESSTEMPLATEID, processTemplateIdList)));
            processCriteria.add(Restrictions.eq("process.processId", processId));
            processCriteria.add(Restrictions.eq(DELETE_FLAG, false));
            List<Process> jpaProcessList = processCriteria.list();
            Iterator<Process> iterator = jpaProcessList.iterator();
            while (iterator.hasNext()) {
                Process jpaProcess = iterator.next();
                com.wipro.ats.bdre.md.beans.table.ProcessTemplate tableProcessTemplate = new com.wipro.ats.bdre.md.beans.table.ProcessTemplate();
                tableProcessTemplate.setProcessId(jpaProcess.getProcessId());
                tableProcessTemplate.setDescription(jpaProcess.getDescription());
                tableProcessTemplate.setProcessName(jpaProcess.getProcessName());
                tableProcessTemplate.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
                tableProcessTemplate.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
                tableProcessTemplate.setParentProcessId(jpaProcess.getProcess().getProcessId());
                tableProcessTemplate.setCanRecover(jpaProcess.getCanRecover());
                tableProcessTemplate.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
                tableProcessTemplate.setBatchPattern(jpaProcess.getBatchCutPattern());
                tableProcessTemplate.setNextProcessIds(jpaProcess.getNextProcessId());
                tableProcessTemplate.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
                tableProcessTemplate.setCounter(jpaProcessList.size());
                tableProcessTemplateList.add(tableProcessTemplate);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return tableProcessTemplateList;
    }

    public List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> selectMissingSubTList(Integer processId, Integer processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> processTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.ProcessTemplate>();

        try {
            session.beginTransaction();

            Criteria fetchProcessTemplateId = session.createCriteria(Process.class).add(Restrictions.eq(DELETE_FLAG, false)).add(Restrictions.eq("process.processId", processId)).setProjection(Projections.property(PARENTPROCESSTEMPLATEID));
            List<Integer> processTemplateIdList = fetchProcessTemplateId.list();
            LOGGER.info("processTemplateId List size:" + processTemplateIdList);


            Criteria fetchProcessTemplate = session.createCriteria(ProcessTemplate.class).add(Restrictions.not(Restrictions.in(PROCESSTEMPLATEID, processTemplateIdList))).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId));
            List<ProcessTemplate> jpaProcessTemplateList = fetchProcessTemplate.list();
            LOGGER.info("jpaprocessTemplate List size:" + jpaProcessTemplateList);
            for (ProcessTemplate jpaProcessTemplate : jpaProcessTemplateList) {
                com.wipro.ats.bdre.md.beans.table.ProcessTemplate processTemplate = new com.wipro.ats.bdre.md.beans.table.ProcessTemplate();
                processTemplate.setProcessTemplateId(jpaProcessTemplate.getProcessTemplateId());
                processTemplate.setDescription(jpaProcessTemplate.getDescription());
                processTemplate.setProcessName(jpaProcessTemplate.getProcessName());
                processTemplate.setBusDomainId(jpaProcessTemplate.getBusDomain().getBusDomainId());
                processTemplate.setProcessTypeId(jpaProcessTemplate.getProcessType().getProcessTypeId());
                processTemplate.setParentProcessId(jpaProcessTemplate.getProcessTemplate().getProcessTemplateId());
                processTemplate.setCanRecover(jpaProcessTemplate.getCanRecover());
                processTemplate.setBatchPattern(jpaProcessTemplate.getBatchCutPattern());
                processTemplate.setNextProcessIds(jpaProcessTemplate.getNextProcessTemplateId());
                processTemplate.setWorkflowId(jpaProcessTemplate.getWorkflowType().getWorkflowId());
                processTemplate.setCounter(jpaProcessTemplateList.size());
                processTemplateList.add(processTemplate);
                LOGGER.info("processTemplate is :" + processTemplate);
                session.getTransaction().commit();
            }
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processTemplateList;
    }


    public List<com.wipro.ats.bdre.md.beans.table.Properties> selectMissingPropListForP(Integer processId, Integer processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.Properties> propertiesList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Properties>();

        try {
            session.beginTransaction();

            //select prop_temp_key from properties_template where process_template_id=p_t_id
            Criteria fetchPropTempKey = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplate.class).add(Restrictions.eq("id.processTemplateId", processTemplateId)).setProjection(Projections.property("id.propTempKey"));
            List<String> propTempKeyList = fetchPropTempKey.list();
            LOGGER.info("prop temp key list size:" + fetchPropTempKey.list().size());

            //select config_group from properties_template where process_template_id=p_t_id
            Criteria fetchconfigGroup = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplate.class).add(Restrictions.eq("id.processTemplateId", processTemplateId)).setProjection(Projections.property(CONFIG_GROUP));
            List<String> configGroupList = fetchconfigGroup.list();
            LOGGER.info("config Group list size:" + configGroupList.size());


            if (!configGroupList.isEmpty()) {
       /* (config_group not in (select config_group from properties_template where process_template_id=p_t_id ) or prop_key not in
        (select prop_temp_key from properties_template where process_template_id=p_t_id ))*/
                Criteria fetchMissingCgPropertiesList = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq(CONFIG_GROUPID, processId)).add(Restrictions.not(Restrictions.in(CONFIG_GROUP, configGroupList)));
                List<com.wipro.ats.bdre.md.dao.jpa.Properties> jpaCgPropertiesList = fetchMissingCgPropertiesList.list();
                for (com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperty : jpaCgPropertiesList) {
                    Properties properties = new Properties();
                    properties.setProcessId(jpaProperty.getId().getProcessId());
                    properties.setKey(jpaProperty.getId().getPropKey());
                    properties.setConfigGroup(jpaProperty.getConfigGroup());
                    properties.setValue(jpaProperty.getPropValue());
                    properties.setDescription(jpaProperty.getDescription());
                    propertiesList.add(properties);
                }

                if (!propTempKeyList.isEmpty()) {
                    Criteria fetchMissingKeyPropertiesList = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq(CONFIG_GROUPID, processId)).add(Restrictions.not(Restrictions.in("id.propKey", propTempKeyList)));
                    List<com.wipro.ats.bdre.md.dao.jpa.Properties> jpaKeyPropertiesList = fetchMissingKeyPropertiesList.list();
                    for (com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperty : jpaKeyPropertiesList) {
                        Properties properties = new Properties();
                        properties.setProcessId(jpaProperty.getId().getProcessId());
                        properties.setKey(jpaProperty.getId().getPropKey());
                        properties.setConfigGroup(jpaProperty.getConfigGroup());
                        properties.setValue(jpaProperty.getPropValue());
                        properties.setDescription(jpaProperty.getDescription());
                        propertiesList.add(properties);
                    }
                }


            } else if (!propTempKeyList.isEmpty()) {
                Criteria fetchMissingKeyPropertiesList = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class).add(Restrictions.eq(CONFIG_GROUPID, processId)).add(Restrictions.not(Restrictions.in("id.propKey", propTempKeyList)));
                List<com.wipro.ats.bdre.md.dao.jpa.Properties> jpaKeyPropertiesList = fetchMissingKeyPropertiesList.list();
                for (com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperty : jpaKeyPropertiesList) {
                    Properties properties = new Properties();
                    properties.setProcessId(jpaProperty.getId().getProcessId());
                    properties.setKey(jpaProperty.getId().getPropKey());
                    properties.setConfigGroup(jpaProperty.getConfigGroup());
                    properties.setValue(jpaProperty.getPropValue());
                    properties.setDescription(jpaProperty.getDescription());
                    propertiesList.add(properties);
                }
            }
            session.getTransaction().commit();
            Integer counter = propertiesList.size();
            for (Properties properties : propertiesList) {
                properties.setCounter(counter);
            }
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesList;
    }

    public List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> selectMissingPropListForT(Integer processId, Integer parentProcessId, Integer processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> returnPropertiesTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate>();

        try {
            session.beginTransaction();

            //select process_template_id from process_template where parent_process_id=p_p_id
            Criteria fetchProcessTempId = session.createCriteria(ProcessTemplate.class).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, parentProcessId)).setProjection(Projections.property(PROCESSTEMPLATEID));
            List<Integer> processTempIdList = fetchProcessTempId.list();
            LOGGER.info("List size of process template id:" + fetchProcessTempId.list().size());


            if (!fetchProcessTempId.list().isEmpty()) {

                //select process_id from process where process_template_id in processTempIdList
                Criteria fetchProcessId = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.in(PARENTPROCESSTEMPLATEID, processTempIdList)).setProjection(Projections.property("processId"));
                List<Integer> processIdList = fetchProcessId.list();
                LOGGER.info("List size of process template id:" + fetchProcessId.list().size());
                if (!fetchProcessId.list().isEmpty()) {

                    Criteria joinPropertiesAndProcess = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Properties.class, "properties")
                            .createAlias("properties.process", "process")
                            .add(Restrictions.in("properties.process.processId", processIdList))//properties.process_id in processIdList
                            .add(Restrictions.eq("process.processId", processId));//process.process_id=p_id
                    joinPropertiesAndProcess.setProjection(Projections.property(CONFIG_GROUP));
                    List<String> configGroupList = joinPropertiesAndProcess.list();
                    LOGGER.info("configGroupList size:" + joinPropertiesAndProcess.list().size());

                    if (!joinPropertiesAndProcess.list().isEmpty()) {
                        //select * from properties_template where process_template_id =p_t_id
                        Criteria fetchPropertiesTemplate = session.createCriteria(PropertiesTemplate.class).add(Restrictions.eq(PARENTPROCESSTEMPLATEID, processTemplateId)).add(Restrictions.not(Restrictions.in(CONFIG_GROUP, configGroupList)));
                        List<PropertiesTemplate> propertiesTemplateList = fetchPropertiesTemplate.list();

                        //select * from process where process_id=p_id
                        Criteria fetchProcess = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.eq("processId", processId));
                        fetchProcess.uniqueResult();
                        com.wipro.ats.bdre.md.dao.jpa.Process process = (com.wipro.ats.bdre.md.dao.jpa.Process) fetchProcess.list();

                        if (!fetchProcess.list().isEmpty() && !fetchPropertiesTemplate.list().isEmpty()) {
                            for (PropertiesTemplate pt : propertiesTemplateList) {

                                if (pt.getId().getProcessTemplateId() == process.getProcessTemplate().getProcessTemplateId()) {
                                    com.wipro.ats.bdre.md.beans.table.PropertiesTemplate properties = new com.wipro.ats.bdre.md.beans.table.PropertiesTemplate();
                                    properties.setProcessTemplateId(pt.getId().getProcessTemplateId());
                                    properties.setKey(pt.getId().getPropTempKey());
                                    properties.setConfigGroup(pt.getConfigGroup());
                                    properties.setValue(pt.getPropTempValue());
                                    properties.setDescription(pt.getDescription());
                                    properties.setProcessId(process.getProcessId());
                                    properties.setCounter(propertiesTemplateList.size());
                                    returnPropertiesTemplateList.add(properties);
                                }
                            }
                        }

                    }
                }
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

        return returnPropertiesTemplateList;
    }


    // ListSubProcessTemplates
}
