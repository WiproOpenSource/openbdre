package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.ModelProperties;
import com.wipro.ats.bdre.md.dao.jpa.ModelPropertiesId;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by cloudera on 10/17/17.
 */
@Transactional
@Service
public class ModelPropertiesDAO {

    private static final Logger LOGGER = Logger.getLogger(ModelPropertiesDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    //numResults is how many records to display in a page

    public List<ModelProperties> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        //Transaction begin
        session.beginTransaction();
        //Everything would be under transaction
        Criteria criteria = session.createCriteria(ModelProperties.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<ModelProperties> properties = criteria.list();
        //Transaction end (commit)
        session.getTransaction().commit();
        session.close();
        return properties;
    }

    //This returns the number of records in a given table

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ModelProperties.class);
        long size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public ModelProperties get(ModelPropertiesId modelPropertiesId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ModelProperties modelProperties = (ModelProperties) session.get(ModelProperties.class, modelPropertiesId);
        session.getTransaction().commit();
        session.close();
        return modelProperties;
    }

    //Returns the id field
    public ModelPropertiesId insert(ModelProperties modelProperties) {
        Session session = sessionFactory.openSession();
        ModelPropertiesId propertyId = null;
        try {
            session.beginTransaction();
            propertyId = (ModelPropertiesId) session.save(modelProperties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertyId;
    }

    public void update(ModelProperties modelProperties) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(modelProperties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(ModelPropertiesId propertyId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            ModelProperties modelProperties = (ModelProperties) session.get(ModelProperties.class, propertyId);
            session.delete(modelProperties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

}
