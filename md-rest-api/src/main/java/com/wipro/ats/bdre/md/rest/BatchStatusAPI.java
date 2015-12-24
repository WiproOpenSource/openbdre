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
import com.wipro.ats.bdre.md.beans.table.BatchStatus;
import com.wipro.ats.bdre.md.dao.BatchStatusDAO;
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
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/batchstatus")


public class BatchStatusAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(BatchStatusAPI.class);
    @Autowired
    private BatchStatusDAO batchStatusDAO;

    /**
     * This method calls proc GetBatchStatus and fetches a record from BatchStatus table corresponding
     * to batchStateId passed.
     *
     * @param batchStateId
     * @return restWrapper It contains an instance of BatchStatus corresponding to batchStateId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @SuppressWarnings("unchecked")
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer batchStateId, Principal principal
    ) {

        RestWrapper restWrapper = null;

        try {

            BatchStatus batchStatus = new BatchStatus();
            com.wipro.ats.bdre.md.dao.jpa.BatchStatus jpaBatchStatus = batchStatusDAO.get(batchStateId);
            LOGGER.debug(batchStateId);
            if (jpaBatchStatus != null) {
                batchStatus.setBatchStateId(jpaBatchStatus.getBatchStateId());
                batchStatus.setDescription(jpaBatchStatus.getDescription());
            }
            // batchStatus = s.selectOne("call_procedures.GetBatchStatus", batchStatus);

            restWrapper = new RestWrapper(batchStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchStateId + " selected from BatchStatus by User:" + principal.getName());
        } catch (Exception e) {
            e.printStackTrace();

            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;

    }

    /**
     * This method calls DeleteBatchStatus and fetches a record corresponding to the batchStateId passed.
     *
     * @param batchStateId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer batchStateId,
            ModelMap model, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            batchStatusDAO.delete(batchStateId);
            //  s.delete("call_procedures.DeleteBatchStatus", batchStatus);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchStateId + " deleted from BatchStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetStatusBatches and fetches a list of BatchStatus records.
     *
     * @param
     * @return restWrapper It contains list of instances of BatchStatus.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=batchStatusDAO.totalRecordCount().intValue();
            List<com.wipro.ats.bdre.md.dao.jpa.BatchStatus> jpaBatchStatuses = batchStatusDAO.list(startPage, pageSize);
            List<BatchStatus> batchStatuses = new ArrayList<BatchStatus>();

            for (com.wipro.ats.bdre.md.dao.jpa.BatchStatus batchStatus : jpaBatchStatuses) {
                BatchStatus returnBatchStatus = new BatchStatus();
                returnBatchStatus.setBatchStateId(batchStatus.getBatchStateId());
                returnBatchStatus.setDescription(batchStatus.getDescription());
                returnBatchStatus.setCounter(counter);
                batchStatuses.add(returnBatchStatus);
            }
            //  List<BatchStatus> batchStatuses = s.selectList("call_procedures.GetBatchStatuses", batchStatus);

            restWrapper = new RestWrapper(batchStatuses, RestWrapper.OK);
            LOGGER.info("All records listed from BatchStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls UpdateBatchStatus and updates the record passed. It also validates
     * the values of the record passed.
     *
     * @param batchStatus   Instance of BatchStatus.
     * @param bindingResult
     * @return restWrapper The updated instance of BatchStatus.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("batchstatus")
                       @Valid BatchStatus batchStatus, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into update for batch_status table");

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
            com.wipro.ats.bdre.md.dao.jpa.BatchStatus jpaBatchStatus = new com.wipro.ats.bdre.md.dao.jpa.BatchStatus();
            jpaBatchStatus.setBatchStateId(batchStatus.getBatchStateId());
            jpaBatchStatus.setDescription(batchStatus.getDescription());
            batchStatusDAO.update(jpaBatchStatus);
            LOGGER.debug("Updating Batch State Id" + jpaBatchStatus.getBatchStateId());
            //  BatchStatus batchStatuses = s.selectOne("call_procedures.UpdateBatchStatus", batchStatus);

            LOGGER.debug("Exiting from update for batch_status table");
            restWrapper = new RestWrapper(batchStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchStatus.getBatchStateId() + " updated in BatchStatus by User:" + principal.getName() + batchStatus);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertBatchStatus and adds a new record in the database. It also validates the
     * values passed.
     *
     * @param batchStatus   Instance of BatchStatus.
     * @param bindingResult
     * @return restWrapper Instance of BatchStatus passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("batchstatus")
                       @Valid BatchStatus batchStatus, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into insert for batch_status table");


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

            com.wipro.ats.bdre.md.dao.jpa.BatchStatus jpaBatchStatus = new com.wipro.ats.bdre.md.dao.jpa.BatchStatus();
            jpaBatchStatus.setBatchStateId(batchStatus.getBatchStateId());
            jpaBatchStatus.setDescription(batchStatus.getDescription());
            Integer batchStatusID = batchStatusDAO.insert(jpaBatchStatus);
            batchStatus.setBatchStateId(batchStatusID);
            LOGGER.debug("Batch State Id" + batchStatus.getBatchStateId());
            //BatchStatus batchStatuses = s.selectOne("call_procedures.InsertBatchStatus", batchStatus);

            LOGGER.debug("Exiting from insert for batch_status table");
            restWrapper = new RestWrapper(batchStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchStatus.getBatchStateId() + " inserted in BatchStatus by User:" + principal.getName() + batchStatus);
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
    RestWrapperOptions listOptions(Principal principal) {

        RestWrapperOptions restWrapperOptions = null;

        try {

            List<com.wipro.ats.bdre.md.dao.jpa.BatchStatus> jpaBatchStatuses = batchStatusDAO.list(0, 0);
            List<BatchStatus> batchStatuses = new ArrayList<BatchStatus>();
            for (com.wipro.ats.bdre.md.dao.jpa.BatchStatus batchStatus : jpaBatchStatuses) {
                BatchStatus returnBatchStatus = new BatchStatus();
                returnBatchStatus.setBatchStateId(batchStatus.getBatchStateId());
                returnBatchStatus.setDescription(batchStatus.getDescription());
                returnBatchStatus.setCounter(jpaBatchStatuses.size());
                batchStatuses.add(returnBatchStatus);
            }
            //  List<BatchStatus> batchStatuses = s.selectList("call_procedures.GetBatchStatuses");

            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (BatchStatus batchStatus1 : batchStatuses) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(batchStatus1.getDescription(), batchStatus1.getBatchStateId());
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
            LOGGER.info("BatchStatusAPI:ListOptions : User:" + principal.getName() + ":ID:" + "All");
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



