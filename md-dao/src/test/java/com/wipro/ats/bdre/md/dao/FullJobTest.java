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
 *//*


package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

*/
/**
 * Created by PR324290 on 12/17/2015.
 *//*

public class FullJobTest {
    private static final Logger LOGGER = Logger.getLogger(FullJobTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ProcessDAO processDAO;
    @Autowired
    BusDomainDAO busDomainDAO;
    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Autowired
    WorkflowTypeDAO workflowTypeDAO;
    @Autowired
    PropertiesDAO propertiesDAO;
    @Autowired
    BatchDAO batchDAO;
    @Autowired
    InstanceExecDAO instanceExecDAO;
    @Autowired
    JobDAO jobDAO;
    @Autowired
    StepDAO stepDAO;
    @Autowired
    BatchConsumpQueueDAO batchConsumpQueueDAO;


    @Test
    public void completeJobTest() throws Exception {

        Integer busDomainId = null;
        PropertiesId propertiesId = null;
        Integer firstParentProcessId = null;
        Integer firstChildOfParentProcess1Id = null;
        Integer secondChildOfParentProcess1Id = null;
        Integer secondParentProcessId = null;
        Integer firstChildOfParentProcess2Id = null;
        Integer secondChildOfParentProcess2Id = null;
        Integer flag = 0;
    try{
        BusDomain busDomain = new BusDomain();
        busDomain.setBusDomainName("testName");
        busDomain.setDescription("testDescription");
        busDomain.setBusDomainOwner("testOwner");

        //inserting new BusDomain
         busDomainId = busDomainDAO.insert(busDomain);
        //updating inserted BusDomain
        busDomain.setDescription("updateDescription");
        busDomainDAO.update(busDomain);
        //fetching updated busDomain
        BusDomain updatedBusDomain = busDomainDAO.get(busDomainId);
        assertEquals("busDomain update failed", "updateDescription", updatedBusDomain.getDescription());


        ProcessType parentProcessType = processTypeDAO.get(1);
        ProcessType childProcessType = processTypeDAO.get(12);

        WorkflowType parentWorkflowType = workflowTypeDAO.get(0);
        WorkflowType childWorkflowType = workflowTypeDAO.get(1);

        Process firstParentProcess = new Process();
        firstParentProcess.setProcessName("Test");
        firstParentProcess.setDescription("Test Process");
        firstParentProcess.setBusDomain(busDomain);
        firstParentProcess.setProcessType(parentProcessType);
        firstParentProcess.setAddTs(new Date());
        firstParentProcess.setCanRecover(true);
        firstParentProcess.setEnqueuingProcessId("0");
        firstParentProcess.setProcess(null);
        firstParentProcess.setNextProcessId("1");
        firstParentProcess.setDeleteFlag(false);
        firstParentProcess.setEditTs(new Date());
        firstParentProcess.setWorkflowType(parentWorkflowType);
        //inserting parent process1
         firstParentProcessId = processDAO.insert(firstParentProcess);


        Process firstChildOfParentProcess1 = new Process();
        firstChildOfParentProcess1.setProcessName("Test");
        firstChildOfParentProcess1.setDescription("Test Process");
        firstChildOfParentProcess1.setBusDomain(busDomain);
        firstChildOfParentProcess1.setProcessType(childProcessType);
        firstChildOfParentProcess1.setAddTs(new Date());
        firstChildOfParentProcess1.setCanRecover(true);
        firstChildOfParentProcess1.setEnqueuingProcessId("0");
        firstChildOfParentProcess1.setProcess(firstParentProcess);
        firstChildOfParentProcess1.setNextProcessId(firstParentProcessId.toString());
        firstChildOfParentProcess1.setDeleteFlag(false);
        firstChildOfParentProcess1.setEditTs(new Date());
        firstChildOfParentProcess1.setWorkflowType(childWorkflowType);
        //inserting sub process1
         firstChildOfParentProcess1Id = processDAO.insert(firstChildOfParentProcess1);
        //fetching process
        LOGGER.info("sub pid " + firstChildOfParentProcess1Id);
        Process updatedProcess = processDAO.get(firstChildOfParentProcess1Id);
        assertNotNull("editTs not null", firstChildOfParentProcess1.getEditTs());
        assertFalse(updatedProcess.getDeleteFlag());


        Process secondChildOfParentProcess1 = new Process();
        secondChildOfParentProcess1.setProcessName("Test");
        secondChildOfParentProcess1.setDescription("Test Process");
        secondChildOfParentProcess1.setBusDomain(busDomain);
        secondChildOfParentProcess1.setProcessType(childProcessType);
        secondChildOfParentProcess1.setAddTs(new Date());
        secondChildOfParentProcess1.setCanRecover(true);
        secondChildOfParentProcess1.setEnqueuingProcessId("0");
        secondChildOfParentProcess1.setProcess(firstParentProcess);
        secondChildOfParentProcess1.setNextProcessId(firstParentProcessId.toString());
        secondChildOfParentProcess1.setDeleteFlag(false);
        secondChildOfParentProcess1.setEditTs(new Date());
        secondChildOfParentProcess1.setWorkflowType(childWorkflowType);
        //inserting secondChildOfParentProcess1
         secondChildOfParentProcess1Id = processDAO.insert(secondChildOfParentProcess1);


        firstParentProcess.setNextProcessId(firstChildOfParentProcess1Id.toString() + "," + secondChildOfParentProcess1Id.toString());
        processDAO.update(firstParentProcess);
        Process updatedParentProcess = processDAO.get(firstParentProcessId);
        assertEquals("next processId of parent process not update succesfully", firstChildOfParentProcess1Id.toString() + "," + secondChildOfParentProcess1Id.toString(), updatedParentProcess.getNextProcessId());


        Process secondParentProcess = new Process();
        secondParentProcess.setProcessName("Test");
        secondParentProcess.setDescription("Test Process");
        secondParentProcess.setBusDomain(busDomain);
        secondParentProcess.setProcessType(parentProcessType);
        secondParentProcess.setAddTs(new Date());
        secondParentProcess.setCanRecover(true);
        secondParentProcess.setEnqueuingProcessId("0");
        secondParentProcess.setProcess(null);
        secondParentProcess.setNextProcessId("1");
        secondParentProcess.setDeleteFlag(false);
        secondParentProcess.setEditTs(new Date());
        secondParentProcess.setWorkflowType(parentWorkflowType);
         secondParentProcessId = processDAO.insert(secondParentProcess);


        Process firstChildOfParentProcess2 = new Process();
        firstChildOfParentProcess2.setProcessName("Test");
        firstChildOfParentProcess2.setDescription("Test Process");
        firstChildOfParentProcess2.setBusDomain(busDomain);
        firstChildOfParentProcess2.setProcessType(childProcessType);
        firstChildOfParentProcess2.setAddTs(new Date());
        firstChildOfParentProcess2.setCanRecover(true);
        firstChildOfParentProcess2.setEnqueuingProcessId(firstParentProcessId.toString());
        firstChildOfParentProcess2.setProcess(secondParentProcess);
        firstChildOfParentProcess2.setNextProcessId("1");
        firstChildOfParentProcess2.setDeleteFlag(false);
        firstChildOfParentProcess2.setEditTs(new Date());
        firstChildOfParentProcess2.setWorkflowType(childWorkflowType);
        //inserting firstChildOfParentProcess2
         firstChildOfParentProcess2Id = processDAO.insert(firstChildOfParentProcess2);

        Process secondChildOfParentProcess2 = new Process();
        secondChildOfParentProcess2.setProcessName("Test");
        secondChildOfParentProcess2.setDescription("Test Process");
        secondChildOfParentProcess2.setBusDomain(busDomain);
        secondChildOfParentProcess2.setProcessType(childProcessType);
        secondChildOfParentProcess2.setAddTs(new Date());
        secondChildOfParentProcess2.setCanRecover(true);
        secondChildOfParentProcess2.setEnqueuingProcessId(firstParentProcessId.toString());
        secondChildOfParentProcess2.setProcess(secondParentProcess);
        secondChildOfParentProcess2.setNextProcessId(secondParentProcessId.toString());
        secondChildOfParentProcess2.setDeleteFlag(false);
        secondChildOfParentProcess2.setEditTs(new Date());
        secondChildOfParentProcess2.setWorkflowType(childWorkflowType);
        //inserting secondChildOfParentProcess2
         secondChildOfParentProcess2Id = processDAO.insert(secondChildOfParentProcess2);


//      secondParentProcess=processDAO.get(secondParentProcessId);
        secondParentProcess.setNextProcessId(firstChildOfParentProcess2Id.toString() + "," + secondChildOfParentProcess2Id.toString());
        processDAO.update(secondParentProcess);


        propertiesId = new PropertiesId();
        propertiesId.setProcessId(firstChildOfParentProcess1Id);
        propertiesId.setPropKey("Test key");
        Properties properties = new Properties();
        properties.setDescription("test Description");
        properties.setConfigGroup("Test CG");
        properties.setId(propertiesId);
        properties.setProcess(firstChildOfParentProcess1);
        properties.setPropValue("Test Value");
        //inserting properties
        propertiesId = propertiesDAO.insert(properties);
        //updating properties
        properties.setDescription("updateDescription");
        //fetching properties
        Properties updatedProperties = propertiesDAO.get(propertiesId);
        assertEquals("update properties failed", "test Description", updatedProperties.getDescription());

        List<Properties> propertiesList = propertiesDAO.getPropertiesForConfig(firstChildOfParentProcess1Id, "Test CG");
        assertEquals("getPropertiesForConfig failed", propertiesList.get(0).getId().getPropKey(), "Test key");


        List<InitJobRowInfo> initJobRowInfos1 = jobDAO.initJob(firstParentProcessId, 1);
        Long parentInstanceExecId1 = initJobRowInfos1.get(0).getInstanceExecId();
        assertEquals(new Integer(2), instanceExecDAO.get(parentInstanceExecId1).getExecStatus().getExecStateId());

        Long subInstanceExecId1 = stepDAO.initStep(firstChildOfParentProcess1Id);
        assertEquals(new Integer(2), instanceExecDAO.get(subInstanceExecId1).getExecStatus().getExecStateId());

        Long subInstanceExecId2 = stepDAO.initStep(secondChildOfParentProcess1Id);
        assertEquals(new Integer(2), instanceExecDAO.get(subInstanceExecId2).getExecStatus().getExecStateId());

        stepDAO.haltStep(firstChildOfParentProcess1Id);
        assertEquals(new Integer(3), instanceExecDAO.get(subInstanceExecId1).getExecStatus().getExecStateId());


        stepDAO.haltStep(secondChildOfParentProcess1Id);
        assertEquals(new Integer(3), instanceExecDAO.get(subInstanceExecId2).getExecStatus().getExecStateId());


        jobDAO.haltJob(firstParentProcessId, "parentProcessFirst");
        assertEquals(new Integer(3), instanceExecDAO.get(parentInstanceExecId1).getExecStatus().getExecStateId());

        //  checking for record in bcq
        Integer numOfEntires = batchConsumpQueueDAO.getBCQForProcessId(firstChildOfParentProcess2);
        assertEquals(new Long(1),new Long(numOfEntires));
        assertNotNull(batchConsumpQueueDAO.list(0, 10));

        List<InitJobRowInfo> initJobRowInfos2 = jobDAO.initJob(secondParentProcessId, 1);
        Long parentInstanceExecId2 = initJobRowInfos2.get(0).getInstanceExecId();
        assertEquals(new Integer(2), instanceExecDAO.get(parentInstanceExecId2).getExecStatus().getExecStateId());

        Long subInstanceExecId4 = stepDAO.initStep(firstChildOfParentProcess2Id);
        assertEquals(new Integer(2), instanceExecDAO.get(subInstanceExecId4).getExecStatus().getExecStateId());

        Long subInstanceExecId5 = stepDAO.initStep(secondChildOfParentProcess2Id);
        assertEquals(new Integer(2), instanceExecDAO.get(subInstanceExecId5).getExecStatus().getExecStateId());

        stepDAO.haltStep(firstChildOfParentProcess2Id);
        assertEquals(new Integer(3), instanceExecDAO.get(subInstanceExecId4).getExecStatus().getExecStateId());

        stepDAO.termStep(secondChildOfParentProcess2Id);
        assertEquals(new Integer(6), instanceExecDAO.get(subInstanceExecId5).getExecStatus().getExecStateId());

        jobDAO.termJob(secondParentProcessId);
        assertEquals(new Integer(6), instanceExecDAO.get(parentInstanceExecId2).getExecStatus().getExecStateId());
//checking bcq
        numOfEntires = batchConsumpQueueDAO.getBCQForProcessId(firstChildOfParentProcess2);
        assertEquals(new Long(1),new Long(numOfEntires));

        List<InitJobRowInfo> initJobRowInfos3 = jobDAO.initJob(secondParentProcessId, 1);
        Long parentInstanceExecId3 = initJobRowInfos3.get(0).getInstanceExecId();
        assertEquals(new Integer(2), instanceExecDAO.get(parentInstanceExecId3).getExecStatus().getExecStateId());

        Long subInstanceExecId6 = stepDAO.initStep(firstChildOfParentProcess2Id);
        assertEquals(new Integer(2), instanceExecDAO.get(subInstanceExecId6).getExecStatus().getExecStateId());
        Long subInstanceExecId7 = stepDAO.initStep(secondChildOfParentProcess2Id);
        assertEquals(new Integer(2), instanceExecDAO.get(subInstanceExecId7).getExecStatus().getExecStateId());
        stepDAO.haltStep(firstChildOfParentProcess2Id);
        assertEquals(new Integer(3), instanceExecDAO.get(subInstanceExecId6).getExecStatus().getExecStateId());
        stepDAO.haltStep(secondChildOfParentProcess2Id);
        assertEquals(new Integer(3), instanceExecDAO.get(subInstanceExecId7).getExecStatus().getExecStateId());

        jobDAO.haltJob(secondParentProcessId, "parentProcessSecond");
        assertEquals(new Integer(3), instanceExecDAO.get(parentInstanceExecId3).getExecStatus().getExecStateId());

        numOfEntires = batchConsumpQueueDAO.getBCQForProcessId(firstChildOfParentProcess2);
        assertEquals(new Long(0),new Long(numOfEntires));
    }
    finally

    {
        try {
            propertiesDAO.delete(propertiesId);
        }catch (Exception e) {
            LOGGER.info("unable to delete properties");
            flag = 1;
        }

        try {
            processDAO.testDelete(firstChildOfParentProcess1Id);
        }catch (Exception e) {
            LOGGER.info("unable to delete firstChildOfParentProcess1");
            flag = 1;
        }

        try {
            processDAO.testDelete(secondChildOfParentProcess1Id);
        }catch (Exception e) {
            LOGGER.info("unable to delete secondChildOfParentProcess1");
            flag = 1;
        }

        try {
            processDAO.testDelete(firstChildOfParentProcess2Id);
        }catch (Exception e) {
            LOGGER.info("unable to delete firstChildOfParentProcess2");
            flag = 1;
        }

        try {
            processDAO.testDelete(secondChildOfParentProcess2Id);
        }catch (Exception e) {
            LOGGER.info("unable to delete secondChildOfParentProcess2");
            flag = 1;
        }

        try {
            processDAO.testDelete(firstParentProcessId);
        }catch (Exception e) {
            LOGGER.info("unable to delete firstParentProcess");
            flag = 1;
        }

        try {
            processDAO.testDelete(secondParentProcessId);
        }catch (Exception e) {
            LOGGER.info("unable to delete secondParentProcess");
            flag = 1;
        }

        try {
            busDomainDAO.delete(busDomainId);
        }catch (Exception e) {
            LOGGER.info("unable to delete busDomain");
            flag = 1;
        }
        assertEquals(new Integer(0),flag);
    }

    }
}

*/
