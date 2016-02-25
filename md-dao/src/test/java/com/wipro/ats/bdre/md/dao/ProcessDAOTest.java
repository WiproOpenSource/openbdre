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


import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.Process;
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
import java.util.List;

/**
 * Created by MR299389 on 10/16/2015.
 */
public class ProcessDAOTest {
    private static final Logger LOGGER = Logger.getLogger(ProcessDAOTest.class);

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

    @Test
    @Ignore
    public void testList() throws Exception {
        LOGGER.info("Size of Process list is atleast:" + processDAO.list(10802, 0, 10).size());
    }

    @Test
    @Ignore
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Total Process is:" + processDAO.totalRecordCount(10802));
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("Process(10835) Name:" + processDAO.get(10835).getProcess().getProcessId());

    }
    @Ignore
    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        BusDomain busDomain = busDomainDAO.get(1);
        ProcessType processType = processTypeDAO.get(1);
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
        LOGGER.info("Process is added with Id:" + id);
        process.setProcessName("Test Process");
        processDAO.update(process);
        process = processDAO.get(id);
        LOGGER.info("Updated Process Name is:" + process.getProcessName());
        processDAO.testDelete(id);
        LOGGER.info("Deleted process with ID:" + id);
    }

    @Test
    @Ignore
    public void testSubProcesslist() throws Exception {
        for(Process process: processDAO.subProcesslist(182))
        {
            LOGGER.info("process "+process.getProcessId() + " is "+process.getProcessName());
        }
        LOGGER.info("Total Sub Process count is:" + processDAO.subProcesslist(182).size());
    }

    @Test
    @Ignore
    public void testSelectProcessList() throws Exception {
        for(Process process: processDAO.selectProcessList(1))
        {
            LOGGER.info("process "+process.getProcessId() + " is "+process.getProcessName());
        }
        LOGGER.info("Total sub process along with parent process count is:" + processDAO.selectProcessList(1).size());
    }

    @Ignore
    @Test
    public void testDelete() throws Exception {
        processDAO.testDelete(10837);
        LOGGER.info("Deleted process with ID:");
    }

    @Test
    @Ignore
    public void testSelectProcessListWithExec() throws Exception {
        List<ProcessInfo> processInfoList = processDAO.selectProcessListWithExec(10802 ,196l);
        for (ProcessInfo processInfo : processInfoList) {
            LOGGER.info(processInfo + "\n");
        }
    }

}
