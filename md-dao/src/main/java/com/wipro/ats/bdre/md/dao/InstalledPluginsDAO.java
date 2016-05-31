package com.wipro.ats.bdre.md.dao;

/**
 * Created by sh324337 on 5/27/16.
 */

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins;
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
public class InstalledPluginsDAO {

    private static final Logger LOGGER = Logger.getLogger(InstalledPluginsDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<InstalledPlugins> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(InstalledPlugins.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<InstalledPlugins> installedPlugins = criteria.list();
        session.getTransaction().commit();
        session.close();
        return installedPlugins;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(InstalledPlugins.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public InstalledPlugins get(String id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        InstalledPlugins installedPlugins = (InstalledPlugins) session.get(InstalledPlugins.class, id);
        session.getTransaction().commit();
        session.close();
        return installedPlugins;
    }

    public String insert(InstalledPlugins installedPlugins) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String id = null;
        try {
            id = (String) session.save(installedPlugins);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(InstalledPlugins installedPlugins) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(installedPlugins);
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
            InstalledPlugins installedPlugins = (InstalledPlugins) session.get(InstalledPlugins.class, id);
            session.delete(installedPlugins);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
