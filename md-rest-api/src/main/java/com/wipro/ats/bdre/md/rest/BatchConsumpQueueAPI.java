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
import com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue;
import com.wipro.ats.bdre.md.dao.BatchConsumpQueueDAO;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.BatchStatus;
import com.wipro.ats.bdre.md.dao.jpa.Process;
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
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/bcq")


public class BatchConsumpQueueAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(BatchConsumpQueueAPI.class);
    @Autowired
    BatchConsumpQueueDAO batchConsumpQueueDAO;

    /**
     * This method calls proc GetBatchConsumpQueue and fetches a record corresponding to
     * the passed queueId.
     *
     * @param queueId .
     * @return restWrapper returns an instance of BatchConsumpQueue object.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Long queueId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue jpaBcq = batchConsumpQueueDAO.get(queueId);
            BatchConsumpQueue batchConsumpQueue = new BatchConsumpQueue();
            if (jpaBcq != null) {
                batchConsumpQueue.setQueueId(jpaBcq.getQueueId());
                batchConsumpQueue.setStartTs(jpaBcq.getStartTs());
                batchConsumpQueue.setEndTs(jpaBcq.getEndTs());
                batchConsumpQueue.setInsertTs(jpaBcq.getInsertTs());
                batchConsumpQueue.setProcessId(jpaBcq.getProcess().getProcessId());
                batchConsumpQueue.setSourceProcessId(jpaBcq.getSourceProcessId());
                batchConsumpQueue.setSourceBatchId(jpaBcq.getBatchBySourceBatchId().getBatchId());
                if (jpaBcq.getBatchByTargetBatchId() != null) {
                    batchConsumpQueue.setTargetBatchId(jpaBcq.getBatchByTargetBatchId().getBatchId());
                }
                batchConsumpQueue.setBatchMarking(jpaBcq.getBatchMarking());
                batchConsumpQueue.setBatchState(jpaBcq.getBatchStatus().getBatchStateId());
            }
            //batchConsumpQueue = s.selectOne("call_procedures.GetBatchConsumpQueue", batchConsumpQueue);
            if (batchConsumpQueue.getEndTs() != null) {
                batchConsumpQueue.setTableEndTS(DateConverter.dateToString(batchConsumpQueue.getEndTs()));
            }
            if (batchConsumpQueue.getStartTs() != null) {
                batchConsumpQueue.setTableStartTS(DateConverter.dateToString(batchConsumpQueue.getStartTs()));
            }

            batchConsumpQueue.setTableInsertTS(DateConverter.dateToString(batchConsumpQueue.getInsertTs()));

            restWrapper = new RestWrapper(batchConsumpQueue, RestWrapper.OK);
            LOGGER.info("Record with ID:" + queueId + " selected from BatchConsumpQueue by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteBatchConsumpQueue and deletes a record corresponding to
     * passed queueId.
     *
     * @param queueId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Long queueId, Principal principal,
            ModelMap model) {

        RestWrapper restWrapper = null;
        try {
            batchConsumpQueueDAO.delete(queueId);
            // s.delete("call_procedures.DeleteBatchConsumpQueue", batchConsumpQueue);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + queueId + " deleted from BatchConsumpQueue by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetBatchConsumpQueues and fetches a list records from
     * BatchConsumpQueues table.
     *
     * @param
     * @return restWrapper returns a list of instances of BatchConsumpQueue object.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Integer counter=batchConsumpQueueDAO.totalRecordCount().intValue();
            List<com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue> jpaBcqList = batchConsumpQueueDAO.list(startPage, pageSize);
            List<BatchConsumpQueue> batchConsumpQueues = new ArrayList<BatchConsumpQueue>();
            for (com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue jpaBcq : jpaBcqList) {
                BatchConsumpQueue batchConsumpQueue = new BatchConsumpQueue();
                batchConsumpQueue.setQueueId(jpaBcq.getQueueId());
                batchConsumpQueue.setStartTs(jpaBcq.getStartTs());
                batchConsumpQueue.setEndTs(jpaBcq.getEndTs());
                batchConsumpQueue.setInsertTs(jpaBcq.getInsertTs());
                if (jpaBcq.getProcess() != null)
                    batchConsumpQueue.setProcessId(jpaBcq.getProcess().getProcessId());

                batchConsumpQueue.setSourceProcessId(jpaBcq.getSourceProcessId());
                batchConsumpQueue.setSourceBatchId(jpaBcq.getBatchBySourceBatchId().getBatchId());
                if (jpaBcq.getBatchByTargetBatchId() != null) {
                    batchConsumpQueue.setTargetBatchId(jpaBcq.getBatchByTargetBatchId().getBatchId());
                }
                batchConsumpQueue.setBatchMarking(jpaBcq.getBatchMarking());
                batchConsumpQueue.setBatchState(jpaBcq.getBatchStatus().getBatchStateId());
                batchConsumpQueue.setCounter(counter);
                LOGGER.info(batchConsumpQueue.getCounter());
                batchConsumpQueues.add(batchConsumpQueue);
            }

            // List<BatchConsumpQueue> batchConsumpQueues = s.selectList("call_procedures.GetBatchConsumpQueues", batchConsumpQueue);

            for (BatchConsumpQueue bcq : batchConsumpQueues) {
                if (bcq.getEndTs() != null) {
                    bcq.setTableEndTS(DateConverter.dateToString(bcq.getEndTs()));
                }
                if (bcq.getStartTs() != null) {
                    bcq.setTableStartTS(DateConverter.dateToString(bcq.getStartTs()));
                }
                bcq.setTableInsertTS(DateConverter.dateToString(bcq.getInsertTs()));

            }
            restWrapper = new RestWrapper(batchConsumpQueues, RestWrapper.OK);
            LOGGER.info("All records listed from BatchConsumpQueue by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method calls proc UpdateBatchConsumpQueue and updates the values of the bacthConsumpQueue
     * object passed.This also validates the values passed.
     *
     * @param batchConsumpQueue Instance of BatchConsumpQueue.
     * @param bindingResult
     * @return restWrapper Updated instance of BatchConsumpQueue.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("bcq")
                       @Valid BatchConsumpQueue batchConsumpQueue, BindingResult bindingResult, Principal principal) {
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

            batchConsumpQueue.setEndTs(DateConverter.stringToDate(batchConsumpQueue.getTableEndTS()));
            batchConsumpQueue.setStartTs(DateConverter.stringToDate(batchConsumpQueue.getTableStartTS()));
            batchConsumpQueue.setInsertTs(DateConverter.stringToDate(batchConsumpQueue.getTableInsertTS()));
            if (batchConsumpQueue.getBatchMarking().isEmpty()) {
                batchConsumpQueue.setBatchMarking(null);
            }

            com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue jpaBcq = new com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue();
            jpaBcq.setQueueId(batchConsumpQueue.getQueueId());
            jpaBcq.setSourceProcessId(batchConsumpQueue.getSourceProcessId());
            jpaBcq.setInsertTs(batchConsumpQueue.getInsertTs());
            jpaBcq.setStartTs(batchConsumpQueue.getStartTs());
            jpaBcq.setEndTs(batchConsumpQueue.getEndTs());
            if (batchConsumpQueue.getSourceBatchId() != null) {
                Batch sourceBatch = new Batch();
                sourceBatch.setBatchId(batchConsumpQueue.getSourceBatchId());
                jpaBcq.setBatchBySourceBatchId(sourceBatch);

            }
            if (batchConsumpQueue.getSourceBatchId() != null) {
                Batch targetBatch = new Batch();
                targetBatch.setBatchId(batchConsumpQueue.getTargetBatchId());
                jpaBcq.setBatchByTargetBatchId(targetBatch);
            }
            com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
            process.setProcessId(batchConsumpQueue.getProcessId());
            jpaBcq.setProcess(process);
            BatchStatus batchStatus = new BatchStatus();
            batchStatus.setBatchStateId(batchConsumpQueue.getBatchState());
            jpaBcq.setBatchStatus(batchStatus);
            jpaBcq.setBatchMarking(batchConsumpQueue.getBatchMarking());
            batchConsumpQueueDAO.update(jpaBcq);

            //BatchConsumpQueue batchConsumpQueues = s.selectOne("call_procedures.UpdateBatchConsumpQueue", batchConsumpQueue);
            batchConsumpQueue.setTableStartTS(DateConverter.dateToString(batchConsumpQueue.getStartTs()));
            batchConsumpQueue.setTableEndTS(DateConverter.dateToString(batchConsumpQueue.getEndTs()));
            batchConsumpQueue.setTableInsertTS(DateConverter.dateToString(batchConsumpQueue.getInsertTs()));

            restWrapper = new RestWrapper(batchConsumpQueue, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchConsumpQueue.getQueueId() + " updated in BatchConsumpQueue by User:" + principal.getName() + batchConsumpQueue);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertBatchConsumpQueue and adds the record of the batchConsumpQueue
     * object passed.This method also validates the values passed.
     *
     * @param batchConsumpQueue Instance of BatchConsumpQueue.
     * @param bindingResult
     * @return restWrapper added instance of BatchConsumpQueue.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("bcq")
                       @Valid BatchConsumpQueue batchConsumpQueue, BindingResult bindingResult, Principal principal) {

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
            batchConsumpQueue.setEndTs(DateConverter.stringToDate(batchConsumpQueue.getTableEndTS()));
            batchConsumpQueue.setStartTs(DateConverter.stringToDate(batchConsumpQueue.getTableStartTS()));
            batchConsumpQueue.setInsertTs(DateConverter.stringToDate(batchConsumpQueue.getTableInsertTS()));
            if (batchConsumpQueue.getBatchMarking().isEmpty()) {
                batchConsumpQueue.setBatchMarking(null);
            }
            com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue jpaBcq = new com.wipro.ats.bdre.md.dao.jpa.BatchConsumpQueue();
            jpaBcq.setQueueId(batchConsumpQueue.getQueueId());
            jpaBcq.setSourceProcessId(batchConsumpQueue.getSourceProcessId());
            jpaBcq.setInsertTs(batchConsumpQueue.getInsertTs());
            jpaBcq.setStartTs(batchConsumpQueue.getStartTs());
            jpaBcq.setEndTs(batchConsumpQueue.getEndTs());
            if (batchConsumpQueue.getSourceBatchId() != null) {
                Batch sourceBatch = new Batch();
                sourceBatch.setBatchId(batchConsumpQueue.getSourceBatchId());
                jpaBcq.setBatchBySourceBatchId(sourceBatch);
            }
            if (batchConsumpQueue.getTargetBatchId() != null) {
                Batch targetBatch = new Batch();
                targetBatch.setBatchId(batchConsumpQueue.getTargetBatchId());
                jpaBcq.setBatchByTargetBatchId(targetBatch);
            }
            com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
            process.setProcessId(batchConsumpQueue.getProcessId());
            jpaBcq.setProcess(process);
            BatchStatus batchStatus = new BatchStatus();
            batchStatus.setBatchStateId(batchConsumpQueue.getBatchState());
            jpaBcq.setBatchStatus(batchStatus);
            jpaBcq.setBatchMarking(batchConsumpQueue.getBatchMarking());
            Long queueId = batchConsumpQueueDAO.insert(jpaBcq);
            jpaBcq.setQueueId(queueId);
            // BatchConsumpQueue batchConsumpQueues = s.selectOne("call_procedures.InsertBatchConsumpQueue", batchConsumpQueue);

            batchConsumpQueue.setTableStartTS(DateConverter.dateToString(batchConsumpQueue.getStartTs()));
            batchConsumpQueue.setTableEndTS(DateConverter.dateToString(batchConsumpQueue.getEndTs()));
            batchConsumpQueue.setTableInsertTS(DateConverter.dateToString(batchConsumpQueue.getInsertTs()));

            restWrapper = new RestWrapper(batchConsumpQueue, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchConsumpQueue.getQueueId() + " inserted in BatchConsumpQueue by User:" + principal.getName() + batchConsumpQueue);
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
