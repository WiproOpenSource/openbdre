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
import com.wipro.ats.bdre.md.beans.SLAMonitoringBean;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.InstanceExec;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
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
public class InstanceExecDAO {
    private static final Logger LOGGER = Logger.getLogger(InstanceExecDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<com.wipro.ats.bdre.md.beans.table.InstanceExec> list(Integer processId, Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();


        List<InstanceExec> instanceExeces = new ArrayList<InstanceExec>();
        List<Batch> batchList = new ArrayList<Batch>();
        List<com.wipro.ats.bdre.md.beans.table.InstanceExec> instanceExecList = new ArrayList<com.wipro.ats.bdre.md.beans.table.InstanceExec>();
        Integer counter;
        try {
            if (processId == null) {

                counter = session.createCriteria(InstanceExec.class).list().size();
                Criteria joinBatchInstanceExec = session.createCriteria(InstanceExec.class).addOrder(Order.desc("instanceExecId"));
                joinBatchInstanceExec.setFirstResult(pageNum);
                joinBatchInstanceExec.setMaxResults(numResults);
                instanceExeces = joinBatchInstanceExec.list();

            } else {
                counter = session.createCriteria(InstanceExec.class).add(Restrictions.eq("process.processId", processId)).list().size();
                Criteria joinBatchInstanceExec = session.createCriteria(Batch.class, "b").createAlias("b.instanceExec", "ieid", JoinType.RIGHT_OUTER_JOIN).add(Restrictions.eq("ieid.process.processId", processId)).addOrder(Order.desc("ieid.instanceExecId"));
                joinBatchInstanceExec.setFirstResult(pageNum);
                joinBatchInstanceExec.setMaxResults(numResults);
                batchList = joinBatchInstanceExec.list();
            }
            LOGGER.info("size of instanceExec:" + instanceExeces.size());
            LOGGER.info("size of list:" + batchList.size());
            LOGGER.info("counter:" + counter);
            for (Batch batch : batchList) {
                if (batch != null && batch.getInstanceExec() != null) {
                    LOGGER.info("instanceExec:" + batch.getInstanceExec().getInstanceExecId());

                    instanceExeces.add(batch.getInstanceExec());
                }
            }

            for (com.wipro.ats.bdre.md.dao.jpa.InstanceExec jpaInstanceExec : instanceExeces) {
                com.wipro.ats.bdre.md.beans.table.InstanceExec instanceExec = new com.wipro.ats.bdre.md.beans.table.InstanceExec();
                instanceExec.setInstanceExecId(jpaInstanceExec.getInstanceExecId().intValue());
                instanceExec.setProcessId(jpaInstanceExec.getProcess().getProcessId());
                instanceExec.setExecState(jpaInstanceExec.getExecStatus().getExecStateId());
                instanceExec.setStartTs(jpaInstanceExec.getStartTs());
                instanceExec.setEndTs(jpaInstanceExec.getEndTs());
                instanceExec.setCounter(counter);
                instanceExecList.add(instanceExec);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

        return instanceExecList;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(InstanceExec.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public InstanceExec get(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        InstanceExec instanceExec = (InstanceExec) session.get(InstanceExec.class, id);
        session.getTransaction().commit();
        session.close();
        return instanceExec;
    }

    public Long insert(InstanceExec instanceExec) {
        Session session = sessionFactory.openSession();
        Long id = null;
        try {
            session.beginTransaction();
            id = (Long) session.save(instanceExec);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(InstanceExec instanceExec) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(instanceExec);
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
            InstanceExec instanceExec = (InstanceExec) session.get(InstanceExec.class, id);
            session.delete(instanceExec);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


   public List<SLAMonitoringBean> slaMonitoringData(List<Process> subProcessList)
    {
        Session session = sessionFactory.openSession();
        List<SLAMonitoringBean> slaMonitoringBeanList=new ArrayList<>();
        try {
            session.beginTransaction();
            for(Process process:subProcessList)
            {
               Criteria instanceExecListCriteria= session.createCriteria(InstanceExec.class).add(Restrictions.eq("process",process)).addOrder(Order.asc("startTs")).setMaxResults(25);
                List<InstanceExec> instanceExecList=instanceExecListCriteria.list();
                long sumTime=0;
                long currentTime=0;
                boolean processRunning = false;
                int total=instanceExecList.size();
                for (InstanceExec instanceExec:instanceExecList)
                {
                    if(instanceExec.getEndTs()!=null){
                    sumTime += (instanceExec.getEndTs().getTime() - instanceExec.getStartTs().getTime());
                    currentTime=instanceExec.getEndTs().getTime() - instanceExec.getStartTs().getTime();
                    }
                    else
                    {
                        processRunning = true;
                        sumTime += (new Date().getTime() - instanceExec.getStartTs().getTime());
                        currentTime=new Date().getTime() - instanceExec.getStartTs().getTime();
                    }
                }

                Criteria propertyCriteria=session.createCriteria(Properties.class).add(Restrictions.eq("process",process)).add(Restrictions.eq("configGroup","groupbar"));
                Properties properties= (Properties) propertyCriteria.uniqueResult();

                SLAMonitoringBean slaMonitoringBean=new SLAMonitoringBean();
                slaMonitoringBean.setProcessId(process.getProcessId());
                slaMonitoringBean.setProcessRunning(processRunning);
                if(total!=0)
                slaMonitoringBean.setAverageExecutionTime(sumTime/(total*1000));
                slaMonitoringBean.setCurrentExecutionTime(currentTime/1000);
                if(properties==null)
                slaMonitoringBean.setsLATime(0);
                else
                slaMonitoringBean.setsLATime(Long.parseLong(properties.getPropValue())/1000);
                slaMonitoringBeanList.add(slaMonitoringBean);
            }
            LOGGER.info("total size of slaMonitotingBeanList "+slaMonitoringBeanList.size());
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

        return slaMonitoringBeanList;
    }
}
