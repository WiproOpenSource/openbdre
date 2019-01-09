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
import com.wipro.ats.bdre.md.dao.jpa.LineageRelation;
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
public class LineageRelationDAOTest {

    private static final Logger LOGGER = Logger.getLogger(LineageRelationDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Autowired
    LineageRelationDAO lineageRelationDAO;
    @Autowired
    LineageQueryTypeDAO lineageQueryTypeDAO;
    @Autowired
    LineageQueryDAO lineageQueryDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of LineageRelation is atleast:" + lineageRelationDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of LineageRelation is:" + lineageRelationDAO.totalRecordCount());
    }


    @Test
    @Ignore
    public void testGet() throws Exception {

        LOGGER.info("LineageRelation(0) relation id :" + lineageRelationDAO.get("Test").getRelationId());
    }


    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        LineageQuery lineageQuery = new LineageQuery();
        lineageQuery.setQueryId("Test5");
        lineageQuery.setCreateTs(new Date());
        LineageQueryType lineageQueryType = lineageQueryTypeDAO.get(1);
        lineageQuery.setLineageQueryType(lineageQueryType);
        String lineageQueryId = lineageQueryDAO.insert(lineageQuery);
        LineageRelation lineageRelation = new LineageRelation();
        lineageRelation.setRelationId("Test_Id5");
        lineageRelation.setLineageQuery(lineageQuery);
        String lineageRelationId = lineageRelationDAO.insert(lineageRelation);
        LOGGER.info("New LineageRelation added with ID:" + lineageRelationId);
        lineageRelation.setDotString("Test Dot String");
        lineageRelationDAO.update(lineageRelation);
        lineageRelation = lineageRelationDAO.get(lineageRelationId);
        LOGGER.info("Updated LineageRelation's Dot string is:" + lineageRelation.getDotString());
        assertEquals("Test Dot String",lineageRelation.getDotString());
        assertNotNull(lineageRelationDAO.list(0,10));
        lineageRelationDAO.delete(lineageRelationId);
        lineageQueryDAO.delete(lineageQueryId);
        LOGGER.info("LineageRelation Deleted with ID:" + lineageRelationId);
        LOGGER.info("Size of LineageRelation is:" + lineageRelationDAO.totalRecordCount());

    }
}
