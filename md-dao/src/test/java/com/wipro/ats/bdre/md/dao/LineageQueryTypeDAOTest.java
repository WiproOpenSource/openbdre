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

import com.wipro.ats.bdre.md.dao.jpa.LineageQueryType;
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
public class LineageQueryTypeDAOTest {
    private static final Logger LOGGER = Logger.getLogger(LineageQueryTypeDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    LineageQueryTypeDAO lineageQueryTypeDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of LineageQueryType is atleast:" + lineageQueryTypeDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of LineageQueryType is:" + lineageQueryTypeDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("LineageQueryType(0) type name :" + lineageQueryTypeDAO.get(Integer.valueOf(1)).getQueryTypeName());
    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        LineageQueryType lineageQueryType = new LineageQueryType();
        lineageQueryType.setQueryTypeId(0);
        lineageQueryType.setQueryTypeName("Test");
        int lineageQueryTypeId = lineageQueryTypeDAO.insert(lineageQueryType);
        LOGGER.info("New LineageQueryType added with ID:" + lineageQueryTypeId);
        lineageQueryType.setQueryTypeName("Test updated");
        lineageQueryTypeDAO.update(lineageQueryType);
        lineageQueryType = lineageQueryTypeDAO.get(lineageQueryTypeId);
        LOGGER.info("Updated lineageQueryType with type name :" + lineageQueryType.getQueryTypeName());
        lineageQueryTypeDAO.delete(lineageQueryTypeId);
        LOGGER.info("LineageQueryType Deleted with ID:" + lineageQueryTypeId);
    }
}
