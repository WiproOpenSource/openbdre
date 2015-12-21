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
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR299389 on 10/28/2015.
 */
@Transactional
@Service
public class PropertiesDAO {
    private static final Logger LOGGER = Logger.getLogger(PropertiesDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<Integer> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Properties.class);

//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.groupProperty("process"));
//        criteria.setProjection(projectionList);
//        criteria.setResultTransformer(Transformers.aliasToBean(Properties.class));
        LOGGER.info("number of entries in properties table" + criteria.list().size());
        criteria.setProjection(Projections.distinct(Projections.property("id.processId")));
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Integer> listOfProcessIDs = criteria.list();
        session.getTransaction().commit();
        session.close();
        return listOfProcessIDs;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria totalRecord = session.createCriteria(Properties.class);

//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.groupProperty("process"));
//        totalRecord.setProjection(projectionList);
//        totalRecord.setResultTransformer(Transformers.aliasToBean(Properties.class));
        totalRecord.setProjection(Projections.distinct(Projections.property("id.processId")));
        int size = totalRecord.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public Properties get(PropertiesId propertiesId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Properties properties = (Properties) session.get(Properties.class, propertiesId);
        session.getTransaction().commit();
        session.close();
        return properties;
    }

    public PropertiesId insert(Properties properties) {
        Session session = sessionFactory.openSession();
        PropertiesId propertiesId = null;
        try {
            session.beginTransaction();
            propertiesId = (PropertiesId) session.save(properties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesId;
    }

    public void update(Properties properties) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(properties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(PropertiesId propertiesId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Properties properties = (Properties) session.get(Properties.class, propertiesId);
            session.delete(properties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<Properties> getPropertiesForConfig(int processId, String configGroup) {
        List<Properties> propertiesList = new ArrayList<Properties>();
        Session session = sessionFactory.openSession();
        try {

            session.beginTransaction();
            Criteria cr = session.createCriteria(Properties.class).add(Restrictions.eq("process.processId", processId)).add(Restrictions.eq("configGroup", configGroup));
            propertiesList = cr.list();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            LOGGER.info("Error " + e);
            return null;
        } finally {
            session.close();
        }
        return propertiesList;
    }

    public void deleteByProcessId(com.wipro.ats.bdre.md.dao.jpa.Process process) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Criteria propertiesByProcessId = session.createCriteria(Properties.class).add(Restrictions.eq("process", process));
            List<Properties> propertiesList = propertiesByProcessId.list();
            for (Properties properties : propertiesList) {
                session.delete(properties);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<Properties> getByProcessId(com.wipro.ats.bdre.md.dao.jpa.Process process) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria propertiesByProcessId = session.createCriteria(Properties.class).add(Restrictions.eq("process", process));
        List<Properties> propertiesList = propertiesByProcessId.list();
        session.getTransaction().commit();
        session.close();
        return propertiesList;
    }


}
