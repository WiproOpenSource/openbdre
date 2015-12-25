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

import com.wipro.ats.bdre.md.dao.jpa.Intermediate;
import com.wipro.ats.bdre.md.dao.jpa.IntermediateId;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by MR299389 on 10/28/2015.
 */
public class HistoryDataImportDAOTest {
    private static final Logger LOGGER = Logger.getLogger(HistoryDataImportDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    IntermediateDAO intermediateDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of intermediate is atleast:" + intermediateDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of intermediate is:" + intermediateDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        IntermediateId intermediateId = new IntermediateId();
        intermediateId.setInterKey("1");
        intermediateId.setUuid("");
        LOGGER.info("Value of intermediateId(1) is:" + intermediateDAO.get(intermediateId).getInterValue());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        IntermediateId intermediateId = new IntermediateId();
        intermediateId.setInterKey("1");
        intermediateId.setUuid("Test UUID");
        Intermediate intermediate = new Intermediate();
        intermediate.setInterValue("Test Value");
        intermediate.setId(intermediateId);
        intermediateId = intermediateDAO.insert(intermediate);
        LOGGER.info("intermediate is added with key:" + intermediateId.getInterKey());
        intermediate.setInterValue("Updated Value");
        intermediateDAO.update(intermediate);
        intermediate = intermediateDAO.get(intermediateId);
        LOGGER.info("Updated value is:" + intermediate.getInterValue());
        intermediateDAO.delete(intermediateId);
        LOGGER.info("Deleted intermediate Entry with Key:" + intermediateId.getInterKey());
    }
}