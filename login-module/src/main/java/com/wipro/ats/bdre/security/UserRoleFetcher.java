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

package com.wipro.ats.bdre.security;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.UserRoles;
import com.wipro.ats.bdre.md.beans.table.Users;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.UsersDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 26-02-2015.
 */

@Component
public class UserRoleFetcher extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(UserRoleFetcher.class);

    @Autowired
    private UsersDAO usersDAO;
    @Autowired
    UserRolesDAO userRolesDAO;

    public UserRoleFetcher() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * This method calls proc GetUsers and returns the details of username passed.
     *
     * @param username
     * @return users An instance of Users corresponding to username passed.
     */
    public Users getUser(String username) {

        Users users = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.Users jpaUser = usersDAO.get(username);
            if (jpaUser != null) {
                users = new Users();
                users.setUsername(jpaUser.getUsername());
                users.setPassword(jpaUser.getPassword());
                users.setEnabled((jpaUser.getEnabled()) ? (short) 1 : 0);
            }
        } catch (BDREException e) {
                throw new BDREException(e);
        }
        if (users != null)
            LOGGER.info("user:" + users.getUsername());
        return users;
    }

    /**
     * This method calls proc ListUserRoles and returns user access details corresponding to
     * username passed.
     *
     * @param username
     * @return userroles List of instances of UserRoles.
     */

    public List<UserRoles> getRoles(String username) {
        List<UserRoles> userRoleList = new ArrayList<UserRoles>();

        try {
            List<com.wipro.ats.bdre.md.dao.jpa.UserRoles> jpaUserRoleList = userRolesDAO.listByName(username);
            for (com.wipro.ats.bdre.md.dao.jpa.UserRoles jpaUserRole : jpaUserRoleList) {
                UserRoles userRole = new UserRoles();
                userRole.setUsername(jpaUserRole.getUsers().getUsername());
                userRole.setUserRoleId(jpaUserRole.getUserRoleId());
                userRole.setRole(jpaUserRole.getRole());
                userRole.setCounter(jpaUserRoleList.size());
                LOGGER.info("role is " + userRole.getRole());
                userRoleList.add(userRole);

            }

        } catch (BDREException e) {
            throw new BDREException(e);
        }
        if (userRoleList != null) {
            LOGGER.info("user role list size:" + userRoleList.size());
        }
        return userRoleList;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}
