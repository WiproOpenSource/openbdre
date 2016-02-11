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
import com.wipro.ats.bdre.md.dao.jpa.DeployStatus;
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
public class DeployStatusDAO {
    private static final Logger LOGGER = Logger.getLogger(DeployStatusDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<DeployStatus> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(DeployStatus.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<DeployStatus> deployStatuses = criteria.list();
        session.getTransaction().commit();
        session.close();
        return deployStatuses;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(DeployStatus.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public DeployStatus get(Short id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        DeployStatus deployStatus = (DeployStatus) session.get(DeployStatus.class, id);
        session.getTransaction().commit();
        session.close();
        return deployStatus;
    }

    public Short insert(DeployStatus deployStatus) {
        Session session = sessionFactory.openSession();
        Short id = null;
        try {
            session.beginTransaction();
            id = (Short) session.save(deployStatus);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(DeployStatus deployStatus) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(deployStatus);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(Short id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            DeployStatus deployStatus = (DeployStatus) session.get(DeployStatus.class, id);
            session.delete(deployStatus);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
