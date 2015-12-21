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
import com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue;
import com.wipro.ats.bdre.md.dao.ProcessDeploymentQueueDAO;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.DeployStatus;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
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
@RequestMapping("/pdq")

public class ProcessDeploymentQueueAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessDeploymentQueueAPI.class);

    @Autowired
    ProcessDeploymentQueueDAO processDeploymentQueueDAO;

    /**
     * This method calls proc GetProcessDeploymentQueue and fetches a record corresponding to
     * the passed queueId.
     *
     * @param deploymentId .
     * @return restWrapper returns an instance of ProcessDeploymentQueue object.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Long deploymentId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue jpaPdq = processDeploymentQueueDAO.get(deploymentId);
            ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
            if (jpaPdq != null) {
                processDeploymentQueue.setDeploymentId(jpaPdq.getDeploymentId());
                processDeploymentQueue.setUserName(jpaPdq.getUserName());
                processDeploymentQueue.setStartTs(jpaPdq.getStartTs());
                processDeploymentQueue.setEndTs(jpaPdq.getEndTs());
                processDeploymentQueue.setInsertTs(jpaPdq.getInsertTs());
                processDeploymentQueue.setDeployScriptLocation(jpaPdq.getDeployScriptLocation());
                processDeploymentQueue.setBusDomainId(jpaPdq.getBusDomain().getBusDomainId());
                processDeploymentQueue.setDeployStatusId((int) (jpaPdq.getDeployStatus().getDeployStatusId()));
                processDeploymentQueue.setProcessId(jpaPdq.getProcess().getProcessId());
                processDeploymentQueue.setProcessTypeId(jpaPdq.getProcessType().getProcessTypeId());
            }
            //  processDeploymentQueue = s.selectOne("call_procedures.GetProcessDeploymentQueue", processDeploymentQueue);
            if (processDeploymentQueue.getEndTs() != null) {
                processDeploymentQueue.setTableEndTs(DateConverter.dateToString(processDeploymentQueue.getEndTs()));
            }
            if (processDeploymentQueue.getStartTs() != null) {
                processDeploymentQueue.setTableStartTs(DateConverter.dateToString(processDeploymentQueue.getStartTs()));
            }

            processDeploymentQueue.setTableInsertTs(DateConverter.dateToString(processDeploymentQueue.getInsertTs()));

            restWrapper = new RestWrapper(processDeploymentQueue, RestWrapper.OK);
            LOGGER.info("Record with ID:" + deploymentId + " selected from ProcessDeploymentQueue by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProcessDeploymentQueue and deletes a record corresponding to
     * passed queueId.
     *
     * @param deploymentId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Long deploymentId, Principal principal,
            ModelMap model) {

        RestWrapper restWrapper = null;
        try {
            processDeploymentQueueDAO.delete(deploymentId);
            // s.delete("call_procedures.DeleteProcessDeploymentQueue", processDeploymentQueue);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + deploymentId + " deleted from ProcessDeploymentQueue by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetProcessDeploymentQueues and fetches a list records from
     * ProcessDeploymentQueues table.
     *
     * @param
     * @return restWrapper returns a list of instances of ProcessDeploymentQueue object.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue> jpaPdqList = processDeploymentQueueDAO.list(startPage, pageSize);
            List<ProcessDeploymentQueue> processDeploymentQueues = new ArrayList<ProcessDeploymentQueue>();
            for (com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue pdq : jpaPdqList) {
                ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
                processDeploymentQueue.setDeploymentId(pdq.getDeploymentId());
                processDeploymentQueue.setProcessTypeId(pdq.getProcessType().getProcessTypeId());
                processDeploymentQueue.setDeployStatusId((int) pdq.getDeployStatus().getDeployStatusId());
                processDeploymentQueue.setStartTs(pdq.getStartTs());
                processDeploymentQueue.setEndTs(pdq.getEndTs());
                processDeploymentQueue.setInsertTs(pdq.getInsertTs());
                processDeploymentQueue.setBusDomainId(pdq.getBusDomain().getBusDomainId());
                processDeploymentQueue.setDeployScriptLocation(pdq.getDeployScriptLocation());
                processDeploymentQueue.setUserName(pdq.getUserName());
                processDeploymentQueue.setProcessId(pdq.getProcess().getProcessId());
                processDeploymentQueue.setCounter(processDeploymentQueueDAO.totalRecordCount());
                processDeploymentQueues.add(processDeploymentQueue);
            }
            //  List<ProcessDeploymentQueue> processDeploymentQueues = s.selectList("call_procedures.GetProcessDeploymentQueues", processDeploymentQueue);
            for (ProcessDeploymentQueue pdq : processDeploymentQueues) {
                if (pdq.getEndTs() != null) {
                    pdq.setTableEndTs(DateConverter.dateToString(pdq.getEndTs()));
                }
                if (pdq.getStartTs() != null) {
                    pdq.setTableStartTs(DateConverter.dateToString(pdq.getStartTs()));
                }
                pdq.setTableInsertTs(DateConverter.dateToString(pdq.getInsertTs()));

            }

            restWrapper = new RestWrapper(processDeploymentQueues, RestWrapper.OK);
            LOGGER.info("All records listed from ProcessDeploymentQueue by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method calls proc UpdateProcessDeploymentQueue and updates the values of the ProcessDeploymentQueue
     * object passed.This also validates the values passed.
     *
     * @param processDeploymentQueue Instance of ProcessDeploymentQueue.
     * @param bindingResult
     * @return restWrapper Updated instance of ProcessDeploymentQueue.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("pdq")
                       @Valid ProcessDeploymentQueue processDeploymentQueue, BindingResult bindingResult, Principal principal) {

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

            processDeploymentQueue.setEndTs(DateConverter.stringToDate(processDeploymentQueue.getTableEndTs()));
            processDeploymentQueue.setStartTs(DateConverter.stringToDate(processDeploymentQueue.getTableStartTs()));
            processDeploymentQueue.setInsertTs(DateConverter.stringToDate(processDeploymentQueue.getTableInsertTs()));
            processDeploymentQueue.setUserName(principal.getName());

            com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue jpaPdq = new com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue();
            jpaPdq.setDeploymentId(processDeploymentQueue.getDeploymentId());
            jpaPdq.setStartTs(processDeploymentQueue.getStartTs());
            jpaPdq.setEndTs(processDeploymentQueue.getEndTs());
            jpaPdq.setInsertTs(processDeploymentQueue.getInsertTs());
            jpaPdq.setUserName(processDeploymentQueue.getUserName());
            jpaPdq.setDeployScriptLocation(processDeploymentQueue.getDeployScriptLocation());
            DeployStatus deployStatus = new DeployStatus();
            deployStatus.setDeployStatusId(processDeploymentQueue.getDeployStatusId().shortValue());
            jpaPdq.setDeployStatus(deployStatus);
            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(processDeploymentQueue.getBusDomainId());
            jpaPdq.setBusDomain(busDomain);
            ProcessType processType = new ProcessType();
            processType.setProcessTypeId(processDeploymentQueue.getProcessTypeId());
            jpaPdq.setProcessType(processType);
            com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
            process.setProcessId(processDeploymentQueue.getProcessId());
            jpaPdq.setProcess(process);
            processDeploymentQueueDAO.update(jpaPdq);

            //  ProcessDeploymentQueue processDeploymentQueues = s.selectOne("call_procedures.UpdateProcessDeploymentQueue", processDeploymentQueue);


            restWrapper = new RestWrapper(processDeploymentQueue, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processDeploymentQueue.getDeploymentId() + " updated in ProcessDeploymentQueue by User:" + principal.getName() + processDeploymentQueue);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProcessDeploymentQueue and adds the record of the processDeploymentQueue
     * object passed.This method also validates the values passed.
     *
     * @param processId
     * @param bindingResult
     * @return restWrapper added instance of ProcessDeploymentQueue.
     */
    @RequestMapping(value = {"{id}", "/{id}"}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@PathVariable("id") Integer processId
            , @ModelAttribute("pdq")
                       @Valid ProcessDeploymentQueue processDeploymentQueue, BindingResult bindingResult, Principal principal) {
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

            com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue jpaPdq = processDeploymentQueueDAO.insertProcessDeploymentQueue(processId, principal.getName());
            // ProcessDeploymentQueue processDeploymentQueues = s.selectOne("call_procedures.InsertProcessDeploymentQueue", processDeploymentQueue);

            if (jpaPdq != null) {

                processDeploymentQueue.setDeploymentId(jpaPdq.getDeploymentId());
                processDeploymentQueue.setUserName(jpaPdq.getUserName());
                processDeploymentQueue.setStartTs(jpaPdq.getStartTs());
                processDeploymentQueue.setEndTs(jpaPdq.getEndTs());
                processDeploymentQueue.setInsertTs(jpaPdq.getInsertTs());
                processDeploymentQueue.setDeployScriptLocation(jpaPdq.getDeployScriptLocation());
                processDeploymentQueue.setBusDomainId(jpaPdq.getBusDomain().getBusDomainId());
                processDeploymentQueue.setDeployStatusId((int) (jpaPdq.getDeployStatus().getDeployStatusId()));
                processDeploymentQueue.setProcessId(jpaPdq.getProcess().getProcessId());
                processDeploymentQueue.setProcessTypeId(jpaPdq.getProcessType().getProcessTypeId());
            }
            //  processDeploymentQueue = s.selectOne("call_procedures.GetProcessDeploymentQueue", processDeploymentQueue);
            if (processDeploymentQueue.getEndTs() != null) {
                processDeploymentQueue.setTableEndTs(DateConverter.dateToString(processDeploymentQueue.getEndTs()));
            }
            if (processDeploymentQueue.getStartTs() != null) {
                processDeploymentQueue.setTableStartTs(DateConverter.dateToString(processDeploymentQueue.getStartTs()));
            }
            restWrapper = new RestWrapper(processDeploymentQueue, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processDeploymentQueue.getDeploymentId() + " inserted in ProcessDeploymentQueue by User:" + principal.getName() + processDeploymentQueue);
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

