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

import com.wipro.ats.bdre.md.dao.jpa.Servers;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by PR324290 on 10/28/2015.
 */
public class ServersDAOTest {


    private static final Logger LOGGER = Logger.getLogger(ServersDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Autowired
    ServersDAO serversDAO;
    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of Servers is atleast:" + serversDAO.list(0, 10).size());
    }
    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of Servers is:" + serversDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {

        LOGGER.info("Servers(123461) name:" + serversDAO.get(123461).getServerName());
    }

    @Ignore
    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        Servers servers = new Servers();
        servers.setServerType("Test");
        servers.setServerName("Test");
        int serversId = (Integer) serversDAO.insert(servers);
        LOGGER.info("New Servers added with ID:" + serversId);
        servers.setServerName("Test_update");
        serversDAO.update(servers);
        servers = serversDAO.get(serversId);
        LOGGER.info("Updated servers with name:" + servers.getServerName());
        ;
        serversDAO.delete(serversId);
        LOGGER.info("Servers Deleted with ID:" + serversId);
    }
}
