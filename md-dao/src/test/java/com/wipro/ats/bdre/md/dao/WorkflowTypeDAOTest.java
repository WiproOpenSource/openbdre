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

import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
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
 * Created by PR324290 on 10/28/2015.
 */
public class WorkflowTypeDAOTest {


    private static final Logger LOGGER = Logger.getLogger(WorkflowTypeDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Autowired
    WorkflowTypeDAO workflowTypeDAO;
    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of WorkflowType is atleast:" + workflowTypeDAO.list(0, 10).size());
    }
    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of WorkflowType is:" + workflowTypeDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {

        LOGGER.info("WorkflowType(0) name:" + workflowTypeDAO.get(1).getWorkflowTypeName());
    }



    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        WorkflowType workflowType = new WorkflowType();
        workflowType.setWorkflowId(100);
        workflowType.setWorkflowTypeName("Test");
        int workflowTypeId = (Integer) workflowTypeDAO.insert(workflowType);
        LOGGER.info("New WorkflowType added with ID:" + workflowTypeId);
        workflowType.setWorkflowTypeName("Test_updated");
        workflowTypeDAO.update(workflowType);
        workflowType = workflowTypeDAO.get(workflowTypeId);
        assertEquals("Test_updated",workflowType.getWorkflowTypeName());
        LOGGER.info("Updated workflowType with name:" + workflowType.getWorkflowTypeName());
        assertNotNull(workflowTypeDAO.list(0, 10));
        workflowTypeDAO.delete(workflowTypeId);
        LOGGER.info("WorkflowType Deleted with ID:" + workflowTypeId);
        LOGGER.info("Size of WorkflowType is:" + workflowTypeDAO.totalRecordCount());
      //  LOGGER.info("Size of WorkflowType is atleast:" + workflowTypeDAO.list(0, 10).size());
    }
}
