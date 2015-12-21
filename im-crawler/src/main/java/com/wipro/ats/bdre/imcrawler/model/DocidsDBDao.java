package com.wipro.ats.bdre.imcrawler.model;


import com.wipro.ats.bdre.imcrawler.jpa.Docidsdb;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by AS294216 on 26-11-2015.
 */

@Transactional
@Service
public class DocidsDBDao {

    private static final Logger LOGGER = Logger.getLogger(DocidsDBDao.class);
    @Autowired
    SessionFactory sessionFactory;
    public List<Docidsdb> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Criteria criteria=session.createCriteria(Docidsdb.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Docidsdb> docidsdbs = criteria.list();
        session.getTransaction().commit();
        return docidsdbs;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        long size=session.createCriteria(Docidsdb.class).list().size();
        session.getTransaction().commit();
        return size;
    }

    public Docidsdb get(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Docidsdb docidsdb =(Docidsdb)session.get(Docidsdb.class,id);
        session.getTransaction().commit();
        return docidsdb;
    }

    public Docidsdb getLastElement() {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Criteria getLastElementCriteria = session.createCriteria(Docidsdb.class).addOrder(Order.desc("docId"));
        Docidsdb docidsdb = (Docidsdb) getLastElementCriteria.list().get(0);
        session.getTransaction().commit();
        return docidsdb;
    }

    //insert specific values only; not used as auto increment working for remaining values of DocIDsDB
    @Deprecated
    public void insertSpecific(String url) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query qry = session.createQuery("INSERT INTO DOCIDSDB (URL) VALUES (:urllink)");
        qry.setParameter("urllink",url);
        int result = qry.executeUpdate();
        session.getTransaction().commit();
    }

    public Integer insert(Docidsdb docidsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Integer id=(Integer)session.save(docidsdb);
        session.getTransaction().commit();
        return id;
    }

    public void update(Docidsdb docidsdb) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(docidsdb);
        session.getTransaction().commit();
    }

    public void delete(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Docidsdb docidsdb=(Docidsdb)session.get(Docidsdb.class,id);
        session.delete(docidsdb);
        session.getTransaction().commit();
    }
}
