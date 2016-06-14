package com.wipro.ats.bdre.md.dao;

/**
 * Created by cloudera on 5/27/16.
 */


        import com.wipro.ats.bdre.exception.MetadataException;
        import com.wipro.ats.bdre.md.dao.jpa.PluginConfig;
        import com.wipro.ats.bdre.md.dao.jpa.PluginConfigId;
        import org.apache.log4j.Logger;
        import org.hibernate.Criteria;
        import org.hibernate.Session;
        import org.hibernate.SessionFactory;
        import org.hibernate.criterion.Order;
        import org.hibernate.criterion.Projections;
        import org.hibernate.criterion.Restrictions;
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
    public List<String> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PluginConfig.class);

        LOGGER.info("number of entries in properties table" + criteria.list().size());
        criteria.setProjection(Projections.distinct(Projections.property("id.pluginUniqueId")));
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<String> listOfProcessIDs = criteria.list();
        session.getTransaction().commit();
        session.close();
        return listOfProcessIDs;
    }

    public List<PluginConfig> getConfigForPlugin(String pluginUniqueId,Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PluginConfig.class).add(Restrictions.eq("id.pluginUniqueId",pluginUniqueId));
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<PluginConfig> pluginConfig = criteria.list();
        session.getTransaction().commit();
        session.close();
        return pluginConfig;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Integer size = session.createCriteria(PluginConfig.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public PluginConfig get(PluginConfigId id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        PluginConfig pluginConfig = (PluginConfig) session.get(PluginConfig.class, id);
        session.getTransaction().commit();
        session.close();
        return pluginConfig;
    }

    public PluginConfigId insert(PluginConfig pluginConfig) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
         PluginConfigId pluginConfigId=new PluginConfigId();
        try {
            pluginConfigId = (PluginConfigId) session.save(pluginConfig);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return pluginConfigId;
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

    public void delete(PluginConfigId id) {
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


    public void deleteByPluginId(String pluginUniqueId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Criteria propertiesByProcessId = session.createCriteria(PluginConfig.class).add(Restrictions.eq("id.pluginUniqueId", pluginUniqueId));
            List<PluginConfig> pluginConfigList = propertiesByProcessId.list();
            for (PluginConfig pluginConfig : pluginConfigList) {
                session.delete(pluginConfig);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

public List<String> distinctPluginConfig(String configGroup)
{
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    Criteria criteria = session.createCriteria(PluginConfig.class).setProjection(Projections.distinct(Projections.property("id.pluginUniqueId"))).add(Restrictions.like("configGroup","%"+configGroup));
    List<String> pluginUniqueIdList = criteria.list();
    session.getTransaction().commit();
    session.close();
    return pluginUniqueIdList;
}

public List<String> getWithConfig(String pluginUniqueId,String configGroup)
{
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    Criteria criteria = session.createCriteria(PluginConfig.class).add(Restrictions.eq("configGroup",configGroup)).
            add(Restrictions.eq("id.pluginUniqueId",pluginUniqueId)).setProjection(Projections.property("pluginValue")).addOrder(Order.asc("id.pluginKey"));
    List<String> pluginValuesList = criteria.list();
    session.getTransaction().commit();
    session.close();
    return pluginValuesList;
}

    public List<String> getWithKeyAndConfigGroup(String configGroup,String key)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PluginConfig.class).add(Restrictions.eq("configGroup",configGroup)).
                                                                       add(Restrictions.eq("id.pluginKey",key))
                                                                       .setProjection(Projections.property("pluginValue"));
        List<String> pluginValuesList = criteria.list();
        session.getTransaction().commit();
        session.close();
        return pluginValuesList;
    }

    public List<String> listPluginKeys(String pluginUniqueId,String configGroup)
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PluginConfig.class).add(Restrictions.eq("configGroup",configGroup)).
                add(Restrictions.eq("id.pluginUniqueId",pluginUniqueId)).setProjection(Projections.property("id.pluginKey")).addOrder(Order.asc("id.pluginKey"));
        List<String> pluginKeysList = criteria.list();
        session.getTransaction().commit();
        session.close();
        return pluginKeysList;
    }
}
