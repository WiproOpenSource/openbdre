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
import com.wipro.ats.bdre.md.dao.jpa.Servers;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by PR324290 on 10/28/2015.
 */
@Transactional
@Service
public class ServersDAO {


    private static final Logger LOGGER = Logger.getLogger(ServersDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<Servers> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Servers.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Servers> servers = criteria.list();
        session.getTransaction().commit();
        session.close();
        return servers;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Servers.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public Servers get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Servers servers = (Servers) session.get(Servers.class, id);
        session.getTransaction().commit();
        session.close();
        return servers;
    }


    public Integer insert(Servers servers) {
        Session session = sessionFactory.openSession();
        Integer id = null;
        try {
            session.beginTransaction();
            id = (Integer) session.save(servers);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }


    public void update(Servers servers) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(servers);
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
            Servers servers = (Servers) session.get(Servers.class, id);
            session.delete(servers);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}