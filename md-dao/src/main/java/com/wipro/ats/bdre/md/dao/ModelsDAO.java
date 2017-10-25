package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.Models;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by cloudera on 10/17/17.
 */
public class ModelsDAO {

    private static final Logger LOGGER = Logger.getLogger(ModelsDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    public String insert(Models models) {
        Session session = sessionFactory.openSession();
        String id = null;
        try {
            session.beginTransaction();
            id = (String) session.save(models);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }


    public List<Models> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Models.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Models> modelsList = criteria.list();
        session.getTransaction().commit();
        session.close();
        return modelsList;
    }



    public Models get(String id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Models models = (Models) session.get(Models.class, id);
        session.getTransaction().commit();
        session.close();
        return models;
    }

    public void update(Models models) {
        Session session = sessionFactory.openSession();
        try{
            session.beginTransaction();

            session.update(models);
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
        try{
            session.beginTransaction();
            Models models = (Models) session.get(Models.class, id);
            session.delete(models);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

    }

}
