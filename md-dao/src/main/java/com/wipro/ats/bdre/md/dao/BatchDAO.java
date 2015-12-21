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
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by MR299389 on 10/15/2015.
 */

@Transactional
@Service
public class BatchDAO {
    private static final Logger LOGGER = Logger.getLogger(BatchDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<Batch> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Batch.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Batch> batches = criteria.list();
        session.getTransaction().commit();
        session.close();
        return batches;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Batch.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public Batch get(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Batch batch = (Batch) session.get(Batch.class, id);
        session.getTransaction().commit();
        session.close();
        return batch;
    }

    public Long insert(Batch batch) {
        Session session = sessionFactory.openSession();

        Long id = null;
        try {
            session.beginTransaction();
            id = (Long) session.save(batch);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(Batch batch) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            session.update(batch);
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
            Batch batch = (Batch) session.get(Batch.class, id);
            session.delete(batch);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
