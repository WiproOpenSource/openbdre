/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.beans.GetHiveTablesInfo;
import com.wipro.ats.bdre.md.dao.jpa.EtlDriver;
import com.wipro.ats.bdre.md.dao.jpa.HiveTables;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by PR324290 on 10/29/2015.
 */
public class ETLDriverDAOTest {

    private static final Logger LOGGER = Logger.getLogger(ETLDriverDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ETLDriverDAO etlDriverDAO;
    @Autowired
    HiveTablesDAO hiveTablesDAO;
    @Autowired
    ProcessDAO processDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of EtlDriver list is atleast:" + etlDriverDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Total EtlDriver is:" + etlDriverDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        LOGGER.info("EtlDriver(10802) DropRaw:" + etlDriverDAO.get(10802).getDropRaw());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        EtlDriver etlDriver = new EtlDriver();
        etlDriver.setEtlProcessId(10805);
        HiveTables hiveTables = new HiveTables();
        hiveTables.setComments("comment");
        hiveTables.setLocationType("L_type0");
        hiveTables.setTableName("TestTable");
        hiveTables.setType("type0");
        hiveTables.setDdl("ddl");
        Integer hiveTablesId = hiveTablesDAO.insert(hiveTables);
        etlDriver.setHiveTablesByRawTableId(hiveTables);
        etlDriver.setHiveTablesByRawViewId(hiveTables);
        etlDriver.setHiveTablesByBaseTableId(hiveTables);
        etlDriver.setProcess(processDAO.get(10805));
        etlDriver.setDropRaw(true);
        Integer id = etlDriverDAO.insert(etlDriver);
        List<GetHiveTablesInfo> hiveTablesList = etlDriverDAO.getETLDriverTables(10805);
        for (GetHiveTablesInfo hiveTable : hiveTablesList) {
            LOGGER.info(hiveTable);
        }
        LOGGER.info("EtlDriver is added with Id:" + id);
        etlDriver.setDropRaw(false);
        etlDriverDAO.update(etlDriver);
        etlDriver = etlDriverDAO.get(id);
        LOGGER.info("Updated EtlDriver DropRaw is:" + etlDriver.getDropRaw());
        etlDriverDAO.delete(id);
        hiveTablesDAO.delete(hiveTablesId);
        LOGGER.info("Deleted etlDriver with ID:" + id);
    }


    @Test
    public void testGetETLDriverTables() throws Exception {
        try {
            List<GetHiveTablesInfo> hiveTablesList = etlDriverDAO.getETLDriverTables(10849);
            for (GetHiveTablesInfo hiveTables : hiveTablesList) {
                LOGGER.info(hiveTables);
            }
        } catch (Exception e) {
            LOGGER.info("Error occured " + e);
        }
    }
}


