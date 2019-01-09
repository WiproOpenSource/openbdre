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

import com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
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
 * Created by MR299389 on 10/15/2015.
 */
public class ArchiveConsumpQueueDAOTest {
    private static final Logger LOGGER = Logger.getLogger(ArchiveConsumpQueueDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ArchiveConsumpQueueDAO archiveConsumpQueueDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    BatchDAO batchDAO;
    @Autowired
    BatchStatusDAO batchStatusDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of ArchiveConsumpQueue is atleast: " + archiveConsumpQueueDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of ArchiveConsumpQueue is: " + archiveConsumpQueueDAO.totalRecordCount());
    }


    @Test
    @Ignore

    public void testGet() throws Exception {
        LOGGER.info("BatchMarking of ArchiveConsumpQueue(2) is: " + archiveConsumpQueueDAO.get((long) 2).getBatchMarking());
    }

    @Test
    @Ignore
    public void testInsertUpdateAndDelete() throws Exception {
        com.wipro.ats.bdre.md.dao.jpa.Process process = processDAO.get(10835);
        Batch batch = batchDAO.get((long) 0);
        BatchStatus batchStatus = batchStatusDAO.get(0);
        ArchiveConsumpQueue archiveConsumpQueue = new ArchiveConsumpQueue();

        archiveConsumpQueue.setBatchBySourceBatchId(batch);
        archiveConsumpQueue.setBatchStatus(batchStatus);
        archiveConsumpQueue.setProcess(process);
        archiveConsumpQueue.setInsertTs(new Date());
        Long id = archiveConsumpQueueDAO.insert(archiveConsumpQueue);
        LOGGER.info("New ArchiveConsumpQueue added with id: " + id);
        archiveConsumpQueue.setBatchMarking("Test BatchMarking");
        archiveConsumpQueueDAO.update(archiveConsumpQueue);
        archiveConsumpQueue = archiveConsumpQueueDAO.get(id);
        LOGGER.info("Updated BatchMarking is:" + archiveConsumpQueue.getBatchMarking());
        archiveConsumpQueueDAO.delete(id);
        LOGGER.info("Deleted ArchiveConsumpQueue Entry with ID:" + id);
    }
}
