package com.wipro.ats.bdre.imcrawler.model;

import com.wipro.ats.bdre.imcrawler.jpa.Weburlsdb;
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
public class WebUrlsDBDao {
    private static final Logger LOGGER = Logger.getLogger(WebUrlsDBDao.class);
    @Autowired
    SessionFactory sessionFactory;
    public List<Weburlsdb> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Criteria criteria=session.createCriteria(Weburlsdb.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Weburlsdb> weburlsdbs = criteria.list();
        session.getTransaction().commit();
        return weburlsdbs;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        long size=session.createCriteria(Weburlsdb.class).list().size();
        session.getTransaction().commit();
        return size;
    }

    public Weburlsdb get(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long lid = new Long(id.intValue());
        Weburlsdb weburlsdb=(Weburlsdb)session.get(Weburlsdb.class,lid);
        session.getTransaction().commit();
        return weburlsdb;
    }

    public Integer insert(Weburlsdb weburlsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long id=(Long)session.save(weburlsdb);
        session.getTransaction().commit();
        return new Integer(id.intValue());
    }

    public void update(Weburlsdb weburlsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(weburlsdb);
        session.getTransaction().commit();
    }

    public void delete(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Long lid = new Long(id.intValue());
        Weburlsdb weburlsdb=(Weburlsdb)session.get(Weburlsdb.class,lid);
        session.delete(weburlsdb);
        session.getTransaction().commit();
    }
}
