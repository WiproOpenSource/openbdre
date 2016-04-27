package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.dao.jpa.PermissionType;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by cloudera on 4/6/16.
 */
@Transactional
@Service
public class AppPermissionDAO {
    private static final Logger LOGGER = Logger.getLogger(AppPermissionDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    public PermissionType get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        PermissionType permissionType = (PermissionType) session.get(PermissionType.class, id);
        session.getTransaction().commit();
        session.close();
        return permissionType;
    }
    public List<PermissionType> permissionTypeList()
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PermissionType.class);
        List<PermissionType> permissionTypeList=criteria.list();
        session.getTransaction().commit();
        session.close();
        return permissionTypeList;
    }

}
