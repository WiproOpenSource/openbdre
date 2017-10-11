package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by cloudera on 5/21/17.
 */
@Transactional
@Service
public class MessagesDAO {
    private static final Logger LOGGER = Logger.getLogger(MessagesDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    public String insert(Messages messages) {
        Session session = sessionFactory.openSession();
        String id = null;
        try {
            session.beginTransaction();
            id = (String) session.save(messages);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }


    public List<Messages> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Messages.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Messages> messagesList = criteria.list();
        session.getTransaction().commit();
        session.close();
        return messagesList;
    }



    public Messages get(String id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Messages messages = (Messages) session.get(Messages.class, id);
        session.getTransaction().commit();
        session.close();
        return messages;
    }

    public void update(Messages message) {
        Session session = sessionFactory.openSession();
        try{
            session.beginTransaction();

            session.update(message);
            //session.get(Connections.class, message.getConnections().getConnectionName());
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
        try{
            session.beginTransaction();
            Messages messages = (Messages) session.get(Messages.class, id);
            session.delete(messages);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

    }

}
