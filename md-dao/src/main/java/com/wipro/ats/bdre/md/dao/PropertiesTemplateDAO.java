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
import com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplate;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplateId;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR299389 on 10/28/2015.
 */

@Transactional
@Service
public class PropertiesTemplateDAO {
    private static final Logger LOGGER = Logger.getLogger(PropertiesTemplateDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    private static final String PROPTEMPKEY="id.propTempKey";
    private static final String PROCESSTEMPLATEID="id.processTemplateId";

    public List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> listPropertiesTemplateBean(Integer processTemplateId) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> propertiesTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate>();

        try {
            session.beginTransaction();
            if (processTemplateId == 0 || processTemplateId == null) {
                Criteria countCriteria = session.createCriteria(PropertiesTemplate.class).addOrder(Order.asc(PROCESSTEMPLATEID)).addOrder(Order.asc(PROPTEMPKEY));
                int counter = countCriteria.list().size();

                Criteria criteria = session.createCriteria(PropertiesTemplate.class).addOrder(Order.asc(PROPTEMPKEY));
                List<PropertiesTemplate> jpapropertiesTemplateList = criteria.list();

                for (PropertiesTemplate jparopertiesTemplate : jpapropertiesTemplateList) {
                    com.wipro.ats.bdre.md.beans.table.PropertiesTemplate propertiesTemplate = new com.wipro.ats.bdre.md.beans.table.PropertiesTemplate();
                    propertiesTemplate.setProcessTemplateId(jparopertiesTemplate.getId().getProcessTemplateId());
                    propertiesTemplate.setKey(jparopertiesTemplate.getId().getPropTempKey());
                    propertiesTemplate.setConfigGroup(jparopertiesTemplate.getConfigGroup());
                    propertiesTemplate.setValue(jparopertiesTemplate.getPropTempValue());
                    propertiesTemplate.setDescription(jparopertiesTemplate.getDescription());
                    propertiesTemplate.setCounter(counter);
                    propertiesTemplateList.add(propertiesTemplate);
                }

            } else {
                Criteria countCriteria = session.createCriteria(PropertiesTemplate.class).addOrder(Order.asc(PROPTEMPKEY)).add(Restrictions.eq(PROCESSTEMPLATEID, processTemplateId)).setProjection(Projections.distinct(Projections.property(PROPTEMPKEY)));
                int counter = countCriteria.list().size();

                Criteria criteria = session.createCriteria(PropertiesTemplate.class).add(Restrictions.eq(PROCESSTEMPLATEID, processTemplateId)).addOrder(Order.asc(PROPTEMPKEY));

                List<PropertiesTemplate> jpapropertiesTemplateList = criteria.list();

                for (PropertiesTemplate jparopertiesTemplate : jpapropertiesTemplateList) {
                    com.wipro.ats.bdre.md.beans.table.PropertiesTemplate propertiesTemplate = new com.wipro.ats.bdre.md.beans.table.PropertiesTemplate();
                    propertiesTemplate.setProcessTemplateId(jparopertiesTemplate.getId().getProcessTemplateId());
                    propertiesTemplate.setKey(jparopertiesTemplate.getId().getPropTempKey());
                    propertiesTemplate.setConfigGroup(jparopertiesTemplate.getConfigGroup());
                    propertiesTemplate.setValue(jparopertiesTemplate.getPropTempValue());
                    propertiesTemplate.setDescription(jparopertiesTemplate.getDescription());
                    propertiesTemplate.setCounter(counter);
                    propertiesTemplateList.add(propertiesTemplate);
                }

            }
            session.getTransaction().commit();

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesTemplateList;

    }

    public List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> listPropertyTemplate(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> propertiesTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate>();

        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(PropertiesTemplate.class);
            criteria.setFirstResult(pageNum);
            criteria.setMaxResults(numResults);

            List<PropertiesTemplate> jpaPropertiesTemplateList = criteria.list();
            int counter = jpaPropertiesTemplateList.size();

            for (PropertiesTemplate jpaPropertiesTemplate : jpaPropertiesTemplateList) {
                com.wipro.ats.bdre.md.beans.table.PropertiesTemplate newPropertiesTemplate = new com.wipro.ats.bdre.md.beans.table.PropertiesTemplate();
                newPropertiesTemplate.setConfigGroup(jpaPropertiesTemplate.getConfigGroup());
                newPropertiesTemplate.setKey(jpaPropertiesTemplate.getId().getPropTempKey());
                newPropertiesTemplate.setDescription(jpaPropertiesTemplate.getDescription());
                newPropertiesTemplate.setValue(jpaPropertiesTemplate.getPropTempValue());
                newPropertiesTemplate.setProcessTemplateId(jpaPropertiesTemplate.getId().getProcessTemplateId());
                newPropertiesTemplate.setCounter(counter);
                LOGGER.info("Config Group of PropertiesTemplate is " + newPropertiesTemplate.getConfigGroup());
                propertiesTemplateList.add(newPropertiesTemplate);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }

        return propertiesTemplateList;

    }


    public List<PropertiesTemplate> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(PropertiesTemplate.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<PropertiesTemplate> propertiesTemplatees = criteria.list();
        session.getTransaction().commit();
        session.close();
        return propertiesTemplatees;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(PropertiesTemplate.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public PropertiesTemplate get(PropertiesTemplateId propertiesTemplateId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        PropertiesTemplate propertiesTemplate = (PropertiesTemplate) session.get(PropertiesTemplate.class, propertiesTemplateId);
        session.getTransaction().commit();
        session.close();
        return propertiesTemplate;
    }

    public com.wipro.ats.bdre.md.beans.table.PropertiesTemplate insertProcessTemplate(com.wipro.ats.bdre.md.beans.table.PropertiesTemplate propertiesTemplate) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> propertiesTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate>();

        try {
            session.beginTransaction();
            PropertiesTemplate jpaPropertiesTemplateInsert = new PropertiesTemplate();
            jpaPropertiesTemplateInsert.setDescription(propertiesTemplate.getDescription());
            jpaPropertiesTemplateInsert.setConfigGroup(propertiesTemplate.getConfigGroup());
            jpaPropertiesTemplateInsert.setPropTempValue(propertiesTemplate.getValue());

            PropertiesTemplateId jpaPropertiesTemplateId = new PropertiesTemplateId();
            jpaPropertiesTemplateId.setPropTempKey(propertiesTemplate.getKey());
            jpaPropertiesTemplateId.setProcessTemplateId(propertiesTemplate.getProcessTemplateId());
            jpaPropertiesTemplateInsert.setId(jpaPropertiesTemplateId);

            ProcessTemplate processTemplate = (ProcessTemplate) session.get(ProcessTemplate.class, propertiesTemplate.getProcessTemplateId());
            jpaPropertiesTemplateInsert.setProcessTemplate(processTemplate);

            PropertiesTemplateId propertiesTemplateId = (PropertiesTemplateId) session.save(jpaPropertiesTemplateInsert);
            LOGGER.info("Inserted properties Template:" + jpaPropertiesTemplateInsert);

            Criteria returningCriteria = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplate.class).add(Restrictions.eq(PROCESSTEMPLATEID, propertiesTemplateId.getProcessTemplateId())).add(Restrictions.eq(PROPTEMPKEY, propertiesTemplateId.getPropTempKey()));

            List<PropertiesTemplate> jpaPropertiesTemplateList = returningCriteria.list();
            for (PropertiesTemplate jpaPropertiesTemplate : jpaPropertiesTemplateList) {
                com.wipro.ats.bdre.md.beans.table.PropertiesTemplate newPropertiesTemplate = new com.wipro.ats.bdre.md.beans.table.PropertiesTemplate();
                newPropertiesTemplate.setConfigGroup(jpaPropertiesTemplate.getConfigGroup());
                newPropertiesTemplate.setKey(jpaPropertiesTemplate.getId().getPropTempKey());
                newPropertiesTemplate.setDescription(jpaPropertiesTemplate.getDescription());
                newPropertiesTemplate.setValue(jpaPropertiesTemplate.getPropTempValue());
                newPropertiesTemplate.setProcessTemplateId(jpaPropertiesTemplate.getId().getProcessTemplateId());
                newPropertiesTemplate.setCounter(returningCriteria.list().size());
                propertiesTemplateList.add(newPropertiesTemplate);
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesTemplateList.get(0);

    }

    public PropertiesTemplateId insert(PropertiesTemplate propertiesTemplate) {
        Session session = sessionFactory.openSession();
        PropertiesTemplateId propertiesTemplateId = new PropertiesTemplateId();
        try {
            session.beginTransaction();
            propertiesTemplateId = (PropertiesTemplateId) session.save(propertiesTemplate);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesTemplateId;
    }

    public com.wipro.ats.bdre.md.beans.table.PropertiesTemplate updateProcessTemplate(com.wipro.ats.bdre.md.beans.table.PropertiesTemplate propertiesTemplate) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> propertiesTemplateList = new ArrayList<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate>();

        try {
            session.beginTransaction();
            Criteria updatingCriteria = session.createCriteria(PropertiesTemplate.class).add(Restrictions.eq(PROCESSTEMPLATEID, propertiesTemplate.getProcessTemplateId())).add(Restrictions.eq(PROPTEMPKEY, propertiesTemplate.getKey()));
            List<PropertiesTemplate> jpaPropertiesTemplateList = updatingCriteria.list();
            for (PropertiesTemplate jpaPropertiesTemplate : jpaPropertiesTemplateList) {
                jpaPropertiesTemplate.setDescription(propertiesTemplate.getDescription());
                jpaPropertiesTemplate.setConfigGroup(propertiesTemplate.getConfigGroup());
                jpaPropertiesTemplate.setPropTempValue(propertiesTemplate.getValue());
                session.update(jpaPropertiesTemplate);
                LOGGER.info("Updated properties Template:" + jpaPropertiesTemplate);
            }

            for (PropertiesTemplate jpaPropertiesTemplate : jpaPropertiesTemplateList) {
                com.wipro.ats.bdre.md.beans.table.PropertiesTemplate newPropertiesTemplate = new com.wipro.ats.bdre.md.beans.table.PropertiesTemplate();
                newPropertiesTemplate.setConfigGroup(jpaPropertiesTemplate.getConfigGroup());
                newPropertiesTemplate.setDescription(jpaPropertiesTemplate.getDescription());
                newPropertiesTemplate.setValue(jpaPropertiesTemplate.getPropTempValue());
                if (jpaPropertiesTemplate.getId() != null) {
                    newPropertiesTemplate.setProcessTemplateId(jpaPropertiesTemplate.getId().getProcessTemplateId());
                    newPropertiesTemplate.setKey(jpaPropertiesTemplate.getId().getPropTempKey());
                }
                propertiesTemplateList.add(newPropertiesTemplate);
            }

            session.getTransaction().commit();
            LOGGER.info(propertiesTemplateList.size());

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        if (!propertiesTemplateList.isEmpty()) {
            return propertiesTemplateList.get(0);
        } else return null;
    }

    public void update(PropertiesTemplate propertiesTemplate) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(propertiesTemplate);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void deletePropertiesTemplate(Integer processTemplateId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            Criteria deletingCriteria = session.createCriteria(PropertiesTemplate.class).add(Restrictions.eq(PROCESSTEMPLATEID, processTemplateId));
            List<PropertiesTemplate> propertiesTemplateList = deletingCriteria.list();
            LOGGER.info("delete Properties Template List size: " + propertiesTemplateList.size());
            for (PropertiesTemplate propertiesTemplate : propertiesTemplateList) {
                session.delete(propertiesTemplate);
                LOGGER.info("Config group of deleted Properties Template is" + propertiesTemplate.getConfigGroup());
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void deletePropertyTemplate(Integer processTemplateId, String key) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            Criteria deletingCriteria = session.createCriteria(PropertiesTemplate.class).add(Restrictions.eq(PROCESSTEMPLATEID, processTemplateId)).add(Restrictions.eq(PROPTEMPKEY, key));
            List<PropertiesTemplate> propertiesTemplateList = deletingCriteria.list();
            for (PropertiesTemplate propertiesTemplate : propertiesTemplateList) {
                session.delete(propertiesTemplate);
                LOGGER.info("Config group of deleted Properties Template is" + propertiesTemplate.getConfigGroup());
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(PropertiesTemplateId propertiesTemplateId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            PropertiesTemplate propertiesTemplate = (PropertiesTemplate) session.get(PropertiesTemplate.class, propertiesTemplateId);
            session.delete(propertiesTemplate);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
