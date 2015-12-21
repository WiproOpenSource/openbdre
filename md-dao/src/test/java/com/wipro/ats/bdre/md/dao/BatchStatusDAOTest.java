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

import com.wipro.ats.bdre.md.dao.jpa.BatchStatus;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by MR299389 on 10/15/2015.
 */
public class BatchStatusDAOTest {
    private static final Logger LOGGER = Logger.getLogger(BatchStatusDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    BatchStatusDAO batchStatusDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of BatchStatus is atleast:" + batchStatusDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of BatchStatus is:" + batchStatusDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("Description of batchStatusId(1) is:" + batchStatusDAO.get(7777).getDescription());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        BatchStatus batchStatus = new BatchStatus();
        batchStatus.setDescription("Test");
        batchStatus.setBatchStateId(3);
        Integer batchStatusId = batchStatusDAO.insert(batchStatus);
        LOGGER.info("BatchStatus is added with Id:" + batchStatusId);
        batchStatus.setDescription("Test BatchSatus");
        batchStatusDAO.update(batchStatus);
        batchStatus = batchStatusDAO.get(batchStatusId);
        LOGGER.info("Updated Description is:" + batchStatus.getDescription());
        batchStatusDAO.delete(batchStatusId);
        LOGGER.info("Deleted BatchStatus Entry with ID" + batchStatusId);
    }
}
