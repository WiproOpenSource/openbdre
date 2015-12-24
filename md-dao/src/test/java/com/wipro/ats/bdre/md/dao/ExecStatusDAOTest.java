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

import com.wipro.ats.bdre.md.dao.jpa.ExecStatus;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by MR299389 on 10/15/2015.
 */
public class ExecStatusDAOTest {

    private static final Logger LOGGER = Logger.getLogger(ExecStatusDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ExecStatusDAO execStatusDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of ExecStatus is atleast:" + execStatusDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of ExecStatus is:" + execStatusDAO.totalRecordCount());
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("Description of ExecStatusId(1) is:" + execStatusDAO.get(1).getDescription());

    }

    @Ignore
    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        ExecStatus execStatus = new ExecStatus();
        execStatus.setDescription("test");
        execStatus.setExecStateId(7);
        Integer execStatusId = execStatusDAO.insert(execStatus);
        LOGGER.info("ExecStatus is added with Id:" + execStatusId);
        execStatus.setDescription("Test ExecStatus");
        execStatusDAO.update(execStatus);
        execStatus = execStatusDAO.get(execStatusId);
        LOGGER.info("Updated Description is:" + execStatus.getDescription());
        execStatusDAO.delete(execStatusId);
        LOGGER.info("Deleted ExecStatus Entry with ID" + execStatusId);
    }
}
