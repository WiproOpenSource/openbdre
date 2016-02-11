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
import com.wipro.ats.bdre.md.beans.table.UserRoles;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Users;
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
@RequestMapping("/userroles")
public class UserRolesAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(UserRolesAPI.class);
    @Autowired
    UserRolesDAO userRolesDAO;

    /**
     * This method calls GetUserRoles and fetches a record corresponding to id passed.
     *
     * @param
     * @return restWrapper It contains an instance of UserRoles corresponding to id passed.
     */

    @RequestMapping(value = "/{uname}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper list(
            @PathVariable("uname") String username, Principal principal,
            ModelMap model) {
        RestWrapper restWrapper = null;
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.UserRoles> jpaUserRolesList = userRolesDAO.listByName(username);
            List<UserRoles> userRolesList = new ArrayList<UserRoles>();

            Integer counter=jpaUserRolesList.size();
            for (com.wipro.ats.bdre.md.dao.jpa.UserRoles userRoles : jpaUserRolesList) {
                UserRoles userRole = new UserRoles();
                userRole.setUserRoleId(userRoles.getUserRoleId());
                userRole.setUsername(userRoles.getUsers().getUsername());
                userRole.setRole(userRoles.getRole());
             userRole.setCounter(counter);

                userRolesList.add(userRole);
            }
            //  List<UserRoles> userRolesList = s.selectList("call_procedures.ListUserRoles", userroles);
            restWrapper = new RestWrapper(userRolesList, RestWrapper.OK);
            LOGGER.info("Records with username:" + username + " selected from  by UserRoles:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls the proc deleteUserRoles and deletes a record corresponding to the id passed.
     *
     * @param userRoleId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{urid}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("urid") Integer userRoleId, Principal principal,
            ModelMap model) {
        RestWrapper restWrapper = null;
        try {
            userRolesDAO.delete(userRoleId);
            // s.delete("call_procedures.DeleteUserRoles", userroles);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + userRoleId + " deleted from UserRoles by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateUserRoles and updates the values. It also validates the values passed.
     *
     * @param userRoles     Instance of UserRoles.
     * @param bindingResult
     * @return restWrapper Contains updated instance of UserRoles .
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("userroles")
                       @Valid UserRoles userRoles, BindingResult bindingResult, Principal principal) {
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

            com.wipro.ats.bdre.md.dao.jpa.UserRoles jpaUserRoles = new com.wipro.ats.bdre.md.dao.jpa.UserRoles();
            jpaUserRoles.setUserRoleId(userRoles.getUserRoleId());
            jpaUserRoles.setRole(userRoles.getRole());
            Users users = new Users();
            users.setUsername(userRoles.getUsername());
            jpaUserRoles.setUsers(users);
            userRolesDAO.update(jpaUserRoles);
            //  UserRoles userRolesUpdate = s.selectOne("call_procedures.UpdateUserRoles", userroles);

            restWrapper = new RestWrapper(userRoles, RestWrapper.OK);
            LOGGER.info("Record with ID:" + userRoles.getUserRoleId() + " updated in UserRoles by User:" + principal.getName() + userRoles);

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertUserRoles and adds a record in UserRoles table. It also validates the values passed.
     *
     * @param userRoles     Instance of UserRoles.
     * @param bindingResult
     * @return restWrapper It contains an instance of UserRoles newly added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("userroles")
                       @Valid UserRoles userRoles, BindingResult bindingResult, Principal principal) {
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
            com.wipro.ats.bdre.md.dao.jpa.UserRoles jpaUserRoles = new com.wipro.ats.bdre.md.dao.jpa.UserRoles();
            jpaUserRoles.setUserRoleId(userRoles.getUserRoleId());
            jpaUserRoles.setRole(userRoles.getRole());
            Users users = new Users();
            users.setUsername(userRoles.getUsername());
            jpaUserRoles.setUsers(users);
            Integer userRolesId = userRolesDAO.insert(jpaUserRoles);
            userRoles.setUserRoleId(userRolesId);
            //  UserRoles userRolesInsert = s.selectOne("call_procedures.InsertUserRoles", userroles);

            restWrapper = new RestWrapper(userRoles, RestWrapper.OK);
            LOGGER.info("Record with ID:" + userRoles.getUserRoleId() + " inserted in UserRoles by User:" + principal.getName() + userRoles);

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }


}
