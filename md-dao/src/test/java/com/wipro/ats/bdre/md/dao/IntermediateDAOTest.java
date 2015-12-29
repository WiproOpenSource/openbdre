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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.logging.Logger;

public class IntermediateDAOTest {
    private static final Logger LOGGER = Logger.getLogger("IntermediateDAOTest.class");

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    private IntermediateDAO intermediateDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of intermediate list is atleast:" + intermediateDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Total intermediate is:" + intermediateDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {
        IntermediateId intermediateId = new IntermediateId();
        intermediateId.setInterKey("test");
        intermediateId.setUuid("uuid");
        LOGGER.info("intermediate(10802) value:" + intermediateDAO.get(intermediateId).getInterValue());
    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        Intermediate intermediate = new Intermediate();
        intermediate.setInterValue("testing insert");
        IntermediateId intermediateId = new IntermediateId();
        intermediateId.setUuid("sdafsdr44tradf");
        intermediateId.setInterKey("testing");
        intermediate.setId(intermediateId);
        LOGGER.info("" + intermediate.getInterValue() + " " + intermediate.getId().getUuid() + " " + intermediate.getId().getInterKey());
        intermediateDAO.insert(intermediate);
        intermediate.setInterValue("test2");
        intermediateDAO.update(intermediate);
        LOGGER.info("" + intermediate.getInterValue() + " " + intermediate.getId().getUuid() + " " + intermediate.getId().getInterKey());
        intermediateDAO.delete(intermediateId);
        //LOGGER.info("inserted ID is " + insertedIntermediateId.getInterKey() + "  " + insertedIntermediateId.getUuid());
    }


}