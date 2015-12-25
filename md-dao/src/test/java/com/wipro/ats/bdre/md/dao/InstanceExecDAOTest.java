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

import com.wipro.ats.bdre.md.dao.jpa.ExecStatus;
import com.wipro.ats.bdre.md.dao.jpa.InstanceExec;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created by MR299389 on 10/16/2015.
 */
public class InstanceExecDAOTest {
    private static final Logger LOGGER = Logger.getLogger(InstanceExecDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    InstanceExecDAO instanceExecDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    ExecStatusDAO execStatusDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of InstanceExec is atleast:" + instanceExecDAO.list(null, 0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of InstanceExec is:" + instanceExecDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        LOGGER.info("name of process of InstanceExec(2) is:" + instanceExecDAO.get((long) 2).getEndTs());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        ExecStatus execStatus = execStatusDAO.get(1);
        Process process = processDAO.get(10835);
        InstanceExec instanceExec = new InstanceExec();
        instanceExec.setProcess(process);
        instanceExec.setExecStatus(execStatus);
        instanceExec.setStartTs(new Date());
        Long id = instanceExecDAO.insert(instanceExec);
        LOGGER.info("InstanceExec is added with Id:" + id);
        instanceExec.setEndTs(new Date());
        instanceExecDAO.update(instanceExec);
        instanceExec = instanceExecDAO.get(id);
        LOGGER.info("Updated EndTs is:" + instanceExec.getEndTs());
        instanceExecDAO.delete(id);
        LOGGER.info("Deleted InstanceExec Entry with ID" + id);
    }
}
