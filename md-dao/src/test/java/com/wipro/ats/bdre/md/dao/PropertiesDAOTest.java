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

import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by MR299389 on 10/28/2015.
 */
public class PropertiesDAOTest {
    private static final Logger LOGGER = Logger.getLogger(PropertiesDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    PropertiesDAO propertiesDAO;
    @Autowired
    ProcessDAO processDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of properties is atleast:" + propertiesDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Total Size of properties is:" + propertiesDAO.totalRecordCount());
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        PropertiesId propertiesId = new PropertiesId();
        propertiesId.setPropKey("CP_ACCOUNT_TABLE");
        propertiesId.setProcessId(10805);
        LOGGER.info("Description of propertiesId(10805) is:" + propertiesDAO.get(propertiesId).getDescription());

    }

    @Ignore
    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        PropertiesId propertiesId = new PropertiesId();
        propertiesId.setProcessId(10805);
        propertiesId.setPropKey("Test key");
        Process process = processDAO.get(10805);
        Properties properties = new Properties();
        properties.setDescription("test Description");
        properties.setConfigGroup("Test CG");
        properties.setId(propertiesId);
        properties.setProcess(process);
        properties.setPropValue("Test Value");
        propertiesId = propertiesDAO.insert(properties);
        LOGGER.info("properties is added with key:" + propertiesId.getPropKey());
        properties.setDescription("Test prop");
        propertiesDAO.update(properties);
        properties = propertiesDAO.get(propertiesId);
        LOGGER.info("Updated Description is:" + properties.getDescription());
        propertiesDAO.delete(propertiesId);
        LOGGER.info("Deleted properties Entry with key:" + propertiesId.getPropKey());
    }

    @Ignore
    @Test
    public void testGetPropertiesForConfig() throws Exception {
        for (Properties property : propertiesDAO.getPropertiesForConfig(10835, "PL_ACCOUNT_TABLE")) {
            LOGGER.info("Properties fetched:" + property.getConfigGroup() + " " + property.getId().getPropKey() + " " + property.getId().getProcessId() + " " + property.getPropValue());
        }
    }
}