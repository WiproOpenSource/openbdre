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

import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;


/**
 * Created by SU324335 on 17-Nov-15.
 */
public class BatchEnqueuerDAOTest {

    private static final Logger LOGGER = Logger.getLogger(BatchEnqueuerDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    BatchEnqueuerDAO batchEnqueuerDAO;

    @Test
    public void batchEnqueuerTest() throws Exception {

        RegisterFileInfo registerFileInfo = new RegisterFileInfo();
        registerFileInfo.setParentProcessId(1);
        registerFileInfo.setServerId(123461);
        registerFileInfo.setPath("1");
        registerFileInfo.setFileSize(Long.valueOf(1));
        registerFileInfo.setFileHash("1");
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        registerFileInfo.setCreationTs(new Timestamp(now.getTime()));
        registerFileInfo.setBatchId(Long.valueOf(0));
        registerFileInfo.setBatchMarking("1");

        List<BatchConsumpQueue> batchConsumpQueues = batchEnqueuerDAO.batchEnqueue(registerFileInfo);

        LOGGER.info("batchConsumption Queues are " + batchConsumpQueues);

    }


}
