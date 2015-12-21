package com.wipro.ats.bdre.imcrawler.model;

import com.wipro.ats.bdre.imcrawler.jpa.Pendingurlsdb;
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
public class PendingUrlsDBDao {
    private static final Logger LOGGER = Logger.getLogger(PendingUrlsDBDao.class);
    @Autowired
    SessionFactory sessionFactory;
    public List<Pendingurlsdb> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Criteria criteria=session.createCriteria(Pendingurlsdb.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Pendingurlsdb> pendingurlsdbs = criteria.list();
        session.getTransaction().commit();
        return pendingurlsdbs;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        long size=session.createCriteria(Pendingurlsdb.class).list().size();
        session.getTransaction().commit();
        return size;
    }

    public Pendingurlsdb get(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long longid = new Long (id.intValue());
        Pendingurlsdb pendingurlsdb =(Pendingurlsdb)session.get(Pendingurlsdb.class,longid);
        session.getTransaction().commit();
        return pendingurlsdb;
    }

    public Integer insert(Pendingurlsdb pendingurlsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long id=(Long)session.save(pendingurlsdb);
        session.getTransaction().commit();
        return new Integer(id.intValue());
    }

    public void update(Pendingurlsdb pendingurlsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(pendingurlsdb);
        session.getTransaction().commit();
    }

    public void delete(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long longid = new Long (id.intValue());
        Pendingurlsdb pendingurlsdb=(Pendingurlsdb)session.get(Pendingurlsdb.class,longid);
        session.delete(pendingurlsdb);
        session.getTransaction().commit();
    }
}
