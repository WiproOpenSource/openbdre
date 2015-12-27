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

import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SU324335 on 18-Nov-15.
 */
public class RegisterFileDAOTest {
    private static final Logger LOGGER = Logger.getLogger(RegisterFileDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    RegisterFileDAO registerFileDAO;

    @Autowired
    BusDomainDAO busDomainDAO;
    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Autowired
    WorkflowTypeDAO workflowTypeDAO;
    @Autowired
    ProcessTemplateDAO processTemplateDAO;
    @Autowired
    ProcessDAO processDAO;

    @Test
    @Ignore
    public void registerFileTest() {

        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = new Process();
        Process childProcess = new Process();
        WorkflowType workflowType = workflowTypeDAO.get(1);
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessTemplate processTemplate = processTemplateDAO.get(0);
        ProcessType processType = processTypeDAO.get(1);
        //*parentProcess.setProcessId(10802);
        parentProcess.setProcess(null);
        parentProcess.setDescription("Test Parent Process");
        parentProcess.setAddTs(new Date());
        parentProcess.setProcessName("Test parent process name");
        parentProcess.setBusDomain(busDomain);
        parentProcess.setProcessType(processType);
        parentProcess.setCanRecover(false);
        parentProcess.setEnqueuingProcessId(0);
        parentProcess.setNextProcessId("10805");
        parentProcess.setWorkflowType(workflowType);
        parentProcess.setDeleteFlag(false);
        parentProcess.setProcessTemplate(processTemplate);
        parentProcess.setEditTs(new Date());
        Integer parentProcessId = processDAO.insert(parentProcess);
        LOGGER.info("prnt proc id" + parentProcessId);
        processType = processTypeDAO.get(12);

        //childProcess.setProcessId(10805);
        childProcess.setProcess(processDAO.get(parentProcessId));
        childProcess.setDescription("Test Child Process");
        childProcess.setAddTs(new Date());
        childProcess.setProcessName("Test child process name");
        childProcess.setBusDomain(busDomain);
        childProcess.setProcessType(processType);
        childProcess.setCanRecover(false);
        childProcess.setEnqueuingProcessId(0);
        childProcess.setNextProcessId("1");
        childProcess.setWorkflowType(workflowType);
        childProcess.setDeleteFlag(false);
        childProcess.setProcessTemplate(processTemplate);
        childProcess.setEditTs(new Date());

        Integer childProcessId = processDAO.insert(childProcess);
        LOGGER.info("chld process id" + childProcessId);
        parentProcess = processDAO.get(parentProcessId);
        String cpid = childProcessId.toString();
        LOGGER.info("child process id " + cpid);
        parentProcess.setNextProcessId(cpid);
        processDAO.update(parentProcess);
        LOGGER.info("child and parent inserted successfully");


        RegisterFileInfo registerFileInfo = new RegisterFileInfo();
        registerFileInfo.setSubProcessId(childProcessId);
        registerFileInfo.setServerId(123461);
        registerFileInfo.setPath("test path");
        registerFileInfo.setFileSize(Long.valueOf(1));
        registerFileInfo.setFileHash("12345");
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        registerFileInfo.setCreationTs(new Timestamp(now.getTime()));
        registerFileInfo.setBatchId(Long.valueOf(0));

        registerFileDAO.registerFile(registerFileInfo);

        processDAO.delete(childProcessId);
        processDAO.delete(parentProcessId);

    }
}

