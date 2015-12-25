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

import com.wipro.ats.bdre.md.dao.jpa.LineageNodeType;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by PR324290 on 10/28/2015.
 */
public class LineageNodeTypeDAOTest {

    private static final Logger LOGGER = Logger.getLogger(LineageNodeTypeDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    LineageNodeTypeDAO lineageNodeTypeDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of LineageNodeType is atleast:" + lineageNodeTypeDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of LineageNodeType is:" + lineageNodeTypeDAO.totalRecordCount());
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("LineageNodeType(0) typename:" + lineageNodeTypeDAO.get(Integer.valueOf(1)).getNodeTypeName());
    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        LineageNodeType lineageNodeType = new LineageNodeType();
        lineageNodeType.setNodeTypeId(0);
        lineageNodeType.setNodeTypeName("Test");
        int lineageNodeTypeId = lineageNodeTypeDAO.insert(lineageNodeType);
        LOGGER.info("New LineageNodeType added with ID:" + lineageNodeTypeId);
        lineageNodeType.setNodeTypeName("Updated Test");
        lineageNodeTypeDAO.update(lineageNodeType);
        lineageNodeType = lineageNodeTypeDAO.get(lineageNodeTypeId);
        LOGGER.info("Updated lineageNodeType with typename:" + lineageNodeType.getNodeTypeName());
        lineageNodeTypeDAO.delete(lineageNodeTypeId);
        LOGGER.info("LineageNodeType Deleted with ID:" + lineageNodeTypeId);
    }
}
