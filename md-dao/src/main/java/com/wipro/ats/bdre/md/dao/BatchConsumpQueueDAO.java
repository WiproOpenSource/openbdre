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

import com.sun.org.apache.regexp.internal.RE;
import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by MR299389 on 10/16/2015.
 */

@Transactional
@Service
public class BatchConsumpQueueDAO {
    private static final Logger LOGGER = Logger.getLogger(BatchConsumpQueueDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<BatchConsumpQueue> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(BatchConsumpQueue.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<BatchConsumpQueue> batchConsumpQueues = criteria.list();
        session.getTransaction().commit();
        session.close();
        return batchConsumpQueues;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(BatchConsumpQueue.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public BatchConsumpQueue get(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        BatchConsumpQueue batchConsumpQueue = (BatchConsumpQueue) session.get(BatchConsumpQueue.class, id);
        session.getTransaction().commit();
        session.close();
        return batchConsumpQueue;
    }

    public Long insert(BatchConsumpQueue batchConsumpQueue) {
        Session session = sessionFactory.openSession();

        Long id = null;
        try {
            session.beginTransaction();
            id = (Long) session.save(batchConsumpQueue);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(BatchConsumpQueue batchConsumpQueue) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            session.update(batchConsumpQueue);
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
            BatchConsumpQueue batchConsumpQueue = (BatchConsumpQueue) session.get(BatchConsumpQueue.class, id);
            session.delete(batchConsumpQueue);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public Integer getBCQForProcessId(Process process) {
        Session session = sessionFactory.openSession();
        Integer size = null;
        try {
            session.beginTransaction();
            Criteria getBCQTargetBatchIdCriteria = session.createCriteria(BatchConsumpQueue.class).add(Restrictions.eq("process", process));
            size = getBCQTargetBatchIdCriteria.list().size();
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return size;
    }
}
