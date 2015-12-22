/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.DeployStatus;
import com.wipro.ats.bdre.md.dao.DeployStatusDAO;
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
 * Created by MI294210 on 8/31/2015.
 */
@Controller
@RequestMapping("/deploystatus")


public class DeployStatusAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(DeployStatusAPI.class);

    @Autowired
    DeployStatusDAO deployStatusDAO;

    /**
     * This method calls proc GetDeployStatus and fetches a record from DeployStatus table corresponding
     * to deployStatusId passed.
     *
     * @param deployStatusId
     * @return restWrapper It contains an instance of DeployStatus corresponding to deployStatusId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer deployStatusId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.DeployStatus jpaDeployStatus = deployStatusDAO.get(deployStatusId.shortValue());
            DeployStatus deployStatus = new DeployStatus();
            if (jpaDeployStatus != null) {
                deployStatus.setDeployStatusId((int) jpaDeployStatus.getDeployStatusId());
                deployStatus.setDescription(jpaDeployStatus.getDescription());
            }
            // deployStatus = s.selectOne("call_procedures.GetDeployStatus", deployStatus);
            restWrapper = new RestWrapper(deployStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + deployStatusId + " selected from DeployStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;

    }

    /**
     * This method calls DeleteDeployStatus and fetches a record corresponding to the deployStatusId passed.
     *
     * @param deployStatusId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer deployStatusId,
            ModelMap model, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            deployStatusDAO.delete(deployStatusId.shortValue());
            // s.delete("call_procedures.DeleteDeployStatus", deployStatus);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + deployStatusId + " deleted from DeployStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListStatusDeploy and fetches a list of DeployStatus records.
     *
     * @param
     * @return restWrapper It contains list of instances of DeployStatus.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.DeployStatus> jpaDeployStatus = deployStatusDAO.list(startPage, pageSize);
            List<DeployStatus> deployStatuses = new ArrayList<DeployStatus>();
            Integer counter =jpaDeployStatus.size();
            for (com.wipro.ats.bdre.md.dao.jpa.DeployStatus deployStatus : jpaDeployStatus) {
                DeployStatus returnDeployStatus = new DeployStatus();
                returnDeployStatus.setDeployStatusId((int) deployStatus.getDeployStatusId());
                returnDeployStatus.setDescription(deployStatus.getDescription());
                returnDeployStatus.setCounter(counter);
                deployStatuses.add(returnDeployStatus);
            }
            // List<DeployStatus> deployStatuses = s.selectList("call_procedures.GetDeployStatuses", deployStatus);
            restWrapper = new RestWrapper(deployStatuses, RestWrapper.OK);
            LOGGER.info("All records listed from DeployStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls UpdateDeployStatus and updates the record passed. It also validates
     * the values of the record passed.
     *
     * @param deployStatus  Instance of DeployStatus.
     * @param bindingResult
     * @return restWrapper The updated instance of DeployStatus.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("deploystatus")
                       @Valid DeployStatus deployStatus, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into update for deploy_status table");
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
            com.wipro.ats.bdre.md.dao.jpa.DeployStatus jpaDeployStatus = new com.wipro.ats.bdre.md.dao.jpa.DeployStatus();
            jpaDeployStatus.setDeployStatusId(deployStatus.getDeployStatusId().shortValue());
            jpaDeployStatus.setDescription(deployStatus.getDescription());
            deployStatusDAO.update(jpaDeployStatus);
            LOGGER.debug("Updating Deploy Status Id" + jpaDeployStatus.getDeployStatusId());
            //DeployStatus deployStatuses = s.selectOne("call_procedures.UpdateDeployStatus", deployStatus);

            LOGGER.debug("Exiting from update for deploy_status table");
            restWrapper = new RestWrapper(deployStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + deployStatus.getDeployStatusId() + " updated in DeployStatus by User:" + principal.getName() + deployStatus);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertDeployStatus and adds a new record in the database. It also validates the
     * values passed.
     *
     * @param deployStatus  Instance of DeployStatus.
     * @param bindingResult
     * @return restWrapper Instance of DeployStatus passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("deploystatus")
                       @Valid DeployStatus deployStatus, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into insert for deploy_status table");
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
            com.wipro.ats.bdre.md.dao.jpa.DeployStatus jpaDeployStatus = new com.wipro.ats.bdre.md.dao.jpa.DeployStatus();
            jpaDeployStatus.setDeployStatusId(deployStatus.getDeployStatusId().shortValue());
            jpaDeployStatus.setDescription(deployStatus.getDescription());
            Short deployStatusId = deployStatusDAO.insert(jpaDeployStatus);
            deployStatus.setDeployStatusId(Integer.valueOf(deployStatusId));
            System.out.println("Deploy Status Id" + jpaDeployStatus.getDeployStatusId());
            LOGGER.debug("Deploy Status Id" + deployStatus.getDeployStatusId());
            //DeployStatus deployStatuses = s.selectOne("call_procedures.InsertDeployStatus", deployStatus);
            LOGGER.debug("Exiting from insert for deploy_status table");
            restWrapper = new RestWrapper(deployStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + deployStatus.getDeployStatusId() + " inserted in DeployStatus by User:" + principal.getName() + deployStatus);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * @return
     */

    @RequestMapping(value = {"/options", "/options/"}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapperOptions listOptions() {

        RestWrapperOptions restWrapperOptions = null;
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.DeployStatus> jpaDeployStatus = deployStatusDAO.list(0, 0);
            List<DeployStatus> deployStatuses = new ArrayList<DeployStatus>();
            for (com.wipro.ats.bdre.md.dao.jpa.DeployStatus deployStatus : jpaDeployStatus) {
                DeployStatus returnDeployStatus = new DeployStatus();
                returnDeployStatus.setDeployStatusId((int) deployStatus.getDeployStatusId());
                returnDeployStatus.setDescription(deployStatus.getDescription());
                returnDeployStatus.setCounter(jpaDeployStatus.size());
                deployStatuses.add(returnDeployStatus);
            }
            // List<DeployStatus> deployStatuses = s.selectList("call_procedures.GetDeployStatuses");

            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (DeployStatus deploy : deployStatuses) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(deploy.getDescription(), deploy.getDeployStatusId());
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapperOptions.ERROR);
        }
        return restWrapperOptions;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}



