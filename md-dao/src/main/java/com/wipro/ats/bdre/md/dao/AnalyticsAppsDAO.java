package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.table.AnalyticsApps;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by cloudera on 4/20/16.
 */

@Transactional
@Service
public class AnalyticsAppsDAO {
    private static final Logger LOGGER = Logger.getLogger(AnalyticsAppsDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public Long insert(com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps analyticApps) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long id = null;
        try {
            id = (Long) session.save(analyticApps);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }
}
