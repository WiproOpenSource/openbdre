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
import com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue;
import com.wipro.ats.bdre.md.dao.jpa.BatchStatus;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created by MR299389 on 10/16/2015.
 */
public class BatchConsumpQueueDAOTest {
    private static final Logger LOGGER = Logger.getLogger(BatchConsumpQueueDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    BatchConsumpQueueDAO batchConsumpQueueDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    BatchDAO batchDAO;
    @Autowired
    BatchStatusDAO batchStatusDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of BatchConsumpQueue is atleast:" + batchConsumpQueueDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of BatchConsumpQueue is:" + batchConsumpQueueDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        LOGGER.info("BatchMarking of batchConsumpQueue(2) is:" + batchConsumpQueueDAO.get((long) 2).getBatchMarking());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        com.wipro.ats.bdre.md.dao.jpa.Process process = processDAO.get(10835);
        Batch batch = batchDAO.get((long) 0);
        BatchStatus batchStatus = batchStatusDAO.get(0);
        BatchConsumpQueue batchConsumpQueue = new BatchConsumpQueue();
        batchConsumpQueue.setProcess(process);
        batchConsumpQueue.setBatchBySourceBatchId(batch);
        batchConsumpQueue.setBatchStatus(batchStatus);
        batchConsumpQueue.setInsertTs(new Date());
        Long id = batchConsumpQueueDAO.insert(batchConsumpQueue);
        LOGGER.info("batchConsumpQueue is added with Id:" + id);
        batchConsumpQueue.setBatchMarking("Test BatchMarking");
        batchConsumpQueueDAO.update(batchConsumpQueue);
        batchConsumpQueue = batchConsumpQueueDAO.get(id);
        LOGGER.info("Updated BatchMarking is:" + batchConsumpQueue.getBatchMarking());
        batchConsumpQueueDAO.delete(id);
        LOGGER.info("Deleted batchConsumpQueue Entry with ID:" + id);
    }
}
