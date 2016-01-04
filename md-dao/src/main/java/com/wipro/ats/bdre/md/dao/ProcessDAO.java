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
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.jpa.InstanceExec;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
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
 * Created by MR299389 on 10/16/2015.
 */
@Transactional
@Service
public class ProcessDAO {
    private static final Logger LOGGER = Logger.getLogger(ProcessDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<com.wipro.ats.bdre.md.dao.jpa.Process> list(Integer pid, Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        List<Process> processes = new ArrayList<Process>();
        try {
            session.beginTransaction();


            Process argument = new Process();
            Process process = new Process();
            if (pid != null) {
                argument.setProcessId(pid);
                process = (Process) session.get(Process.class, pid);
            }
            Criteria checkSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", argument)).add(Restrictions.eq("deleteFlag", false));

            if (pid == null) {
                Criteria criteria = session.createCriteria(Process.class).add(Restrictions.isNull("process.processId")).add(Restrictions.eq("deleteFlag", false))
                        .addOrder(Order.desc("processId"));
                criteria.setFirstResult(pageNum);
                criteria.setMaxResults(numResults);
                processes = criteria.list();
            } else if (checkSubProcessCriteria.list().size() == 0 && process.getProcessId() == pid) {
                Criteria listOfRelatedSP = session.createCriteria(Process.class).add(Restrictions.eq("processId", process.getProcess().getProcessId())).add(Restrictions.eq("deleteFlag", false))
                        .addOrder(Order.desc("processId"));
                listOfRelatedSP.setFirstResult(pageNum);
                listOfRelatedSP.setMaxResults(numResults);
                processes = listOfRelatedSP.list();
            } else {
                Criteria processList = session.createCriteria(Process.class).add(Restrictions.isNull("process.processId")).add(Restrictions.eq("processId", pid))
                        .add(Restrictions.eq("deleteFlag", false));
                processList.setFirstResult(pageNum);
                processList.setMaxResults(numResults);
                processes = processList.list();
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processes;
    }

    public Integer totalRecordCount(Integer pid) {
        Session session = sessionFactory.openSession();
        Integer size = 0;
        try {
            session.beginTransaction();

            Process argument = new Process();
            Process process = new Process();
            if (pid != null) {
                argument.setProcessId(pid);
                process = (Process) session.get(Process.class, pid);
            }
            Criteria checkSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", argument)).add(Restrictions.eq("deleteFlag", false));

            if (pid == null) {
                Criteria criteria = session.createCriteria(Process.class).add(Restrictions.isNull("process.processId")).add(Restrictions.eq("deleteFlag", false));
                size = criteria.list().size();
            } else if (checkSubProcessCriteria.list().size() == 0 && process.getProcessId() == pid) {
                Criteria listOfRelatedSP = session.createCriteria(Process.class).add(Restrictions.eq("processId", process.getProcess().getProcessId())).add(Restrictions.eq("deleteFlag", false))
                        .addOrder(Order.desc("processId"));
                size = listOfRelatedSP.list().size();
            } else {
                Criteria processList = session.createCriteria(Process.class).add(Restrictions.isNull("process.processId")).add(Restrictions.eq("processId", pid))
                        .add(Restrictions.eq("deleteFlag", false));
                LOGGER.info("size of pro is " + processList.list().size());
                size = processList.list().size();
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return size;
    }

    public Process get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Process process = (Process) session.get(Process.class, id);
        session.getTransaction().commit();
        session.close();
        return process;
    }

    public Integer insert(Process process) {
        Session session = sessionFactory.openSession();
        Integer id = null;
        try {
            session.beginTransaction();
            id = (Integer) session.save(process);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public Process update(Process process) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            process.setEditTs(new Date());
            session.update(process);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return process;
    }

    public void delete(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Process process = (Process) session.get(Process.class, id);
            process.setDeleteFlag(true);
            session.delete(process);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<com.wipro.ats.bdre.md.dao.jpa.Process> subProcesslist(Integer processId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Process parentProcess = (Process) session.get(Process.class, processId);
        Criteria listSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess)).add(Restrictions.eq("deleteFlag", false));
        List<Process> subProcesses = listSubProcessCriteria.list();
        LOGGER.info("Total number of sub processes:" + listSubProcessCriteria.list().size());
        session.getTransaction().commit();
        session.close();
        return subProcesses;
    }

    //fetching parent process along with its sub processes
    public List<Process> selectProcessList(Integer processId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<Process> processSubProcessList = new ArrayList<Process>();
        try {
            Process parentProcess = (Process) session.get(Process.class, processId);
            Criteria checkProcessSubProcessList = session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq("processId", processId), Restrictions.eq("process", parentProcess)));
            processSubProcessList = checkProcessSubProcessList.list();
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processSubProcessList;
    }

    public void updateProcessId(Integer oldProcessId, Integer newProcessId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Process parentProcess = (Process) session.get(Process.class, newProcessId);
            Process nullProcess = new Process();
            nullProcess.setProcessId(null);
            Criteria updateProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq("process", parentProcess));
            if (parentProcess.getProcess().getProcessId() == null) {
                List<Process> updateProcessList = updateProcessCriteria.list();

                for (Process updateProcess : updateProcessList) {
                    updateProcess.setProcess(nullProcess);
                    updateProcess.setEditTs(new Date());
                    session.update(updateProcess);
                }
            }
            Criteria deletePropCriteria = session.createCriteria(Properties.class).add(Restrictions.eq("process", parentProcess));
            List<Properties> deletePropertiesList = deletePropCriteria.list();
            for (Properties deleteProperty : deletePropertiesList) {
                session.delete(deleteProperty);
            }
            if (parentProcess.getDeleteFlag()) {
                session.delete(parentProcess);
            }
            Process oldProcess = (Process) session.get(Process.class, oldProcessId);
            oldProcess.setProcessId(newProcessId);
            oldProcess.setEditTs(new Date());
            session.update(oldProcess);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

    }

    public Process cloneProcess(Integer processId) {
        Session session = sessionFactory.openSession();
        Process newProcess = new Process();
       /* insert into process (description,process_name, bus_domain_id, process_type_id, parent_process_id, can_recover, enqueuing_process_id, batch_cut_pattern, next_process_id, workflow_id,process_template_id)
        select  description,concat(process_name, ' - copy'), bus_domain_id, process_type_id, parent_process_id, can_recover,0, batch_cut_pattern, '0', workflow_id,process_template_id from process where (process_id = p_id and delete_flag != 1)  ;
*/
        Process updateProcess=new Process();

        try {
            session.beginTransaction();
            Criteria fetchReferenceProcess = session.createCriteria(Process.class).add(Restrictions.eq("processId", processId)).add(Restrictions.eq("deleteFlag", false));

            Process referencedProcess = (Process) fetchReferenceProcess.uniqueResult();
            Integer newProcessId = null;
            if (fetchReferenceProcess.list().size() != 0) {
                newProcess.setProcessName(referencedProcess.getProcessName() + "-copy");
                newProcess.setEnqueuingProcessId(0);
                newProcess.setNextProcessId("0");

                newProcess.setProcessType(referencedProcess.getProcessType());
                newProcess.setWorkflowType(referencedProcess.getWorkflowType());
                newProcess.setBusDomain(referencedProcess.getBusDomain());
                newProcess.setProcessTemplate(referencedProcess.getProcessTemplate());
                newProcess.setProcess(referencedProcess.getProcess());
                newProcess.setDescription(referencedProcess.getDescription());
                newProcess.setAddTs(referencedProcess.getAddTs());
                newProcess.setCanRecover(referencedProcess.getCanRecover());
                newProcess.setBatchCutPattern(referencedProcess.getBatchCutPattern());
                newProcess.setDeleteFlag(referencedProcess.getDeleteFlag());

                newProcessId = (Integer) session.save(newProcess);


                //insert into properties (process_id,config_group,prop_key,prop_value,description) select (select last_insert_id() from process limit 1),config_group,prop_key,prop_value,description  from properties where process_id=p_id ;

                Criteria copyPropertiesCriteraia = session.createCriteria(Properties.class).add(Restrictions.eq("id.processId", processId));
                List<Properties> insertProperties = copyPropertiesCriteraia.list();
                for (Properties insertProperty : insertProperties) {
                    Properties property = new Properties();

                    PropertiesId propertiesId = new PropertiesId();
                    propertiesId.setProcessId(newProcessId);
                    propertiesId.setPropKey(insertProperty.getId().getPropKey());

                    property.setId(propertiesId);
                    property.setProcess(newProcess);
                    property.setConfigGroup(insertProperty.getConfigGroup());
                    property.setPropValue(insertProperty.getPropValue());
                    property.setDescription(insertProperty.getDescription());

                    session.save(property);

                }
                updateProcess=(Process)session.get(Process.class,referencedProcess.getProcess().getProcessId());
                updateProcess.setEditTs(new Date());
                session.update(updateProcess);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return newProcess;
    }


//SelectProcessListWithExec

    public List<ProcessInfo> selectProcessListWithExec(Integer processId, Long instanceExecId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<ProcessInfo> returnProcessList = new ArrayList<ProcessInfo>();
        Date vStartTs, vEndTs;
        try {
            Criteria checkProcessIdWithIEId = session.createCriteria(InstanceExec.class).add(Restrictions.eq("instanceExecId", instanceExecId));
            InstanceExec instanceExec = (InstanceExec) checkProcessIdWithIEId.uniqueResult();
            Integer processIdWithIEId = null;
            if (instanceExec!=null) {
                processIdWithIEId = instanceExec.getProcess().getProcessId();
                LOGGER.info("processIdWithIEId:" + processIdWithIEId);
            }
            if (processId.equals(processIdWithIEId)) {

                vStartTs = instanceExec.getStartTs();
                vEndTs = instanceExec.getEndTs();

                if (vEndTs == null) {
                    vEndTs = new Date();
                }

                Criteria fetchProcessList = session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq("process.processId", processId), Restrictions.eq("processId", processId))).add(Restrictions.eq("deleteFlag", false));
                List<Process> processList = fetchProcessList.list();
                LOGGER.info("Process list size:" + fetchProcessList.list().size());

                fetchProcessList.setProjection(Projections.property("processId"));
                List<Integer> processIdList = fetchProcessList.list();
                LOGGER.info("process id list size:" + fetchProcessList.list().size());
                if (processIdList.size() != 0) {
                    Criteria fetchInstanceExecList = session.createCriteria(InstanceExec.class).add(Restrictions.ge("instanceExecId", instanceExecId)).add(Restrictions.in("process.processId", processIdList)).add(Restrictions.between("startTs", vStartTs, vEndTs));
                    List<InstanceExec> instanceExecList = fetchInstanceExecList.list();
                    LOGGER.info("instance exec list size:" + fetchInstanceExecList.list().size());

                    for (InstanceExec ieId : instanceExecList) {
                        LOGGER.info(ieId.getInstanceExecId() + " , " + ieId.getProcess().getProcessId());
                    }
// Process outer left join with InstanceExec on processId
                    for (Process process : processList) {

                        ProcessInfo processInfo = new ProcessInfo();

                        processInfo.setProcessId(process.getProcessId());
                        processInfo.setBusDomainId(process.getBusDomain().getBusDomainId());
                        processInfo.setProcessTypeId(process.getProcessType().getProcessTypeId());
                        processInfo.setCanRecover(process.getCanRecover());
                        processInfo.setDescription(process.getDescription());
                        if (process.getProcess() != null) {
                            processInfo.setParentProcessId(process.getProcess().getProcessId());
                        }
                        processInfo.setProcessName(process.getProcessName());
                        processInfo.setEnqProcessId(process.getEnqueuingProcessId());
                        processInfo.setNextProcessIds(process.getNextProcessId());
                        processInfo.setWorkflowId(process.getWorkflowType().getWorkflowId());
                        processInfo.setBatchCutPattern(process.getBatchCutPattern());
                        processInfo.setDeleteFlag(process.getDeleteFlag());

                        for (InstanceExec instanceExec1 : instanceExecList) {
                            if (process.getProcessId().equals(instanceExec1.getProcess().getProcessId())){

                                processInfo.setInstanceExecId(instanceExec1.getInstanceExecId());
                                processInfo.setStartTs(instanceExec1.getStartTs());
                                processInfo.setEndTs(instanceExec1.getEndTs());
                                if (instanceExec1.getExecStatus() != null)
                                    processInfo.setExecState(instanceExec1.getExecStatus().getExecStateId());
                            }
                        }
                        returnProcessList.add(processInfo);
                    }
                }

                for (ProcessInfo processInfo : returnProcessList) {
                    processInfo.setCounter(returnProcessList.size());
                }
                if (returnProcessList.size() != 0) {
                    LOGGER.info("processInfo bean:" + returnProcessList.get(0).getCounter());
                }

            } else {
                Criteria fetchProcessList = session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq("process.processId", processId), Restrictions.eq("processId", processId))).add(Restrictions.eq("deleteFlag", false));
                List<Process> processList = fetchProcessList.list();
                Integer sizeOfProcessList = fetchProcessList.list().size();
                LOGGER.info("Process list size:" + sizeOfProcessList);
                for (Process process : processList) {
                    ProcessInfo processInfo = new ProcessInfo();
                    processInfo.setProcessId(process.getProcessId());
                    processInfo.setBusDomainId(process.getBusDomain().getBusDomainId());
                    processInfo.setProcessTypeId(process.getProcessType().getProcessTypeId());
                    processInfo.setCanRecover(process.getCanRecover());
                    processInfo.setDescription(process.getDescription());
                    if (process.getProcess() != null) {
                        processInfo.setParentProcessId(process.getProcess().getProcessId());
                    }
                    processInfo.setProcessName(process.getProcessName());
                    processInfo.setEnqProcessId(process.getEnqueuingProcessId());
                    processInfo.setNextProcessIds(process.getNextProcessId());
                    processInfo.setWorkflowId(process.getWorkflowType().getWorkflowId());
                    processInfo.setBatchCutPattern(process.getBatchCutPattern());
                    processInfo.setDeleteFlag(process.getDeleteFlag());
                    processInfo.setCounter(sizeOfProcessList);
                    returnProcessList.add(processInfo);

                }
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            throw new MetadataException(e);
        } finally {
            session.close();
        }
        return returnProcessList;
    }

public List<Process> createOneChildJob(Process parentProcess, Process childProcess, List<Properties> parentProps, List<Properties> childProps ){
    Session session = sessionFactory.openSession();
    Integer parentPid = null;
    Integer childPid = null;
    List<Process> processList=new ArrayList<>();
    try {
        session.beginTransaction();
        parentPid = (Integer) session.save(parentProcess);
        parentProcess.setProcessId(parentPid);
        childProcess.setProcess(parentProcess);
        childPid= (Integer) session.save(childProcess);
        parentProcess.setNextProcessId(childPid.toString());
        childProcess.setNextProcessId(parentPid.toString());
        childProcess.setProcessId(childPid);
        session.update(parentProcess);
        session.update(childProcess);
        if(parentProps!=null && !parentProps.isEmpty()){
            for(Properties properties: parentProps){
                properties.setProcess(parentProcess);
                session.save(properties);
            }
        }

        if(childProps!=null && !childProps.isEmpty()){
            for(Properties properties: childProps){
                properties.setProcess(childProcess);
                session.save(properties);
            }
        }
        processList.add(parentProcess);
        processList.add(childProcess);
        session.getTransaction().commit();

    } catch (MetadataException e) {
        session.getTransaction().rollback();
        LOGGER.error(e);
    } finally {
        session.close();
    }
    return processList;
}


}
