package com.wipro.ats.bdre.imcrawler.model;


import com.wipro.ats.bdre.md.dao.jpa.Statisticsdb;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by AS294216 on 26-11-2015.
 */

@Transactional
@Service
public class StatisticsDBDao {
    private static final Logger LOGGER = Logger.getLogger(StatisticsDBDao.class);
    @Autowired
    SessionFactory sessionFactory;
    public List<Statisticsdb> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Criteria criteria=session.createCriteria(Statisticsdb.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Statisticsdb> statisticsdbs = criteria.list();
        session.getTransaction().commit();
        return statisticsdbs;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        long size=session.createCriteria(Statisticsdb.class).list().size();
        session.getTransaction().commit();
        return size;
    }

    public Statisticsdb get(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long longid = new Long(id.intValue());
        Statisticsdb statisticsdb =(Statisticsdb)session.get(Statisticsdb.class,longid);
        session.getTransaction().commit();
        return statisticsdb;
    }

    public Integer insert(Statisticsdb statisticsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long id=(Long)session.save(statisticsdb);
        session.getTransaction().commit();
        return new Integer(id.intValue());
    }

    public void update(Statisticsdb statisticsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(statisticsdb);
        session.getTransaction().commit();
    }

    public void delete(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long longid = new Long(id.intValue());
        Statisticsdb statisticsdb=(Statisticsdb)session.get(Statisticsdb.class,longid);
        session.delete(statisticsdb);
        session.getTransaction().commit();
    }
}
