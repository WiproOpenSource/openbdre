package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.table.AnalyticsApps;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
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
    public List<String> listIndustries() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps.class);
        criteria.setProjection(Projections.distinct(Projections.property("industryName")));
        LOGGER.info("number of distinct industries in AnalyticsApp table" + criteria.list().size());
        List<String> listOfIndustries = criteria.list();
        session.getTransaction().commit();
        session.close();
        return listOfIndustries;
    }

    public List<String> listCategories(String industry) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps.class).add(Restrictions.eq("industryName", industry));
        criteria.setProjection(Projections.distinct(Projections.property("categoryName")));
        LOGGER.info("number of distinct categories" + criteria.list().size());
        List<String> categories = criteria.list();
        session.getTransaction().commit();
        session.close();
        return categories;
    }

    public List<String> listApps(String industry, String category) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps.class).add(Restrictions.eq("industryName", industry)).add(Restrictions.eq("categoryName", category));
        criteria.setProjection(Projections.distinct(Projections.property("appName")));
        LOGGER.info("number of apps" + criteria.list().size());

        List<String> apps = criteria.list();
        session.getTransaction().commit();
        session.close();
        return apps;
    }

    public List<String> getDashBoardURL(String industry, String category, String app) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps.class).add(Restrictions.eq("industryName", industry)).add(Restrictions.eq("categoryName", category)).add(Restrictions.eq("appName", app));
        criteria.setProjection(Projections.distinct(Projections.property("dashboardUrl")));
        LOGGER.info("Dashboard url" + criteria.list().get(0));

        List<String> dashboardurl = criteria.list();
        session.getTransaction().commit();
        session.close();
        return dashboardurl;
    }

    public List<String> getJson(String industry, String category, String app) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps.class).add(Restrictions.eq("industryName", industry)).add(Restrictions.eq("categoryName", category)).add(Restrictions.eq("appName", app));
        criteria.setProjection(Projections.distinct(Projections.property("questionsJson")));
        LOGGER.info("json" + criteria.list().get(0));

        List<String> json = criteria.list();
        session.getTransaction().commit();
        session.close();
        return json;
    }



}
