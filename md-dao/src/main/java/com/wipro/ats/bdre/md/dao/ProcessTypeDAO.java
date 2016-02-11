/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR299389 on 10/16/2015.
 */

@Transactional
@Service
public class ProcessTypeDAO {
    private static final Logger LOGGER = Logger.getLogger(ProcessTypeDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<ProcessType> list(Integer processTypeId, Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<ProcessType> processTypes = new ArrayList<ProcessType>();
        if (processTypeId == null) {
            Criteria parentProcessTypeCriteria = session.createCriteria(ProcessType.class).add(Restrictions.isNull("parentProcessTypeId"));
            processTypes = parentProcessTypeCriteria.list();
        } else {
            Criteria processTypeCriteria = session.createCriteria(ProcessType.class).add(Restrictions.eq("parentProcessTypeId", processTypeId));
            processTypes = processTypeCriteria.list();
        }
        session.getTransaction().commit();
        session.close();
        return processTypes;
    }

    public List<ProcessType> listFull(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProcessType.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<ProcessType> processTypes = criteria.list();
        session.getTransaction().commit();
        session.close();
        return processTypes;
    }

    public Integer totalRows() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(ProcessType.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public Integer totalRecordCount(Integer processTypeId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Integer size;
        if (processTypeId == null) {
            Criteria parentProcessTypeCriteria = session.createCriteria(ProcessType.class).add(Restrictions.isNull("parentProcessTypeId"));
            size = parentProcessTypeCriteria.list().size();
        } else {
            Criteria processTypeCriteria = session.createCriteria(ProcessType.class).add(Restrictions.eq("parentProcessTypeId", processTypeId));
            size = processTypeCriteria.list().size();
        }
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public ProcessType get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        ProcessType processType = (ProcessType) session.get(ProcessType.class, id);
        session.getTransaction().commit();
        session.close();
        return processType;
    }

    public Integer insert(ProcessType processType) {
        Session session = sessionFactory.openSession();
        Integer id = null;
        try {
            session.beginTransaction();
            id = (Integer) session.save(processType);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(ProcessType processType) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(processType);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            ProcessType processType = (ProcessType) session.get(ProcessType.class, id);
            session.delete(processType);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
