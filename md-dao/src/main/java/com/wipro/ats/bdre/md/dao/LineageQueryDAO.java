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
import com.wipro.ats.bdre.md.dao.jpa.LineageQuery;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PR324290 on 10/28/2015.
 */
@Transactional
@Service
public class LineageQueryDAO {

    private static final Logger LOGGER = Logger.getLogger(LineageQueryDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<LineageQuery> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(LineageQuery.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<LineageQuery> lineageQuerys = criteria.list();
        session.getTransaction().commit();
        session.close();
        return lineageQuerys;
    }

    public List<LineageQuery> getLastInstanceExecLists(Integer processId) {
        Long instanceExecId;
        List<LineageQuery> lineageQueryList = new ArrayList<LineageQuery>();
        int counter = 1;
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Criteria getLastElementCriteria = session.createCriteria(LineageQuery.class).add(Restrictions.eq("processId", processId)).addOrder(Order.desc("instanceExecId"));

        if(getLastElementCriteria.list().size() != 0) {
            LineageQuery lineageQuery = (LineageQuery) getLastElementCriteria.list().get(0);
            instanceExecId = lineageQuery.getInstanceExecId();
            while (lineageQuery.getInstanceExecId() == instanceExecId) {
                lineageQueryList.add(lineageQuery);
                //break the loop if the counter exceeds the criteria size
                if (counter < getLastElementCriteria.list().size())
                    lineageQuery = (LineageQuery) getLastElementCriteria.list().get(counter++);
                else
                    break;
            }
        }
        session.getTransaction().commit();
        return lineageQueryList;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(LineageQuery.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public LineageQuery get(String id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        LineageQuery lineageQuery = (LineageQuery) session.get(LineageQuery.class, id);
        session.getTransaction().commit();
        session.close();
        return lineageQuery;
    }


    public String insert(LineageQuery lineageQuery) {
        Session session = sessionFactory.openSession();
        String id = null;
        try {
            session.beginTransaction();
            id = (String) session.save(lineageQuery);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }


    public void update(LineageQuery lineageQuery) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(lineageQuery);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


    public void delete(String id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            LineageQuery lineageQuery = (LineageQuery) session.get(LineageQuery.class, id);
            session.delete(lineageQuery);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


}
