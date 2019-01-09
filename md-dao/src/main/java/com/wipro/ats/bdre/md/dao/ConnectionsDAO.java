package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cloudera on 5/30/17.
 */
@Transactional
@Service
public class ConnectionsDAO {
    private static final Logger LOGGER = Logger.getLogger(ConnectionsDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    //numResults is how many records to display in a page

    public List<Connections> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        //Transaction begin
        session.beginTransaction();
        //Everything would be under transaction
        Criteria criteria = session.createCriteria(Connections.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Connections> connections = criteria.list();
        //Transaction end (commit)
        session.getTransaction().commit();
        session.close();
        return connections;
    }



    public List<Connections> listByConnectionType(String connectionType,Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        //Transaction begin
        session.beginTransaction();
        //Everything would be under transaction
        Criteria criteria = session.createCriteria(Connections.class);
        LOGGER.info("connectionType "+connectionType);
        String connectionRegex = connectionType+"%";
        criteria.add(Restrictions.like("connectionType",connectionRegex));
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Connections> connections = criteria.list();
        LOGGER.info("inside md-dao "+Arrays.asList(connections));
        //Transaction end (commit)
        session.getTransaction().commit();
        session.close();
        return connections;
    }

    //This returns the number of records in a given table

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Connections.class);
        long size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public Connections get(String connectionName) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Connections connection = (Connections) session.get(Connections.class, connectionName);
        session.getTransaction().commit();
        session.close();
        return connection;
    }

    //Returns the id field
    public String insert(Connections connections) {
        Session session = sessionFactory.openSession();
        String connectionName = null;
        try {
            session.beginTransaction();
            connectionName = (String) session.save(connections);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return connectionName;
    }

    public void update(Connections connections) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(connections);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(String connectionsName) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Connections connection = (Connections) session.get(Connections.class, connectionsName);
            session.delete(connection);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
