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
import com.wipro.ats.bdre.md.beans.table.ProcessTemplate;
import com.wipro.ats.bdre.md.dao.ProcessTemplateDAO;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/subprocesstemplate")


public class SubProcessTemplateAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(SubProcessTemplateAPI.class);
    @Autowired
    ProcessTemplateDAO processTemplateDAO;

    /**
     * This method calls proc GetSubProcesses and returns a record corresponding to the processid passed.
     *
     * @param
     * @return restWrapper It contains an instance of SubProcess corresponding to processid passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper get(
            @PathVariable("id") Integer processTemplateId, Principal principal
    ) {
        RestWrapper restWrapper = null;
        try {
            List<ProcessTemplate> processTemplates = processTemplateDAO.listSubProcessTemplates(processTemplateId);
            for (ProcessTemplate p : processTemplates) {
                p.setTableAddTS(DateConverter.dateToString(p.getAddTS()));
            }

            restWrapper = new RestWrapper(processTemplates, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + " selected from ProcessTemplate by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProcess and deletes a record from process table corresponding to
     * processId passed.
     *
     * @param processTemplateId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)

    @ResponseBody
    public RestWrapper delete(
            @PathVariable("id") Integer processTemplateId, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            processTemplateDAO.delete(processTemplateId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + " deleted from ProcessTemplate by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateProcess and updates the values. It also validates the values passed.
     *
     * @param processTemplate Instance of Process.
     * @param bindingResult
     * @return restWrapper It contains the updated instance of Process.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper update(@ModelAttribute("processtemplate")
                       @Valid ProcessTemplate processTemplate, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {

            processTemplate.setAddTS(DateConverter.stringToDate(processTemplate.getTableAddTS()));
            if (processTemplate.getBatchPattern().isEmpty()) {
                processTemplate.setBatchPattern(null);
            }
            com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate jpaProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
            jpaProcessTemplate.setProcessTemplateId(processTemplate.getProcessTemplateId());
            jpaProcessTemplate.setDescription(processTemplate.getDescription());
            jpaProcessTemplate.setProcessName(processTemplate.getProcessName());
            jpaProcessTemplate.setBatchCutPattern(processTemplate.getBatchPattern());
            jpaProcessTemplate.setAddTs(processTemplate.getAddTS());
            if (processTemplate.getCanRecover() == null)
                jpaProcessTemplate.setCanRecover(true);
            else
                jpaProcessTemplate.setCanRecover(processTemplate.getCanRecover());
            if (processTemplate.getDeleteFlag() == null)
                jpaProcessTemplate.setDeleteFlag(false);
            else
                jpaProcessTemplate.setDeleteFlag(processTemplate.getDeleteFlag());
            jpaProcessTemplate.setNextProcessTemplateId(processTemplate.getNextProcessTemplateId());
            ProcessType processType = new ProcessType();
            processType.setProcessTypeId(processTemplate.getProcessTypeId());
            jpaProcessTemplate.setProcessType(processType);
            com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate pt = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
            pt.setProcessTemplateId(processTemplate.getParentProcessId());
            jpaProcessTemplate.setProcessTemplate(pt);
            WorkflowType workflowType = new WorkflowType();
            workflowType.setWorkflowId(processTemplate.getWorkflowId());
            jpaProcessTemplate.setWorkflowType(workflowType);
            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(processTemplate.getBusDomainId());
            jpaProcessTemplate.setBusDomain(busDomain);

            processTemplateDAO.update(jpaProcessTemplate);


            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplate.getProcessTemplateId() + " updated in ProcessTemplate by User:" + principal.getName() + processTemplate);

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProcess and adds a record in process table. it also validates the values passed.
     *
     * @param processTemplate Instance of ProcessTemplate.
     * @param bindingResult
     * @return restWrapper It contains an instance of Process newly added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)

    @ResponseBody
    public RestWrapper insert(@ModelAttribute("processtemplate")
                       @Valid ProcessTemplate processTemplate, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {

            processTemplate.setAddTS(DateConverter.stringToDate(processTemplate.getTableAddTS()));
            if (processTemplate.getBatchPattern().isEmpty()) {
                processTemplate.setBatchPattern(null);
            }

            processTemplate.setAddTS(DateConverter.stringToDate(processTemplate.getTableAddTS()));
            com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate jpaProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
            jpaProcessTemplate.setProcessTemplateId(processTemplate.getProcessTemplateId());
            jpaProcessTemplate.setDescription(processTemplate.getDescription());
            jpaProcessTemplate.setProcessName(processTemplate.getProcessName());
            jpaProcessTemplate.setBatchCutPattern(processTemplate.getBatchPattern());
            jpaProcessTemplate.setAddTs(processTemplate.getAddTS());
            if (processTemplate.getCanRecover() == null)
                jpaProcessTemplate.setCanRecover(true);
            else
                jpaProcessTemplate.setCanRecover(processTemplate.getCanRecover());
            if (processTemplate.getDeleteFlag() == null)
                jpaProcessTemplate.setDeleteFlag(false);
            else
                jpaProcessTemplate.setDeleteFlag(processTemplate.getDeleteFlag());
            jpaProcessTemplate.setNextProcessTemplateId(processTemplate.getNextProcessTemplateId());
            ProcessType processType = new ProcessType();
            processType.setProcessTypeId(processTemplate.getProcessTypeId());
            jpaProcessTemplate.setProcessType(processType);
            com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate pt = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
            pt.setProcessTemplateId(processTemplate.getParentProcessId());
            jpaProcessTemplate.setProcessTemplate(pt);
            WorkflowType workflowType = new WorkflowType();
            workflowType.setWorkflowId(processTemplate.getWorkflowId());
            jpaProcessTemplate.setWorkflowType(workflowType);
            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(processTemplate.getBusDomainId());
            jpaProcessTemplate.setBusDomain(busDomain);

            Integer processTemplateId = processTemplateDAO.insert(jpaProcessTemplate);
            processTemplate.setProcessTemplateId(processTemplateId);

            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplate.getProcessTemplateId() + " inserted in ProcessTemplate by User:" + principal.getName() + processTemplate);

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
