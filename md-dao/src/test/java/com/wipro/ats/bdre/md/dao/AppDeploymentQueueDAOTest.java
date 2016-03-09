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

/**
 * Created by SU324335 on 3/8/2016.
 */
import com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueue;
import com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
public class AppDeploymentQueueDAOTest {

    private static final Logger LOGGER = Logger.getLogger(AppDeploymentQueueDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    AppDeploymentQueueDAO appDeploymentQueueDAO;

    @Ignore
    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        AppDeploymentQueue adq = new AppDeploymentQueue();
        adq.setAppName("Test");
        adq.setAppDomain("Banking");
        AppDeploymentQueueStatus adqStatus=new AppDeploymentQueueStatus();
        adqStatus.setDescription("Merged");
        adqStatus.setAppDeployStatusId((short) 0);
        adq.setAppDeploymentQueueStatus(adqStatus);
        Process process=new Process();
        process.setProcessId(141);
        adq.setProcess(process);
        Integer adqId = appDeploymentQueueDAO.insert(adq);
        LOGGER.info("Adq is added with Id:" + adqId);

        appDeploymentQueueDAO.update(adq);
        adq = appDeploymentQueueDAO.get(adqId);
        assertEquals("Test Updated",adq.getAppName());
        LOGGER.info("Updated Description is:" + adqStatus.getDescription());
        LOGGER.info("Deleted AdqStatus Entry with ID" + adq.getAppDeploymentQueueStatus().getAppDeployStatusId());
    }

}
