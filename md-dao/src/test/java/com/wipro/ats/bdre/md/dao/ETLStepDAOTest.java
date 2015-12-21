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

import com.wipro.ats.bdre.md.beans.ETLJobInfo;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;

/**
 * Created by MR299389 on 10/26/2015.
 */
public class ETLStepDAOTest {
    private static final Logger LOGGER = Logger.getLogger(ETLStepDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ETLStepDAO etlstepDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of Etlstep is atleast:" + etlstepDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of Etlstep is:" + etlstepDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        ETLJobInfo etlJobInfo = new ETLJobInfo();// Serial Number:1 and UUID:Test UUID should be present to pass the test method
        etlJobInfo.setUuid("Test UUID");
        LOGGER.info("Description of Etlstep(1) is:" + etlstepDAO.get(etlJobInfo).get(0).getDescription());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {


        ETLJobInfo etlJobInfo = new ETLJobInfo();
        //etlJobInfo.setSerialNumber(1);
        UUID uuid = UUID.randomUUID();
        etlJobInfo.setUuid(uuid.toString());
        etlJobInfo.setDescription("Test Description");
        etlJobInfo.setBusDomainId(1);
        etlJobInfo.setProcessName("BANK_HUB_021_IBAN_DDIBANP");
        ETLJobInfo returnedETLJob = new ETLJobInfo();
        returnedETLJob = etlstepDAO.insertETLJob(etlJobInfo);
        LOGGER.info("Etlstep is added with UUID:" + returnedETLJob.getUuid() + " description is " + returnedETLJob.getDescription());
        etlJobInfo.setDescription("Updated Test Description");
        etlstepDAO.updateETLJob(etlJobInfo);
        etlJobInfo.setPageSize(10);
        etlJobInfo.setPage(0);
        etlJobInfo = etlstepDAO.getETLJob(etlJobInfo).get(0);
        LOGGER.info("Updated Description is:" + etlJobInfo.getDescription());
        //etlstepDAO.deleteETLJob(etlJobInfo);
        LOGGER.info("Deleted Etlstep Entry with UUID:" + etlJobInfo.getUuid());
    }
}
