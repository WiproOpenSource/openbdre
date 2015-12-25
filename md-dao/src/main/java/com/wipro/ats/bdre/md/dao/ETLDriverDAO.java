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
import com.wipro.ats.bdre.md.beans.GetHiveTablesInfo;
import com.wipro.ats.bdre.md.dao.jpa.EtlDriver;
import com.wipro.ats.bdre.md.dao.jpa.HiveTables;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PR324290 on 10/29/2015.
 */
@Transactional
@Service
public class ETLDriverDAO {
    private static final Logger LOGGER = Logger.getLogger(ETLDriverDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<EtlDriver> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(EtlDriver.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<EtlDriver> etlDrivers = criteria.list();
        session.getTransaction().commit();
        session.close();
        return etlDrivers;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(EtlDriver.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public EtlDriver get(Integer id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        EtlDriver etlDriver = (EtlDriver) session.get(EtlDriver.class, id);
        session.getTransaction().commit();
        session.close();
        return etlDriver;
    }

    public Integer insert(EtlDriver etlDriver) {
        Session session = sessionFactory.openSession();
        Integer id = null;
        try {
            session.beginTransaction();
            id = (Integer) session.save(etlDriver);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public void update(EtlDriver etlDriver) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(etlDriver);
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
            EtlDriver etlDriver = (EtlDriver) session.get(EtlDriver.class, id);
            session.delete(etlDriver);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


    public List<com.wipro.ats.bdre.md.beans.GetHiveTablesInfo> getETLDriverTables(int processId) {
        List<com.wipro.ats.bdre.md.beans.GetHiveTablesInfo> returnHiveTablesList = new ArrayList<GetHiveTablesInfo>();
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            Criteria checkValidProcessCriteria = session.createCriteria(EtlDriver.class).add(Restrictions.eq("etlProcessId", processId));
            Integer eTLProcessCount = checkValidProcessCriteria.list().size();
            if (eTLProcessCount == 0) {
                LOGGER.info("Invalid process id");
                throw new MetadataException("Invalid process id");


            } else {
                List<EtlDriver> etlDriverList = checkValidProcessCriteria.list();

                List<Integer> tableIdList = new ArrayList<Integer>();
                tableIdList.add(etlDriverList.get(0).getHiveTablesByBaseTableId().getTableId());
                tableIdList.add(etlDriverList.get(0).getHiveTablesByRawTableId().getTableId());
                tableIdList.add(etlDriverList.get(0).getHiveTablesByRawViewId().getTableId());
                Criteria fetchHiveTablesCriteria = session.createCriteria(HiveTables.class).add(Restrictions.in("tableId", tableIdList)).addOrder(Order.asc("tableId")).addOrder(Order.asc("etlDriversForRawViewId")).addOrder(Order.asc("etlDriversForRawTableId")).addOrder(Order.asc("etlDriversForBaseTableId"));
                Integer hiveTablesCount = fetchHiveTablesCriteria.list().size();
                if (hiveTablesCount != 0) {
                    List<HiveTables> hiveTablesList = fetchHiveTablesCriteria.list();
                    for (HiveTables hiveTables : hiveTablesList) {
                        com.wipro.ats.bdre.md.beans.GetHiveTablesInfo returnHiveTable = new com.wipro.ats.bdre.md.beans.GetHiveTablesInfo();
                        returnHiveTable.setTableId(hiveTables.getTableId());
                        returnHiveTable.setComments(hiveTables.getComments());
                        returnHiveTable.setLocationType(hiveTables.getLocationType());
                        returnHiveTable.setDbName(hiveTables.getDbname());
                        returnHiveTable.setBatchIdPartitionCol(hiveTables.getBatchIdPartitionCol());
                        returnHiveTable.setTableName(hiveTables.getTableName());
                        returnHiveTable.setType(hiveTables.getType());
                        returnHiveTable.setDdl(hiveTables.getDdl());
                        returnHiveTablesList.add(returnHiveTable);
                    }

                }

            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
        } finally {
            session.close();
        }

        return returnHiveTablesList;
    }
}
