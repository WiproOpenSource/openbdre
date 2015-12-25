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

import com.wipro.ats.bdre.md.dao.jpa.DeployStatus;
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
public class DeployStatusDAOTest {

    private static final Logger LOGGER = Logger.getLogger(DeployStatusDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    DeployStatusDAO deployStatusDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of DeployStatus is atleast:" + deployStatusDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of DeployStatus is:" + deployStatusDAO.totalRecordCount());
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("Description of DeployStatusId(1) is:" + deployStatusDAO.get((short) 1).getDescription());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        DeployStatus deployStatus = new DeployStatus();
        deployStatus.setDeployStatusId((short) 6);
        deployStatus.setDescription("Test");
        Short deployStatusId = deployStatusDAO.insert(deployStatus);
        LOGGER.info("deployStatus is added with Id:" + deployStatusId);
        deployStatus.setDescription("Test BatchSatus");
        deployStatusDAO.update(deployStatus);
        deployStatus = deployStatusDAO.get(deployStatusId);
        LOGGER.info("Updated Description is:" + deployStatus.getDescription());
        deployStatusDAO.delete(deployStatusId);
        LOGGER.info("Deleted BatchStatus Entry with ID:" + deployStatusId);
    }
}
