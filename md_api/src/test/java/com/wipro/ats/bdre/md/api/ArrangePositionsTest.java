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
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ArrangePositionsTest {
    private static final Logger LOGGER = Logger.getLogger(ArrangePositions.class);
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
        Integer flag=0;

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
            firstParentProcess.setEnqueuingProcessId(0);
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
            firstChildOfParentProcess1.setEnqueuingProcessId(0);
            firstChildOfParentProcess1.setProcess(firstParentProcess);
            firstChildOfParentProcess1.setNextProcessId("0");
            firstChildOfParentProcess1.setDeleteFlag(false);
            firstChildOfParentProcess1.setEditTs(new Date());
            firstChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting sub process1
            firstChildOfParentProcess1Id = processDAO.insert(firstChildOfParentProcess1);

            Process secondChildOfParentProcess1 = new Process();
            secondChildOfParentProcess1.setProcessName("Test");
            secondChildOfParentProcess1.setDescription("Test Process");
            secondChildOfParentProcess1.setBusDomain(busDomain);
            secondChildOfParentProcess1.setProcessType(childProcessType);
            secondChildOfParentProcess1.setAddTs(new Date());
            secondChildOfParentProcess1.setCanRecover(true);
            secondChildOfParentProcess1.setEnqueuingProcessId(0);
            secondChildOfParentProcess1.setProcess(firstParentProcess);
            secondChildOfParentProcess1.setNextProcessId("0");
            secondChildOfParentProcess1.setDeleteFlag(false);
            secondChildOfParentProcess1.setEditTs(new Date());
            secondChildOfParentProcess1.setWorkflowType(childWorkflowType);
            //inserting secondChildOfParentProcess1
            secondChildOfParentProcess1Id = processDAO.insert(secondChildOfParentProcess1);

            Process parentProcess = processDAO.get(firstParentProcessId);
            parentProcess.setNextProcessId(firstChildOfParentProcess1Id.toString() + "," + secondChildOfParentProcess1Id.toString());
            processDAO.update(parentProcess);


            Map<String, PositionsInfo> positionsInfoList = arrangePositions.getListPositionInfo(firstChildOfParentProcess1Id);
            for (PositionsInfo pinfo : positionsInfoList.values()) {
                LOGGER.info(pinfo.getProcessId() + " process id" + pinfo.getxPos() + "x pos" + pinfo.getyPos() + "y pos");
            }


                processDAO.delete(firstChildOfParentProcess1Id);


                processDAO.delete(secondChildOfParentProcess1Id);




                processDAO.delete(firstParentProcessId);



        }
    }

