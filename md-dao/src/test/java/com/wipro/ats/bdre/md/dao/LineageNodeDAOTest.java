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

import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import com.wipro.ats.bdre.md.dao.jpa.LineageNodeType;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

import java.util.Date;

/**
 * Created by PR324290 on 10/28/2015.
 */
public class LineageNodeDAOTest {

    private static final Logger LOGGER = Logger.getLogger(LineageNodeDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Autowired
    LineageNodeDAO lineageNodeDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of LineageNode is atleast:" + lineageNodeDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of LineageNode is:" + lineageNodeDAO.totalRecordCount());
    }


    @Test
    @Ignore
    public void testGet() throws Exception {

        LOGGER.info("LineageNode(0) Insert Time is :" + lineageNodeDAO.get("Test1").getInsertTs());
    }


    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        LineageNode lineageNode = new LineageNode();
        lineageNode.setNodeId("Test2");
        lineageNode.setDisplayName("test");
        lineageNode.setLineageNodeType(new LineageNodeType(1, "table"));
        lineageNode.setInsertTs(new Date());
        String lineageNodeId = lineageNodeDAO.insert(lineageNode);
        LOGGER.info("New LineageNode added with ID:" + lineageNodeId);
        lineageNode.setDisplayName("test update");
        lineageNode.setInsertTs(new Date());
        lineageNodeDAO.update(lineageNode);
        lineageNode = lineageNodeDAO.get(lineageNodeId);
        assertEquals("test update",lineageNode.getDisplayName());
        assertNotNull(lineageNodeDAO.list(0,10));
        LOGGER.info("Updated lineageNode with Inset Time:" + lineageNode.getInsertTs());
        lineageNodeDAO.delete(lineageNodeId);
        LOGGER.info("LineageNode Deleted with ID:" + lineageNodeId);
        LOGGER.info("Size of LineageNode is:" + lineageNodeDAO.totalRecordCount());

    }

}
