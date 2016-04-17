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
import java.util.Map;

/**
 * Created by MR299389 on 10/16/2015.
 */
@Transactional
@Service
public class ProcessDAO {
    private static final Logger LOGGER = Logger.getLogger(ProcessDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    private static final String PROCESS="process";
    private static final String DELETE_FLAG="deleteFlag";
    private static final String PARENTPROCESSID="process.processId";
    private static final String PROCESSID="processId";
    private static final String PROCESSCODE="processCode";
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
            Criteria checkSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq(PROCESS, argument)).add(Restrictions.eq(DELETE_FLAG, false));

            if (pid == null) {
                Criteria criteria = session.createCriteria(Process.class).add(Restrictions.isNull(PARENTPROCESSID)).add(Restrictions.eq(DELETE_FLAG, false))
                        .addOrder(Order.desc(PROCESSID));
                criteria.setFirstResult(pageNum);
                criteria.setMaxResults(numResults);
                processes = criteria.list();
            } else if (checkSubProcessCriteria.list().isEmpty() && process.getProcessId() == pid) {
                Criteria listOfRelatedSP = session.createCriteria(Process.class).add(Restrictions.eq(PROCESSID, process.getProcess().getProcessId())).add(Restrictions.eq(DELETE_FLAG, false))
                        .addOrder(Order.desc(PROCESSID));
                listOfRelatedSP.setFirstResult(pageNum);
                listOfRelatedSP.setMaxResults(numResults);
                processes = listOfRelatedSP.list();
            } else {
                Criteria processList = session.createCriteria(Process.class).add(Restrictions.isNull(PARENTPROCESSID)).add(Restrictions.eq(PROCESSID, pid))
                        .add(Restrictions.eq(DELETE_FLAG, false));
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
            Criteria checkSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq(PROCESS, argument)).add(Restrictions.eq(DELETE_FLAG, false));

            if (pid == null) {
                Criteria criteria = session.createCriteria(Process.class).add(Restrictions.isNull(PARENTPROCESSID)).add(Restrictions.eq(DELETE_FLAG, false));
                size = criteria.list().size();
            } else if (checkSubProcessCriteria.list().isEmpty() && process.getProcessId() == pid) {
                Criteria listOfRelatedSP = session.createCriteria(Process.class).add(Restrictions.eq(PROCESSID, process.getProcess().getProcessId())).add(Restrictions.eq(DELETE_FLAG, false))
                        .addOrder(Order.desc(PROCESSID));
                size = listOfRelatedSP.list().size();
            } else {
                Criteria processList = session.createCriteria(Process.class).add(Restrictions.isNull(PARENTPROCESSID)).add(Restrictions.eq(PROCESSID, pid))
                        .add(Restrictions.eq(DELETE_FLAG, false));
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
            session.update(process);
            List<Process> subProcessList = subProcesslist(id);
            if (!subProcessList.isEmpty()){
                for(Process subProcess : subProcessList){
                    subProcess.setDeleteFlag(true);
                    session.update(subProcess);
                }
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void testDelete(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Process process = (Process) session.get(Process.class, id);
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
        Criteria listSubProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq(PROCESS, parentProcess)).add(Restrictions.eq(DELETE_FLAG, false));
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
            Criteria checkProcessSubProcessList = session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq(PROCESSID, processId), Restrictions.eq(PROCESS, parentProcess))).add(Restrictions.eq(DELETE_FLAG, false));
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

    public List<Process> selectProcessList(String processCode,String username) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<Process> processSubProcessList = new ArrayList<Process>();
        try {
             Process parentProcess = (Process) session.createCriteria(Process.class).add(Restrictions.eq(PROCESSCODE,processCode)).add(Restrictions.eq("userName",username)).uniqueResult();
            Criteria checkProcessSubProcessList = session.createCriteria(Process.class).add(Restrictions.eq(PROCESS, parentProcess));
            processSubProcessList = checkProcessSubProcessList.list();
            processSubProcessList.add(parentProcess);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processSubProcessList;
    }

    public Process returnProcess(String processCode,String username) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Process parentProcess=null;

        try {
           parentProcess = (Process) session.createCriteria(Process.class).add(Restrictions.eq(PROCESSCODE,processCode)).add(Restrictions.eq("userName",username)).uniqueResult();
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return parentProcess;

    }




    public List<Process> returnProcesses(String processCode) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Process parentProcess=null;
        List<Process> processes=new ArrayList<>();
        try {
            processes = session.createCriteria(Process.class).add(Restrictions.eq(PROCESSCODE,processCode)).list();
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processes;

    }




    public void updateProcessId(Integer oldProcessId, Integer newProcessId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Process parentProcess = (Process) session.get(Process.class, newProcessId);
            Process nullProcess = new Process();
            nullProcess.setProcessId(null);
            Criteria updateProcessCriteria = session.createCriteria(Process.class).add(Restrictions.eq(PROCESS, parentProcess));
            if (parentProcess.getProcess().getProcessId() == null) {
                List<Process> updateProcessList = updateProcessCriteria.list();

                for (Process updateProcess : updateProcessList) {
                    updateProcess.setProcess(nullProcess);
                    updateProcess.setEditTs(new Date());
                    session.update(updateProcess);
                }
            }
            Criteria deletePropCriteria = session.createCriteria(Properties.class).add(Restrictions.eq(PROCESS, parentProcess));
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
        Process updateProcess=new Process();

        try {
            session.beginTransaction();
            Criteria fetchReferenceProcess = session.createCriteria(Process.class).add(Restrictions.eq(PROCESSID, processId)).add(Restrictions.eq(DELETE_FLAG, false));

            Process referencedProcess = (Process) fetchReferenceProcess.uniqueResult();
            Integer newProcessId = null;
            if (!fetchReferenceProcess.list().isEmpty()) {
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

                Criteria fetchProcessList = session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq(PARENTPROCESSID, processId), Restrictions.eq(PROCESSID, processId))).add(Restrictions.eq(DELETE_FLAG, false));
                List<Process> processList = fetchProcessList.list();
                LOGGER.info("Process list size:" + fetchProcessList.list().size());

                fetchProcessList.setProjection(Projections.property(PROCESSID));
                List<Integer> processIdList = fetchProcessList.list();
                LOGGER.info("process id list size:" + fetchProcessList.list().size());
                if (!processIdList.isEmpty()) {
                    Criteria fetchInstanceExecList = session.createCriteria(InstanceExec.class).add(Restrictions.ge("instanceExecId", instanceExecId)).add(Restrictions.in(PARENTPROCESSID, processIdList)).add(Restrictions.between("startTs", vStartTs, vEndTs));
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
                if (!returnProcessList.isEmpty()) {
                    LOGGER.info("processInfo bean:" + returnProcessList.get(0).getCounter());
                }

            } else {
                Criteria fetchProcessList = session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq(PARENTPROCESSID, processId), Restrictions.eq(PROCESSID, processId))).add(Restrictions.eq(DELETE_FLAG, false));
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
        LOGGER.info("parent processId:"+parentPid);
        parentProcess.setProcessId(parentPid);
        childProcess.setProcess(parentProcess);
        childProcess.setNextProcessId(parentPid.toString());

        childPid= (Integer) session.save(childProcess);
        LOGGER.info("child processId:"+childPid);

        parentProcess.setNextProcessId(childPid.toString());
        childProcess.setProcessId(childPid);
        session.update(parentProcess);
        if(parentProps!=null && !parentProps.isEmpty()){
            for(Properties properties: parentProps){

                properties.getId().setProcessId(parentPid);
                properties.setProcess(parentProcess);
                session.save(properties);
            }
        }

        if(childProps!=null && !childProps.isEmpty()){
            for(Properties properties: childProps){
                properties.getId().setProcessId(childPid);
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

    public List<Process> createDataloadJob(Process parentProcess, List<Process> childProcesses, List<Properties> parentProps, Map<Process,List<Properties>> childProps ){
        Session session = sessionFactory.openSession();
        Integer parentPid = null;
        Process file2Raw = null;
        Process raw2Stage = null;
        Process stage2Base = null;
        List<Process> processList=new ArrayList<Process>();
        try {
            session.beginTransaction();
            parentPid = (Integer) session.save(parentProcess);
            LOGGER.info("parent processId:"+parentPid);
            parentProcess.setProcessId(parentPid);
            for (Process childProcess : childProcesses){
                childProcess.setProcess(parentProcess);
                if (childProcess.getProcessType().getProcessTypeId() == 8){
                    stage2Base = childProcess;
                    stage2Base.setNextProcessId(parentPid.toString());
                    stage2Base.setProcessId((Integer) session.save(stage2Base));
                }else  if (childProcess.getProcessType().getProcessTypeId() == 7){
                    raw2Stage = childProcess;
                    raw2Stage.setNextProcessId(parentPid.toString());
                    raw2Stage.setProcessId((Integer) session.save(raw2Stage));
                }else  if (childProcess.getProcessType().getProcessTypeId() == 6){
                    file2Raw = childProcess;
                    file2Raw.setNextProcessId(parentPid.toString());
                    file2Raw.setProcessId((Integer) session.save(file2Raw));
                }
            }

            parentProcess.setNextProcessId(file2Raw.getProcessId().toString());
            file2Raw.setNextProcessId(raw2Stage.getProcessId().toString());
            raw2Stage.setNextProcessId(stage2Base.getProcessId().toString());

            session.update(parentProcess);
            session.update(file2Raw);
            session.update(raw2Stage);

            if(parentProps!=null && !parentProps.isEmpty()){
                for(Properties properties: parentProps){

                    properties.getId().setProcessId(parentPid);
                    properties.setProcess(parentProcess);
                    session.save(properties);
                }
            }
            for(Process process : childProps.keySet()) {
                List<Properties> childProperties = childProps.get(process);
                if (childProperties != null && !childProperties.isEmpty()) {
                    for (Properties properties : childProperties) {
                        if(process.getProcessType().getProcessTypeId() == 6){
                            properties.getId().setProcessId(file2Raw.getProcessId());
                            properties.setProcess(file2Raw);
                            session.save(properties);
                        }else if(process.getProcessType().getProcessTypeId() == 7){
                            properties.getId().setProcessId(raw2Stage.getProcessId());
                            properties.setProcess(raw2Stage);
                            session.save(properties);
                        }else if(process.getProcessType().getProcessTypeId() == 8){
                            properties.getId().setProcessId(stage2Base.getProcessId());
                            properties.setProcess(stage2Base);
                            session.save(properties);
                        }
                    }
                }
            }
            processList.add(parentProcess);
            processList.add(file2Raw);
            processList.add(raw2Stage);
            processList.add(stage2Base);
            session.getTransaction().commit();

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processList;
    }

public String securityCheck(Integer processId,String username,String action){
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    Process process = (Process) session.get(Process.class, processId);
    Criteria criteria = session.createCriteria(UserRoles.class).add(Restrictions.eq("users.username", username));
    List<UserRoles> userRoles = criteria.list();
    List<String> userRolesNameList=new ArrayList<>();
    String processCreater=process.getUserRoles().getRole();
    for(UserRoles userRoles1:userRoles)
    {userRolesNameList.add(userRoles1.getRole());}

    session.getTransaction().commit();
    session.close();
    List<Integer> readList=new ArrayList<>();
    readList.add(4);
    readList.add(5);
    readList.add(6);
    readList.add(7);
    List<Integer> writeList=new ArrayList<>();
    writeList.add(2);
    writeList.add(3);
    writeList.add(6);
    writeList.add(7);
    List<Integer> executeList=new ArrayList<>();
    executeList.add(1);
    executeList.add(3);
    executeList.add(5);
    executeList.add(7);
    if (process.getUserName().equals(username))
    {
        switch (action){
            case "write": if (writeList.contains(process.getPermissionTypeByUserAccessId().getPermissionTypeId())
                    || (userRolesNameList.contains(processCreater)&&writeList.contains(process.getPermissionTypeByGroupAccessId().getPermissionTypeId()))
                    || (!userRolesNameList.contains(processCreater)&&writeList.contains(process.getPermissionTypeByOthersAccessId().getPermissionTypeId()))
                    )
            {return "ACCESS GRANTED";}
            else
            {LOGGER.info("user write");throw new SecurityException("ACCESS DENIED");}
            case "read": if (readList.contains(process.getPermissionTypeByUserAccessId().getPermissionTypeId()) ||
                    (userRolesNameList.contains(processCreater)&&readList.contains(process.getPermissionTypeByGroupAccessId().getPermissionTypeId()))
                    || (!userRolesNameList.contains(processCreater)&&readList.contains(process.getPermissionTypeByOthersAccessId().getPermissionTypeId()))
                    )
            {return "ACCESS GRANTED";}
            else
            {LOGGER.info("user read");throw new SecurityException("ACCESS DENIED");}
            case "execute": if (executeList.contains(process.getPermissionTypeByUserAccessId().getPermissionTypeId())||
                    (userRolesNameList.contains(processCreater)&&executeList.contains(process.getPermissionTypeByGroupAccessId().getPermissionTypeId()))
                    || (!userRolesNameList.contains(processCreater)&&executeList.contains(process.getPermissionTypeByOthersAccessId().getPermissionTypeId()))
                    )
            {return "ACCESS GRANTED";}
            else
            {LOGGER.info("user execute");throw new SecurityException("ACCESS DENIED");}
        }
        {LOGGER.info("no user");throw new SecurityException("ACCESS DENIED");}    }
    else{
    if (userRolesNameList.contains(processCreater))
    {
        switch (action){
            case "write": if (writeList.contains(process.getPermissionTypeByGroupAccessId().getPermissionTypeId()))
                               {return "ACCESS GRANTED";}
                           else
                          {LOGGER.info("group write");throw new SecurityException("ACCESS DENIED");}
            case "read": if (readList.contains(process.getPermissionTypeByGroupAccessId().getPermissionTypeId()))
                               {return "ACCESS GRANTED";}
                           else
                          {LOGGER.info("group read");throw new SecurityException("ACCESS DENIED");}
            case "execute": if (executeList.contains(process.getPermissionTypeByGroupAccessId().getPermissionTypeId()))
                            {return "ACCESS GRANTED";}
                            else
                            {LOGGER.info("group execute");throw new SecurityException("ACCESS DENIED");}
        }
        {LOGGER.info("group");throw new SecurityException("ACCESS DENIED");}
    }
    else {
        switch (action){
            case "write": if (writeList.contains(process.getPermissionTypeByOthersAccessId().getPermissionTypeId()))
            {return "ACCESS GRANTED";}
            else
            {LOGGER.info("other write");throw new SecurityException("ACCESS DENIED");}
            case "read": if (readList.contains(process.getPermissionTypeByOthersAccessId().getPermissionTypeId()))
            {return "ACCESS GRANTED";}
            else
            {LOGGER.info("other read");throw new SecurityException("ACCESS DENIED");}
            case "execute": if (executeList.contains(process.getPermissionTypeByOthersAccessId().getPermissionTypeId()))
            {return "ACCESS GRANTED";}
            else
            {LOGGER.info("other execute");throw new SecurityException("ACCESS DENIED");}
        }
        {LOGGER.info("other");throw new SecurityException("ACCESS DENIED");}
    }

}
}
}
