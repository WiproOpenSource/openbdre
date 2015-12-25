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
import com.wipro.ats.bdre.md.beans.table.Batch;
import com.wipro.ats.bdre.md.dao.BatchDAO;
import com.wipro.ats.bdre.md.dao.jpa.InstanceExec;
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
 * Created by leela on 13-01-2015.
 */
@Controller
@RequestMapping("/batch")


public class BatchAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(BatchAPI.class);
    @Autowired
    BatchDAO batchDAO;

    /**
     * This method calls proc GetBatch and fetches a record corresponding to passes batchId.
     *
     * @param batchId
     * @return restWrapper an instance of Batch object.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Long batchId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.Batch jpaBatch = batchDAO.get(batchId);
            Batch batch = new Batch();
            if (jpaBatch != null) {
                batch.setBatchId(jpaBatch.getBatchId());
                batch.setBatchType(jpaBatch.getBatchType());
                if (jpaBatch.getInstanceExec() != null) {
                    batch.setSourceInstanceExecId(jpaBatch.getInstanceExec().getInstanceExecId());
                }
            }
            // batch = s.selectOne("call_procedures.GetBatch", batch);

            restWrapper = new RestWrapper(batch, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchId + " selected from Batch by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;

    }

    /**
     * This method calls proc DeleteBatch and deletes a record corresponding to passed batchId.
     *
     * @param batchId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Long batchId, Principal principal,
            ModelMap model) {

        RestWrapper restWrapper = null;
        try {
            batchDAO.delete(batchId);
            // s.delete("call_procedures.DeleteBatch", batch);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchId + " deleted from Batch by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetBatches and fetches list of records from Batch table.
     *
     * @param
     * @return restWrapper It contains list of instances of Batch.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Integer counter=batchDAO.totalRecordCount();
            List<com.wipro.ats.bdre.md.dao.jpa.Batch> jpaBatchList = batchDAO.list(startPage, pageSize);

            List<Batch> batches = new ArrayList<Batch>();
            for (com.wipro.ats.bdre.md.dao.jpa.Batch batch : jpaBatchList) {
                Batch returnBatch = new Batch();
                returnBatch.setBatchId(batch.getBatchId());
                if (batch.getInstanceExec() != null) {
                    returnBatch.setSourceInstanceExecId(batch.getInstanceExec().getInstanceExecId());
                }
                returnBatch.setBatchType(batch.getBatchType());
                returnBatch.setCounter(counter);
                batches.add(returnBatch);

            }
            //List<Batch> batches = s.selectList("call_procedures.GetBatches", batch);

            restWrapper = new RestWrapper(batches, RestWrapper.OK);
            LOGGER.info("All records listed from Batch by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc Updatebatch and updates the values of the Batch object passed.It
     * also validates the values of the object passed.
     *
     * @param batch         Instance of Batch.
     * @param bindingResult
     * @return restWrapper updated instance of Batch passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("batch")
                       @Valid Batch batch, BindingResult bindingResult, Principal principal) {
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
            com.wipro.ats.bdre.md.dao.jpa.Batch jpaBatch = new com.wipro.ats.bdre.md.dao.jpa.Batch();
            jpaBatch.setBatchId(batch.getBatchId());
            jpaBatch.setBatchType(batch.getBatchType());
            InstanceExec instanceExec = new InstanceExec();
            if (batch.getSourceInstanceExecId() != null) {
                instanceExec.setInstanceExecId(batch.getSourceInstanceExecId());
                jpaBatch.setInstanceExec(instanceExec);
            }

            batchDAO.update(jpaBatch);
            // Batch batches = s.selectOne("call_procedures.UpdateBatch", batch);
            restWrapper = new RestWrapper(batch, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batch.getBatchId() + " updated in Batch by User:" + principal.getName() + batch);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertBatch and adds a record in database. It also validates the
     * values of the object passed.
     *
     * @param batch         Instance of Batch.
     * @param bindingResult
     * @return restWrapper It contains the instance of Batch passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("batch")
                       @Valid Batch batch, BindingResult bindingResult, Principal principal) {
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
            com.wipro.ats.bdre.md.dao.jpa.Batch jpaBatch = new com.wipro.ats.bdre.md.dao.jpa.Batch();

            jpaBatch.setBatchType(batch.getBatchType());
            InstanceExec instanceExec = new InstanceExec();
            if (batch.getSourceInstanceExecId() != null) {
                instanceExec.setInstanceExecId(batch.getSourceInstanceExecId());
                jpaBatch.setInstanceExec(instanceExec);
            }

            Long autoGenBatchId = batchDAO.insert(jpaBatch);
            batch.setBatchId(autoGenBatchId);
            //Batch batches = s.selectOne("call_procedures.InsertBatch", batch);

            restWrapper = new RestWrapper(batch, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batch.getBatchId() + " inserted in Batch by User:" + principal.getName() + batch);
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