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

    public ProcessLogInfo getLastValue(ProcessLogInfo processLogInfo) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            com.wipro.ats.bdre.md.dao.jpa.Process process = (Process) session.get(Process.class, processLogInfo.getProcessId());
            Criteria processLogCriteria = session.createCriteria(ProcessLog.class).add(Restrictions.eq("process", process)).add(Restrictions.eq("messageId", processLogInfo.getMessageId())).add(Restrictions.eq("logCategory", processLogInfo.getLogCategory())).addOrder(Order.desc("logId"));
            processLogCriteria.setMaxResults(1);
            ProcessLogInfo returnProcessLogInfo = new ProcessLogInfo();

            if (processLogCriteria.list().size() != 0) {
                ProcessLog processLog = (ProcessLog) processLogCriteria.list().get(0);

                //mapping values to returnProcessLogInfo bean
                returnProcessLogInfo.setInstanceRef(processLog.getInstanceRef());
                returnProcessLogInfo.setAddTs(processLog.getAddTs());
                returnProcessLogInfo.setLogCategory(processLog.getLogCategory());
                returnProcessLogInfo.setLogId(processLog.getLogId().intValue());
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
            Criteria listProcessLogCriteria = session.createCriteria(ProcessLog.class).setProjection(Projections.distinct(Projections.property("process")));
            int counter = listProcessLogCriteria.list().size();
            LOGGER.info("number of distinct processid is " + counter);
            List<Process> processList = new ArrayList<Process>();
            Criteria processLogListCriteria = session.createCriteria(ProcessLog.class).setProjection(Projections.distinct(Projections.property("process")));
            processLogListCriteria.addOrder(Order.desc("process")).setFirstResult(processLogInfo.getPage()).setMaxResults(processLogInfo.getPageSize());
            processList = processLogListCriteria.list();
            LOGGER.info("size of processlog " + processList.size() + processList);
            Iterator<Process> iterator = processList.iterator();
            while (iterator.hasNext()) {
                Process process = iterator.next();
                ProcessLogInfo processLogInfo1 = new ProcessLogInfo();
                processLogInfo1.setProcessId(process.getProcessId());
                LOGGER.info("processid is " + process.getProcessId());
                //Process process= (Process) session.get(Process.class, processLog.getProcess().getProcessId());
                LOGGER.info("parentprocessid is " + process.getProcess());
                if (process != null)
                    processLogInfo1.setParentProcessId(process.getProcessId());
                else
                    processLogInfo1.setParentProcessId(null);
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


    public List<ProcessLogInfo> getProcessLog(ProcessLogInfo processLogInfo) {
        Session session = sessionFactory.openSession();
        List<ProcessLogInfo> processLogInfoList = new ArrayList<ProcessLogInfo>();

        try {
            session.beginTransaction();
            Process process = (Process) session.get(Process.class, processLogInfo.getProcessId());
            Integer counter = session.createCriteria(ProcessLog.class).add(Restrictions.eq("process", process)).list().size();


            List<ProcessLog> processLogList = null;
            Criteria processLogListCriteria = session.createCriteria(ProcessLog.class).add(Restrictions.eq("process", process));

            processLogList = processLogListCriteria.list();


            Iterator iterator = processLogList.iterator();
            while (iterator.hasNext()) {
                ProcessLog processLog = (ProcessLog) iterator.next();
                ProcessLogInfo processLogInfo1 = new ProcessLogInfo();
                processLogInfo1.setProcessId(processLog.getProcess().getProcessId());
                processLogInfo1.setLogId(counter.intValue());
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


}
