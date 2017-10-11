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
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.beans.table.ProcessTemplate;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.beans.table.PropertiesTemplate;
import com.wipro.ats.bdre.md.dao.*;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    PermissionTypeDAO appPermissionDAO;
    @Autowired
    UserRolesDAO userRolesDAO;
    @Autowired
    UsersDAO usersDAO;
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper get(
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
                processTemplate.setWorkflowId(jpaProcessTemplate.getWorkflowType().getWorkflowId());
                processTemplate.setOwnerRoleId(jpaProcessTemplate.getUserRoles().getUserRoleId());
                processTemplate.setPermissionTypeByUserAccessId(jpaProcessTemplate.getPermissionTypeByUserAccessId().getPermissionTypeId());
                processTemplate.setPermissionTypeByGroupAccessId(jpaProcessTemplate.getPermissionTypeByUserAccessId().getPermissionTypeId());
                processTemplate.setPermissionTypeByOthersAccessId(jpaProcessTemplate.getPermissionTypeByOthersAccessId().getPermissionTypeId());
                processTemplate.setBatchPattern(jpaProcessTemplate.getBatchCutPattern());
                processTemplate.setNextProcessTemplateId(jpaProcessTemplate.getNextProcessTemplateId());
            }
            processTemplate.setTableAddTS(DateConverter.dateToString(processTemplate.getAddTS()));

            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + " selected from ProcessTemplate by User:" + principal.getName());

        } catch (MetadataException e) {
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
    @ResponseBody public
    RestWrapper delete(
            @PathVariable("id") Integer processTemplateId, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            processTemplateDAO.delete(processTemplateId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + " deleted from ProcessTemplate by User:" + principal.getName());

        } catch (MetadataException e) {
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

    @ResponseBody public
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, @RequestParam(value = "pid", defaultValue = "0") Integer pid, Principal principal) {


        RestWrapper restWrapper = null;
        Integer processId = pid;
        try {
            if (pid == 0) {
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

        } catch (MetadataException e) {
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
    @ResponseBody public
    RestWrapper update(@ModelAttribute("processtemplate")
                       @Valid ProcessTemplate processTemplate, BindingResult bindingResult, Principal principal) {


        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
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
            jpaProcessTemplate.setUserRoles(userRolesDAO.get(processTemplate.getOwnerRoleId()));
            jpaProcessTemplate.setPermissionTypeByUserAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByUserAccessId()));
            jpaProcessTemplate.setPermissionTypeByGroupAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByGroupAccessId()));
            jpaProcessTemplate.setPermissionTypeByOthersAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByOthersAccessId()));
            BusDomain busDomain = busDomainDAO.get(processTemplate.getBusDomainId());
            jpaProcessTemplate.setBusDomain(busDomain);

            processTemplateDAO.update(jpaProcessTemplate);

            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplate.getProcessTemplateId() + " updated in BatchStatus by User:" + principal.getName() + processTemplate);

        } catch (MetadataException e) {
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
    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Autowired
    WorkflowTypeDAO workflowTypeDAO;
    @Autowired
    BusDomainDAO busDomainDAO;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(@ModelAttribute("processtemplate")
                       @Valid ProcessTemplate processTemplate, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
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
            ;
            jpaProcessTemplate.setProcessType(processType);
            if (processTemplate.getParentProcessId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate pt = processTemplateDAO.get(processTemplate.getParentProcessId());
                jpaProcessTemplate.setProcessTemplate(pt);
            } else {
                jpaProcessTemplate.setProcessTemplate(null);
            }

            WorkflowType workflowType = workflowTypeDAO.get(processTemplate.getWorkflowId());
            jpaProcessTemplate.setWorkflowType(workflowType);
            if (processTemplate.getOwnerRoleId() != null)
                jpaProcessTemplate.setUserRoles(userRolesDAO.get(processTemplate.getOwnerRoleId()));
            else
                jpaProcessTemplate.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));

            if (processTemplate.getPermissionTypeByUserAccessId() != null)
                jpaProcessTemplate.setPermissionTypeByUserAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByUserAccessId()));
            else
                jpaProcessTemplate.setPermissionTypeByUserAccessId(appPermissionDAO.get(7));
            if (processTemplate.getPermissionTypeByGroupAccessId() != null)
                jpaProcessTemplate.setPermissionTypeByGroupAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByGroupAccessId()));
            else
                jpaProcessTemplate.setPermissionTypeByGroupAccessId(appPermissionDAO.get(4));
            if (processTemplate.getPermissionTypeByOthersAccessId() != null)
                jpaProcessTemplate.setPermissionTypeByOthersAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByOthersAccessId()));
            else
                jpaProcessTemplate.setPermissionTypeByOthersAccessId(appPermissionDAO.get(0));
            BusDomain busDomain = busDomainDAO.get(processTemplate.getBusDomainId());
            jpaProcessTemplate.setBusDomain(busDomain);

            Integer processTemplateId = processTemplateDAO.insert(jpaProcessTemplate);

            processTemplate.setProcessTemplateId(processTemplateId);
            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplate.getProcessTemplateId() + " inserted in ProcessTemplate by User:" + principal.getName() + processTemplate);

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Autowired
    PropertiesTemplateDAO propertiesTemplateDAO;

    /**
     * This method handles the creation of a new process from process template and its associated properties
     *
     * @param processTemplate
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = {"/create", "/create/"}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper create(@ModelAttribute("processtemplate")
                       @Valid ProcessTemplate processTemplate,Principal principal, BindingResult bindingResult) {

        RestWrapper restWrapper = null;
        List<Process> processes = new ArrayList<Process>();
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        Map<Integer,Integer> idMap=new HashMap<>();
        try {

            LOGGER.info("process.id = " + processTemplate.getProcessName() + " " + processTemplate.getDescription());
            LOGGER.info("processTemplate id is " + processTemplate.getProcessTemplateId());
            List<ProcessTemplate> processTemplateInfos = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            LOGGER.info("size of processTemplateInfos "+processTemplateInfos.size());
            int tmp=0;
            int ppid=0;
            for (ProcessTemplate processTemplate1:processTemplateInfos)
            {
                LOGGER.info("process template id is "+processTemplate1.getProcessTemplateId());
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
                LOGGER.info("processTemplate.getOwnerRoleId() is "+processTemplate1.getOwnerRoleId()+" username is "+principal.getName());
                if (processTemplate1.getOwnerRoleId() != null)
                    insertDaoProcess.setUserRoles(userRolesDAO.get(processTemplate1.getOwnerRoleId()));
                else {
                    insertDaoProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
                    LOGGER.info("userRolesDAO.minUserRoleId(principal.getName() "+userRolesDAO.minUserRoleId(principal.getName()));
                }
                if (processTemplate1.getPermissionTypeByUserAccessId() != null)
                    insertDaoProcess.setPermissionTypeByUserAccessId(appPermissionDAO.get(processTemplate1.getPermissionTypeByUserAccessId()));
                else
                    insertDaoProcess.setPermissionTypeByUserAccessId(appPermissionDAO.get(7));
                if (processTemplate1.getPermissionTypeByGroupAccessId() != null)
                    insertDaoProcess.setPermissionTypeByGroupAccessId(appPermissionDAO.get(processTemplate1.getPermissionTypeByGroupAccessId()));
                else
                    insertDaoProcess.setPermissionTypeByGroupAccessId(appPermissionDAO.get(4));
                if (processTemplate1.getPermissionTypeByOthersAccessId() != null)
                    insertDaoProcess.setPermissionTypeByOthersAccessId(appPermissionDAO.get(processTemplate1.getPermissionTypeByOthersAccessId()));
                else
                    insertDaoProcess.setPermissionTypeByOthersAccessId(appPermissionDAO.get(0));
                if (processTemplate1.getProcessTemplateId() != null) {
                    com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate daoProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
                    daoProcessTemplate.setProcessTemplateId(processTemplate1.getProcessTemplateId());
                    insertDaoProcess.setProcessTemplate(daoProcessTemplate);
                }
                if (tmp!=0) {
                    LOGGER.info("ppid is "+ppid);
                    insertDaoProcess.setProcess(processDAO.get(ppid));
                }
                else
                {
                    insertDaoProcess.setProcess(null);
                }
                if (tmp==0)
                insertDaoProcess.setDescription(processTemplate.getDescription());
                else
                    insertDaoProcess.setDescription(processTemplate1.getDescription());
                insertDaoProcess.setAddTs(processTemplate1.getAddTS());
                if (tmp==0)
                insertDaoProcess.setProcessName(processTemplate.getProcessName());
                else
                  insertDaoProcess.setProcessName(processTemplate1.getProcessName());
                if (processTemplate1.getCanRecover() == null)
                    insertDaoProcess.setCanRecover(true);
                else
                    insertDaoProcess.setCanRecover(processTemplate1.getCanRecover());
                if (processTemplate1.getDeleteFlag() == null)
                    insertDaoProcess.setDeleteFlag(false);
                else
                    insertDaoProcess.setDeleteFlag(processTemplate1.getDeleteFlag());
                if(processTemplate1.getEnqProcessId()!=null)
                insertDaoProcess.setEnqueuingProcessId(processTemplate1.getEnqProcessId());
                else
                    insertDaoProcess.setEnqueuingProcessId(0);
                if (processTemplate1.getBatchPattern() != null) {
                    insertDaoProcess.setBatchCutPattern(processTemplate1.getBatchPattern());
                }
                if (processTemplate1.getNextProcessTemplateId()!=null)
                insertDaoProcess.setNextProcessId(processTemplate1.getNextProcessTemplateId());
                else
                insertDaoProcess.setNextProcessId("");
                insertDaoProcess.setUsers(usersDAO.get(principal.getName()));
                Integer processId = processDAO.insert(insertDaoProcess);
                if(tmp==0)
                    ppid=processId;
                idMap.put(processTemplate1.getProcessTemplateId(),processId);
                processTemplate1.setProcessId(processId);
                processTemplate1.setTableAddTS(DateConverter.dateToString(processTemplate1.getAddTS()));


                Process process = new Process();
                process.setProcessId(processId);
                process.setPermissionTypeByOthersAccessId(processTemplate1.getPermissionTypeByOthersAccessId());
                process.setPermissionTypeByGroupAccessId(processTemplate1.getPermissionTypeByGroupAccessId());
                process.setPermissionTypeByUserAccessId(processTemplate1.getPermissionTypeByUserAccessId());
                process.setAddTS(processTemplate1.getAddTS());
                process.setBatchPattern(processTemplate1.getBatchPattern());
                process.setBusDomainId(processTemplate1.getBusDomainId());
                process.setCanRecover(processTemplate1.getCanRecover());
                process.setDeleteFlag(processTemplate1.getDeleteFlag());
                process.setEnqProcessId(0);
                process.setProcessName(processTemplate1.getProcessName());
                process.setDescription(processTemplate1.getDescription());
                process.setProcessTemplateId(processTemplate1.getProcessTemplateId());
                process.setProcessTypeId(processTemplate1.getProcessTypeId());
                if (tmp==0)
                    process.setParentProcessId(null);
                else
                    process.setParentProcessId(ppid);
                LOGGER.info("loop no "+tmp+1);
                processes.add(process);
                tmp++;
            }
             LOGGER.info("size of processes "+processes.size());
            for (ProcessTemplate processTempInfo : processTemplateInfos) {
                processTempInfo.setEnqProcessId(0);
                processTempInfo.setNextProcessIds("");
                // Inserting properties for newly created process from template
                List<PropertiesTemplate> propertiesTemplateList = propertiesTemplateDAO.listPropertiesTemplateBean(processTempInfo.getProcessTemplateId());
                for (PropertiesTemplate propertiesTemplate : propertiesTemplateList) {
                    com.wipro.ats.bdre.md.dao.jpa.Properties properties=new com.wipro.ats.bdre.md.dao.jpa.Properties();
                   PropertiesId propertiesId=new PropertiesId();
                    propertiesId.setProcessId(processTempInfo.getProcessId());
                    propertiesId.setPropKey(propertiesTemplate.getKey());

                    properties.setId(propertiesId);
                    properties.setConfigGroup(propertiesTemplate.getConfigGroup());
                    properties.setPropValue(propertiesTemplate.getValue());
                    properties.setDescription(propertiesTemplate.getDescription());

                    propertiesDAO.insert(properties);

                }
            }
            LOGGER.info("process count" + processes.size());
            LOGGER.info("size of map element"+idMap.size());
            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
        } catch (MetadataException e) {
            LOGGER.info("Medadata exception occured ");
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }finally {
            adjustNextIdsForInsert(idMap, processes);
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
    @ResponseBody public
    RestWrapper apply(@ModelAttribute("processtemplate")
                      @Valid ProcessTemplate processTemplate, Principal principal, BindingResult bindingResult) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            LOGGER.info("parent.process.id = " + processTemplate.getParentProcessId());

            // Updating existing processes with changes made in template
            List<ProcessTemplate> processTemplateInfos = new ArrayList<ProcessTemplate>();
            processTemplateInfos = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            for (ProcessTemplate processTempInfo : processTemplateInfos) {
                List<Process> processInfos = new ArrayList<Process>();
                processInfos = processTemplateDAO.selectPListForTemplate(processTemplate.getProcessTemplateId());
                for (Process processInfo : processInfos) {
                    LOGGER.info("Entered update");
                    processTempInfo.setProcessId(processInfo.getProcessId());
                    processTempInfo.setParentProcessId(processInfo.getParentProcessId());
                    processTempInfo.setEnqProcessId(processInfo.getEnqProcessId());
                    processTempInfo.setProcessName(processInfo.getProcessName());
                    processTempInfo.setDescription(processInfo.getDescription());
                    LOGGER.info("processtempinfo.batchmarking= " + processTempInfo.getBatchPattern());
                    LOGGER.info("processtempinfo.type= " + processTempInfo.getProcessTypeId());
                    LOGGER.info("processtempinfo.canrecover= " + processTempInfo.getCanRecover());
                    LOGGER.info("processInfo next process id is " + processInfo.getNextProcessIds());
                    processTempInfo.setNextProcessIds(processInfo.getNextProcessIds());
                }
            }
            // Updating existing properties with changes made in properties template
            for (ProcessTemplate processTempInfo : processTemplateInfos) {
                List<Process> processInfos = processTemplateDAO.selectPListForTemplate(processTemplate.getProcessTemplateId());
                for (Process processInfo : processInfos) {
                    List<PropertiesTemplate> propTemplateList = propertiesTemplateDAO.listPropertiesTemplateBean(processTemplate.getProcessTemplateId());
                    if (propTemplateList.isEmpty()) {
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
                LOGGER.info("ProcessId= " + process.getProcessId());
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
                    if (processTemplate.getOwnerRoleId() != null)
                        insertDaoProcess.setUserRoles(userRolesDAO.get(processTemplate.getOwnerRoleId()));
                    else
                        insertDaoProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));

                    if (processTemplate.getPermissionTypeByUserAccessId() != null)
                        insertDaoProcess.setPermissionTypeByUserAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByUserAccessId()));
                    else
                        insertDaoProcess.setPermissionTypeByUserAccessId(appPermissionDAO.get(7));
                    if (processTemplate.getPermissionTypeByGroupAccessId() != null)
                        insertDaoProcess.setPermissionTypeByGroupAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByGroupAccessId()));
                    else
                        insertDaoProcess.setPermissionTypeByGroupAccessId(appPermissionDAO.get(4));
                    if (processTemplate.getPermissionTypeByOthersAccessId() != null)
                        insertDaoProcess.setPermissionTypeByOthersAccessId(appPermissionDAO.get(processTemplate.getPermissionTypeByOthersAccessId()));
                    else
                        insertDaoProcess.setPermissionTypeByOthersAccessId(appPermissionDAO.get(0));
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
                LOGGER.info("ProcessId= " + process.getProcessId());
                List<ProcessTemplate> processTemplates = processTemplateDAO.selectMissingSubPList(processTemplate.getProcessId(), processTemplate.getProcessTemplateId());

                for (ProcessTemplate processTemplate1 : processTemplates) {
                    processDAO.delete(processTemplate1.getProcessId());
                }
            }

            // Adding to existing properties if any extra properties are defined in the properties template
            List<ProcessTemplate> processTemplatesForInsert = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());

            for (ProcessTemplate processTemplateForInsert : processTemplatesForInsert) {
                LOGGER.info("HERE processTemplateForInsertid= " + processTemplateForInsert.getProcessTemplateId());

                List<Process> processesForPropInsert = processTemplateDAO.selectPListForTemplate(processTemplateForInsert.getProcessTemplateId());
                for (Process processForPropInsert : processesForPropInsert) {
                    LOGGER.info("HERE processForPropInsertid= " + processForPropInsert.getProcessId());
                    ProcessTemplate procTemplate = new ProcessTemplate();
                    procTemplate.setParentProcessId(processTemplatesForInsert.get(0).getProcessTemplateId());
                    LOGGER.info("procTemplate.setParentProcessId = " + procTemplate.getParentProcessId());
                    procTemplate.setProcessId(processForPropInsert.getProcessId());
                    LOGGER.info("procTemplate.setProcessId= " + procTemplate.getProcessId());
                    procTemplate.setProcessTemplateId(processTemplateForInsert.getProcessTemplateId());
                    LOGGER.info("procTemplate.setProcessTemplateId= " + procTemplate.getProcessTemplateId());
                    List<PropertiesTemplate> propertiesTemplates = processTemplateDAO.selectMissingPropListForT(procTemplate.getProcessId(), procTemplate.getParentProcessId(), procTemplate.getProcessTemplateId());
                    for (PropertiesTemplate propertyTemplates : propertiesTemplates) {
                        LOGGER.info("HERE property to be inserted = " + propertyTemplates.getProcessId() + "property config= " + propertyTemplates.getConfigGroup());
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

            LOGGER.info("checking proctemplate before using it= " + processTemplate.getProcessTemplateId());
            List<ProcessTemplate> processTemplatesForDelete = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            for (ProcessTemplate processTemplateForDelete : processTemplatesForDelete) {
                List<Process> processesForPropDelete = processTemplateDAO.selectPListForTemplate(processTemplateForDelete.getProcessTemplateId());
                for (Process processForPropDelete : processesForPropDelete) {
                    List<Properties> propertiesForDelete = processTemplateDAO.selectMissingPropListForP(processForPropDelete.getProcessId(), processForPropDelete.getProcessTemplateId());
                    for (Properties propertyForDelete : propertiesForDelete) {

                        LOGGER.info("prperty to be deleted  = " + propertyForDelete.getProcessId() + " " + propertyForDelete.getKey());
                        PropertiesId propertiesId = new PropertiesId();
                        propertiesId.setProcessId(propertyForDelete.getProcessId());
                        propertiesId.setPropKey(propertyForDelete.getKey());
                        propertiesDAO.delete(propertiesId);
                    }
                }
            }

            restWrapper = new RestWrapper(processTemplate, RestWrapper.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } finally {

            AdjustNextIdsForApply(processTemplate);
        }
        return restWrapper;
    }

    public void AdjustNextIdsForApply(ProcessTemplate processTemplate) {
        // Updating the next process ids to maintain the workflow structure defined in the template

        try {

            List<ProcessTemplate> processTemplatesForNext = processTemplateDAO.selectPTList(processTemplate.getProcessTemplateId());
            ProcessTemplate processTemplate2;
            List<Process> processesForParentList = processTemplateDAO.selectPListForTemplate(processTemplatesForNext.get(0).getProcessTemplateId());
            List<Integer> parentProcessList = new ArrayList<Integer>();
            for (Process p : processesForParentList) {
                parentProcessList.add(p.getProcessId());
                LOGGER.info("parentProcessList= " + p.getProcessId());
            }
            int count_outer = 0;
            for (ProcessTemplate processTemplateForNext : processTemplatesForNext) {
                LOGGER.info("processTemplate id= " + processTemplateForNext.getProcessTemplateId());
                String[] nextTemplateList = processTemplateForNext.getNextProcessTemplateId().split(",");
                LOGGER.info("nextTemplateList= " + nextTemplateList[0]);

                processTemplate2 = new ProcessTemplate();
                //LOGGER.info("processTemplatesForNext.get(count)= " + processTemplateForNext.getProcessId());
                int countInner = 0;
                List<Process> processesForNext = processTemplateDAO.selectPListForTemplate(processTemplatesForNext.get(count_outer).getProcessTemplateId());
                for (Process processForNext : processesForNext) {

                    LOGGER.info("processesForNext= " + processForNext.getProcessId());
                    String nextProcessList = "";
                    LOGGER.info("countInner= " + countInner);
                    processTemplate2.setParentProcessId(parentProcessList.get(countInner));
                    for (int i = 0; i < nextTemplateList.length; i++) {

                        LOGGER.info("nextTemplate item" + i + "  " + nextTemplateList[i]);
                        processTemplate2.setProcessId(Integer.parseInt(nextTemplateList[i]));
                        LOGGER.info("processTemplate2.setProcessId= " + processTemplate2.getProcessId());

                        LOGGER.info("processTemplate2.setParentProcessId= " + processTemplate2.getParentProcessId());
                        Process processForNext1 = processTemplateDAO.selectNextForPid(processTemplate2.getProcessId(), processTemplate2.getParentProcessId());
                        LOGGER.info("processForNext1  id=" + processForNext1.getProcessId());
                        nextProcessList = nextProcessList + processForNext1.getProcessId() + ",";
                        LOGGER.info("nextProcessList in this=" + nextProcessList);
                    }
                    processForNext.setNextProcessIds(nextProcessList.substring(0, nextProcessList.length() - 1));
                    LOGGER.info("before updating processForNext = " + processForNext.getProcessId() + " NAME= " + processForNext.getProcessName());
                    LOGGER.info("processfornext.batchmarking= " + processForNext.getBatchPattern() + "processfornext.can reover= " + processForNext.getCanRecover() + "type= " + processForNext.getProcessTypeId());


                    com.wipro.ats.bdre.md.dao.jpa.Process updateDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                    updateDaoProcess.setProcessId(processForNext.getProcessId());
                    com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
                    daoProcessType.setProcessTypeId(processForNext.getProcessTypeId());
                    updateDaoProcess.setProcessType(daoProcessType);
                    if (processForNext.getWorkflowId() != null) {
                        WorkflowType daoWorkflowType = new WorkflowType();
                        updateDaoProcess.setWorkflowType(daoWorkflowType);
                        updateDaoProcess.setUserRoles(userRolesDAO.get(processForNext.getOwnerRoleId()));
                        updateDaoProcess.setPermissionTypeByUserAccessId(appPermissionDAO.get(processForNext.getPermissionTypeByUserAccessId()));
                        updateDaoProcess.setPermissionTypeByGroupAccessId(appPermissionDAO.get(processForNext.getPermissionTypeByGroupAccessId()));
                        updateDaoProcess.setPermissionTypeByOthersAccessId(appPermissionDAO.get(processForNext.getPermissionTypeByOthersAccessId()));

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
                    countInner++;
                }
                count_outer++;
            }
        } catch (MetadataException e) {
            LOGGER.error(e);
        }

    }

    public void adjustNextIdsForInsert(Map<Integer,Integer> idMap, List<Process> processes) {
        // Updating the next process ids to maintain the workflow structure defined in the template
            LOGGER.info("size of passed processed "+processes.size());
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.Process> subProcessList=processDAO.selectProcessList(processes.get(0).getProcessId());
            for(com.wipro.ats.bdre.md.dao.jpa.Process subProcess:subProcessList)
            {
                String oldNextProcessId=subProcess.getNextProcessId();
                if(oldNextProcessId.equals("0"))
                    continue;
                String updatedNextProcessId="";
                String[] nextProcessList=oldNextProcessId.split(",");
                for (int i=0;i<nextProcessList.length;i++)
                {
                    LOGGER.info("nextProcessList key is "+nextProcessList[i]+" value is "+idMap.get(Integer.parseInt(nextProcessList[i])));
                    updatedNextProcessId=updatedNextProcessId+idMap.get(Integer.parseInt(nextProcessList[i]))+",";
                }
                LOGGER.info("oldNextProcessId is "+oldNextProcessId+" updatedNextProcessId is "+updatedNextProcessId);
                subProcess.setNextProcessId(updatedNextProcessId.substring(0,updatedNextProcessId.length()-1));
                processDAO.update(subProcess);
            }

        } catch (MetadataException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
