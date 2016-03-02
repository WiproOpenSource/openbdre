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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.ProcessType;
import com.wipro.ats.bdre.md.dao.ProcessTypeDAO;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/processtype")


public class ProcessTypeAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger("ProcessTypeAPI.class");
    @Autowired
    ProcessTypeDAO processTypeDAO;

    /**
     * This method calls proc GetProcessType and fetches a record corresponding to processTypeId passed.
     *
     * @param processTypeId
     * @return restWrapper It contains instance of ProcessType corresponding to processTypeId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer processTypeId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.ProcessType jpaProcessType = processTypeDAO.get(processTypeId);
            ProcessType processType = new ProcessType();
            if (jpaProcessType != null) {
                processType.setProcessTypeId(jpaProcessType.getProcessTypeId());
                processType.setParentProcessTypeId(jpaProcessType.getParentProcessTypeId());
                processType.setProcessTypeName(jpaProcessType.getProcessTypeName());
            }
            restWrapper = new RestWrapper(processType, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTypeId + " selected from ProcessType by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProcessType and deletes a record corresponding to processTypeId passed.
     *
     * @param processTypeId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer processTypeId, Principal principal,
            ModelMap model) {
        RestWrapper restWrapper = null;
        try {
            processTypeDAO.delete(processTypeId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTypeId + " deleted from ProcessType by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetProcessTypes and fetches a list of instances of ProcessTypes.
     *
     * @param
     * @return restWrapper It contains list of instances of ProcessTypes.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Integer counter=processTypeDAO.totalRows();
            List<com.wipro.ats.bdre.md.dao.jpa.ProcessType> jpaProcessTypes = processTypeDAO.listFull(startPage, pageSize);
            List<ProcessType> processTypes = new ArrayList<ProcessType>();
            Integer totalRows=jpaProcessTypes.size();
            for (com.wipro.ats.bdre.md.dao.jpa.ProcessType processType : jpaProcessTypes) {
                ProcessType returnProcessType = new ProcessType();
                returnProcessType.setProcessTypeId(processType.getProcessTypeId());
                returnProcessType.setParentProcessTypeId(processType.getParentProcessTypeId());
                returnProcessType.setProcessTypeName(processType.getProcessTypeName());
                returnProcessType.setCounter(counter);
                processTypes.add(returnProcessType);
            }
            restWrapper = new RestWrapper(processTypes, RestWrapper.OK);
            LOGGER.info("All records listed from ProcessType by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateProcessType and updates the values of the record passed. It also validates
     * the values passed.
     *
     * @param processType   Instance of ProcessType.
     * @param bindingResult
     * @return restWrapper It contains the updated instance of ProcessType.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("processtype")
                       @Valid ProcessType processType, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.ProcessType jpaProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
            jpaProcessType.setProcessTypeId(processType.getProcessTypeId());
            jpaProcessType.setParentProcessTypeId(processType.getParentProcessTypeId());
            jpaProcessType.setProcessTypeName(processType.getProcessTypeName());
            processTypeDAO.update(jpaProcessType);

            restWrapper = new RestWrapper(processType, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processType.getProcessTypeId() + " updated in ProcessType by User:" + principal.getName() + processType);

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProcessType and adds a record of ProcessType. It also validates the values passed.
     *
     * @param processType   Instance of ProcessType.
     * @param bindingResult
     * @return restWrapper It contains an instance of ProcessType just added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("processtype")
                       @Valid ProcessType processType, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.ProcessType jpaProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
            jpaProcessType.setProcessTypeId(processType.getProcessTypeId());
            jpaProcessType.setParentProcessTypeId(processType.getParentProcessTypeId());
            jpaProcessType.setProcessTypeName(processType.getProcessTypeName());
            processTypeDAO.insert(jpaProcessType);

            restWrapper = new RestWrapper(processType, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processType.getProcessTypeId() + " inserted in ProcessType by User:" + principal.getName() + processType);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method is used to list the ProcessTypes applicable for processTypeId passed in dropdown list.
     *
     * @param processTypeId
     * @param model
     * @return
     */
    @RequestMapping(value = {"/options/{ptid}"}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapperOptions options(@PathVariable("ptid") Integer processTypeId,
                               ModelMap model) {

        RestWrapperOptions restWrapperOptions = null;
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.ProcessType> jpaProcessTypes = processTypeDAO.list(processTypeId, 0, 0);
            List<ProcessType> processTypes = new ArrayList<ProcessType>();
            for (com.wipro.ats.bdre.md.dao.jpa.ProcessType processType : jpaProcessTypes) {
                ProcessType returnProcessType = new ProcessType();
                returnProcessType.setProcessTypeId(processType.getProcessTypeId());
                returnProcessType.setParentProcessTypeId(processType.getParentProcessTypeId());
                returnProcessType.setProcessTypeName(processType.getProcessTypeName());
                returnProcessType.setCounter(jpaProcessTypes.size());
                processTypes.add(returnProcessType);
            }
            LOGGER.debug(processTypes.get(0).getProcessTypeId());
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (ProcessType type : processTypes) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(type.getProcessTypeName(), type.getProcessTypeId());
                options.add(option);
                LOGGER.debug(option.getDisplayText());
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapperOptions.ERROR);
        }
        return restWrapperOptions;
    }

    /**
     * This method is used to list ProcessTypes for dropdown list.
     *
     * @return
     */
    @RequestMapping(value = {"/optionslist"}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapperOptions listOptions() {

        RestWrapperOptions restWrapperOptions = null;
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.ProcessType> jpaProcessTypes = processTypeDAO.list(null, 0, 0);
            List<ProcessType> processTypes = new ArrayList<ProcessType>();
            for (com.wipro.ats.bdre.md.dao.jpa.ProcessType processType : jpaProcessTypes) {
                ProcessType returnProcessType = new ProcessType();
                returnProcessType.setProcessTypeId(processType.getProcessTypeId());
                returnProcessType.setParentProcessTypeId(processType.getParentProcessTypeId());
                returnProcessType.setProcessTypeName(processType.getProcessTypeName());
                returnProcessType.setCounter(jpaProcessTypes.size());
                processTypes.add(returnProcessType);
            }
            LOGGER.debug(processTypes.get(0).getProcessTypeId());
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (ProcessType type : processTypes) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(type.getProcessTypeName(), type.getProcessTypeId());
                options.add(option);
                LOGGER.debug(option.getDisplayText());
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapperOptions.ERROR);
        }
        return restWrapperOptions;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}
