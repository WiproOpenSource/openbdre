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

import com.wipro.ats.bdre.md.dao.jpa.HiveTables;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by MR299389 on 10/16/2015.
 */
public class HiveTablesDAOTest {
    private static final Logger LOGGER = Logger.getLogger(HiveTablesDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    HiveTablesDAO hiveTablesDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of hiveTables is atleast:" + hiveTablesDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of hiveTables is:" + hiveTablesDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        LOGGER.info("Name of hiveTables(2) is:" + hiveTablesDAO.get(2).getTableName());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        HiveTables hiveTables = new HiveTables();
        hiveTables.setComments("comment");
        hiveTables.setLocationType("L_type0");
        hiveTables.setTableName("TestTable");
        hiveTables.setType("type0");
        hiveTables.setDdl("ddl");
        Integer id = hiveTablesDAO.insert(hiveTables);
        LOGGER.info("HiveTables entry is added with Id:" + id);
        hiveTables.setComments("Test Comment");
        hiveTablesDAO.update(hiveTables);
        hiveTables = hiveTablesDAO.get(id);
        LOGGER.info("Updated Comment is:" + hiveTables.getComments());
        hiveTablesDAO.delete(id);
        LOGGER.info("Deleted hiveTables Entry with ID:" + id);
    }
}
