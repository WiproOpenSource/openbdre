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
import com.wipro.ats.bdre.md.beans.PositionsInfo;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MR299389 on 10/28/2015.
 */
@Transactional
@Service
public class PropertiesDAO {
    private static final Logger LOGGER = Logger.getLogger(PropertiesDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    public List<Integer> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Properties.class);

//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.groupProperty("process"));
//        criteria.setProjection(projectionList);
//        criteria.setResultTransformer(Transformers.aliasToBean(Properties.class));
        LOGGER.info("number of entries in properties table" + criteria.list().size());
        criteria.setProjection(Projections.distinct(Projections.property("id.processId")));
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Integer> listOfProcessIDs = criteria.list();
        session.getTransaction().commit();
        session.close();
        return listOfProcessIDs;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria totalRecord = session.createCriteria(Properties.class);

//        ProjectionList projectionList = Projections.projectionList();
//        projectionList.add(Projections.groupProperty("process"));
//        totalRecord.setProjection(projectionList);
//        totalRecord.setResultTransformer(Transformers.aliasToBean(Properties.class));
        totalRecord.setProjection(Projections.distinct(Projections.property("id.processId")));
        int size = totalRecord.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public Properties get(PropertiesId propertiesId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Properties properties = (Properties) session.get(Properties.class, propertiesId);
        session.getTransaction().commit();
        session.close();
        return properties;
    }

    public PropertiesId insert(Properties properties) {
        Session session = sessionFactory.openSession();
        PropertiesId propertiesId = null;
        try {
            session.beginTransaction();
            Process updateProcess=new Process();

            propertiesId = (PropertiesId) session.save(properties);
            updateProcess=(Process)session.get(Process.class,properties.getId().getProcessId());
            LOGGER.info("process add ts"+updateProcess.getAddTs());
            session.update(properties);
            updateProcess.setEditTs(new Date());
            session.update(updateProcess);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return propertiesId;
    }

    public void update(Properties properties) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Process updateProcess=new Process();

            updateProcess=(Process)session.get(Process.class,properties.getId().getProcessId());
            LOGGER.info("process add ts"+updateProcess.getAddTs());
            session.update(properties);
            updateProcess.setEditTs(new Date());
            session.update(updateProcess);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public void delete(PropertiesId propertiesId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Properties properties = (Properties) session.get(Properties.class, propertiesId);
            session.delete(properties);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<Properties> getPropertiesForConfig(int processId, String configGroup) {
        List<Properties> propertiesList = new ArrayList<Properties>();
        Session session = sessionFactory.openSession();
        try {

            session.beginTransaction();
            Criteria cr = session.createCriteria(Properties.class).add(Restrictions.eq("process.processId", processId)).add(Restrictions.eq("configGroup", configGroup));
            propertiesList = cr.list();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            LOGGER.info("Error " + e);
            return null;
        } finally {
            session.close();
        }
        return propertiesList;
    }

    public void deleteByProcessId(com.wipro.ats.bdre.md.dao.jpa.Process process) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            LOGGER.info("deleting properties");
            Criteria propertiesByProcessId = session.createCriteria(Properties.class).add(Restrictions.eq("process", process));
            List<Properties> propertiesList = propertiesByProcessId.list();
            for (Properties properties : propertiesList) {
                session.delete(properties);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<Properties> getByProcessId(com.wipro.ats.bdre.md.dao.jpa.Process process) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria propertiesByProcessId = session.createCriteria(Properties.class).add(Restrictions.eq("process", process));
        List<Properties> propertiesList = propertiesByProcessId.list();
        session.getTransaction().commit();
        session.close();
        return propertiesList;
    }

    public void updateArrangePositions(Integer parentPid, List<PositionsInfo> positionsInfoList) {
        Session session = sessionFactory.openSession();
        try {
            java.util.Date date = new java.util.Date();

            LOGGER.info("start time" + date);
            session.beginTransaction();
            Integer parentProcessId = positionsInfoList.get(0).getProcessId();
            String deletePropsQuery = "delete from Properties props where props.process.processId in (select processId from Process where process.processId= :pid or processId= :pid) " +
                    "and props.configGroup = 'position'";
            Query query = session.createQuery(deletePropsQuery);
            query.setParameter("pid", parentPid);
            int result = query.executeUpdate();
            LOGGER.info("Rows affected: " + result);

            Criteria fetchProcessIdList = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.or(Restrictions.eq("processId", parentProcessId), Restrictions.eq("process.processId", parentProcessId))).setProjection(Projections.property("processId"));
            List<Integer> pidList = fetchProcessIdList.list();
            if (fetchProcessIdList.list().size() != 0) {
                //Inserting new Positions
                for (PositionsInfo positionsInfo : positionsInfoList) {
                    com.wipro.ats.bdre.md.dao.jpa.Properties propertiesX = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                    com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
                    process.setProcessId(positionsInfo.getProcessId());
                    PropertiesId propertiesIdForX = new PropertiesId();
                    propertiesIdForX.setProcessId(positionsInfo.getProcessId());
                    propertiesIdForX.setPropKey("x");
                    propertiesX.setProcess(process);
                    propertiesX.setConfigGroup("position");
                    propertiesX.setPropValue(positionsInfo.getxPos().toString());
                    propertiesX.setDescription("xposition");
                    propertiesX.setId(propertiesIdForX);
                    session.save(propertiesX);


                    LOGGER.info("Property inserted:" + propertiesX.getId().getPropKey());

                    com.wipro.ats.bdre.md.dao.jpa.Properties propertiesY = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                    PropertiesId propertiesIdForY = new PropertiesId();

                    propertiesIdForY.setProcessId(positionsInfo.getProcessId());
                    propertiesIdForY.setPropKey("y");

                    propertiesY.setConfigGroup("position");
                    propertiesY.setPropValue(positionsInfo.getyPos().toString());
                    propertiesY.setDescription("yposition");
                    propertiesY.setId(propertiesIdForY);
                    propertiesY.setProcess(process);
                    session.save(propertiesY);

                    LOGGER.info("Property inserted:" + propertiesY.getId().getPropKey());

                }


            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            java.util.Date date = new java.util.Date();
            LOGGER.info("closing time" + date);
            session.close();
        }
    }

}
