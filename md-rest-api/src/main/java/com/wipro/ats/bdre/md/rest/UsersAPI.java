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

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.Users;
import com.wipro.ats.bdre.md.dao.UsersDAO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leela on 26-02-2015.
 */

@Controller
@RequestMapping("/users")
public class UsersAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(UsersAPI.class);
    @Autowired
    UsersDAO usersDAO;

    /**
     * This method calls proc GetUsers and returns the details of username passed.
     *
     * @param username
     * @return restWraper Instance of Users corresponding to passed username.
     */
    @RequestMapping(value = "/{uname}", method = RequestMethod.GET)

    @ResponseBody
    public RestWrapper get(
            @PathVariable("uname") String username, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.Users jpaUsers = usersDAO.get(username);
            com.wipro.ats.bdre.md.beans.table.Users users = new Users();
            if (jpaUsers != null) {
                users.setUsername(jpaUsers.getUsername());
                users.setPassword(jpaUsers.getPassword());

                users.setEnabled((jpaUsers.getEnabled() == true) ? (short) 1 : 0);

            }
            //  users = s.selectOne("call_procedures.GetUsers", users);

            restWrapper = new RestWrapper(users, RestWrapper.OK);
            LOGGER.info("Record with name:" + username + " selected from Users by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteUsers and removes record corresponding to username passed.
     *
     * @param username
     * @return nothing.
     */
    @RequestMapping(value = "/{uname}", method = RequestMethod.DELETE)

    @ResponseBody
    public RestWrapper delete(
            @PathVariable("uname") String username, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {

            usersDAO.delete(username);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with name:" + username + " deleted from Users by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListUsers and returns a list of instances of Users.
     *
     * @param
     * @return restWrapper It contains a list of instances of Users.
     */

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter = usersDAO.totalRecordCount();
            List<com.wipro.ats.bdre.md.dao.jpa.Users> jpaUsers = usersDAO.list(startPage, pageSize);
            List<Users> usersList = new ArrayList<Users>();
            for (com.wipro.ats.bdre.md.dao.jpa.Users users : jpaUsers) {
                Users returnUsers = new Users();
                returnUsers.setUsername(users.getUsername());
                returnUsers.setPassword(users.getPassword());

                returnUsers.setEnabled((users.getEnabled() == true) ? (short) 1 : 0);
                returnUsers.setCounter(counter);
                usersList.add(returnUsers);
            }

            restWrapper = new RestWrapper(usersList, RestWrapper.OK);
            LOGGER.info("All records listed from Users by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls UpdateUsers and updates the values of the instance of Users passed. It also
     * validates the values passed.
     *
     * @param users         Instance of Users.
     * @param bindingResult
     * @return restWrapper It contains the updated instance of Users passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper update(@ModelAttribute("users")
                       @Valid Users users, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder("<p>Please fix following errors and try again<p><ul>");
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append("<li>");
                errorMessages.append(error.getField());
                errorMessages.append(". Bad value: '");
                errorMessages.append(error.getRejectedValue());
                errorMessages.append("'</li>");
            }
            errorMessages.append("</ul>");
            restWrapper = new RestWrapper(errorMessages.toString(), RestWrapper.ERROR);
            return restWrapper;
        }
        try {
            String hashedPassword = DigestUtils.sha1Hex(users.getPassword());
            users.setPassword(hashedPassword);
            com.wipro.ats.bdre.md.dao.jpa.Users jpaUsers = new com.wipro.ats.bdre.md.dao.jpa.Users();
            jpaUsers.setPassword(hashedPassword);
            jpaUsers.setUsername(users.getUsername());

            jpaUsers.setEnabled((users.getEnabled() == 1) ? true : false);
            usersDAO.update(jpaUsers);


            restWrapper = new RestWrapper(users, RestWrapper.OK);
            LOGGER.info("Record with name:" + users.getUsername() + " updated in Users by User:" + principal.getName() + users);

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;
    }

    /**
     * This method calls InsertUsers and adds a record passed in Users table. It also validates the values passed.
     *
     * @param users         Instance of Users.
     * @param bindingResult
     * @return restWrapper Instance of Users newly added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)

    @ResponseBody
    public RestWrapper insert(@ModelAttribute("users")
                       @Valid Users users, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder("<p>Please fix following errors and try again<p><ul>");
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append("<li>");
                errorMessages.append(error.getField());
                errorMessages.append(". Bad value: '");
                errorMessages.append(error.getRejectedValue());
                errorMessages.append("'</li>");
            }
            errorMessages.append("</ul>");
            restWrapper = new RestWrapper(errorMessages.toString(), RestWrapper.ERROR);
            return restWrapper;
        }
        try {

            String hashedPassword = DigestUtils.sha1Hex(users.getPassword());
            com.wipro.ats.bdre.md.dao.jpa.Users jpaUsers = new com.wipro.ats.bdre.md.dao.jpa.Users();
            jpaUsers.setPassword(hashedPassword);
            jpaUsers.setUsername(users.getUsername());
            jpaUsers.setEnabled((users.getEnabled() == 1) ? true : false);
            usersDAO.insert(jpaUsers);

            restWrapper = new RestWrapper(users, RestWrapper.OK);
            LOGGER.info("Record with ID:" + users.getUsername() + " inserted in Users by User:" + principal.getName() + users);

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}
