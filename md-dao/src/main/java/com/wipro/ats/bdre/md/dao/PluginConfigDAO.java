package com.wipro.ats.bdre.md.dao;

/**
 * Created by cloudera on 5/27/16.
 */


        import com.wipro.ats.bdre.exception.MetadataException;
        import com.wipro.ats.bdre.md.dao.jpa.PluginConfig;
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
public class PluginConfigDAO {
    private static final Logger LOGGER = Logger.getLogger(PluginConfigDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<PluginConfig> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PluginConfig.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<PluginConfig> pluginConfig = criteria.list();
        session.getTransaction().commit();
        session.close();
        return pluginConfig;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(PluginConfig.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public PluginConfig get(String id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        PluginConfig pluginConfig = (PluginConfig) session.get(PluginConfig.class, id);
        session.getTransaction().commit();
        session.close();
        return pluginConfig;
    }

    public String insert(PluginConfig pluginConfig) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String id = null;
        try {
            id = (String) session.save(pluginConfig);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(PluginConfig pluginConfig) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(pluginConfig);
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
            PluginConfig pluginConfig = (PluginConfig) session.get(PluginConfig.class, id);
            session.delete(pluginConfig);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }





}
