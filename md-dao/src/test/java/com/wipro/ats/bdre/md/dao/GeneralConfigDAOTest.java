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

import com.wipro.ats.bdre.md.dao.jpa.GeneralConfig;
import com.wipro.ats.bdre.md.dao.jpa.GeneralConfigId;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Created by MR299389 on 10/26/2015.
 */
public class GeneralConfigDAOTest {
    private static final Logger LOGGER = Logger.getLogger(GeneralConfigDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    GeneralConfigDAO generalConfigDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of GeneralConfig is atleast:" + generalConfigDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of GeneralConfig is:" + generalConfigDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {
        GeneralConfigId generalConfigId = new GeneralConfigId();
        generalConfigId.setConfigGroup("1");
        generalConfigId.setGcKey("credential");
        LOGGER.info("Description of GeneralConfig(1) is:" + generalConfigDAO.get(generalConfigId).getDescription());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        GeneralConfigId generalConfigId = new GeneralConfigId("Test CG", "Test Key");
        generalConfigId.setConfigGroup("Test CG");
        generalConfigId.setGcKey("Test key");
        GeneralConfig generalConfig = new GeneralConfig();
        generalConfig.setId(generalConfigId);
        generalConfig.setDescription("Test Entry");
        generalConfig.setRequired(true);
        generalConfig.setType("text");
        generalConfigId = generalConfigDAO.insert(generalConfig);
        LOGGER.info("General Config is added with key:" + generalConfigId.getGcKey());
        generalConfig.setDescription("Test General Config");
        generalConfigDAO.update(generalConfig);
        generalConfig = generalConfigDAO.get(generalConfigId);
        assertEquals("Test General Config",generalConfig.getDescription());
        LOGGER.info("Updated Description is:" + generalConfig.getDescription());
        assertNotNull(generalConfigDAO.list(0,10));
        generalConfigDAO.delete(generalConfigId);
        LOGGER.info("Deleted General Config Entry with Key:" + generalConfigId.getGcKey());
        LOGGER.info("Size of GeneralConfig is:" + generalConfigDAO.totalRecordCount());

    }

    @Test
    public void getGeneralConfigTest() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.GeneralConfig> generalConfigList = generalConfigDAO.getGeneralConfig("1", 1);
        LOGGER.info("generalConfigList.size" + generalConfigList.size());
    }

    @Ignore
    @Test
    public void getGenConfigPropTest() throws Exception {
        com.wipro.ats.bdre.md.beans.table.GeneralConfig generalConfig = generalConfigDAO.GetGenConfigProperty("1", "credential");
        LOGGER.info("generalConfig" + generalConfig);
    }


    @Test
    public void getLikeGeneralConfigTest() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.GeneralConfig> generalConfigList = generalConfigDAO.getLikeGeneralConfig("cluster", 1);
        LOGGER.info("generalConfig" + generalConfigList);
    }
}