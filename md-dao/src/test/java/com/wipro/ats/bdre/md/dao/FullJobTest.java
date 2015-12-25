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

import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import com.wipro.ats.bdre.md.beans.table.*;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.BatchStatus;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.DeployStatus;
import com.wipro.ats.bdre.md.dao.jpa.ExecStatus;
import com.wipro.ats.bdre.md.dao.jpa.InstanceExec;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.UserRoles;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

/**
 * Created by PR324290 on 12/17/2015.
 */
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



    @Test
    public void completeJobTest() throws Exception {


        BusDomain busDomain=new BusDomain();
        busDomain.setBusDomainName("testName");
        busDomain.setDescription("testDescription");
        busDomain.setBusDomainOwner("testOwner");

        //inserting new BusDomain
        int busDomainId = busDomainDAO.insert(busDomain);
        //updating inserted BusDomain
        busDomain.setDescription("updateDescription");
        busDomainDAO.update(busDomain);
        //fetching updated busDomain
        BusDomain updatedBusDomain=busDomainDAO.get(busDomainId);

        assertEquals("busDomain update failed","updateDescription",updatedBusDomain.getDescription());


        ProcessType processType = processTypeDAO.get(1);
        ProcessType childProcessType = processTypeDAO.get(6);

        WorkflowType workflowType = workflowTypeDAO.get(0);
        WorkflowType childWworkflowType= workflowTypeDAO.get(1);

        Process parentProcess1 = new Process();
        parentProcess1.setProcessName("Test");
        parentProcess1.setDescription("Test Process");
        parentProcess1.setBusDomain(busDomain);
        parentProcess1.setProcessType(processTypeDAO.get(1));
        parentProcess1.setAddTs(new Date());
        parentProcess1.setCanRecover(true);
        parentProcess1.setEnqueuingProcessId(0);
        parentProcess1.setProcess(null);
        parentProcess1.setNextProcessId("1");
        parentProcess1.setDeleteFlag(false);
        parentProcess1.setEditTs(new Date());
        parentProcess1.setWorkflowType(workflowType);
        //inserting parent process1
        Integer parentProcessId1 = processDAO.insert(parentProcess1);



        Process subProcess1 = new Process();
        subProcess1.setProcessName("Test");
        subProcess1.setDescription("Test Process");
        subProcess1.setBusDomain(busDomain);
        subProcess1.setProcessType(processTypeDAO.get(12));
        subProcess1.setAddTs(new Date());
        subProcess1.setCanRecover(true);
        subProcess1.setEnqueuingProcessId(0);
        subProcess1.setProcess(parentProcess1);
        subProcess1.setNextProcessId(parentProcessId1.toString());
        subProcess1.setDeleteFlag(false);
        subProcess1.setEditTs(new Date());
        subProcess1.setWorkflowType(childWworkflowType);
        //inserting sub process1
        Integer subProcessId1 = processDAO.insert(subProcess1);
        //fetching process
        LOGGER.info("sub pid "+subProcessId1);
        Process updatedProcess = processDAO.get(subProcessId1);
        assertNotNull("editTs not null",subProcess1.getEditTs());
        assertFalse(updatedProcess.getDeleteFlag());


        Process subProcess2 = new Process();
        subProcess2.setProcessName("Test");
        subProcess2.setDescription("Test Process");
        subProcess2.setBusDomain(busDomain);
        subProcess2.setProcessType(processTypeDAO.get(12));
        subProcess2.setAddTs(new Date());
        subProcess2.setCanRecover(true);
        subProcess2.setEnqueuingProcessId(0);
        subProcess2.setProcess(parentProcess1);
        subProcess2.setNextProcessId(parentProcessId1.toString());
        subProcess2.setDeleteFlag(false);
        subProcess2.setEditTs(new Date());
        subProcess2.setWorkflowType(childWworkflowType);
        //inserting subProcess2
        Integer subProcessId2 = processDAO.insert(subProcess2);

        Process subProcess3 = new Process();
        subProcess3.setProcessName("Test");
        subProcess3.setDescription("Test Process");
        subProcess3.setBusDomain(busDomain);
        subProcess3.setProcessType(processTypeDAO.get(12));
        subProcess3.setAddTs(new Date());
        subProcess3.setCanRecover(true);
        subProcess3.setEnqueuingProcessId(0);
        subProcess3.setProcess(parentProcess1);
        subProcess3.setNextProcessId(parentProcessId1.toString());
        subProcess3.setDeleteFlag(false);
        subProcess3.setEditTs(new Date());
        subProcess3.setWorkflowType(childWworkflowType);
        //inserting subProcess3
        Integer subProcessId3 = processDAO.insert(subProcess3);


        parentProcess1.setNextProcessId(subProcessId1.toString()+","+subProcessId2.toString());
        processDAO.update(parentProcess1);
        Process updatedParentProcess = processDAO.get(parentProcessId1);
        assertEquals("next processId of parent process not update succesfully",subProcessId1.toString()+","+subProcessId2.toString(),updatedParentProcess.getNextProcessId());


        Process parentProcess2 = new Process();
        parentProcess2.setProcessName("Test");
        parentProcess2.setDescription("Test Process");
        parentProcess2.setBusDomain(busDomain);
        parentProcess2.setProcessType(processTypeDAO.get(1));
        parentProcess2.setAddTs(new Date());
        parentProcess2.setCanRecover(true);
        parentProcess2.setEnqueuingProcessId(0);
        parentProcess2.setProcess(null);
        parentProcess2.setNextProcessId("1");
        parentProcess2.setDeleteFlag(false);
        parentProcess2.setEditTs(new Date());
        parentProcess2.setWorkflowType(workflowType);
        Integer parentProcessId2 = processDAO.insert(parentProcess2);


        Process subProcess4 = new Process();
        subProcess4.setProcessName("Test");
        subProcess4.setDescription("Test Process");
        subProcess4.setBusDomain(busDomain);
        subProcess4.setProcessType(processTypeDAO.get(12));
        subProcess4.setAddTs(new Date());
        subProcess4.setCanRecover(true);
        subProcess4.setEnqueuingProcessId(parentProcessId1);
        subProcess4.setProcess(parentProcess2);
        subProcess4.setNextProcessId("1");
        subProcess4.setDeleteFlag(false);
        subProcess4.setEditTs(new Date());
        subProcess4.setWorkflowType(childWworkflowType);
        //inserting subProcess4
        Integer subProcessId4 = processDAO.insert(subProcess4);

        Process subProcess5 = new Process();
        subProcess5.setProcessName("Test");
        subProcess5.setDescription("Test Process");
        subProcess5.setBusDomain(busDomain);
        subProcess5.setProcessType(processTypeDAO.get(12));
        subProcess5.setAddTs(new Date());
        subProcess5.setCanRecover(true);
        subProcess5.setEnqueuingProcessId(parentProcessId1);
        subProcess5.setProcess(parentProcess2);
        subProcess5.setNextProcessId(parentProcessId2.toString());
        subProcess5.setDeleteFlag(false);
        subProcess5.setEditTs(new Date());
        subProcess5.setWorkflowType(childWworkflowType);
        //inserting subProcess5
        Integer subProcessId5 = processDAO.insert(subProcess5);


//      parentProcess2=processDAO.get(parentProcessId2);
        parentProcess2.setNextProcessId(subProcessId4.toString()+","+subProcessId5.toString());
        processDAO.update(parentProcess2);


        PropertiesId propertiesId = new PropertiesId();
        propertiesId.setProcessId(subProcessId1);
        propertiesId.setPropKey("Test key");
        Properties properties = new Properties();
        properties.setDescription("test Description");
        properties.setConfigGroup("Test CG");
        properties.setId(propertiesId);
        properties.setProcess(subProcess1);
        properties.setPropValue("Test Value");
        //inserting properties
        propertiesId = propertiesDAO.insert(properties);
        //updating properties
        properties.setDescription("updateDescription");
        //fetching properties
        Properties updatedProperties = propertiesDAO.get(propertiesId);
        assertEquals("update properties failed","test Description",updatedProperties.getDescription());

        List<Properties> propertiesList = propertiesDAO.getPropertiesForConfig(subProcessId1,"Test CG");
        assertEquals("getPropertiesForConfig failed",propertiesList.get(0).getId().getPropKey(),"Test key");

        Batch batch = new Batch();
        batch.setBatchType("Test");
        //inserting batch
        Long batchId = batchDAO.insert(batch);
        //updating  batch
        batch.setBatchType("UpdateBatchType");
        batchDAO.update(batch);
        Batch updatedBatch = batchDAO.get(batchId);
        assertEquals("updated Batch failure","UpdateBatchType",updatedBatch.getBatchType());



        List<InitJobRowInfo> initJobRowInfos1 = jobDAO.initJob(parentProcessId1, 1);
        assertNotNull(initJobRowInfos1.get(0).getInstanceExecId());
        Long sub_instance_exec_id1 = stepDAO.initStep(subProcessId1);
        assertNotNull( sub_instance_exec_id1);
        Long sub_instance_exec_id2 = stepDAO.initStep(subProcessId2);
        assertNotNull(sub_instance_exec_id2);
        Long sub_instance_exec_id3 = stepDAO.initStep(subProcessId3);
        assertNotNull(sub_instance_exec_id3);
        //   LOGGER.info("1st is "+sub_instance_exec_id1+"1st is "+sub_instance_exec_id2+"1st is "+sub_instance_exec_id3);

        stepDAO.haltStep(subProcessId1);
        InstanceExec instanceExec = instanceExecDAO.get(sub_instance_exec_id1);
        assertNotNull(instanceExec.getInstanceExecId());

        stepDAO.haltStep(subProcessId2);
        stepDAO.haltStep(subProcessId3);

        jobDAO.haltJob(parentProcessId1,"parentProcessFirst");
        instanceExec = instanceExecDAO.get(initJobRowInfos1.get(0).getInstanceExecId());
        assertNotNull(instanceExec.getInstanceExecId());

        List<InitJobRowInfo> initJobRowInfos2 = jobDAO.initJob(parentProcessId2, 1);
        assertNotNull(initJobRowInfos2.get(0).getInstanceExecId());
        Long sub_instance_exec_id4 = stepDAO.initStep(subProcessId4);
        assertNotNull(sub_instance_exec_id4);
        Long sub_instance_exec_id5 = stepDAO.initStep(subProcessId5);
        assertNotNull(sub_instance_exec_id5);
        LOGGER.info(sub_instance_exec_id4+"1st is "+sub_instance_exec_id5);
        stepDAO.termStep(subProcessId4);
        stepDAO.haltStep(subProcessId5);

        //Long sub_instance_exec_id6 = stepDAO.initStep(subProcessId4);
        // stepDAO.haltStep(subProcessId4);
        jobDAO.haltJob(parentProcessId2,"parentProcessSecond");
        jobDAO.initJob(parentProcessId2, 1);
        Long sub_instance_exec_id6=stepDAO.initStep(subProcessId4);
        stepDAO.haltStep(subProcessId4);
        LOGGER.info("1st is "+sub_instance_exec_id6);
        jobDAO.haltJob(parentProcessId2,"parentProcessSecond");



        propertiesDAO.delete(propertiesId);
        processDAO.delete(subProcessId1);
        processDAO.delete(subProcessId2);
        processDAO.delete(subProcessId3);
        processDAO.delete(subProcessId4);
        processDAO.delete(subProcessId5);
        processDAO.delete(parentProcessId1);
        processDAO.delete(parentProcessId2);
        busDomainDAO.delete(busDomainId);

        batchDAO.delete(batchId);


    }
}
