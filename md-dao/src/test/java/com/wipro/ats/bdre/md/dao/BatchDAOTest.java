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

import com.wipro.ats.bdre.md.dao.jpa.Batch;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by MR299389 on 10/15/2015.
 */
public class BatchDAOTest {
    private static final Logger LOGGER = Logger.getLogger(BatchDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    BatchDAO batchDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of Batch is atleast:" + batchDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of Batch is:" + batchDAO.totalRecordCount());
    }

    @Test
    public void testGet() throws Exception {
        LOGGER.info("Batch(0) type:" + batchDAO.get(Long.valueOf(0)).getBatchType());
    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        Batch batch = new Batch();
        batch.setBatchType("Test");
        Long batchId = batchDAO.insert(batch);
        LOGGER.info("New Batch added with ID:" + batchId);
        batch.setBatchType("Test Update");
        batchDAO.update(batch);
        batch = batchDAO.get(batchId);
        LOGGER.info("Updated batch with type:" + batch.getBatchType());
        batchDAO.delete(batchId);
        LOGGER.info("Batch Deleted with ID:" + batchId);
    }
}
