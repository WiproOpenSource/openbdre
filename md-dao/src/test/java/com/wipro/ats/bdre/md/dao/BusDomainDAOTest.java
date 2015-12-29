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

import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

public class BusDomainDAOTest {
    private static final Logger LOGGER = Logger.getLogger(BusDomainDAOTest.class);

    @Before
    //every test case must have this method
    //to init the beans
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired()
    BusDomainDAO busDomainDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of Busdomain is atleast: " + busDomainDAO.list(0, 10).size());
    }
    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of Busdomain is: " + busDomainDAO.totalRecordCount());
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        LOGGER.info("Busdomain(1) is: " + busDomainDAO.get(1).getBusDomainName());
    }

    @Ignore
    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        BusDomain busDomain = new BusDomain();
        busDomain.setDescription("Dmain desc");
        busDomain.setBusDomainName("Domain");
        busDomain.setBusDomainOwner("John De");
        Integer busDomainId = busDomainDAO.insert(busDomain);
        LOGGER.info("New BusDomain added with id: " + busDomainId);
        busDomain.setBusDomainOwner("Jane Doe");
        busDomainDAO.update(busDomain);
        busDomain = busDomainDAO.get(busDomainId);
        assertEquals("Jane Doe",busDomain.getBusDomainOwner());
        LOGGER.info("New BusDomain updated with new owner: " + busDomain.getBusDomainOwner());
        assertNotNull(busDomainDAO.list(0, 10));
        busDomainDAO.delete(busDomainId);
        LOGGER.info("Deleted busdomain");
       // LOGGER.info("Size of Busdomain is atleast: " + busDomainDAO.list(0, 10).size());
        LOGGER.info("Size of Busdomain is: " + busDomainDAO.totalRecordCount());

    }
}