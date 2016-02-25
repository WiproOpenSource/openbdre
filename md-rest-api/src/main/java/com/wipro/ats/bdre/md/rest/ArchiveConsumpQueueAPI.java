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
import com.wipro.ats.bdre.md.beans.table.ArchiveConsumpQueue;
import com.wipro.ats.bdre.md.dao.ArchiveConsumpQueueDAO;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.BatchStatus;
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
@RequestMapping("/acq")


public class ArchiveConsumpQueueAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ArchiveConsumpQueueAPI.class);
    private static final String RECORDWITHID = "Record with ID:";
    @Autowired
    ArchiveConsumpQueueDAO archiveConsumpQueueDAO;

    /**
     * This method calls proc GetArchiveConsumpQueue and fetches a record corresponding to
     * the passed queueId.
     *
     * @param queueId
     * @return restWrapper returns an instance of ArchiveConsumpQueue object.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper get(
            @PathVariable("id") Long queueId, Principal principal
    ) {
        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue jpaAcq = archiveConsumpQueueDAO.get(queueId);
            ArchiveConsumpQueue archiveConsumpQueue = new ArchiveConsumpQueue();
            if (jpaAcq != null) {
                archiveConsumpQueue.setQueueId(jpaAcq.getQueueId());
                archiveConsumpQueue.setStartTs(jpaAcq.getStartTs());
                archiveConsumpQueue.setEndTs(jpaAcq.getEndTs());
                archiveConsumpQueue.setInsertTs(jpaAcq.getInsertTs());
                archiveConsumpQueue.setProcessId(jpaAcq.getProcess().getProcessId());
                archiveConsumpQueue.setSourceProcessId(jpaAcq.getSourceProcessId());
                archiveConsumpQueue.setSourceBatchId(jpaAcq.getBatchBySourceBatchId().getBatchId());
                if (jpaAcq.getBatchByTargetBatchId() != null) {
                    archiveConsumpQueue.setTargetBatchId(jpaAcq.getBatchByTargetBatchId().getBatchId());
                }
                archiveConsumpQueue.setBatchMarking(jpaAcq.getBatchMarking());
                archiveConsumpQueue.setBatchState(jpaAcq.getBatchStatus().getBatchStateId());
            }

            archiveConsumpQueue.setQueueId(queueId);
            if (archiveConsumpQueue.getEndTs() != null) {
                archiveConsumpQueue.setTableEndTS(DateConverter.dateToString(archiveConsumpQueue.getEndTs()));
            }
            if (archiveConsumpQueue.getStartTs() != null) {
                archiveConsumpQueue.setTableStartTS(DateConverter.dateToString(archiveConsumpQueue.getStartTs()));
            }
            archiveConsumpQueue.setTableInsertTS(DateConverter.dateToString(archiveConsumpQueue.getInsertTs()));


            restWrapper = new RestWrapper(archiveConsumpQueue, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + queueId + " selected from ArchiveConsumpQueue by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error("error occurred :" + e.getMessage());
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteArchiveConsumpQueue and deletes a record corresponding to
     * passed queueId.
     *
     * @param queueId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody public
    RestWrapper delete(
            @PathVariable("id") Long queueId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            archiveConsumpQueueDAO.delete(queueId);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + queueId + " deleted from ArchiveConsumpQueue by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error("error occurred :" + e.getMessage());
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetArchiveConsumpQueues and fetches a list records from
     * ArchiveConsumpQueues table.
     *
     * @param
     * @return restWrapper returns a list of instances of ArchiveConsumpQueue object.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    @ResponseBody public
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Integer counter=archiveConsumpQueueDAO.totalRecordCount().intValue();
            List<com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue> jpaAcqList = archiveConsumpQueueDAO.list(startPage, pageSize);
            List<ArchiveConsumpQueue> archiveConsumpQueues = new ArrayList<ArchiveConsumpQueue>();
            for (com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue jpaAcq : jpaAcqList) {
                ArchiveConsumpQueue archiveConsumpQueue = new ArchiveConsumpQueue();
                archiveConsumpQueue.setQueueId(jpaAcq.getQueueId());
                archiveConsumpQueue.setStartTs(jpaAcq.getStartTs());
                archiveConsumpQueue.setEndTs(jpaAcq.getEndTs());
                archiveConsumpQueue.setInsertTs(jpaAcq.getInsertTs());
                archiveConsumpQueue.setProcessId(jpaAcq.getProcess().getProcessId());
                archiveConsumpQueue.setSourceProcessId(jpaAcq.getSourceProcessId());
                archiveConsumpQueue.setSourceBatchId(jpaAcq.getBatchBySourceBatchId().getBatchId());
                if (jpaAcq.getBatchByTargetBatchId() != null) {
                    archiveConsumpQueue.setTargetBatchId(jpaAcq.getBatchByTargetBatchId().getBatchId());
                }
                archiveConsumpQueue.setBatchMarking(jpaAcq.getBatchMarking());
                archiveConsumpQueue.setBatchState(jpaAcq.getBatchStatus().getBatchStateId());
                archiveConsumpQueue.setCounter(counter);
                archiveConsumpQueues.add(archiveConsumpQueue);
            }
            for (ArchiveConsumpQueue acq : archiveConsumpQueues) {
                if (acq.getEndTs() != null) {
                    acq.setTableEndTS(DateConverter.dateToString(acq.getEndTs()));
                }
                if (acq.getStartTs() != null) {
                    acq.setTableStartTS(DateConverter.dateToString(acq.getStartTs()));
                }
                acq.setTableInsertTS(DateConverter.dateToString(acq.getInsertTs()));
            }
            restWrapper = new RestWrapper(archiveConsumpQueues, RestWrapper.OK);
            LOGGER.info("All records listed from ArchiveConsumpQueue by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error("error occurred :" + e.getMessage());
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;
    }

    /**
     * This method calls proc UpdateArchiveConsumpQueue and updates the values of the archiveConsumpQueue
     * object passed.This also validates the values passed.
     *
     * @param archiveConsumpQueue Instance of ArchiveConsumpQueue.
     * @param bindingResult
     * @return restWrapper Updated instance of ArchiveConsumpQueue.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper update(@ModelAttribute("acq")
                       @Valid ArchiveConsumpQueue archiveConsumpQueue, BindingResult bindingResult, Principal principal) {
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
            archiveConsumpQueue.setStartTs(DateConverter.stringToDate(archiveConsumpQueue.getTableStartTS()));
            archiveConsumpQueue.setEndTs(DateConverter.stringToDate(archiveConsumpQueue.getTableEndTS()));
            archiveConsumpQueue.setInsertTs(DateConverter.stringToDate(archiveConsumpQueue.getTableInsertTS()));
            com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue jpaAcq = new com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue();
            jpaAcq.setQueueId(archiveConsumpQueue.getQueueId());
            jpaAcq.setSourceProcessId(archiveConsumpQueue.getSourceProcessId());
            jpaAcq.setInsertTs(archiveConsumpQueue.getInsertTs());
            jpaAcq.setStartTs(archiveConsumpQueue.getStartTs());
            jpaAcq.setEndTs(archiveConsumpQueue.getEndTs());
            if (archiveConsumpQueue.getSourceBatchId() != null) {
                Batch sourceBatch = new Batch();
                sourceBatch.setBatchId(archiveConsumpQueue.getSourceBatchId());
                jpaAcq.setBatchBySourceBatchId(sourceBatch);

            }
            if (archiveConsumpQueue.getSourceBatchId() != null) {
                Batch targetBatch = new Batch();
                targetBatch.setBatchId(archiveConsumpQueue.getTargetBatchId());
                jpaAcq.setBatchByTargetBatchId(targetBatch);
            }
            com.wipro.ats.bdre.md.dao.jpa.Process process = new com.wipro.ats.bdre.md.dao.jpa.Process();
            process.setProcessId(archiveConsumpQueue.getProcessId());
            jpaAcq.setProcess(process);
            BatchStatus batchStatus = new BatchStatus();
            batchStatus.setBatchStateId(archiveConsumpQueue.getBatchState());
            jpaAcq.setBatchStatus(batchStatus);
            jpaAcq.setBatchMarking(archiveConsumpQueue.getBatchMarking());
            archiveConsumpQueueDAO.update(jpaAcq);

            archiveConsumpQueue.setTableStartTS(DateConverter.dateToString(archiveConsumpQueue.getStartTs()));
            archiveConsumpQueue.setTableEndTS(DateConverter.dateToString(archiveConsumpQueue.getEndTs()));
            archiveConsumpQueue.setTableInsertTS(DateConverter.dateToString(archiveConsumpQueue.getInsertTs()));

            restWrapper = new RestWrapper(archiveConsumpQueue, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + archiveConsumpQueue.getQueueId() + " updated from ArchiveConsumpQueue by User:" + principal.getName() + archiveConsumpQueue);
        } catch (Exception e) {
            LOGGER.error("error occurred :" + e.getMessage());
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertArchiveConsumpQueue and adds the record of the archiveConsumpQueue
     * object passed.This method also validates the values passed.
     *
     * @param archiveConsumpQueue Instance of ArchiveConsumpQueue.
     * @param bindingResult
     * @return restWrapper added instance of ArchiveConsumpQueue.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(@ModelAttribute("acq")
                       @Valid ArchiveConsumpQueue archiveConsumpQueue, BindingResult bindingResult, Principal principal) {
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

            archiveConsumpQueue.setStartTs(DateConverter.stringToDate(archiveConsumpQueue.getTableStartTS()));
            archiveConsumpQueue.setEndTs(DateConverter.stringToDate(archiveConsumpQueue.getTableEndTS()));
            archiveConsumpQueue.setInsertTs(DateConverter.stringToDate(archiveConsumpQueue.getTableInsertTS()));

            com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue jpaAcq = new com.wipro.ats.bdre.md.dao.jpa.ArchiveConsumpQueue();
            jpaAcq.setQueueId(archiveConsumpQueue.getQueueId());
            jpaAcq.setSourceProcessId(archiveConsumpQueue.getSourceProcessId());
            jpaAcq.setInsertTs(archiveConsumpQueue.getInsertTs());
            jpaAcq.setStartTs(archiveConsumpQueue.getStartTs());
            jpaAcq.setEndTs(archiveConsumpQueue.getEndTs());
            if (archiveConsumpQueue.getSourceBatchId() != null) {
                Batch sourceBatch = new Batch();
                sourceBatch.setBatchId(archiveConsumpQueue.getSourceBatchId());
                jpaAcq.setBatchBySourceBatchId(sourceBatch);

            }
            if (archiveConsumpQueue.getSourceBatchId() != null) {
                Batch targetBatch = new Batch();
                targetBatch.setBatchId(archiveConsumpQueue.getTargetBatchId());
                jpaAcq.setBatchByTargetBatchId(targetBatch);
            }
            com.wipro.ats.bdre.md.dao.jpa.Process process = new com.wipro.ats.bdre.md.dao.jpa.Process();
            process.setProcessId(archiveConsumpQueue.getProcessId());
            jpaAcq.setProcess(process);
            BatchStatus batchStatus = new BatchStatus();
            batchStatus.setBatchStateId(archiveConsumpQueue.getBatchState());
            jpaAcq.setBatchStatus(batchStatus);
            jpaAcq.setBatchMarking(archiveConsumpQueue.getBatchMarking());
            Long queueId = archiveConsumpQueueDAO.insert(jpaAcq);
            archiveConsumpQueue.setQueueId(queueId);
            archiveConsumpQueue.setTableStartTS(DateConverter.dateToString(archiveConsumpQueue.getStartTs()));
            archiveConsumpQueue.setTableEndTS(DateConverter.dateToString(archiveConsumpQueue.getEndTs()));
            archiveConsumpQueue.setTableInsertTS(DateConverter.dateToString(archiveConsumpQueue.getInsertTs()));

            restWrapper = new RestWrapper(archiveConsumpQueue, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + archiveConsumpQueue.getQueueId() + " inserted into ArchiveConsumpQueue by User:" + principal.getName() + archiveConsumpQueue);
        } catch (Exception e) {
            LOGGER.error("error occurred :" + e.getMessage());
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
