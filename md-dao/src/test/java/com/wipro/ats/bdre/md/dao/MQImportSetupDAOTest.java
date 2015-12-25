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

import com.wipro.ats.bdre.md.beans.MQImportInfo;
import com.wipro.ats.bdre.md.beans.table.Properties;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by SU324335 on 30-Nov-15.
 */
public class MQImportSetupDAOTest {
    private static final Logger LOGGER = Logger.getLogger(MQImportSetupDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    MQImportSetupDAO mqImportSetupDAO;

    @Test
    public void InsertUpdateTest() {
        MQImportInfo mqImportInfo = new MQImportInfo();
        mqImportInfo.setBusDomainId(1);
        mqImportInfo.setCanRecover(false);
        mqImportInfo.setConfigGroup("test");
        mqImportInfo.setBrokerUrlValue("BrokerURL test");
        mqImportInfo.setQueueNameValue("QueueName test");
        mqImportInfo.setNumSpoutsValue(1);
        mqImportInfo.setNumBoltsValue(1);

        List<Properties> propertiesList = mqImportSetupDAO.insert(mqImportInfo);
        LOGGER.info("Broker URl of Properties Inserted :" + mqImportInfo.getBrokerUrlValue());

        mqImportInfo.setBrokerUrlValue("updated BrokerUrl value");
        List<Properties> updatedPropertiesList = mqImportSetupDAO.update(mqImportInfo);
        LOGGER.info("Updated Broker Url :" + mqImportInfo.getBrokerUrlValue());


    }

    @Ignore
    @Test
    public void delete() {
        int processId = 10839;
        LOGGER.info("Properties to be deleted" + processId);
        mqImportSetupDAO.delete(processId);
        LOGGER.info("processId of property deleted" + processId);
    }

    @Test
    public void listTest() {
        List<Properties> tableProperties = mqImportSetupDAO.list(0, 10);
        for (Properties properties : tableProperties) {
            LOGGER.info("properties:" + properties);
        }

    }

}
