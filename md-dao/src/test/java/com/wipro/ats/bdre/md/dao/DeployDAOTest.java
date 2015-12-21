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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

    @Test
    public void testInitDeploy() throws Exception {
        deployDAO.initDeploy(2l);
        LOGGER.info("The init deploy test executed ");
    }

    @Test
    public void testTermDeploy() throws Exception {
        deployDAO.termDeploy(1l);
        LOGGER.info("The term deploy test executed ");
    }

    @Test
    public void testHaltDeploy() throws Exception {
        deployDAO.haltDeploy(4L);
        LOGGER.info("The halt deploy test executed ");
    }
}