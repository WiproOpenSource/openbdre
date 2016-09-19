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

import com.wipro.ats.bdre.md.beans.table.*;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.DeployStatus;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    BusDomainDAO busDomainDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Autowired
    DeployStatusDAO deployStatusDAO;
    @Autowired
    ProcessDeploymentQueueDAO processDeploymentQueueDAO;
    @Test
    public void testInitDeploy() throws Exception {
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(2);
        Process process = new Process();
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
        Integer id = processDAO.insert(process);
        DeployStatus deployStatus = deployStatusDAO.get((short) 1);
        ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
        processDeploymentQueue.setProcess(process);
        processDeploymentQueue.setBusDomain(busDomain);
        processDeploymentQueue.setDeployStatus(deployStatus);
        processDeploymentQueue.setInsertTs(new Date());
        processDeploymentQueue.setProcessType(processType);
        processDeploymentQueue.setUserName("Test");
        Long processDeploymentQueueId = processDeploymentQueueDAO.insert(processDeploymentQueue);
        deployDAO.initDeploy(processDeploymentQueueId);
        LOGGER.info("The init deploy test executed ");
        processDeploymentQueueDAO.delete(processDeploymentQueueId);
        processDAO.testDelete(id);
    }

    @Test
    public void testTermDeploy() throws Exception {
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(2);
        Process process = new Process();
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
        Integer id = processDAO.insert(process);
        DeployStatus deployStatus = deployStatusDAO.get((short) 2);
        ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
        processDeploymentQueue.setProcess(process);
        processDeploymentQueue.setBusDomain(busDomain);
        processDeploymentQueue.setDeployStatus(deployStatus);
        processDeploymentQueue.setInsertTs(new Date());
        processDeploymentQueue.setProcessType(processType);
        processDeploymentQueue.setUserName("Test");
        Long processDeploymentQueueId = processDeploymentQueueDAO.insert(processDeploymentQueue);
        deployDAO.termDeploy(processDeploymentQueueId);
        LOGGER.info("The term deploy test executed ");
        processDeploymentQueueDAO.delete(processDeploymentQueueId);
        processDAO.testDelete(id);
    }

    @Test
    public void testHaltDeploy() throws Exception {
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(2);
        Process process = new Process();
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
        Integer id = processDAO.insert(process);
        DeployStatus deployStatus = deployStatusDAO.get((short) 2);
        ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
        processDeploymentQueue.setProcess(process);
        processDeploymentQueue.setBusDomain(busDomain);
        processDeploymentQueue.setDeployStatus(deployStatus);
        processDeploymentQueue.setInsertTs(new Date());
        processDeploymentQueue.setProcessType(processType);
        processDeploymentQueue.setUserName("Test");
        Long processDeploymentQueueId = processDeploymentQueueDAO.insert(processDeploymentQueue);
        deployDAO.haltDeploy(processDeploymentQueueId);
        LOGGER.info("The halt deploy test executed ");
        processDeploymentQueueDAO.delete(processDeploymentQueueId);
        processDAO.testDelete(id);
    }
}