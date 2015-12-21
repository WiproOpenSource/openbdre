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
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by AR288503 on 10/14/2015.
 */
//Annotate all classes like this
@Transactional
@Service
//For each table create a DAO class
//Each dao class should have list, get, insert, update, delete
//All DAO must have unit test cases and all methods must be tested
//Do not write public static void main to test these. Only use UnitTestCase
//You can run the unit test methods by right clicking on them
public class BusDomainDAO {
    private static final Logger LOGGER = Logger.getLogger(BusDomainDAO.class);
    //Session factory must be autowired in all DAO
    @Autowired
    private SessionFactory sessionFactory;

    //start page must be 0
    //numResults is how many records to display in a page

    public List<BusDomain> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        //Transaction begin
        session.beginTransaction();
        //Everything would be under transaction
        Criteria criteria = session.createCriteria(BusDomain.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<BusDomain> busDomains = criteria.list();
        //Transaction end (commit)
        session.getTransaction().commit();
        session.close();
        return busDomains;
    }

    //This returns the number of records in a given table

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(BusDomain.class);
        long size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public BusDomain get(Integer busDomainId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        BusDomain busDomain = (BusDomain) session.get(BusDomain.class, busDomainId);
        session.getTransaction().commit();
        session.close();
        return busDomain;
    }

    //Returns the id field
    public Integer insert(BusDomain busDomain) {
        Session session = sessionFactory.openSession();
        Integer busDomainId = null;
        try {
            session.beginTransaction();
            busDomainId = (Integer) session.save(busDomain);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return busDomainId;
    }

    public void update(BusDomain busDomain) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(busDomain);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(Integer busDomainId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            BusDomain busDomain = (BusDomain) session.get(BusDomain.class, busDomainId);
            session.delete(busDomain);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
