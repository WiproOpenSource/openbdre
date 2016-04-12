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
import com.wipro.ats.bdre.md.dao.jpa.UserRoles;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by PR324290 on 10/28/2015.
 */
@Transactional
@Service
public class UserRolesDAO {
    private static final Logger LOGGER = Logger.getLogger(UserRolesDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<UserRoles> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserRoles.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<UserRoles> userRoles = criteria.list();
        session.getTransaction().commit();
        session.close();
        return userRoles;
    }

    public Map<String,Integer> diffRoleList() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserRoles.class).setProjection(Projections.distinct(Projections.property("role")));
        ProjectionList p1=Projections.projectionList();
        p1.add(Projections.property("role"));
        p1.add(Projections.property("userRoleId"));
        criteria.setProjection(p1);
        List l=criteria.list();
        Iterator it=l.iterator();
        Map<String,Integer> diffRoles=new HashMap<>();
        while(it.hasNext())
        {
            Object ob[] = (Object[])it.next();
            diffRoles.put((String) ob[0],(Integer)ob[1]);
        }
        session.getTransaction().commit();
        session.close();
        return diffRoles;
    }

    public List<UserRoles> listByName(String userName) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserRoles.class).add(Restrictions.eq("users.username", userName));
        List<UserRoles> userRoles = criteria.list();
        session.getTransaction().commit();
        session.close();
        return userRoles;
    }


    public UserRoles minUserRoleId(String userName) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        UserRoles userRoles=new UserRoles();
        Criteria criteria = session.createCriteria(UserRoles.class).add(Restrictions.eq("users.username", userName)).addOrder(Order.asc("userRoleId"));
        if (criteria.list()!=null){
         userRoles = (UserRoles) criteria.list().get(0);
        }
        session.getTransaction().commit();
        session.close();
        return userRoles;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(UserRoles.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public UserRoles get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        UserRoles userRoles = (UserRoles) session.get(UserRoles.class, id);
        session.getTransaction().commit();
        session.close();
        return userRoles;
    }


    public Integer insert(UserRoles userRoles) {
        Session session = sessionFactory.openSession();
        Integer id = null;
        try {
            session.beginTransaction();
            id = (Integer) session.save(userRoles);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }


    public void update(UserRoles userRoles) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(userRoles);
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
            UserRoles userRoles = (UserRoles) session.get(UserRoles.class, id);
            session.delete(userRoles);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
