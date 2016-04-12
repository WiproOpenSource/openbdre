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

import com.wipro.ats.bdre.md.dao.jpa.UserRoles;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by PR324290 on 10/28/2015.
 */
public class UserRolesDAOTest {

    private static final Logger LOGGER = Logger.getLogger(UserRolesDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Autowired
    UserRolesDAO userRolesDAO;
    @Autowired
    UsersDAO usersDAO;

    @Ignore
    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of UserRoles is atleast:" + userRolesDAO.list(0, 10).size());
    }

    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of UserRoles is:" + userRolesDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {

        LOGGER.info("UserRoles(1) role:" + userRolesDAO.get(2).getRole());
    }


    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        UserRoles userRoles = new UserRoles();
        Users user = usersDAO.get("admin");
        userRoles.setUsers(user);
        userRoles.setRole("Role");
        Integer userRolesId = userRolesDAO.insert(userRoles);
        LOGGER.info("New UserRoles added with ID:" + userRolesId);
        userRoles.setRole("Role_updated");
        userRolesDAO.update(userRoles);
        userRoles = userRolesDAO.get(userRolesId);
        assertEquals("Role_updated",userRoles.getRole());
        LOGGER.info("Updated userRoles with role:" + userRoles.getRole());
        assertNotNull(userRolesDAO.list(0,10));
        userRolesDAO.delete(userRolesId);
        LOGGER.info("UserRoles Deleted with ID:" + userRolesId);
        LOGGER.info("Size of UserRoles is:" + userRolesDAO.totalRecordCount());

    }

    @Test
    public void testListByName() throws Exception {
        List<UserRoles> userRolesList = userRolesDAO.listByName("admin");
        LOGGER.info("No. of records fetched:" + userRolesList.get(0).getRole());

    }
   @Test
    public  void testDiffRoles() throws Exception{
        Map<String,Integer> objects=userRolesDAO.diffRoleList();
       Iterator it = objects.entrySet().iterator();
       while (it.hasNext()) {
           Map.Entry pair = (Map.Entry)it.next();
           System.out.println(pair.getKey() + " = " + pair.getValue());
           it.remove(); // avoids a ConcurrentModificationException
       }
    }
}
