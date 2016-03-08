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

/**
 * Created by SU324335 on 3/8/2016.
 */
import com.wipro.ats.bdre.md.dao.jpa.Adq;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;
public class AdqDAOTest {

    private static final Logger LOGGER = Logger.getLogger(AdqDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    AdqDAO adqDAO;

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        Adq adq = new Adq();
        adq.set
        Integer adqId = adqDAO.insert(adq);
        LOGGER.info("Adq is added with Id:" + adqId);

        adqDAO.update(adq);
        adq = adqDAO.get(adqId);
        assertEquals("Test Updated",adqStatus.getDescription());
        LOGGER.info("Updated Description is:" + adqStatus.getDescription());
        adqStatusDAO.delete(adqStateId);
        LOGGER.info("Deleted AdqStatus Entry with ID" + adqStateId);
    }

}
