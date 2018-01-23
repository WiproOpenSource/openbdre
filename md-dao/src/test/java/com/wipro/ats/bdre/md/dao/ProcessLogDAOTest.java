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

import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessLog;
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
 * Created by PR324290 on 10/28/2015.
 */
public class ProcessLogDAOTest {
    private static final Logger LOGGER = Logger.getLogger(ProcessLogDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ProcessLogDAO processLogDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    BusDomainDAO busDomainDAO;
    @Autowired
    ProcessTypeDAO processTypeDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of ProcessLog is atleast:" + processLogDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of ProcessLog is:" + processLogDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {


        LOGGER.info("ProcessLog(0) message:" + processLogDAO.get(Long.valueOf(2)).getMessage());
    }

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
        process.setEnqueuingProcessId("0");
        process.setNextProcessId("10802");
        process.setDeleteFlag(false);
        process.setEditTs(new Date());
        Integer id = processDAO.insert(process);
        ProcessLog processLog = new ProcessLog();

        processLog.setProcess(process);
        processLog.setAddTs(new Date());
        processLog.setMessage("Test1");
        processLog.setMessageId("Test2");
        processLog.setLogCategory("Test3");
        Long processLogId = processLogDAO.insert(processLog);
        LOGGER.info("New ProcessLog added with ID:" + processLogId);
        processLog.setMessage("Test1_updated");
        processLogDAO.update(processLog);
        processLog = processLogDAO.get(processLogId);
        LOGGER.info("Updated processLog with message:" + processLog.getMessage());
        processLogDAO.delete(processLogId);
        LOGGER.info("ProcessLog Deleted with ID:" + processLogId);
        processDAO.testDelete(id);
    }

    @Test
    @Ignore
    public void testLog() throws Exception {
        ProcessLogInfo processLogInfo = new ProcessLogInfo();
        processLogInfo.setProcessId(5);
        processLogInfo.setAddTs(new Date());
        processLogInfo.setMessage("Test1");
        processLogInfo.setMessageId("Test2");
        processLogInfo.setLogCategory("Test3");

        processLogDAO.log(processLogInfo);

        LOGGER.debug("Check DB to find new log");
    }

    @Test
    @Ignore
    public void testGetLastValue() throws Exception {
        ProcessLogInfo processLogInfo = new ProcessLogInfo();
        processLogInfo.setProcessId(5);
        processLogInfo.setLogCategory("test");
        processLogInfo.setMessage("test");
        processLogInfo.setMessageId("test");
        processLogInfo.setInstanceRef(Long.parseLong("10"));
        processLogInfo.setAddTs(new Date());
        ProcessLogInfo processLogInfo1 = processLogDAO.getLastValue(processLogInfo);
        LOGGER.info(" processid of Last inserted process is" + processLogInfo1.getProcessId());
        ;
    }
  @Ignore
    @Test
    public void testlistLog() {
        ProcessLogInfo processLogInfo = new ProcessLogInfo();
        processLogInfo.setParentProcessId(null);
        processLogInfo.setPage(0);
        processLogInfo.setPageSize(10);
        List<ProcessLogInfo> processLogInfoList = processLogDAO.listLog(processLogInfo);
        LOGGER.info("size of the list is " + processLogInfoList.size());
    }

    @Test
    @Ignore
    public void testGetProcessLog() {
        ProcessLogInfo processLogInfo = new ProcessLogInfo();
        processLogInfo.setProcessId(5);
        processLogInfo.setLogCategory("test");
        processLogInfo.setMessage("test");
        processLogInfo.setMessageId("test");
        processLogInfo.setInstanceRef(Long.parseLong("10"));
        processLogInfo.setAddTs(new Date());
        List<ProcessLogInfo> processLogInfoList = processLogDAO.getProcessLog(processLogInfo);
        LOGGER.info("size of the list is " + processLogInfoList.size());
    }
}
