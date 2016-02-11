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
import com.wipro.ats.bdre.md.dao.jpa.GeneralConfig;
import com.wipro.ats.bdre.md.dao.jpa.GeneralConfigId;
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
public class GeneralConfigDAO {
    private static final Logger LOGGER = Logger.getLogger(GeneralConfigDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<GeneralConfig> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(GeneralConfig.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<GeneralConfig> generalConfigs = criteria.list();
        session.getTransaction().commit();
        session.close();
        return generalConfigs;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(GeneralConfig.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public GeneralConfig get(GeneralConfigId generalConfigId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        GeneralConfig generalConfig = (GeneralConfig) session.get(GeneralConfig.class, generalConfigId);
        session.getTransaction().commit();
        session.close();
        return generalConfig;
    }


    public GeneralConfigId insert(GeneralConfig generalConfig) {
        Session session = sessionFactory.openSession();
        GeneralConfigId generalConfigId = new GeneralConfigId();
        try {
            session.beginTransaction();
            generalConfigId = (GeneralConfigId) session.save(generalConfig);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return generalConfigId;

    }

    public void update(GeneralConfig generalConfig) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(generalConfig);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(GeneralConfigId generalConfigId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            GeneralConfig generalConfig = (GeneralConfig) session.get(GeneralConfig.class, generalConfigId);
            session.delete(generalConfig);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<com.wipro.ats.bdre.md.beans.table.GeneralConfig> getGeneralConfig(String configGroup, int required) {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.GeneralConfig> generalConfigList = new ArrayList<com.wipro.ats.bdre.md.beans.table.GeneralConfig>();

        try {
            session.beginTransaction();

            if (required == 2) {
                Criteria getGeneralConfigCriteria = session.createCriteria(GeneralConfig.class).add(Restrictions.eq("id.configGroup", configGroup)).add(Restrictions.eq("enabled", true));
                List<GeneralConfig> jpaGeneralConfigList = getGeneralConfigCriteria.list();
                for (GeneralConfig jpaGeneralConfig : jpaGeneralConfigList) {
                    com.wipro.ats.bdre.md.beans.table.GeneralConfig generalConfig = new com.wipro.ats.bdre.md.beans.table.GeneralConfig();
                    generalConfig.setCounter(jpaGeneralConfigList.size());
                    if (jpaGeneralConfig.getRequired() == true)
                        generalConfig.setRequired(1);
                    else
                        generalConfig.setRequired(0);
                    generalConfig.setConfigGroup(jpaGeneralConfig.getId().getConfigGroup());
                    generalConfig.setKey(jpaGeneralConfig.getId().getGcKey());
                    generalConfig.setValue(jpaGeneralConfig.getGcValue());
                    generalConfig.setDescription(jpaGeneralConfig.getDescription());
                    generalConfig.setDefaultVal(jpaGeneralConfig.getDefaultVal());
                    generalConfig.setEnabled(jpaGeneralConfig.getEnabled());
                    generalConfig.setType(jpaGeneralConfig.getType());

                    generalConfigList.add(generalConfig);
                }
            } else {
                // only difference in both criteria is that requiredRestriction is added in this one.
                boolean req = (required == 1) ? true : false;
                Criteria getGeneralConfigCriteria = session.createCriteria(GeneralConfig.class).add(Restrictions.eq("id.configGroup", configGroup)).add(Restrictions.eq("enabled", true)).add(Restrictions.eq("required", req));
                List<GeneralConfig> jpaGeneralConfigList = getGeneralConfigCriteria.list();
                for (GeneralConfig jpaGeneralConfig : jpaGeneralConfigList) {
                    com.wipro.ats.bdre.md.beans.table.GeneralConfig generalConfig = new com.wipro.ats.bdre.md.beans.table.GeneralConfig();
                    generalConfig.setCounter(jpaGeneralConfigList.size());
                    if (jpaGeneralConfig.getRequired())
                        generalConfig.setRequired(1);
                    else
                        generalConfig.setRequired(0);
                    generalConfig.setConfigGroup(jpaGeneralConfig.getId().getConfigGroup());
                    generalConfig.setKey(jpaGeneralConfig.getId().getGcKey());
                    generalConfig.setValue(jpaGeneralConfig.getGcValue());
                    generalConfig.setDescription(jpaGeneralConfig.getDescription());
                    generalConfig.setDefaultVal(jpaGeneralConfig.getDefaultVal());
                    generalConfig.setEnabled(jpaGeneralConfig.getEnabled());
                    generalConfig.setType(jpaGeneralConfig.getType());

                    generalConfigList.add(generalConfig);
                }

            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return generalConfigList;
    }

    public com.wipro.ats.bdre.md.beans.table.GeneralConfig GetGenConfigProperty(String configGroup, String key) {
        Session session = sessionFactory.openSession();
        com.wipro.ats.bdre.md.beans.table.GeneralConfig generalConfig = new com.wipro.ats.bdre.md.beans.table.GeneralConfig();

        try {
            session.beginTransaction();

            Criteria getGenConfigPropertyCriteria = session.createCriteria(GeneralConfig.class).add(Restrictions.eq("id.configGroup", configGroup)).add(Restrictions.eq("id.gcKey", key));
            List<GeneralConfig> jpaGeneralConfigList = getGenConfigPropertyCriteria.list();
            if (jpaGeneralConfigList == null)
                return null;
            GeneralConfig jpaGeneralConfig = jpaGeneralConfigList.get(0);

            generalConfig.setCounter(jpaGeneralConfigList.size());
            if (jpaGeneralConfig.getRequired())
                generalConfig.setRequired(1);
            else
                generalConfig.setRequired(0);
            generalConfig.setConfigGroup(jpaGeneralConfig.getId().getConfigGroup());
            generalConfig.setKey(jpaGeneralConfig.getId().getGcKey());
            generalConfig.setValue(jpaGeneralConfig.getGcValue());
            generalConfig.setDescription(jpaGeneralConfig.getDescription());
            generalConfig.setDefaultVal(jpaGeneralConfig.getDefaultVal());
            generalConfig.setEnabled(jpaGeneralConfig.getEnabled());
            generalConfig.setType(jpaGeneralConfig.getType());

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return generalConfig;

    }


}
