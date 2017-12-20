package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.ConnectionProperties;
import com.wipro.ats.bdre.md.dao.jpa.ConnectionPropertiesId;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 5/30/17.
 */
@Transactional
@Service
public class ConnectionPropertiesDAO {
    private static final Logger LOGGER = Logger.getLogger(ConnectionPropertiesDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    //numResults is how many records to display in a page

    public List<ConnectionProperties> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        //Transaction begin
        session.beginTransaction();
        //Everything would be under transaction
        Criteria criteria = session.createCriteria(ConnectionProperties.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<ConnectionProperties> properties = criteria.list();
        //Transaction end (commit)
        session.getTransaction().commit();
        session.close();
        return properties;
    }

    //This returns the number of records in a given table

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ConnectionProperties.class);
        long size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public ConnectionProperties get(ConnectionPropertiesId connectionPropertiesId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ConnectionProperties connection = (ConnectionProperties) session.get(ConnectionProperties.class, connectionPropertiesId);
        session.getTransaction().commit();
        session.close();
        return connection;
    }

    //Returns the id field
    public ConnectionPropertiesId insert(ConnectionProperties connectionProperties) {
        Session session = sessionFactory.openSession();
        ConnectionPropertiesId propertyId = null;
        try {
            session.beginTransaction();
            propertyId = (ConnectionPropertiesId) session.save(connectionProperties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertyId;
    }

    public void update(ConnectionProperties connectionProperties) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(connectionProperties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(ConnectionPropertiesId propertyId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            ConnectionProperties connectionProperties = (ConnectionProperties) session.get(ConnectionProperties.class, propertyId);
            session.delete(connectionProperties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<ConnectionProperties> getConnectionsByConnectionName(String connectionName, Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria propertiesByConnectionName = session.createCriteria(ConnectionProperties.class).add(Restrictions.eq("connections.connectionName", connectionName));
        propertiesByConnectionName.setFirstResult(pageNum);
        propertiesByConnectionName.setMaxResults(numResults);
        List<ConnectionProperties> propertiesList = propertiesByConnectionName.list();
        session.getTransaction().commit();
        session.close();
        return propertiesList;
    }

    public Integer recordCountByConnectionName(String connectionName) {
        Session session = sessionFactory.openSession();
        Integer size = 0;
        try {
            session.beginTransaction();
            Criteria propertiesByConnectionName = session.createCriteria(ConnectionProperties.class).add(Restrictions.eq("connections.connectionName", connectionName));
            List<ConnectionProperties> propertiesList = propertiesByConnectionName.list();
            size=propertiesList.size();
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return size;
    }

    public ConnectionProperties getConnectionsById(ConnectionPropertiesId connectionPropertiesId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ConnectionProperties connectionProperties = (ConnectionProperties) session.get(ConnectionProperties.class, connectionPropertiesId);
        session.getTransaction().commit();
        session.close();
        return connectionProperties;
    }

    public List<ConnectionProperties> getConnectionPropertiesForConfig(String connectionName, String configGroup) {
        List<ConnectionProperties> propertiesList = new ArrayList<ConnectionProperties>();
        Session session = sessionFactory.openSession();
        try {

            session.beginTransaction();
            Criteria cr = session.createCriteria(ConnectionProperties.class).add(Restrictions.eq("connections.connectionName", connectionName)).add(Restrictions.eq("configGroup", configGroup));
            propertiesList = cr.list();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            LOGGER.info("Error " + e);
            return propertiesList;
        } finally {
            session.close();
        }
        return propertiesList;
    }




public List<ConnectionProperties> getAllConnectionProperties(String connectionName){
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    Criteria propertiesByConnectionName = session.createCriteria(ConnectionProperties.class).add(Restrictions.eq("connections.connectionName", connectionName));
    List<ConnectionProperties> propertiesList = propertiesByConnectionName.list();
    session.getTransaction().commit();
    session.close();
    return propertiesList;
}
}