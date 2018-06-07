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

package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.beans.PositionsInfo;
import com.wipro.ats.bdre.md.dao.*;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ArrangePositionsTest {
    private static final Logger LOGGER = Logger.getLogger(ArrangePositions.class);
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

    @Test
    public void testGetListPositionInfo() throws Exception {
        Integer firstParentProcessId = null;
        Integer firstChildOfParentProcess1Id = null;
        Integer secondChildOfParentProcess1Id = null;
        Integer thirdChildOfParentProcess1Id=null;
        Integer fourthChildOfParentProcess1Id=null;
        Integer fifthChildOfParentProcess1Id=null;
        Integer sixthChildOfParentProcess1Id=null;
        Integer seventhChildOfParentProcess1Id=null;
        Integer flag=0;

        try {
            BusDomain busDomain = busDomainDAO.get(1);
            ProcessType parentProcessType = processTypeDAO.get(1);
            ProcessType childProcessType = processTypeDAO.get(12);
            ArrangePositions arrangePositions = new ArrangePositions();
            WorkflowType parentWorkflowType = workflowTypeDAO.get(0);
            WorkflowType childWorkflowType = workflowTypeDAO.get(1);


            com.wipro.ats.bdre.md.dao.jpa.Process firstParentProcess = new Process();
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
            firstChildOfParentProcess1.setNextProcessId("0");
            firstChildOfParentProcess1.setDeleteFlag(false);
            firstChildOfParentProcess1.setEditTs(new Date());
            firstChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting sub process1
            firstChildOfParentProcess1Id = processDAO.insert(firstChildOfParentProcess1);

            Process process = processDAO.get(firstParentProcessId);
            process.setNextProcessId(firstChildOfParentProcess1Id.toString());
            processDAO.update(process);

            Process secondChildOfParentProcess1 = new Process();
            secondChildOfParentProcess1.setProcessName("Test");
            secondChildOfParentProcess1.setDescription("Test Process");
            secondChildOfParentProcess1.setBusDomain(busDomain);
            secondChildOfParentProcess1.setProcessType(childProcessType);
            secondChildOfParentProcess1.setAddTs(new Date());
            secondChildOfParentProcess1.setCanRecover(true);
            secondChildOfParentProcess1.setEnqueuingProcessId("0");
            secondChildOfParentProcess1.setProcess(firstParentProcess);
            secondChildOfParentProcess1.setNextProcessId("0");
            secondChildOfParentProcess1.setDeleteFlag(false);
            secondChildOfParentProcess1.setEditTs(new Date());
            secondChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting secondChildOfParentProcess1
            secondChildOfParentProcess1Id = processDAO.insert(secondChildOfParentProcess1);

            Process thirdChildOfParentProcess1 = new Process();
            thirdChildOfParentProcess1.setProcessName("Test");
            thirdChildOfParentProcess1.setDescription("Test Process");
            thirdChildOfParentProcess1.setBusDomain(busDomain);
            thirdChildOfParentProcess1.setProcessType(childProcessType);
            thirdChildOfParentProcess1.setAddTs(new Date());
            thirdChildOfParentProcess1.setCanRecover(true);
            thirdChildOfParentProcess1.setEnqueuingProcessId("0");
            thirdChildOfParentProcess1.setProcess(firstParentProcess);
            thirdChildOfParentProcess1.setNextProcessId("0");
            thirdChildOfParentProcess1.setDeleteFlag(false);
            thirdChildOfParentProcess1.setEditTs(new Date());
            thirdChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting thirdChildOfParentProcess1
            thirdChildOfParentProcess1Id = processDAO.insert(thirdChildOfParentProcess1);


            Process fourthChildOfParentProcess1 = new Process();
            fourthChildOfParentProcess1.setProcessName("Test");
            fourthChildOfParentProcess1.setDescription("Test Process");
            fourthChildOfParentProcess1.setBusDomain(busDomain);
            fourthChildOfParentProcess1.setProcessType(childProcessType);
            fourthChildOfParentProcess1.setAddTs(new Date());
            fourthChildOfParentProcess1.setCanRecover(true);
            fourthChildOfParentProcess1.setEnqueuingProcessId("0");
            fourthChildOfParentProcess1.setProcess(firstParentProcess);
            fourthChildOfParentProcess1.setNextProcessId("0");
            fourthChildOfParentProcess1.setDeleteFlag(false);
            fourthChildOfParentProcess1.setEditTs(new Date());
            fourthChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting fourthChildOfParentProcess1
            fourthChildOfParentProcess1Id = processDAO.insert(fourthChildOfParentProcess1);

            process = processDAO.get(firstChildOfParentProcess1Id);
            process.setNextProcessId(secondChildOfParentProcess1Id.toString() + "," + thirdChildOfParentProcess1Id.toString() + "," + fourthChildOfParentProcess1Id.toString());
            processDAO.update(process);

            Process fifthChildOfParentProcess1 = new Process();
            fifthChildOfParentProcess1.setProcessName("Test");
            fifthChildOfParentProcess1.setDescription("Test Process");
            fifthChildOfParentProcess1.setBusDomain(busDomain);
            fifthChildOfParentProcess1.setProcessType(childProcessType);
            fifthChildOfParentProcess1.setAddTs(new Date());
            fifthChildOfParentProcess1.setCanRecover(true);
            fifthChildOfParentProcess1.setEnqueuingProcessId("0");
            fifthChildOfParentProcess1.setProcess(firstParentProcess);
            fifthChildOfParentProcess1.setNextProcessId("0");
            fifthChildOfParentProcess1.setDeleteFlag(false);
            fifthChildOfParentProcess1.setEditTs(new Date());
            fifthChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting fifthChildOfParentProcess1
            fifthChildOfParentProcess1Id = processDAO.insert(fifthChildOfParentProcess1);

            Process sixthChildOfParentProcess1 = new Process();
            sixthChildOfParentProcess1.setProcessName("Test");
            sixthChildOfParentProcess1.setDescription("Test Process");
            sixthChildOfParentProcess1.setBusDomain(busDomain);
            sixthChildOfParentProcess1.setProcessType(childProcessType);
            sixthChildOfParentProcess1.setAddTs(new Date());
            sixthChildOfParentProcess1.setCanRecover(true);
            sixthChildOfParentProcess1.setEnqueuingProcessId("0");
            sixthChildOfParentProcess1.setProcess(firstParentProcess);
            sixthChildOfParentProcess1.setNextProcessId("0");
            sixthChildOfParentProcess1.setDeleteFlag(false);
            sixthChildOfParentProcess1.setEditTs(new Date());
            sixthChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting sixthChildOfParentProcess1
            sixthChildOfParentProcess1Id = processDAO.insert(sixthChildOfParentProcess1);

            process = processDAO.get(secondChildOfParentProcess1Id);
            process.setNextProcessId(fifthChildOfParentProcess1Id.toString() + "," + sixthChildOfParentProcess1Id.toString());
            processDAO.update(process);

            process = processDAO.get(thirdChildOfParentProcess1Id);
            process.setNextProcessId(fifthChildOfParentProcess1Id.toString() + "," + sixthChildOfParentProcess1Id.toString());
            processDAO.update(process);

            process = processDAO.get(fourthChildOfParentProcess1Id);
            process.setNextProcessId(fifthChildOfParentProcess1Id.toString() + "," + sixthChildOfParentProcess1Id.toString());
            processDAO.update(process);

            Process seventhChildOfParentProcess1 = new Process();
            seventhChildOfParentProcess1.setProcessName("Test");
            seventhChildOfParentProcess1.setDescription("Test Process");
            seventhChildOfParentProcess1.setBusDomain(busDomain);
            seventhChildOfParentProcess1.setProcessType(childProcessType);
            seventhChildOfParentProcess1.setAddTs(new Date());
            seventhChildOfParentProcess1.setCanRecover(true);
            seventhChildOfParentProcess1.setEnqueuingProcessId("0");
            seventhChildOfParentProcess1.setProcess(firstParentProcess);
            seventhChildOfParentProcess1.setNextProcessId("0");
            seventhChildOfParentProcess1.setDeleteFlag(false);
            seventhChildOfParentProcess1.setEditTs(new Date());
            seventhChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting seventhChildOfParentProcess1
            seventhChildOfParentProcess1Id = processDAO.insert(seventhChildOfParentProcess1);

            process = processDAO.get(fifthChildOfParentProcess1Id);
            process.setNextProcessId(seventhChildOfParentProcess1Id.toString());
            processDAO.update(process);

            process = processDAO.get(sixthChildOfParentProcess1Id);
            process.setNextProcessId(seventhChildOfParentProcess1Id.toString());
            processDAO.update(process);

            process = processDAO.get(seventhChildOfParentProcess1Id);
            process.setNextProcessId(firstParentProcessId.toString());
            processDAO.update(process);


            Map<String, PositionsInfo> positionsInfoList = arrangePositions.getListPositionInfo(firstParentProcessId);
            LOGGER.info("list size" + positionsInfoList.size());
            for (PositionsInfo pinfo : positionsInfoList.values()) {
                LOGGER.info(pinfo.getProcessId() + "-process id. " + pinfo.getxPos() + ":x-pos. " + pinfo.getyPos() + ":y-pos.");
                pinfo.setxPos(0);
                pinfo.setyPos(0);
                LOGGER.info(pinfo.getProcessId() + "-process id. " + pinfo.getxPos() + ":x-pos altered." + pinfo.getyPos() + ":y-pos.");
            }
            Map<String, PositionsInfo> positionsInfoListarranged = arrangePositions.getListPositionInfo(firstParentProcessId);

            for (PositionsInfo pinfo : positionsInfoListarranged.values()) {
                LOGGER.info(pinfo.getProcessId() + "-process id. " + pinfo.getxPos() + ":x-pos arranged. " + pinfo.getyPos() + ":y-pos arranged");

            }

            assertEquals(new Integer(1000), positionsInfoListarranged.get(firstParentProcessId.toString()).getxPos());
            assertEquals(new Integer(100),positionsInfoListarranged.get(firstParentProcessId.toString()).getyPos());



        }finally {


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
                processDAO.testDelete(thirdChildOfParentProcess1Id);
            }catch (Exception e) {
                LOGGER.info("unable to delete thirdChildOfParentProcess1");
                flag = 1;
            }

            try {
                processDAO.testDelete(fourthChildOfParentProcess1Id);
            }catch (Exception e) {
                LOGGER.info("unable to delete fourthChildOfParentProcess1");
                flag = 1;
            }

            try {
                processDAO.testDelete(fifthChildOfParentProcess1Id);
            }catch (Exception e) {
                LOGGER.info("unable to delete fifthChildOfParentProcess1");
                flag = 1;
            }

            try {
                processDAO.testDelete(sixthChildOfParentProcess1Id);
            }catch (Exception e) {
                LOGGER.info("unable to delete sixthChildOfParentProcess1");
                flag = 1;
            }

            try {
                processDAO.testDelete(seventhChildOfParentProcess1Id);
            }catch (Exception e) {
                LOGGER.info("unable to delete seventhChildOfParentProcess1");
                flag = 1;
            }
            try {
                processDAO.testDelete(firstParentProcessId);
            }catch (Exception e) {
                LOGGER.info("unable to delete firstParentProcess1");
                flag = 1;
            }


        }
        assertEquals(new Integer(0),flag);
        }
    }

