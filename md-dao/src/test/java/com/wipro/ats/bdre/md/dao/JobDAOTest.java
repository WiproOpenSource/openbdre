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

import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class JobDAOTest {
    private static final Logger LOGGER = Logger.getLogger(JobDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    JobDAO jobDAO;
    @Autowired
    StepDAO stepDAO;

    @Ignore
    @Test
    public void testJobInitHalt() throws Exception {
        List<InitJobRowInfo> initJobRowInfos = jobDAO.initJob(1, 1);
        LOGGER.info("count is:" + initJobRowInfos.size());

        Long sub_instance_exec_id = stepDAO.initStep(2);
        stepDAO.haltStep(2);

        jobDAO.haltJob(2, "@a");
        LOGGER.info("The sub process halt step test executed");

        LOGGER.info("The sub process init step test executed with instance-exec-id :" + sub_instance_exec_id);

    }

    @Ignore
    @Test
    public void testHaltJob() throws Exception {
        jobDAO.haltJob(1, "@a");
    }

    @Ignore
    @Test
    public void testTermJob() throws Exception {
        jobDAO.termJob(1);
    }
}