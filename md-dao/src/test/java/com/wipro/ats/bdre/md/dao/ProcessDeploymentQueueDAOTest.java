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

import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
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
 * Created by PR324290 on 10/28/2015.
 */
public class ProcessDeploymentQueueDAOTest {


    private static final Logger LOGGER = Logger.getLogger(ProcessDeploymentQueueDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ProcessDeploymentQueueDAO processDeploymentQueueDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    BusDomainDAO busDomainDAO;
    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Autowired
    DeployStatusDAO deployStatusDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of ProcessDeploymentQueue is atleast:" + processDeploymentQueueDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of ProcessDeploymentQueue is:" + processDeploymentQueueDAO.totalRecordCount());
    }


    @Test
    @Ignore
    public void testGet() throws Exception {
        LOGGER.info("ProcessDeploymentQueue(0) username :" + processDeploymentQueueDAO.get((long) 0).getUserName());

    }

    @Test
    @Ignore
    public void testInsertProcessDeploymentQueue() throws Exception {
        LOGGER.info("ProcessDeploymentQueue(0)inserted :" + processDeploymentQueueDAO.insertProcessDeploymentQueue(1, "admin"));

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        Process process = processDAO.get(10802);
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(2);
        DeployStatus deployStatus = deployStatusDAO.get((short) 1);
        ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
        processDeploymentQueue.setProcess(process);
        processDeploymentQueue.setBusDomain(busDomain);
        processDeploymentQueue.setDeployStatus(deployStatus);
        processDeploymentQueue.setInsertTs(new Date());
        processDeploymentQueue.setProcessType(processType);
        processDeploymentQueue.setUserName("Test");
        Long processDeploymentQueueId = processDeploymentQueueDAO.insert(processDeploymentQueue);
        LOGGER.info("New ProcessDeploymentQueue added with ID:" + processDeploymentQueueId);
        processDeploymentQueue.setUserName("Test_update");
        processDeploymentQueueDAO.update(processDeploymentQueue);
        processDeploymentQueue = processDeploymentQueueDAO.get(processDeploymentQueueId);
        LOGGER.info("Updated processDeploymentQueue with user name:" + processDeploymentQueue.getUserName());
        processDeploymentQueueDAO.delete(processDeploymentQueueId);
        LOGGER.info("ProcessDeploymentQueue Deleted with ID:" + processDeploymentQueueId);
    }
}
