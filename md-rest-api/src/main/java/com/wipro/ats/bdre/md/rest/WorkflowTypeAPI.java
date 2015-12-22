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
import com.wipro.ats.bdre.md.beans.table.WorkflowType;
import com.wipro.ats.bdre.md.dao.WorkflowTypeDAO;
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
 * Created by leela on 13-04-2015.
 */
@Controller
@RequestMapping("/workflowtype")


public class WorkflowTypeAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger("WorkflowTypeAPI.class");


    /**
     * This method calls proc GetWorkflowType and fetches a record corresponding to the
     * workflowTypeId.
     *
     * @param workflowTypeId
     * @return restWrapper Instance of WorkflowType corresponding to passed workflowTypeId.
     */
    @Autowired
    private WorkflowTypeDAO workflowTypeDAO;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public

    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer workflowTypeId, Principal principal
    ) {
        RestWrapper restWrapper = null;
        try {

            WorkflowType workflowType = new WorkflowType();
            com.wipro.ats.bdre.md.dao.jpa.WorkflowType jpaWorkflowType = workflowTypeDAO.get(workflowTypeId);
            //workflowType = s.selectOne("call_procedures.GetWorkflowType", workflowType);
            if (jpaWorkflowType != null) {
                workflowType.setWorkflowId(jpaWorkflowType.getWorkflowId());
                workflowType.setWorkflowTypeName(jpaWorkflowType.getWorkflowTypeName());
            }
            restWrapper = new RestWrapper(workflowType, RestWrapper.OK);
            LOGGER.info("Record with ID:" + workflowTypeId + " selected from WorkflowType by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteWorkflowType and deletes a record corresponding to the passed
     * WorkflowId.
     *
     * @param workflowId
     * @param model
     * @return nothing
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public

    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer workflowId,
            ModelMap model, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            workflowTypeDAO.delete(workflowId);
            //  s.delete("call_procedures.DeleteWorkflowType", workflowType);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + workflowId + " deleted from WorkflowType by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetWorkflowTypes and fetches the list of instances of WorkflowType.
     *
     * @param
     * @return restWrapper List of instances of WorkflowType.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public

    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            List<WorkflowType> workflowTypes = new ArrayList<WorkflowType>();
            List<com.wipro.ats.bdre.md.dao.jpa.WorkflowType> jpaWorkflowTypes = workflowTypeDAO.list(startPage, pageSize);
            Integer counter=jpaWorkflowTypes.size();
            for (com.wipro.ats.bdre.md.dao.jpa.WorkflowType wfType : jpaWorkflowTypes) {
                WorkflowType workflowType = new WorkflowType();
                workflowType.setWorkflowId(wfType.getWorkflowId());
                workflowType.setWorkflowTypeName(wfType.getWorkflowTypeName());
                workflowType.setCounter(counter);
                workflowTypes.add(workflowType);

            }
            // List<ProcessType> workflowTypes = s.selectList("call_procedures.GetWorkflowTypes", workflowType);
            restWrapper = new RestWrapper(workflowTypes, RestWrapper.OK);
            LOGGER.info("All records listed from WorkflowType by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateWorkflowType and updates the values of the instance of
     * WorkflowType passed. It also validates the values passed.
     *
     * @param workflowType  Instance of WorkflowType.
     * @param bindingResult
     * @return restWrapper Updated instance of WorkflowType.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public

    @ResponseBody
    RestWrapper update(@ModelAttribute("processtype")
                       @Valid WorkflowType workflowType, BindingResult bindingResult, Principal principal) {

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
            com.wipro.ats.bdre.md.dao.jpa.WorkflowType jpaWorkflowType = new com.wipro.ats.bdre.md.dao.jpa.WorkflowType();
            jpaWorkflowType.setWorkflowId(workflowType.getWorkflowId());
            jpaWorkflowType.setWorkflowTypeName(workflowType.getWorkflowTypeName());
            workflowTypeDAO.update(jpaWorkflowType);
            //  WorkflowType workflowTypes = s.selectOne("call_procedures.UpdateWorkflowType", workflowType);

            restWrapper = new RestWrapper(workflowType, RestWrapper.OK);
            LOGGER.info("Record with ID:" + workflowType.getWorkflowId() + " updated in BatchStatus by User:" + principal.getName() + workflowType);

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertWorkflowType and adds in record in the database. It also validates the values
     * of the record passed.
     *
     * @param workflowType  Instance of WorkflowType.
     * @param bindingResult
     * @return restWrapper Instance of WorkflowType passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public

    @ResponseBody
    RestWrapper insert(@ModelAttribute("processtype")
                       @Valid WorkflowType workflowType, BindingResult bindingResult, Principal principal) {

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
            com.wipro.ats.bdre.md.dao.jpa.WorkflowType jpaWorkflowType = new com.wipro.ats.bdre.md.dao.jpa.WorkflowType();
            jpaWorkflowType.setWorkflowId(workflowType.getWorkflowId());
            jpaWorkflowType.setWorkflowTypeName(workflowType.getWorkflowTypeName());
            workflowTypeDAO.insert(jpaWorkflowType);
            // WorkflowType workflowTypes = s.selectOne("call_procedures.InsertWorkflowType", workflowType);

            restWrapper = new RestWrapper(workflowType, RestWrapper.OK);
            LOGGER.info("Record with ID:" + workflowType.getWorkflowId() + " inserted in BatchStatus by User:" + principal.getName() + workflowType);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method is used to get the dropdown list of WorkflowType.
     *
     * @return
     */
    @RequestMapping(value = {"/optionslist"}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapperOptions listOptions() {

        RestWrapperOptions restWrapperOptions = null;
        try {
            List<WorkflowType> workflowTypes = new ArrayList<WorkflowType>();
            List<com.wipro.ats.bdre.md.dao.jpa.WorkflowType> jpaWorkflowTypes = workflowTypeDAO.list(0, 0);

            for (com.wipro.ats.bdre.md.dao.jpa.WorkflowType wfType : jpaWorkflowTypes) {
                WorkflowType workflowType = new WorkflowType();
                workflowType.setWorkflowId(wfType.getWorkflowId());
                workflowType.setWorkflowTypeName(wfType.getWorkflowTypeName());
                workflowType.setCounter(jpaWorkflowTypes.size());
                workflowTypes.add(workflowType);
            }
            //  List<WorkflowType> workflowTypes = s.selectList("call_procedures.GetWorkflowTypes", workflowType);

            LOGGER.debug(workflowTypes.get(0).getWorkflowId());
            if (workflowTypes.get(0).getWorkflowId() == 0) {
                workflowTypes.remove(0);
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (WorkflowType type : workflowTypes) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(type.getWorkflowTypeName(), type.getWorkflowId());
                options.add(option);
                LOGGER.debug(option.getDisplayText());
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
