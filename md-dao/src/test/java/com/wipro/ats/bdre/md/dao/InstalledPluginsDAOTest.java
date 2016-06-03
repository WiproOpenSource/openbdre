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

import com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



/**
 * Created by sh324337 on 5/27/16.
 */
public class InstalledPluginsDAOTest {

    private static final Logger LOGGER = Logger.getLogger(InstalledPluginsDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    InstalledPluginsDAO installedPluginsDAO;

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        InstalledPlugins installedPlugins = new InstalledPlugins();
        installedPlugins.setPluginUniqueId("Test-1");
        installedPlugins.setPluginId("Test");
        installedPlugins.setName("test name");
        installedPlugins.setDescription("Test Description");
        installedPlugins.setVersion(1);
        installedPlugins.setAuthor("Test Author");
        installedPlugins.setAddTs(new Date());
        installedPlugins.setPlugin("TestPlugin");
        installedPlugins.setUninstallable(true);
        String installedPluginId = installedPluginsDAO.insert(installedPlugins);
        LOGGER.info("plugin_id123 "+installedPluginId);
        LOGGER.info("InstalledPlugin is added with Id:" + installedPluginId);
        installedPlugins.setDescription("Test Description 2");
        installedPluginsDAO.update(installedPlugins);
        installedPlugins = installedPluginsDAO.get(installedPluginId);
        assertEquals("Test Description 2",installedPlugins.getDescription());
        LOGGER.info("Updated Description is:" + installedPlugins.getDescription());
        assertNotNull(installedPluginsDAO.list(0, 10));
        installedPluginsDAO.delete(installedPluginId);
        LOGGER.info("Deleted InstalledPlugin Entry with ID" + installedPluginId);
        LOGGER.info("Size of installedPlugins is:" + installedPluginsDAO.totalRecordCount());
        //  LOGGER.info("Size of BatchStatus is atleast:" + batchStatusDAO.list(0, 10).size());
    }

}
