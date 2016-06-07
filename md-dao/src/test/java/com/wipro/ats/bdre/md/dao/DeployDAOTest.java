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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


import java.util.Date;

public class DeployDAOTest {
    private static final Logger LOGGER = Logger.getLogger(DeployDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    DeployDAO deployDAO;
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
    public void testInitDeploy() throws Exception {
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(2);
        com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
        process.setProcessName("Test");
        process.setDescription("Test Process");
        process.setBusDomain(busDomain);
        process.setProcessType(processType);
        process.setAddTs(new Date());
        process.setCanRecover(true);
        process.setEnqueuingProcessId(0);
        process.setNextProcessId("10802");
        process.setDeleteFlag(false);
        process.setEditTs(new Date());
        Integer pid = processDAO.insert(process);
        DeployStatus deployStatus = deployStatusDAO.get((short) 5);
        ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
        processDeploymentQueue.setProcess(process);
        processDeploymentQueue.setBusDomain(busDomain);
        processDeploymentQueue.setDeployStatus(deployStatus);
        processDeploymentQueue.setInsertTs(new Date());
        processDeploymentQueue.setProcessType(processType);
        processDeploymentQueue.setUserName("Test");
        Long processDeploymentQueueId = processDeploymentQueueDAO.insert(processDeploymentQueue);
        LOGGER.info("New ProcessDeploymentQueue added with ID:" + processDeploymentQueueId);
        processDeploymentQueue = processDeploymentQueueDAO.get(processDeploymentQueueId);
        deployDAO.initDeploy((long)processDeploymentQueueId);
        processDeploymentQueueDAO.delete(processDeploymentQueueId);
        processDAO.delete(pid);
        LOGGER.info("Process Deleted with ID:" + pid);
        LOGGER.info("ProcessDeploymentQueue Deleted with ID:" + processDeploymentQueueId);
        LOGGER.info("The init deploy test executed ");
    }

    @Test
    public void testTermDeploy() throws Exception {
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(1);
        com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
        process.setProcessName("Test");
        process.setDescription("Test Process");
        process.setBusDomain(busDomain);
        process.setProcessType(processType);
        process.setAddTs(new Date());
        process.setCanRecover(true);
        process.setEnqueuingProcessId(0);
        process.setNextProcessId("10802");
        process.setDeleteFlag(false);
        process.setEditTs(new Date());
        Integer pid = processDAO.insert(process);
        DeployStatus deployStatus = deployStatusDAO.get((short) 2);
        ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
        processDeploymentQueue.setProcess(process);
        processDeploymentQueue.setBusDomain(busDomain);
        processDeploymentQueue.setDeployStatus(deployStatus);
        processDeploymentQueue.setInsertTs(new Date());
        processDeploymentQueue.setProcessType(processType);
        processDeploymentQueue.setUserName("Test");
        Long processDeploymentQueueId = processDeploymentQueueDAO.insert(processDeploymentQueue);
        LOGGER.info("New ProcessDeploymentQueue added with ID:" + processDeploymentQueueId);
        processDeploymentQueue = processDeploymentQueueDAO.get(processDeploymentQueueId);
        deployDAO.termDeploy((long)processDeploymentQueueId);
        processDeploymentQueueDAO.delete(processDeploymentQueueId);
        processDAO.delete(pid);
        LOGGER.info("Process Deleted with ID:" + pid);
        LOGGER.info("ProcessDeploymentQueue Deleted with ID:" + processDeploymentQueueId);
        LOGGER.info("The term deploy test executed ");
    }

    @Test
    public void testHaltDeploy() throws Exception {
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(1);
        com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
        process.setProcessName("Test");
        process.setDescription("Test Process");
        process.setBusDomain(busDomain);
        process.setProcessType(processType);
        process.setAddTs(new Date());
        process.setCanRecover(true);
        process.setEnqueuingProcessId(0);
        process.setNextProcessId("10802");
        process.setDeleteFlag(false);
        process.setEditTs(new Date());
        Integer pid = processDAO.insert(process);
        DeployStatus deployStatus = deployStatusDAO.get((short) 2);
        ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
        processDeploymentQueue.setProcess(process);
        processDeploymentQueue.setBusDomain(busDomain);
        processDeploymentQueue.setDeployStatus(deployStatus);
        processDeploymentQueue.setInsertTs(new Date());
        processDeploymentQueue.setProcessType(processType);
        processDeploymentQueue.setUserName("Test");
        Long processDeploymentQueueId = processDeploymentQueueDAO.insert(processDeploymentQueue);
        LOGGER.info("New ProcessDeploymentQueue added with ID:" + processDeploymentQueueId);
        processDeploymentQueue = processDeploymentQueueDAO.get(processDeploymentQueueId);
        deployDAO.haltDeploy((long) processDeploymentQueueId);
        processDeploymentQueueDAO.delete(processDeploymentQueueId);
        processDAO.delete(pid);
        LOGGER.info("Process Deleted with ID:" + pid);
        LOGGER.info("ProcessDeploymentQueue Deleted with ID:" + processDeploymentQueueId);
        LOGGER.info("The halt deploy test executed ");
    }
}