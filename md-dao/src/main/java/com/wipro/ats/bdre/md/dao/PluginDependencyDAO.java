package com.wipro.ats.bdre.md.dao;

/**
 * Created by cloudera on 5/27/16.
 */

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.PluginDependency;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Service

public class PluginDependencyDAO {

    private static final Logger LOGGER = Logger.getLogger(PluginDependencyDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<PluginDependency> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PluginDependency.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<PluginDependency> pluginDependency = criteria.list();
        session.getTransaction().commit();
        session.close();
        return pluginDependency;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(PluginDependency.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public PluginDependency get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        PluginDependency pluginDependency = (PluginDependency) session.get(PluginDependency.class, id);
        session.getTransaction().commit();
        session.close();
        return pluginDependency;
    }

    public Integer insert(PluginDependency pluginDependency) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Integer id = null;
        try {
            id = (Integer) session.save(pluginDependency);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(PluginDependency pluginDependency) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(pluginDependency);
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
            PluginDependency pluginDependency = (PluginDependency) session.get(PluginDependency.class, id);
            session.delete(pluginDependency);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

}
