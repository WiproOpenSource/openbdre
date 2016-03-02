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
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.beans.table.ProcessTemplate;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.beans.table.PropertiesTemplate;
import com.wipro.ats.bdre.md.dao.*;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/processtemplate")


public class ProcessTemplateAPI extends MetadataAPIBase {
    /**
     * This method calls proc GetProcess and fetches a record corresponding to processId passed.
     *
     * @param processTemplateId
     * @return restWrapper It contains an instance of Process corresponding to processId passed.
     */
    private static final Logger LOGGER = Logger.getLogger(ProcessTemplateAPI.class);
    @Autowired
    ProcessTemplateDAO processTemplateDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    PropertiesDAO propertiesDAO;
    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Autowired
    WorkflowTypeDAO workflowTypeDAO;
    @Autowired
    BusDomainDAO busDomainDAO;
    @Autowired
    PropertiesTemplateDAO propertiesTemplateDAO;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    
    @ResponseBody
    public RestWrapper get(
            @PathVariable("id") Integer processTemplateId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate jpaProcessTemplate = processTemplateDAO.get(processTemplateId);
            ProcessTemplate processTemplate = new ProcessTemplate();
            if (jpaProcessTemplate != null) {
                processTemplate.setProcessTemplateId(jpaProcessTemplate.getProcessTemplateId());
                processTemplate.setDescription(jpaProcessTemplate.getDescription());
                processTemplate.setAddTS(jpaProcessTemplate.getAddTs());
                processTemplate.setProcessName(jpaProcessTemplate.getProcessName());
                processTemplate.setBusDomainId(jpaProcessTemplate.getBusDomain().getBusDomainId());
                processTemplate.setProcessTypeId(jpaProcessTemplate.getProcessType().getProcessTypeId());
                if (jpaProcessTemplate.getProcessTemplate() != null)
                    processTemplate.setParentProcessId(jpaProcessTemplate.getProcessTemplate().getProcessTemplateId());
                processTemplate.setCanRecover(jpaProcessTemplate.getCanRecover());
                processTemplate.setBatchPattern(jpaProcessTemplate.getBatchCutPattern());
                processTemplate.setNextProcessTemplateId(jpaProcessTemplate.getNextProcessTemplateId());
            }
            processTemplate.setTableAddTS(DateConverter.dateToString(processTemplate.getAddTS()));

            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + " selected from ProcessTemplate by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;

    }

    /**
     * This method calls proc DeleteProcess and deletes a record corresponding to processId passed.
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
     * This method calls proc ListProcess and fetches a list of instances of Process.
     *
     * @param
     * @return restWrapper It contains a list of instances of Process.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    
    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, @RequestParam(value = "pid", defaultValue = "0") Integer pid, Principal principal) {


        RestWrapper restWrapper = null;
        Integer processId=pid;
        try {
            if (processId == 0) {
                processId = null;
            }
            Integer counter=processTemplateDAO.totalRecordCount();
            List<ProcessTemplate> processes = processTemplateDAO.list(startPage, pageSize, processId);
            for (ProcessTemplate p : processes) {
                p.setCounter(counter);
                p.setTableAddTS(DateConverter.dateToString(p.getAddTS()));
            }

            restWrapper = new RestWrapper(processes, RestWrapper.OK);
            LOGGER.info("All records listed from ProcessTemplate by User:" + principal.getName());

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
            ProcessType processType = processTypeDAO.get(processTemplate.getProcessTypeId());
            jpaProcessTemplate.setProcessType(processType);
            if (processTemplate.getParentProcessId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate pt = processTemplateDAO.get(processTemplate.getParentProcessId());
                jpaProcessTemplate.setProcessTemplate(pt);
            } else {
                jpaProcessTemplate.setProcessTemplate(null);
            }

            WorkflowType workflowType = workflowTypeDAO.get(processTemplate.getWorkflowId());
            jpaProcessTemplate.setWorkflowType(workflowType);

            BusDomain busDomain = busDomainDAO.get(processTemplate.getBusDomainId());
            jpaProcessTemplate.setBusDomain(busDomain);

            processTemplateDAO.update(jpaProcessTemplate);

            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplate.getProcessTemplateId() + " updated in BatchStatus by User:" + principal.getName() + processTemplate);

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
     * @return restWrapper.
     */
    

    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    
    @ResponseBody
    public RestWrapper insert(@ModelAttribute("processtemplate")
                       @Valid ProcessTemplate processTemplate, BindingResult bindingResult, Principal principal) {

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

            ProcessType processType = processTypeDAO.get(processTemplate.getProcessTypeId());
            jpaProcessTemplate.setProcessType(processType);
            if (processTemplate.getParentProcessId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate pt = processTemplateDAO.get(processTemplate.getParentProcessId());
                jpaProcessTemplate.setProcessTemplate(pt);
            } else {
                jpaProcessTemplate.setProcessTemplate(null);
            }

            WorkflowType workflowType = workflowTypeDAO.get(processTemplate.getWorkflowId());
            jpaProcessTemplate.setWorkflowType(workflowType);

            BusDomain busDomain = busDomainDAO.get(processTemplate.getBusDomainId());
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

   

    /**
     * This method handles the creation of a new process from process template and its associated properties
     *
     * @param processTemplate
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = {"/create", "/create/"}, method = RequestMethod.PUT)
    
    @ResponseBody
    public RestWrapper create(@ModelAttribute("processtemplate")
                       @Valid ProcessTemplate processTemplate, BindingResult bindingResult) {

        RestWrapper restWrapper = null;
        List<Process> processes = new ArrayList<Process>();
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
            LOGGER.debug("process.id = " + processTemplate.getProcessName() + " " + processTemplate.getDescription());

            LOGGER.debug("processTemplate id is " + processTemplate.getProcessTemplateId());

            // Inserting new process from template
            List<ProcessTemplate> processTemplateInfos = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            int index = 0;
            int pid = 0;
            processTemplateInfos.get(0).setProcessName(processTemplate.getProcessName());
            processTemplateInfos.get(0).setDescription(processTemplate.getDescription());
            for (ProcessTemplate processTempInfo : processTemplateInfos) {
                processTempInfo.setEnqProcessId(0);
                processTempInfo.setNextProcessIds("");

                if (index > 0) {
                    processTempInfo.setParentProcessId(pid);
                }

                pid = processes.get(0).getProcessId();
                LOGGER.debug("index= " + index + "processTempInfo.processtempid=" + processTempInfo.getProcessTemplateId() + "processes.(0)name= " + processes.get(index).getProcessId());

                // Inserting properties for newly created process from template
                List<PropertiesTemplate> propertiesTemplateList = propertiesTemplateDAO.listPropertiesTemplateBean(processTempInfo.getProcessTemplateId());
                for (PropertiesTemplate propertiesTemplate : propertiesTemplateList) {
                    if (!propertiesTemplateList.isEmpty()) {
                        propertiesTemplate.setProcessId(processes.get(index).getProcessId());
                    }
                }
                index++;
            }
            LOGGER.debug("process count" + processes.size());


            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } finally {
            adjustNextIdsForInsert(processTemplate, processes);
        }
        return restWrapper;
    }

    /**
     * This method is used to apply changes made in template to the existing processes and their properties
     *
     * @param processTemplate
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = {"/apply", "/apply/"}, method = RequestMethod.POST)
    
    @ResponseBody
    public RestWrapper apply(@ModelAttribute("processtemplate")
                      @Valid ProcessTemplate processTemplate, BindingResult bindingResult) {

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
            LOGGER.debug("parent.process.id = " + processTemplate.getParentProcessId());

            // Updating existing processes with changes made in template
            List<ProcessTemplate> processTemplateInfos = new ArrayList<ProcessTemplate>();
            processTemplateInfos = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            for (ProcessTemplate processTempInfo : processTemplateInfos) {
                List<Process> processInfos = new ArrayList<Process>();
                processInfos = processTemplateDAO.selectPListForTemplate(processTemplate.getProcessTemplateId());
                for (Process processInfo : processInfos) {
                    LOGGER.debug("Entered update");
                    processTempInfo.setProcessId(processInfo.getProcessId());
                    processTempInfo.setParentProcessId(processInfo.getParentProcessId());
                    processTempInfo.setEnqProcessId(processInfo.getEnqProcessId());
                    processTempInfo.setProcessName(processInfo.getProcessName());
                    processTempInfo.setDescription(processInfo.getDescription());
                    LOGGER.debug("processtempinfo.batchmarking= " + processTempInfo.getBatchPattern());
                    LOGGER.debug("processtempinfo.type= " + processTempInfo.getProcessTypeId());
                    LOGGER.debug("processtempinfo.canrecover= " + processTempInfo.getCanRecover());
                    LOGGER.debug("processInfo next process id is " + processInfo.getNextProcessIds());
                    processTempInfo.setNextProcessIds(processInfo.getNextProcessIds());
                }
            }
            // Updating existing properties with changes made in properties template
            for (ProcessTemplate processTempInfo : processTemplateInfos) {
                LOGGER.debug(processTempInfo.getProcessTemplateId());
                List<Process> processInfos = processTemplateDAO.selectPListForTemplate(processTemplate.getProcessTemplateId());
                for (Process processInfo : processInfos) {
                    List<PropertiesTemplate> propTemplateList = propertiesTemplateDAO.listPropertiesTemplateBean(processTemplate.getProcessTemplateId());
                    if (!propTemplateList.isEmpty()) {
                        for (PropertiesTemplate propertyTemplate : propTemplateList) {
                            propertyTemplate.setProcessId(processInfo.getProcessId());
                        }
                    }
                }
            }
            // Inserting in process if any extra sub processes are added to the templaate
            List<Process> processesForInsert = processTemplateDAO.selectPPListForTemplateId(processTemplate.getProcessTemplateId());
            for (Process process : processesForInsert) {
                processTemplate.setProcessId(process.getProcessId());
                LOGGER.debug("ProcessId= " + process.getProcessId());
                List<ProcessTemplate> processTemplates = processTemplateDAO.selectMissingSubTList(processTemplate.getProcessId(), processTemplate.getProcessTemplateId());
                for (ProcessTemplate processTemplate1 : processTemplates) {
                    processTemplate1.setParentProcessId(process.getProcessId());
                    processTemplate1.setEnqProcessId(0);
                    processTemplate1.setNextProcessIds("");

                    com.wipro.ats.bdre.md.dao.jpa.Process insertDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                    com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
                    daoProcessType.setProcessTypeId(processTemplate1.getProcessTypeId());
                    insertDaoProcess.setProcessType(daoProcessType);
                    if (processTemplate1.getWorkflowId() != null) {
                        WorkflowType daoWorkflowType = new WorkflowType();
                        daoWorkflowType.setWorkflowId(processTemplate1.getWorkflowId());
                        insertDaoProcess.setWorkflowType(daoWorkflowType);
                    }
                    BusDomain daoBusDomain = new BusDomain();
                    daoBusDomain.setBusDomainId(processTemplate1.getBusDomainId());
                    insertDaoProcess.setBusDomain(daoBusDomain);
                    if (processTemplate1.getProcessTemplateId() != null) {
                        com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate daoProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
                        daoProcessTemplate.setProcessTemplateId(processTemplate1.getProcessTemplateId());
                        insertDaoProcess.setProcessTemplate(daoProcessTemplate);
                    }
                    if (processTemplate1.getParentProcessId() != null) {
                        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                        parentProcess.setProcessId(processTemplate1.getParentProcessId());
                        insertDaoProcess.setProcess(parentProcess);
                    }
                    insertDaoProcess.setDescription(processTemplate1.getDescription());
                    insertDaoProcess.setAddTs(processTemplate1.getAddTS());
                    insertDaoProcess.setProcessName(processTemplate1.getProcessName());
                    if (processTemplate1.getCanRecover() == null)
                        insertDaoProcess.setCanRecover(true);
                    else
                        insertDaoProcess.setCanRecover(processTemplate1.getCanRecover());
                    if (processTemplate1.getDeleteFlag() == null)
                        insertDaoProcess.setDeleteFlag(true);
                    else
                        insertDaoProcess.setDeleteFlag(processTemplate1.getDeleteFlag());
                    insertDaoProcess.setEnqueuingProcessId(processTemplate1.getEnqProcessId());
                    if (processTemplate1.getBatchPattern() != null) {
                        insertDaoProcess.setBatchCutPattern(processTemplate1.getBatchPattern());
                    }

                    insertDaoProcess.setNextProcessId(processTemplate1.getNextProcessIds());
                    Integer processId = processDAO.insert(insertDaoProcess);
                    processTemplate1.setProcessId(processId);
                    processTemplate1.setTableAddTS(DateConverter.dateToString(processTemplate1.getAddTS()));
                }
            }
            // Deleting from process if any existing sub processes are deleted from template
            List<Process> processesForDelete = processTemplateDAO.selectPPListForTemplateId(processTemplate.getProcessTemplateId());

            for (Process process : processesForDelete) {
                processTemplate.setProcessId(process.getProcessId());
                LOGGER.debug("ProcessId= " + process.getProcessId());
                List<ProcessTemplate> processTemplates = processTemplateDAO.selectMissingSubPList(processTemplate.getProcessId(), processTemplate.getProcessTemplateId());

                for (ProcessTemplate processTemplate1 : processTemplates) {
                    processDAO.delete(processTemplate1.getProcessId());
                }
            }

            // Adding to existing properties if any extra properties are defined in the properties template
            List<ProcessTemplate> processTemplatesForInsert = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());

            for (ProcessTemplate processTemplateForInsert : processTemplatesForInsert) {
                LOGGER.debug("HERE processTemplateForInsertid= " + processTemplateForInsert.getProcessTemplateId());

                List<Process> processesForPropInsert = processTemplateDAO.selectPListForTemplate(processTemplateForInsert.getProcessTemplateId());
                for (Process processForPropInsert : processesForPropInsert) {
                    LOGGER.debug("HERE processForPropInsertid= " + processForPropInsert.getProcessId());
                    ProcessTemplate procTemplate = new ProcessTemplate();
                    procTemplate.setParentProcessId(processTemplatesForInsert.get(0).getProcessTemplateId());
                    LOGGER.debug("procTemplate.setParentProcessId = " + procTemplate.getParentProcessId());
                    procTemplate.setProcessId(processForPropInsert.getProcessId());
                    LOGGER.debug("procTemplate.setProcessId= " + procTemplate.getProcessId());
                    procTemplate.setProcessTemplateId(processTemplateForInsert.getProcessTemplateId());
                    LOGGER.debug("procTemplate.setProcessTemplateId= " + procTemplate.getProcessTemplateId());
                    List<PropertiesTemplate> propertiesTemplates = processTemplateDAO.selectMissingPropListForT(procTemplate.getProcessId(), procTemplate.getParentProcessId(), procTemplate.getProcessTemplateId());
                    for (PropertiesTemplate propertyTemplates : propertiesTemplates) {
                        LOGGER.debug("HERE property to be inserted = " + propertyTemplates.getProcessId() + "property config= " + propertyTemplates.getConfigGroup());
                        com.wipro.ats.bdre.md.dao.jpa.Properties jpaPropertyTemplate = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                        PropertiesId propertiesId = new PropertiesId();
                        propertiesId.setProcessId(propertyTemplates.getProcessId());
                        propertiesId.setPropKey(propertyTemplates.getKey());
                        com.wipro.ats.bdre.md.dao.jpa.Process process = new com.wipro.ats.bdre.md.dao.jpa.Process();
                        process.setProcessId(propertyTemplates.getProcessId());
                        jpaPropertyTemplate.setProcess(process);
                        jpaPropertyTemplate.setConfigGroup(propertyTemplates.getConfigGroup());
                        jpaPropertyTemplate.setPropValue(propertyTemplates.getValue());
                        jpaPropertyTemplate.setDescription(propertyTemplates.getDescription());
                        jpaPropertyTemplate.setId(propertiesId);

                        propertiesDAO.insert(jpaPropertyTemplate);
                    }

                }
            }

            // Deleting from existing properties if any properties are deleted in the properties template

            LOGGER.debug("checking process template before using it= " + processTemplate.getProcessTemplateId());
            List<ProcessTemplate> processTemplatesForDelete = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            for (ProcessTemplate processTemplateForDelete : processTemplatesForDelete) {
                List<Process> processesForPropDelete = processTemplateDAO.selectPListForTemplate(processTemplateForDelete.getProcessTemplateId());
                for (Process processForPropDelete : processesForPropDelete) {
                    List<Properties> propertiesForDelete = processTemplateDAO.selectMissingPropListForP(processForPropDelete.getProcessId(), processForPropDelete.getProcessTemplateId());
                    for (Properties propertyForDelete : propertiesForDelete) {

                        LOGGER.debug("property to be deleted  = " + propertyForDelete.getProcessId() + " " + propertyForDelete.getKey());
                        PropertiesId propertiesId = new PropertiesId();
                        propertiesId.setProcessId(propertyForDelete.getProcessId());
                        propertiesId.setPropKey(propertyForDelete.getKey());
                        propertiesDAO.delete(propertiesId);
                    }
                }
            }

            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } finally {

            adjustNextIdsForApply(processTemplate);
        }
        return restWrapper;
    }

    public void adjustNextIdsForApply(ProcessTemplate processTemplate) {
        // Updating the next process ids to maintain the workflow structure defined in the template

        try {

            List<ProcessTemplate> processTemplatesForNext = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            ProcessTemplate processTemplate2;
            List<Process> processesForParentList = processTemplateDAO.selectPListForTemplate(processTemplatesForNext.get(0).getProcessTemplateId());
            List<Integer> parentProcessList = new ArrayList<Integer>();
            for (Process p : processesForParentList) {
                parentProcessList.add(p.getProcessId());
                LOGGER.debug("parentProcessList= " + p.getProcessId());
            }
            int countOuter = 0;
            for (ProcessTemplate processTemplateForNext : processTemplatesForNext) {
                LOGGER.debug("processTemplate id= " + processTemplateForNext.getProcessTemplateId());
                String[] nextTemplateList = processTemplateForNext.getNextProcessTemplateId().split(",");
                LOGGER.debug("nextTemplateList= " + nextTemplateList[0]);

                processTemplate2 = new ProcessTemplate();
                LOGGER.debug("processTemplatesForNext.get(count)= " + processTemplateForNext.getProcessId());
                int countInner  = 0;
                List<Process> processesForNext = processTemplateDAO.selectPListForTemplate(processTemplatesForNext.get(countOuter).getProcessTemplateId());
                for (Process processForNext : processesForNext) {

                    LOGGER.debug("processesForNext= " + processForNext.getProcessId());
                    String nextProcessList = "";
                    LOGGER.debug("countInner = " + countInner );
                    processTemplate2.setParentProcessId(parentProcessList.get(countInner ));
                    for (int i = 0; i < nextTemplateList.length; i++) {

                        LOGGER.debug("nextTemplate item" + i + "  " + nextTemplateList[i]);
                        processTemplate2.setProcessId(Integer.parseInt(nextTemplateList[i]));
                        LOGGER.debug("processTemplate2.setProcessId= " + processTemplate2.getProcessId());

                        LOGGER.debug("processTemplate2.setParentProcessId= " + processTemplate2.getParentProcessId());
                        Process processForNext1 = processTemplateDAO.selectNextForPid(processTemplate2.getProcessId(), processTemplate2.getParentProcessId());
                        LOGGER.debug("processForNext1  id=" + processForNext1.getProcessId());
                        nextProcessList = nextProcessList + processForNext1.getProcessId() + ",";
                        LOGGER.debug("nextProcessList in this=" + nextProcessList);
                    }
                    processForNext.setNextProcessIds(nextProcessList.substring(0, nextProcessList.length() - 1));
                    LOGGER.debug("before updating processForNext = " + processForNext.getProcessId() + " NAME= " + processForNext.getProcessName());
                    LOGGER.debug("processfornext.batchmarking= " + processForNext.getBatchPattern() + "processfornext.can reover= " + processForNext.getCanRecover() + "type= " + processForNext.getProcessTypeId());


                    com.wipro.ats.bdre.md.dao.jpa.Process updateDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                    updateDaoProcess.setProcessId(processForNext.getProcessId());
                    com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
                    daoProcessType.setProcessTypeId(processForNext.getProcessTypeId());
                    updateDaoProcess.setProcessType(daoProcessType);
                    if (processForNext.getWorkflowId() != null) {
                        WorkflowType daoWorkflowType = new WorkflowType();
                        daoWorkflowType.setWorkflowId(processForNext.getWorkflowId());
                        updateDaoProcess.setWorkflowType(daoWorkflowType);
                    }
                    BusDomain daoBusDomain = new BusDomain();
                    daoBusDomain.setBusDomainId(processForNext.getBusDomainId());
                    updateDaoProcess.setBusDomain(daoBusDomain);
                    if (processForNext.getProcessTemplateId() != null) {
                        com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate daoProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
                        daoProcessTemplate.setProcessTemplateId(processForNext.getProcessTemplateId());
                        updateDaoProcess.setProcessTemplate(daoProcessTemplate);
                    }
                    if (processForNext.getParentProcessId() != null) {
                        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                        parentProcess.setProcessId(processForNext.getParentProcessId());
                        updateDaoProcess.setProcess(parentProcess);
                    }
                    updateDaoProcess.setDescription(processForNext.getDescription());
                    updateDaoProcess.setAddTs(DateConverter.stringToDate(processForNext.getTableAddTS()));
                    updateDaoProcess.setProcessName(processForNext.getProcessName());
                    if (processForNext.getCanRecover() == null)
                        updateDaoProcess.setCanRecover(true);
                    else
                        updateDaoProcess.setCanRecover(processForNext.getCanRecover());
                    updateDaoProcess.setEnqueuingProcessId(processForNext.getEnqProcessId());
                    if (processForNext.getBatchPattern() != null) {
                        updateDaoProcess.setBatchCutPattern(processForNext.getBatchPattern());
                    }
                    updateDaoProcess.setNextProcessId(processForNext.getNextProcessIds());
                    if (processForNext.getDeleteFlag() == null)
                        updateDaoProcess.setDeleteFlag(false);
                    else
                        updateDaoProcess.setDeleteFlag(processForNext.getDeleteFlag());

                    updateDaoProcess.setEditTs(DateConverter.stringToDate(processForNext.getTableEditTS()));
                    processDAO.update(updateDaoProcess);
                    countInner ++;
                }
                countOuter++;
            }
        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.debug("Exception caught " + e.getStackTrace());

        }

    }

    public void adjustNextIdsForInsert(ProcessTemplate processTemplate, List<Process> processes) {
        // Updating the next process ids to maintain the workflow structure defined in the template


        try {

            List<ProcessTemplate> processTemplatesForNext = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            ProcessTemplate processTemplate2;
            int count = 0;
            for (ProcessTemplate processTemplateForNext : processTemplatesForNext) {
                LOGGER.debug("processTemplate id= " + processTemplateForNext.getProcessTemplateId());
                String[] nextTemplateList = processTemplateForNext.getNextProcessTemplateId().split(",");
                LOGGER.debug("nextTemplateList's first value= " + nextTemplateList[0]);

                processTemplate2 = new ProcessTemplate();
                LOGGER.debug("processTemplatesForNext.get(count)= " + processTemplateForNext.getProcessId());
                LOGGER.debug("processesForNext= " + processes.get(count).getProcessId());
                String nextProcessList = "";

                processTemplate2.setParentProcessId(processes.get(0).getProcessId());
                for (int i = 0; i < nextTemplateList.length; i++) {

                    LOGGER.debug("nextTemplate item" + i + "  " + nextTemplateList[i]);
                    processTemplate2.setProcessId(Integer.parseInt(nextTemplateList[i]));
                    LOGGER.debug("processTemplate2.setProcessId= " + processTemplate2.getProcessId());

                    LOGGER.debug("processTemplate2.setParentProcessId= " + processTemplate2.getParentProcessId());
                    Process processForNext1 = processTemplateDAO.selectNextForPid(processTemplate2.getProcessId(), processTemplate2.getParentProcessId());
                    LOGGER.debug("processForNext1  id=" + processForNext1.getProcessId());
                    nextProcessList = nextProcessList + processForNext1.getProcessId() + ",";
                    LOGGER.debug("nextProcessList in this=" + nextProcessList);
                }
                processes.get(count).setNextProcessIds(nextProcessList.substring(0, nextProcessList.length() - 1));
                LOGGER.debug("before updating processes.get(count) = " + processes.get(count).getProcessId() + " NAME= " + processes.get(count).getProcessName());
                LOGGER.debug("processes.get(count).batchmarking= " + processes.get(count).getBatchPattern() + "processes.get(count).can reover= " + processes.get(count).getCanRecover() + "type= " + processes.get(count).getProcessTypeId());


                com.wipro.ats.bdre.md.dao.jpa.Process updateDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                updateDaoProcess.setProcessId(processes.get(count).getProcessId());
                com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
                daoProcessType.setProcessTypeId(processes.get(count).getProcessTypeId());
                updateDaoProcess.setProcessType(daoProcessType);
                if (processes.get(count).getWorkflowId() != null) {
                    WorkflowType daoWorkflowType = new WorkflowType();
                    daoWorkflowType.setWorkflowId(processes.get(count).getWorkflowId());
                    updateDaoProcess.setWorkflowType(daoWorkflowType);
                }
                BusDomain daoBusDomain = new BusDomain();
                daoBusDomain.setBusDomainId(processes.get(count).getBusDomainId());
                updateDaoProcess.setBusDomain(daoBusDomain);
                if (processes.get(count).getProcessTemplateId() != null) {
                    com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate daoProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
                    daoProcessTemplate.setProcessTemplateId(processes.get(count).getProcessTemplateId());
                    updateDaoProcess.setProcessTemplate(daoProcessTemplate);
                }
                if (processes.get(count).getParentProcessId() != null) {
                    com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                    parentProcess.setProcessId(processes.get(count).getParentProcessId());
                    updateDaoProcess.setProcess(parentProcess);
                }
                updateDaoProcess.setDescription(processes.get(count).getDescription());
                updateDaoProcess.setAddTs(DateConverter.stringToDate(processes.get(count).getTableAddTS()));
                updateDaoProcess.setProcessName(processes.get(count).getProcessName());
                if (processes.get(count).getCanRecover() == null)
                    updateDaoProcess.setCanRecover(true);
                else
                    updateDaoProcess.setCanRecover(processes.get(count).getCanRecover());
                updateDaoProcess.setEnqueuingProcessId(processes.get(count).getEnqProcessId());
                if (processes.get(count).getBatchPattern() != null) {
                    updateDaoProcess.setBatchCutPattern(processes.get(count).getBatchPattern());
                }
                updateDaoProcess.setNextProcessId(processes.get(count).getNextProcessIds());
                if (processes.get(count).getDeleteFlag() == null)
                    updateDaoProcess.setDeleteFlag(false);
                else
                    updateDaoProcess.setDeleteFlag(processes.get(count).getDeleteFlag());
                updateDaoProcess.setEditTs(DateConverter.stringToDate(processes.get(count).getTableEditTS()));
                processDAO.update(updateDaoProcess);
                count++;
            }
        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.debug("Exception caught " + e.getStackTrace());

        }
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
