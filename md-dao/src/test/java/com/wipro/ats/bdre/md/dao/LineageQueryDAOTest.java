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

import com.wipro.ats.bdre.md.dao.jpa.LineageQuery;
import com.wipro.ats.bdre.md.dao.jpa.LineageQueryType;
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
public class LineageQueryDAOTest {

    private static final Logger LOGGER = Logger.getLogger(LineageQueryDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Autowired
    private LineageQueryDAO lineageQueryDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of LineageQuery is atleast:" + lineageQueryDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of LineageQuery is:" + lineageQueryDAO.totalRecordCount());
    }


    @Test
    @Ignore
    public void testGet() throws Exception {

        LOGGER.info("LineageQuery(0) Create time :" + lineageQueryDAO.get("Test1").getCreateTs());
    }


    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        LineageQuery lineageQuery = new LineageQuery();
        lineageQuery.setQueryId("Test4");

        LineageQueryType lineageQueryType = new LineageQueryType();
        lineageQueryType.setQueryTypeId(1);
        lineageQueryType.setQueryTypeName("table");
        lineageQuery.setLineageQueryType(lineageQueryType);
        lineageQuery.setCreateTs(new Date());

        String lineageQueryId = lineageQueryDAO.insert(lineageQuery);
        LOGGER.info("New LineageQuery added with ID:" + lineageQueryId);
        lineageQuery.setCreateTs(new Date());
        lineageQuery.setQueryString("Updatetest");
        lineageQueryDAO.update(lineageQuery);
        lineageQuery = lineageQueryDAO.get(lineageQueryId);
        LOGGER.info("Updated lineageQuery with Create time :" + lineageQuery.getCreateTs());
        assertEquals("Updatetest",lineageQuery.getQueryString());
        assertNotNull(lineageQueryDAO.list(0,10));
        lineageQueryDAO.delete(lineageQueryId);
        LOGGER.info("LineageQuery Deleted with ID:" + lineageQueryId);
        LOGGER.info("Size of LineageQuery is:" + lineageQueryDAO.totalRecordCount());

    }

}
