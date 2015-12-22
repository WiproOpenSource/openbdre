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

import com.wipro.ats.bdre.md.beans.DQSetupInfo;
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
 * Created by PR324290 on 11/27/2015.
 */
public class DataQualityDAOTest {
    private static final Logger LOGGER = Logger.getLogger(DataQualityDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    DataQualityDAO dataQualityDAO;
    @Ignore
    @Test
    public void InsertUpdateTest() {
        DQSetupInfo dqSetupInfo = new DQSetupInfo();
        dqSetupInfo.setDescription("desc");
        dqSetupInfo.setConfigGroup("cg");
        dqSetupInfo.setBusDomainId(1);
        dqSetupInfo.setCanRecover(true);
        dqSetupInfo.setEnqId(1);
        dqSetupInfo.setFileDelimiterRegexValue(",");
        dqSetupInfo.setMinPassThresholdPercentValue("90");
        //dqSetupInfo.setSubProcessId(10901);
        dqSetupInfo.setRulesUserNameValue("u");
        dqSetupInfo.setRulesPasswordValue("p");
        dqSetupInfo.setRulesPackageValue("bank");
        dqSetupInfo.setRulesUserName("user");
        dqSetupInfo.setFileDelimiterRegex("|");
        dqSetupInfo.setMinPassThresholdPercent("100");
        dqSetupInfo.setRulesPassword("password");
        dqSetupInfo.setRulesPackage("bank");
        List<Properties> tableProperties = dataQualityDAO.insertDQSetup(dqSetupInfo);
        LOGGER.info("sub process id is " + tableProperties.get(0).getSubProcessId());
        dqSetupInfo.setSubProcessId(tableProperties.get(0).getSubProcessId());
        dataQualityDAO.updateDQSetup(dqSetupInfo);
        LOGGER.info("size of output list " + tableProperties.size());
    }

    @Test
    public void listTest() {
        List<Properties> tableProperties = dataQualityDAO.listDQSetup(0, 10);
        for (Properties p : tableProperties) {
            LOGGER.info(p);
        }

    }

    @Test
    public void deleteTest() {
        dataQualityDAO.deleteDQSetup(10901);


    }
}
