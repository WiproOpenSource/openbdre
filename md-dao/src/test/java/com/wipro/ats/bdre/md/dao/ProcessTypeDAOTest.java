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

import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;
/**
 * Created by MR299389 on 10/16/2015.
 */
public class ProcessTypeDAOTest {
    private static final Logger LOGGER = Logger.getLogger(ProcessTypeDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of ProcessType is atleast:" + processTypeDAO.list(null, 0, 10).size());
    }
    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of ProcessType is:" + processTypeDAO.totalRecordCount(null));
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("ProcessType(1) Name is:" + processTypeDAO.get(1).getProcessTypeName());

    }


    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        ProcessType processType = new ProcessType();
        processType.setProcessTypeName("test");
        processType.setParentProcessTypeId(24);
        processType.setProcessTypeId(233);
        Integer id = processTypeDAO.insert(processType);
        LOGGER.info("ProcessType is added with Id:" + id);
        processType.setProcessTypeName("Test ProcessType");
        processTypeDAO.update(processType);
        processType = processTypeDAO.get(id);
        assertEquals("Test ProcessType", processType.getProcessTypeName());
        LOGGER.info("Updated ProcessTypeName is:" + processType.getProcessTypeName());
        assertNotNull(processTypeDAO.listFull(0, 10));

        processTypeDAO.delete(id);
        LOGGER.info("Deleted ProcessType Entry with ID:" + id);
       // LOGGER.info("Size of ProcessType is atleast:" + processTypeDAO.listFull(0, 10).size());
        LOGGER.info("Size of ProcessType is atleast:" + processTypeDAO.list(null, 0, 10).size());
        LOGGER.info("Size of ProcessType is:" + processTypeDAO.totalRecordCount(null));

    }
    @Ignore
    @Test
    public void testListFull() throws Exception {
        LOGGER.info("Size of ProcessType is atleast:" + processTypeDAO.listFull(0, 10).size());
    }
}
