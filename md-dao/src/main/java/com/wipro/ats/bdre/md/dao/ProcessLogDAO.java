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
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessLog;
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
 * Created by PR324290 on 10/28/2015.
 */
@Transactional
@Service
public class ProcessLogDAO {
    private static final Logger LOGGER = Logger.getLogger(ProcessLogDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    private static final String PROCESS="process";
    private static final String PARENTPROCESSID="process.processId";

    public List<ProcessLog> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProcessLog.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<ProcessLog> processLogs = criteria.list();
        session.getTransaction().commit();
        session.close();
        return processLogs;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProcessLog.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public ProcessLog get(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ProcessLog processLog = (ProcessLog) session.get(ProcessLog.class, id);
        session.getTransaction().commit();
        session.close();
        return processLog;
    }

    public Long insert(ProcessLog processLog) {
        Session session = sessionFactory.openSession();
        Long id = null;
        try {
            session.beginTransaction();
            id = (Long) session.save(processLog);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(ProcessLog processLog) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(processLog);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            ProcessLog processLog = (ProcessLog) session.get(ProcessLog.class, id);
            session.delete(processLog);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void log(ProcessLogInfo processLogInfo) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            ProcessLog processLog = new ProcessLog();
            //setting the values of processloginfo to processlog and adding to database
            processLog.setProcess((Process) session.get(Process.class, processLogInfo.getProcessId()));
            LOGGER.info("processid is" + processLogInfo.getProcessId());
            processLog.setAddTs(processLogInfo.getAddTs());
            processLog.setLogCategory(processLogInfo.getLogCategory());
            processLog.setMessage(processLogInfo.getMessage());
            processLog.setMessageId(processLogInfo.getMessageId());
            processLog.setInstanceRef(processLogInfo.getInstanceRef());
            session.save(processLog);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void logList(List<ProcessLogInfo> processLogInfoList) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            for(ProcessLogInfo processLogInfo:processLogInfoList){
            ProcessLog processLog = new ProcessLog();
            //setting the values of processloginfo to processlog and adding to database
            processLog.setProcess((Process) session.get(Process.class, processLogInfo.getProcessId()));
            processLog.setAddTs(processLogInfo.getAddTs());
            processLog.setLogCategory(processLogInfo.getLogCategory());
            processLog.setMessage(processLogInfo.getMessage());
            processLog.setMessageId(processLogInfo.getMessageId());
            processLog.setInstanceRef(processLogInfo.getInstanceRef());
            session.save(processLog);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public ProcessLogInfo getLastValue(ProcessLogInfo processLogInfo) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            com.wipro.ats.bdre.md.dao.jpa.Process process = (Process) session.get(Process.class, processLogInfo.getProcessId());
            Criteria processLogCriteria = session.createCriteria(ProcessLog.class).add(Restrictions.eq(PROCESS, process)).add(Restrictions.eq("messageId", processLogInfo.getMessageId())).add(Restrictions.eq("logCategory", processLogInfo.getLogCategory())).addOrder(Order.desc("logId"));
            processLogCriteria.setMaxResults(1);
            ProcessLogInfo returnProcessLogInfo = new ProcessLogInfo();

            if (!processLogCriteria.list().isEmpty()) {
                ProcessLog processLog = (ProcessLog) processLogCriteria.list().get(0);

                //mapping values to returnProcessLogInfo bean
                returnProcessLogInfo.setInstanceRef(processLog.getInstanceRef());
                returnProcessLogInfo.setAddTs(processLog.getAddTs());
                returnProcessLogInfo.setLogCategory(processLog.getLogCategory());
                returnProcessLogInfo.setLogId(processLog.getLogId());
                returnProcessLogInfo.setMessage(processLog.getMessage());
                returnProcessLogInfo.setMessageId(processLog.getMessageId());
                returnProcessLogInfo.setLogCategory(processLog.getLogCategory());
                returnProcessLogInfo.setProcessId(processLog.getProcess().getProcessId());
                returnProcessLogInfo.setCounter(processLogCriteria.list().size());

            }
            session.getTransaction().commit();
            return returnProcessLogInfo;
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOGGER.info("Error " + e);
            return null;
        } finally {
            session.close();
        }
    }


    public List<ProcessLogInfo> listLog(ProcessLogInfo processLogInfo) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<ProcessLogInfo> processLogInfoList = new ArrayList<ProcessLogInfo>();

        try {
            Criteria listProcessLogCriteria = session.createCriteria(ProcessLog.class).setProjection(Projections.distinct(Projections.property(PARENTPROCESSID)));
            int counter = listProcessLogCriteria.list().size();
            LOGGER.info("number of distinct processid is " + counter);
            List<Integer> processList = new ArrayList<Integer>();
            Criteria processLogListCriteria = session.createCriteria(ProcessLog.class).setProjection(Projections.distinct(Projections.property(PARENTPROCESSID)));
            processList = processLogListCriteria.list();
            LOGGER.info("size of processlog " + processList.size() +" and list contains:"+ processList);

            if(processLogInfo.getProcessId()==null){
                if (!processList.isEmpty()) {
                    Criteria distintPProcessId = session.createCriteria(Process.class).add(Restrictions.in("processId",processList)).setProjection(Projections.distinct(Projections.property(PARENTPROCESSID)));
                    distintPProcessId.addOrder(Order.desc(PARENTPROCESSID)).setFirstResult(processLogInfo.getPage()).setMaxResults(processLogInfo.getPageSize());

                    List<Integer>distinctPPidList=distintPProcessId.list();
                    for(Integer pPid:distinctPPidList){
                        ProcessLogInfo processLogInfo1 = new ProcessLogInfo();
                        processLogInfo1.setParentProcessId(pPid);
                        processLogInfo1.setCounter(distinctPPidList.size());
                        processLogInfoList.add(processLogInfo1);
                    }

                }
                }else {
                if (!processList.isEmpty()) {
                    for (Integer processId : processList) {
                        Criteria pProcessId = session.createCriteria(Process.class).setProjection(Projections.property(PARENTPROCESSID)).add(Restrictions.eq("processId", processId));
                        Integer parentProcessId = (Integer) pProcessId.uniqueResult();


                        ProcessLogInfo processLogInfo1 = new ProcessLogInfo();
                        processLogInfo1.setParentProcessId(parentProcessId);
                        processLogInfo1.setProcessId(processId);
                        processLogInfo1.setCounter(counter);
                        processLogInfoList.add(processLogInfo1);
                        LOGGER.info(" ppID is " + parentProcessId);
                        LOGGER.info("process Id is :" + processId);

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
        return processLogInfoList;
    }



    public List<ProcessLogInfo> getProcessLog(ProcessLogInfo processLogInfo) {
        Session session = sessionFactory.openSession();
        List<ProcessLogInfo> processLogInfoList = new ArrayList<ProcessLogInfo>();

        try {
            session.beginTransaction();
            Process process = (Process) session.get(Process.class, processLogInfo.getProcessId());
            Integer counter = session.createCriteria(ProcessLog.class).add(Restrictions.eq(PROCESS, process)).list().size();


            List<ProcessLog> processLogList = null;
            Criteria processLogListCriteria = session.createCriteria(ProcessLog.class).add(Restrictions.eq(PROCESS, process));

            processLogList = processLogListCriteria.list();


            Iterator iterator = processLogList.iterator();
            while (iterator.hasNext()) {
                ProcessLog processLog = (ProcessLog) iterator.next();
                ProcessLogInfo processLogInfo1 = new ProcessLogInfo();
                processLogInfo1.setProcessId(processLog.getProcess().getProcessId());
                processLogInfo1.setLogId(processLog.getLogId());
                processLogInfo1.setAddTs(processLog.getAddTs());
                processLogInfo1.setLogCategory(processLog.getLogCategory());
                processLogInfo1.setMessage(processLog.getMessage());
                processLogInfo1.setMessageId(processLog.getMessageId());
                processLogInfo1.setInstanceRef(processLog.getInstanceRef());
                processLogInfo1.setCounter(counter);
                processLogInfoList.add(processLogInfo1);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return processLogInfoList;
    }

    public List<ProcessLogInfo> listLastInstanceRef(int processId, String messageId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteriaList = session.createCriteria(ProcessLog.class).add(Restrictions.eq("process.processId",processId)).add(Restrictions.eq("messageId",messageId));
        Criteria criteriaMaxInstanceRef = session.createCriteria(ProcessLog.class).add(Restrictions.eq("process.processId",processId)).add(Restrictions.eq("messageId",messageId)).addOrder(Order.desc("instanceRef")).setMaxResults(1);
        ProcessLog maxInstanceRef=!(criteriaMaxInstanceRef.list().isEmpty())?(ProcessLog)criteriaMaxInstanceRef.list().get(0):null;
        List<ProcessLog> processLogs = maxInstanceRef!=null?criteriaList.add(Restrictions.eq("instanceRef",maxInstanceRef.getInstanceRef())).list():new ArrayList<>();
        List<ProcessLogInfo> processLogInfos = new ArrayList<>();
        if (!processLogs.isEmpty()) {
            //mapping values to returnProcessLogInfo bean
            for(ProcessLog processLog:processLogs){
                ProcessLogInfo processLogInfo = new ProcessLogInfo();
                processLogInfo.setInstanceRef(processLog.getInstanceRef());
                processLogInfo.setAddTs(processLog.getAddTs());
                processLogInfo.setLogCategory(processLog.getLogCategory());
                processLogInfo.setLogId(processLog.getLogId());
                processLogInfo.setMessage(processLog.getMessage());
                processLogInfo.setMessageId(processLog.getMessageId());
                processLogInfo.setLogCategory(processLog.getLogCategory());
                processLogInfo.setProcessId(processLog.getProcess().getProcessId());
                processLogInfos.add(processLogInfo);
            }
        }
        session.getTransaction().commit();
        session.close();
        return processLogInfos;
    }

}
