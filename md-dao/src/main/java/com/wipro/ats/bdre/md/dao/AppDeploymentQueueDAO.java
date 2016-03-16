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
import com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueue;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by SU324335 on 3/8/2016.
 */
@Transactional
@Service
public class AppDeploymentQueueDAO {
    private static final Logger LOGGER = Logger.getLogger(AppDeploymentQueueDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<AppDeploymentQueue> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(AppDeploymentQueue.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<AppDeploymentQueue> adqList = criteria.list();
        session.getTransaction().commit();
        session.close();
        return adqList;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Integer size = session.createCriteria(AppDeploymentQueue.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }
    public AppDeploymentQueue get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        AppDeploymentQueue adq = (AppDeploymentQueue) session.get(AppDeploymentQueue.class, id);
        session.getTransaction().commit();
        session.close();
        return adq;
    }

    public Long insert(AppDeploymentQueue adq) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long id = null;
        try {
            id = (Long) session.save(adq);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(AppDeploymentQueue adq) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(adq);
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
            AppDeploymentQueue adq = (AppDeploymentQueue) session.get(AppDeploymentQueue.class, id);
            session.delete(adq);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
