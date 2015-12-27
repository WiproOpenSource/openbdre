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

import com.wipro.ats.bdre.md.beans.ProcessAncestorsInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class ProcessAncestorsDAOTest {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ProcessAncestorsDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired()
    ProcessAncestorsDAO processAncestorsDAO;

    @Test
    @Ignore
    public void testListUpstreams() throws Exception {
        List<Process> upstreamProcessList = processAncestorsDAO.listUpstreams(10802);
        LOGGER.info("Total number of upstream processes:" + upstreamProcessList.size());
        for (Process process : upstreamProcessList) {
            LOGGER.info(process);
        }

    }

    @Test
    @Ignore
    public void testFetchDetails() throws Exception {
        ProcessAncestorsInfo processAncestorsInfo = processAncestorsDAO.fetchDetails(10802);
        LOGGER.info("process deploy details:" + processAncestorsInfo);

    }
}