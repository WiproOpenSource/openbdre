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

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.Export;
import com.wipro.ats.bdre.md.api.Import;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ExecutionInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
import com.wipro.ats.bdre.md.rest.beans.ProcessExport;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.*;

/**
 * Created by arijit on 1/9/15.
 */

@Controller
@RequestMapping("/process")


public class ProcessAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessAPI.class);
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    private PropertiesDAO propertiesDAO;

    /**
     * This method calls proc GetProcess and fetches a record corresponding to processId passed.
     *
     * @param processId
     * @return restWrapper It contains an instance of Process corresponding to processId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper get(
            @PathVariable("id") Integer processId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {

            Process process = new Process();
            process.setProcessId(processId);
            com.wipro.ats.bdre.md.dao.jpa.Process daoProcess = processDAO.get(processId);
            if (daoProcess != null) {
                process.setBusDomainId(daoProcess.getBusDomain().getBusDomainId());
                if (daoProcess.getWorkflowType() != null) {
                    process.setWorkflowId(daoProcess.getWorkflowType().getWorkflowId());
                }
                process.setDescription(daoProcess.getDescription());
                process.setProcessName(daoProcess.getProcessName());
                process.setProcessTypeId(daoProcess.getProcessType().getProcessTypeId());
                if (daoProcess.getProcess() != null) {
                    process.setParentProcessId(daoProcess.getProcess().getProcessId());
                }
                process.setCanRecover(daoProcess.getCanRecover());
                if (daoProcess.getProcessTemplate() != null) {
                    process.setProcessTemplateId(daoProcess.getProcessTemplate().getProcessTemplateId());
                }
                process.setEnqProcessId(daoProcess.getEnqueuingProcessId());
                process.setNextProcessIds(daoProcess.getNextProcessId());
                if (daoProcess.getBatchCutPattern() != null) {
                    process.setBatchPattern(daoProcess.getBatchCutPattern());
                }
                process.setTableAddTS(DateConverter.dateToString(daoProcess.getAddTs()));
                process.setTableEditTS(DateConverter.dateToString(daoProcess.getEditTs()));
            }
            restWrapper = new RestWrapper(process, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " selected from Process by User:" + principal.getName());
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;

    }

    /**
     * This method calls proc DeleteProcess and deletes a record corresponding to processId passed.
     *
     * @param processId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody public
    RestWrapper delete(
            @PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            processDAO.delete(processId);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from Process by User:" + principal.getName());
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
                     @RequestParam(value = "size", defaultValue = "10") int pageSize,
                     @RequestParam(value = "pid", defaultValue = "0") Integer pid, Principal principal) {

        RestWrapper restWrapper = null;
        Integer processId = pid;
        try {
            if (pid == 0) {
                processId = null;
            }
            Integer counter=processDAO.totalRecordCount(processId);
            List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.list(processId, startPage, pageSize);
            List<Process> processes = new ArrayList<Process>();

            for (com.wipro.ats.bdre.md.dao.jpa.Process daoProcess : processList) {
                Process tableProcess = new Process();
                tableProcess.setProcessId(daoProcess.getProcessId());
                tableProcess.setBusDomainId(daoProcess.getBusDomain().getBusDomainId());
                if (daoProcess.getWorkflowType() != null) {
                    tableProcess.setWorkflowId(daoProcess.getWorkflowType().getWorkflowId());
                }
                tableProcess.setDescription(daoProcess.getDescription());
                tableProcess.setProcessName(daoProcess.getProcessName());
                tableProcess.setProcessTypeId(daoProcess.getProcessType().getProcessTypeId());
                if (daoProcess.getProcess() != null) {
                    tableProcess.setParentProcessId(daoProcess.getProcess().getProcessId());
                }
                tableProcess.setCanRecover(daoProcess.getCanRecover());
                if (daoProcess.getProcessTemplate() != null) {
                    tableProcess.setProcessTemplateId(daoProcess.getProcessTemplate().getProcessTemplateId());
                }
                tableProcess.setEnqProcessId(daoProcess.getEnqueuingProcessId());
                tableProcess.setNextProcessIds(daoProcess.getNextProcessId());
                if (daoProcess.getBatchCutPattern() != null) {
                    tableProcess.setBatchPattern(daoProcess.getBatchCutPattern());
                }
                tableProcess.setTableAddTS(DateConverter.dateToString(daoProcess.getAddTs()));
                tableProcess.setTableEditTS(DateConverter.dateToString(daoProcess.getEditTs()));
                tableProcess.setDeleteFlag(daoProcess.getDeleteFlag());
                tableProcess.setCounter(counter);
                processes.add(tableProcess);
            }
            restWrapper = new RestWrapper(processes, RestWrapper.OK);
            LOGGER.info("All records listed from Process by User:" + principal.getName());
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateProcess and updates the values. It also validates the values passed.
     *
     * @param process       Instance of Process.
     * @param bindingResult
     * @return restWrapper It contains the updated instance of Process.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper update(@ModelAttribute("process")
                       @Valid Process process, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.Process updateDaoProcess =processDAO.get(process.getProcessId());
            com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
            daoProcessType.setProcessTypeId(process.getProcessTypeId());
            updateDaoProcess.setProcessType(daoProcessType);
            if (process.getWorkflowId() != null) {
                WorkflowType daoWorkflowType = new WorkflowType();
                daoWorkflowType.setWorkflowId(process.getWorkflowId());
                updateDaoProcess.setWorkflowType(daoWorkflowType);
            }
            BusDomain daoBusDomain = new BusDomain();
            daoBusDomain.setBusDomainId(process.getBusDomainId());
            updateDaoProcess.setBusDomain(daoBusDomain);
            if (process.getProcessTemplateId() != null) {
                ProcessTemplate daoProcessTemplate = new ProcessTemplate();
                daoProcessTemplate.setProcessTemplateId(process.getProcessTemplateId());
                updateDaoProcess.setProcessTemplate(daoProcessTemplate);
            }
            if (process.getParentProcessId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.Process parentProcess =processDAO.get(process.getParentProcessId());
                updateDaoProcess.setProcess(parentProcess);
            }
            updateDaoProcess.setDescription(process.getDescription());
            updateDaoProcess.setAddTs(DateConverter.stringToDate(process.getTableAddTS()));
            updateDaoProcess.setProcessName(process.getProcessName());
            if (process.getCanRecover() == null)
                updateDaoProcess.setCanRecover(true);
            else
                updateDaoProcess.setCanRecover(process.getCanRecover());
            updateDaoProcess.setEnqueuingProcessId(process.getEnqProcessId());
            if (process.getBatchPattern() != null) {
                updateDaoProcess.setBatchCutPattern(process.getBatchPattern());
            }
            updateDaoProcess.setNextProcessId(process.getNextProcessIds());
            if (process.getDeleteFlag() == null)
                updateDaoProcess.setDeleteFlag(false);
            else
                updateDaoProcess.setDeleteFlag(process.getDeleteFlag());

            updateDaoProcess.setEditTs(DateConverter.stringToDate(process.getTableEditTS()));
            updateDaoProcess = processDAO.update(updateDaoProcess);
            process.setTableAddTS(DateConverter.dateToString(updateDaoProcess.getAddTs()));
            process.setTableEditTS(DateConverter.dateToString(updateDaoProcess.getEditTs()));
            restWrapper = new RestWrapper(process, RestWrapper.OK);
            LOGGER.info("Record with ID:" + process.getProcessId() + " updated in Process by User:" + principal.getName() + process);
        }catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProcess and adds a record in process table. it also validates the values passed.
     *
     * @param process       Instance of Process.
     * @param bindingResult
     * @return restWrapper It contains an instance of Process newly added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(@ModelAttribute("process")
                       @Valid Process process, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.Process insertDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
            daoProcessType.setProcessTypeId(process.getProcessTypeId());
            insertDaoProcess.setProcessType(daoProcessType);
            if (process.getWorkflowId() != null) {
                WorkflowType daoWorkflowType = new WorkflowType();
                daoWorkflowType.setWorkflowId(process.getWorkflowId());
                insertDaoProcess.setWorkflowType(daoWorkflowType);
            }
            BusDomain daoBusDomain = new BusDomain();
            daoBusDomain.setBusDomainId(process.getBusDomainId());
            insertDaoProcess.setBusDomain(daoBusDomain);
            if (process.getProcessTemplateId() != null) {
                ProcessTemplate daoProcessTemplate = new ProcessTemplate();
                daoProcessTemplate.setProcessTemplateId(process.getProcessTemplateId());
                insertDaoProcess.setProcessTemplate(daoProcessTemplate);
            }
            if (process.getParentProcessId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.Process parentProcess =processDAO.get(process.getParentProcessId());
                insertDaoProcess.setProcess(parentProcess);
            }
            insertDaoProcess.setDescription(process.getDescription());
            insertDaoProcess.setAddTs(DateConverter.stringToDate(process.getTableAddTS()));
            insertDaoProcess.setProcessName(process.getProcessName());
            if (process.getCanRecover() == null)
                insertDaoProcess.setCanRecover(true);
            else
                insertDaoProcess.setCanRecover(process.getCanRecover());
            insertDaoProcess.setEnqueuingProcessId(process.getEnqProcessId());
            if (process.getBatchPattern() != null) {
                insertDaoProcess.setBatchCutPattern(process.getBatchPattern());
            }
            insertDaoProcess.setNextProcessId(process.getNextProcessIds());
            if (process.getDeleteFlag() == null) {
                insertDaoProcess.setDeleteFlag(false);
            } else {
                insertDaoProcess.setDeleteFlag(process.getDeleteFlag());
            }
            insertDaoProcess.setEditTs(DateConverter.stringToDate(process.getTableEditTS()));
            Integer processId = processDAO.insert(insertDaoProcess);
            process.setProcessId(processId);
            process.setTableAddTS(DateConverter.dateToString(insertDaoProcess.getAddTs()));
            process.setTableEditTS(DateConverter.dateToString(insertDaoProcess.getEditTs()));
            restWrapper = new RestWrapper(process, RestWrapper.OK);
            LOGGER.info("Record with ID:" + process.getProcessId() + " inserted in Process by User:" + principal.getName() + process);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/export/{id}", "/export/{id}/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper export(@PathVariable("id") Integer processId
    ) {
        RestWrapper restWrapper = null;
        try {
            Process process = new Process();
            process.setProcessId(processId);
            List<Process> processList = new ArrayList<Process>();
            List<com.wipro.ats.bdre.md.dao.jpa.Process> daoProcessList = processDAO.selectProcessList(processId);
            for (com.wipro.ats.bdre.md.dao.jpa.Process daoProcess : daoProcessList) {
                Process tableProcess = new Process();
                tableProcess.setProcessId(daoProcess.getProcessId());
                tableProcess.setBusDomainId(daoProcess.getBusDomain().getBusDomainId());
                if (daoProcess.getWorkflowType() != null) {
                    tableProcess.setWorkflowId(daoProcess.getWorkflowType().getWorkflowId());
                }
                tableProcess.setDescription(daoProcess.getDescription());
                tableProcess.setProcessName(daoProcess.getProcessName());
                tableProcess.setProcessTypeId(daoProcess.getProcessType().getProcessTypeId());
                if (daoProcess.getProcess() != null) {
                    tableProcess.setParentProcessId(daoProcess.getProcess().getProcessId());
                }
                tableProcess.setCanRecover(daoProcess.getCanRecover());
                if (daoProcess.getProcessTemplate() != null) {
                    tableProcess.setProcessTemplateId(daoProcess.getProcessTemplate().getProcessTemplateId());
                }
                tableProcess.setEnqProcessId(daoProcess.getEnqueuingProcessId());
                tableProcess.setNextProcessIds(daoProcess.getNextProcessId());
                tableProcess.setBatchPattern(daoProcess.getBatchCutPattern());
                if (daoProcess.getBatchCutPattern() != null) {
                    tableProcess.setTableAddTS(DateConverter.dateToString(daoProcess.getAddTs()));
                }
                tableProcess.setTableEditTS(DateConverter.dateToString(daoProcess.getEditTs()));
                tableProcess.setDeleteFlag(daoProcess.getDeleteFlag());
                processList.add(tableProcess);
            }
            List<Properties> propertiesList = new ArrayList<Properties>();
            com.wipro.ats.bdre.md.dao.jpa.Process process1 = new com.wipro.ats.bdre.md.dao.jpa.Process();
            process1.setProcessId(processId);
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> daoPropertiesList = propertiesDAO.getByProcessId(process1);
            for (com.wipro.ats.bdre.md.dao.jpa.Properties daoProperties : daoPropertiesList) {
                Properties tableProperties = new Properties();
                tableProperties.setProcessId(daoProperties.getProcess().getProcessId());
                tableProperties.setConfigGroup(daoProperties.getConfigGroup());
                tableProperties.setKey(daoProperties.getId().getPropKey());
                tableProperties.setValue(daoProperties.getPropValue());
                tableProperties.setDescription(daoProperties.getDescription());
                propertiesList.add(tableProperties);
            }
            ProcessExport processExport = new ProcessExport();
            processExport.setProcessList(processList);
            processExport.setPropertiesList(propertiesList);
            restWrapper = new RestWrapper(processExport, RestWrapper.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/zippedexport/{id}", "/zippedexport/{id}/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper zippedexport(HttpServletResponse resp,
                       @PathVariable("id") Integer processId
    ) {
        RestWrapper restWrapper = null;
        ProcessExport processExport = new ProcessExport();
        try
           {
            Process process = new Process();
            process.setProcessId(processId);
            List<Process> processList = new ArrayList<Process>();
            List<com.wipro.ats.bdre.md.dao.jpa.Process> daoProcessList = processDAO.selectProcessList(processId);
            for (com.wipro.ats.bdre.md.dao.jpa.Process daoProcess : daoProcessList) {
                Process tableProcess = new Process();
                tableProcess.setProcessId(daoProcess.getProcessId());
                tableProcess.setBusDomainId(daoProcess.getBusDomain().getBusDomainId());
                if (daoProcess.getWorkflowType() != null) {
                    tableProcess.setWorkflowId(daoProcess.getWorkflowType().getWorkflowId());
                }
                tableProcess.setDescription(daoProcess.getDescription());
                tableProcess.setProcessName(daoProcess.getProcessName());
                tableProcess.setProcessTypeId(daoProcess.getProcessType().getProcessTypeId());
                if (daoProcess.getProcess() != null) {
                    tableProcess.setParentProcessId(daoProcess.getProcess().getProcessId());
                }
                tableProcess.setCanRecover(daoProcess.getCanRecover());
                if (daoProcess.getProcessTemplate() != null) {
                    tableProcess.setProcessTemplateId(daoProcess.getProcessTemplate().getProcessTemplateId());
                }
                tableProcess.setEnqProcessId(daoProcess.getEnqueuingProcessId());
                tableProcess.setNextProcessIds(daoProcess.getNextProcessId());
                tableProcess.setBatchPattern(daoProcess.getBatchCutPattern());
                if (daoProcess.getBatchCutPattern() != null) {
                    tableProcess.setTableAddTS(DateConverter.dateToString(daoProcess.getAddTs()));
                }
                tableProcess.setTableEditTS(DateConverter.dateToString(daoProcess.getEditTs()));
                tableProcess.setDeleteFlag(daoProcess.getDeleteFlag());
                tableProcess.setProcessCode(daoProcess.getProcessCode());
                processList.add(tableProcess);
            }
            List<Properties> propertiesList = new ArrayList<Properties>();
            for (com.wipro.ats.bdre.md.dao.jpa.Process process1 : daoProcessList){
            List<com.wipro.ats.bdre.md.dao.jpa.Properties> daoPropertiesList = propertiesDAO.getByProcessId(process1);
            for (com.wipro.ats.bdre.md.dao.jpa.Properties daoProperties : daoPropertiesList) {
                Properties tableProperties = new Properties();
                tableProperties.setProcessId(daoProperties.getProcess().getProcessId());
                tableProperties.setConfigGroup(daoProperties.getConfigGroup());
                tableProperties.setKey(daoProperties.getId().getPropKey());
                tableProperties.setValue(daoProperties.getPropValue());
                tableProperties.setDescription(daoProperties.getDescription());
                propertiesList.add(tableProperties);
            }}
            processExport.setProcessList(processList);
            processExport.setPropertiesList(propertiesList);
            Export export=new Export();
            String zippedFileLocatin=export.compress(processId.toString(),processExport);
            LOGGER.info("zippedfile location is "+zippedFileLocatin);
            // Find this file id in database to get file name, and file type .You must tell the browser the file type you are going to send
            // for example application/pdf, text/plain, text/html, image/jpg
            resp.setContentType("application/zip");
            // Make sure to show the download dialog
            resp.setHeader("Content-Disposition", "attachment; filename=" + processId + ".zip");
            // Assume file name is retrieved from database
            // For example D:\\file\\test.pdf
            File myFile = new File(zippedFileLocatin);
            // This should send the file to browser
            OutputStream out = resp.getOutputStream();
            FileInputStream in = new FileInputStream(myFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            in.close();
                out.flush();
            restWrapper = new RestWrapper(processExport, RestWrapper.OK);
            }
        catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } catch (IOException io){
            LOGGER.error(io);
            restWrapper = new RestWrapper(io.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/import", "/import/"}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper importData(@ModelAttribute("fileString")
                           @Valid String uploadedFileName, BindingResult bindingResult) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String homeDir = System.getProperty("user.home");
            LOGGER.info("home directory" + homeDir);
            Import pimport=new Import();
            String zippedFileLocation = "";
            if (uploadedFileName.contains("bdreappstore-apps")){
                zippedFileLocation = homeDir + "/" + uploadedFileName;
            } else {
                zippedFileLocation = homeDir + "/bdre-wfd/zip/" + uploadedFileName;
            }
            String outputDir=homeDir+"/bdre-wfd/intermediateDir";
            String fileString=pimport.unZipIt(zippedFileLocation,outputDir);
            ProcessExport processExport = mapper.readValue(fileString, ProcessExport.class);
            for (Process process : processExport.getProcessList()) {
                process.setProcessTemplateId(0);
            }
            Map<String,String> importedTable=new HashMap<String,String>();
            List<Process>   allImportedProcessList=processExport.getProcessList();
            for (Process process : allImportedProcessList)
            {
                importedTable.put(process.getProcessCode(),process.getProcessId().toString());

            }
            Process parentProcess = processExport.getProcessList().get(0);
            List<Process> dbList = new ArrayList<Process>();
            com.wipro.ats.bdre.md.dao.jpa.Process dbParentProcess=processDAO.returnProcess(parentProcess.getProcessCode());
            Integer parentProcessId=null;
            if (dbParentProcess==null)
            {
                com.wipro.ats.bdre.md.dao.jpa.Process insertDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
                daoProcessType.setProcessTypeId(parentProcess.getProcessTypeId());
                insertDaoProcess.setProcessType(daoProcessType);
                if (parentProcess.getWorkflowId() != null) {
                    WorkflowType daoWorkflowType = new WorkflowType();
                    daoWorkflowType.setWorkflowId(parentProcess.getWorkflowId());
                    insertDaoProcess.setWorkflowType(daoWorkflowType);
                }
                BusDomain daoBusDomain = new BusDomain();
                daoBusDomain.setBusDomainId(parentProcess.getBusDomainId());
                insertDaoProcess.setBusDomain(daoBusDomain);
                if (parentProcess.getProcessTemplateId() != null) {
                    ProcessTemplate daoProcessTemplate = new ProcessTemplate();
                    daoProcessTemplate.setProcessTemplateId(parentProcess.getProcessTemplateId());
                    insertDaoProcess.setProcessTemplate(daoProcessTemplate);
                }
                    insertDaoProcess.setProcess(null);
                insertDaoProcess.setDescription(parentProcess.getDescription());
                insertDaoProcess.setAddTs(DateConverter.stringToDate(parentProcess.getTableAddTS()));
                insertDaoProcess.setProcessName(parentProcess.getProcessName());
                if (parentProcess.getCanRecover() == null)
                    insertDaoProcess.setCanRecover(true);
                else
                    insertDaoProcess.setCanRecover(parentProcess.getCanRecover());
                insertDaoProcess.setEnqueuingProcessId(parentProcess.getEnqProcessId());
                if (parentProcess.getBatchPattern() != null) {
                    insertDaoProcess.setBatchCutPattern(parentProcess.getBatchPattern());
                }
                insertDaoProcess.setNextProcessId(parentProcess.getNextProcessIds());
                LOGGER.info(parentProcess.getNextProcessIds());
                if (parentProcess.getDeleteFlag() == null)
                    insertDaoProcess.setDeleteFlag(false);
                else
                    insertDaoProcess.setDeleteFlag(parentProcess.getDeleteFlag());
                insertDaoProcess.setEditTs(DateConverter.stringToDate(parentProcess.getTableEditTS()));
                insertDaoProcess.setProcessCode(parentProcess.getProcessCode());
                parentProcessId = processDAO.insert(insertDaoProcess);
                parentProcess.setProcessId(parentProcessId);
                processExport.getProcessList().get(0).setProcessId(parentProcessId);
                parentProcess.setTableAddTS(DateConverter.dateToString(insertDaoProcess.getAddTs()));
                parentProcess.setTableEditTS(DateConverter.dateToString(insertDaoProcess.getEditTs()));
            }
            else
            {
                processExport.getProcessList().get(0).setProcessId(dbParentProcess.getProcessId());
            }
            List<com.wipro.ats.bdre.md.dao.jpa.Process> daoProcessList = processDAO.selectProcessList(parentProcess.getProcessCode());
            for (com.wipro.ats.bdre.md.dao.jpa.Process daoProcess : daoProcessList) {
                Process tableProcess = new Process();
                tableProcess.setProcessId(daoProcess.getProcessId());
                tableProcess.setBusDomainId(daoProcess.getBusDomain().getBusDomainId());
                if (daoProcess.getWorkflowType() != null) {
                    tableProcess.setWorkflowId(daoProcess.getWorkflowType().getWorkflowId());
                }
                tableProcess.setDescription(daoProcess.getDescription());
                tableProcess.setProcessName(daoProcess.getProcessName());
                tableProcess.setProcessTypeId(daoProcess.getProcessType().getProcessTypeId());
                if (daoProcess.getProcess() != null) {
                    tableProcess.setParentProcessId(daoProcess.getProcess().getProcessId());
                }
                tableProcess.setCanRecover(daoProcess.getCanRecover());
                if (daoProcess.getProcessTemplate() != null) {
                    tableProcess.setProcessTemplateId(daoProcess.getProcessTemplate().getProcessTemplateId());
                }
                tableProcess.setEnqProcessId(daoProcess.getEnqueuingProcessId());
                tableProcess.setNextProcessIds(daoProcess.getNextProcessId());
                if (daoProcess.getBatchCutPattern() != null) {
                    tableProcess.setBatchPattern(daoProcess.getBatchCutPattern());
                }
                tableProcess.setTableAddTS(DateConverter.dateToString(daoProcess.getAddTs()));
                tableProcess.setTableEditTS(DateConverter.dateToString(daoProcess.getEditTs()));
                tableProcess.setDeleteFlag(daoProcess.getDeleteFlag());
                tableProcess.setProcessCode(daoProcess.getProcessCode());
                dbList.add(tableProcess);
            }
            List<Integer> dbProcessIdList = new ArrayList<Integer>();
            List<Integer> importProcessIdList = new ArrayList<Integer>();
            List<String> dbProcessCodeList=new ArrayList<>();
            List<String> importedProcessCodeList=new ArrayList<>();
            List<String> commonPCodeList = new ArrayList<>();
            List<String> diffPCodeList = new ArrayList<>();
            List<String> toDeletePCodeList = new ArrayList<String>();
            for (Process p : dbList) {
                dbProcessIdList.add(p.getProcessId());
                dbProcessCodeList.add(p.getProcessCode());
            }
            for (Process p : processExport.getProcessList()) {
                importProcessIdList.add(p.getProcessId());
                importedProcessCodeList.add(p.getProcessCode());
            }
            Set<String> set = new HashSet<String>();
            for (String i : dbProcessCodeList) {
                set.add(i);
            }
            for (String i : importedProcessCodeList) {
                if (set.contains(i)) {
                    commonPCodeList.add(i);
                } else {
                    diffPCodeList.add(i);
                }
            }
            Set<String> setForDelete = new HashSet<String>();
            for (String i : importedProcessCodeList) {
                setForDelete.add(i);
            }
            for (String i : dbProcessCodeList) {
                if (!setForDelete.contains(i)) {
                    toDeletePCodeList.add(i);
                }
            }
            for (Process process : processExport.getProcessList()) {
                if (diffPCodeList.contains(process.getProcessCode())) {
                    com.wipro.ats.bdre.md.dao.jpa.Process insertDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                    com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
                    daoProcessType.setProcessTypeId(process.getProcessTypeId());
                    insertDaoProcess.setProcessType(daoProcessType);
                    if (process.getWorkflowId() != null) {
                        WorkflowType daoWorkflowType = new WorkflowType();
                        daoWorkflowType.setWorkflowId(process.getWorkflowId());
                        insertDaoProcess.setWorkflowType(daoWorkflowType);
                    }
                    BusDomain daoBusDomain = new BusDomain();
                    daoBusDomain.setBusDomainId(process.getBusDomainId());
                    insertDaoProcess.setBusDomain(daoBusDomain);
                    if (process.getProcessTemplateId() != null) {
                        ProcessTemplate daoProcessTemplate = new ProcessTemplate();
                        daoProcessTemplate.setProcessTemplateId(process.getProcessTemplateId());
                        insertDaoProcess.setProcessTemplate(daoProcessTemplate);
                    }
                    if (dbParentProcess!= null) {
                        insertDaoProcess.setProcess(dbParentProcess);
                    }
                    else {
                        insertDaoProcess.setProcess(processDAO.get(parentProcessId));
                    }
                    insertDaoProcess.setDescription(process.getDescription());
                    insertDaoProcess.setAddTs(DateConverter.stringToDate(process.getTableAddTS()));
                    insertDaoProcess.setProcessName(process.getProcessName());
                    if (process.getCanRecover() == null)
                        insertDaoProcess.setCanRecover(true);
                    else
                        insertDaoProcess.setCanRecover(process.getCanRecover());
                    insertDaoProcess.setEnqueuingProcessId(process.getEnqProcessId());
                    if (process.getBatchPattern() != null) {
                        insertDaoProcess.setBatchCutPattern(process.getBatchPattern());
                    }
                    insertDaoProcess.setNextProcessId(process.getNextProcessIds());
                    if (process.getDeleteFlag() == null)
                        insertDaoProcess.setDeleteFlag(false);
                    else
                        insertDaoProcess.setDeleteFlag(process.getDeleteFlag());
                    insertDaoProcess.setEditTs(DateConverter.stringToDate(process.getTableEditTS()));
                    insertDaoProcess.setProcessCode(process.getProcessCode());
                    Integer processId = processDAO.insert(insertDaoProcess);
                    process.setProcessId(processId);
                    process.setTableAddTS(DateConverter.dateToString(insertDaoProcess.getAddTs()));
                    process.setTableEditTS(DateConverter.dateToString(insertDaoProcess.getEditTs()));
                }
            }
            for (Process process : processExport.getProcessList()) {
                if (commonPCodeList.contains(process.getProcessCode())) {
                    com.wipro.ats.bdre.md.dao.jpa.Process updateDaoProcess =processDAO.returnProcess(process.getProcessCode());
                    com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
                    daoProcessType.setProcessTypeId(process.getProcessTypeId());
                    updateDaoProcess.setProcessType(daoProcessType);
                    if (process.getWorkflowId() != null) {
                        WorkflowType daoWorkflowType = new WorkflowType();
                        daoWorkflowType.setWorkflowId(process.getWorkflowId());
                        updateDaoProcess.setWorkflowType(daoWorkflowType);
                    }
                    BusDomain daoBusDomain = new BusDomain();
                    daoBusDomain.setBusDomainId(process.getBusDomainId());
                    updateDaoProcess.setBusDomain(daoBusDomain);
                    if (process.getProcessTemplateId() != null) {
                        ProcessTemplate daoProcessTemplate = new ProcessTemplate();
                        daoProcessTemplate.setProcessTemplateId(process.getProcessTemplateId());
                        updateDaoProcess.setProcessTemplate(daoProcessTemplate);
                    }
                    if (process.getParentProcessId()!=null){
                    if (dbParentProcess!= null) {
                        updateDaoProcess.setProcess(dbParentProcess);
                    }
                    else
                    {
                        updateDaoProcess.setProcess(processDAO.get(parentProcessId));
                    }}
                    updateDaoProcess.setDescription(process.getDescription());
                    updateDaoProcess.setAddTs(DateConverter.stringToDate(process.getTableAddTS()));
                    updateDaoProcess.setProcessName(process.getProcessName());
                    if (process.getCanRecover() == null)
                        updateDaoProcess.setCanRecover(true);
                    else
                        updateDaoProcess.setCanRecover(process.getCanRecover());
                    updateDaoProcess.setEnqueuingProcessId(process.getEnqProcessId());
                    if (process.getBatchPattern() != null) {
                        updateDaoProcess.setBatchCutPattern(process.getBatchPattern());
                    }
                    updateDaoProcess.setNextProcessId(process.getNextProcessIds());
                    if (process.getDeleteFlag() == null)
                        updateDaoProcess.setDeleteFlag(false);
                    else
                        updateDaoProcess.setDeleteFlag(process.getDeleteFlag());

                    updateDaoProcess.setEditTs(DateConverter.stringToDate(process.getTableEditTS()));
                    updateDaoProcess = processDAO.update(updateDaoProcess);
                    process.setProcessId(updateDaoProcess.getProcessId());
                    process.setTableAddTS(DateConverter.dateToString(updateDaoProcess.getAddTs()));
                    process.setTableEditTS(DateConverter.dateToString(updateDaoProcess.getEditTs()));
                }

            }

            for (Process process : dbList) {
                if (toDeletePCodeList.contains(process.getProcessCode())) {
                    processDAO.delete(process.getProcessId());
                }

            }
            Map<String,String> table = new HashMap<String,String>();
            List<com.wipro.ats.bdre.md.dao.jpa.Process> allDaoProcessList = processDAO.selectProcessList(parentProcess.getProcessCode());
             for (com.wipro.ats.bdre.md.dao.jpa.Process dbInsertedProcess : allDaoProcessList)
             {
                 table.put(importedTable.get(dbInsertedProcess.getProcessCode()),dbInsertedProcess.getProcessId().toString());

             }
            for (com.wipro.ats.bdre.md.dao.jpa.Process dbInsertedProcess : allDaoProcessList)
            {
                String nextProcessIds=dbInsertedProcess.getNextProcessId();
                String updatedNextProcessIds="";
                String[] temp=nextProcessIds.split(",");
                for (int i=0;i<temp.length;i++)
                {
                    updatedNextProcessIds=updatedNextProcessIds+table.get(temp[i])+",";
                }
                dbInsertedProcess.setNextProcessId(updatedNextProcessIds.substring(0,updatedNextProcessIds.length()-1));
                processDAO.update(dbInsertedProcess);
                propertiesDAO.deleteByProcessId(dbInsertedProcess);
            }
            for (Properties properties : processExport.getPropertiesList()) {
                Integer updatedProcessId=Integer.valueOf(table.get(properties.getProcessId().toString()));
                properties.setProcessId(updatedProcessId);
                com.wipro.ats.bdre.md.dao.jpa.Properties insertProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId propertiesId = new PropertiesId();
                propertiesId.setPropKey(properties.getKey());
                propertiesId.setProcessId(properties.getProcessId());
                insertProperties.setId(propertiesId);
                com.wipro.ats.bdre.md.dao.jpa.Process process = processDAO.get(properties.getProcessId());
                insertProperties.setProcess(process);
                insertProperties.setConfigGroup(properties.getConfigGroup());
                insertProperties.setPropValue(properties.getValue());
                insertProperties.setDescription(properties.getDescription());
                propertiesDAO.insert(insertProperties);

            }
            com.wipro.ats.bdre.md.dao.jpa.Process parentProcessInserted=processDAO.returnProcess(parentProcess.getProcessCode());
            File oldDir = new File(homeDir+"/bdre-wfd/intermediateDir");
            File newDir=new File(homeDir+"/bdre-wfd/"+parentProcessInserted.getProcessId());
            if (newDir.exists())
            {
                newDir.delete();
            }
            if ( oldDir.isDirectory() ) {
                oldDir.renameTo(newDir);
            } else {
                oldDir.mkdir();
                oldDir.renameTo(newDir);
            }
            restWrapper = new RestWrapper(processExport, RestWrapper.OK);
        }  catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (IOException io){
            LOGGER.error(io);
            restWrapper = new RestWrapper(io.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/execute", "/execute/"}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper executeProcess(@ModelAttribute("process")
                               @Valid Process process, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        ExecutionInfo executionInfo = new ExecutionInfo();
        executionInfo.setProcessId(process.getProcessId());
        try {
            String[] command = {MDConfig.getProperty("execute.script-path") + "/job-executor.sh",process.getBusDomainId().toString(), process.getProcessTypeId().toString(), process.getProcessId().toString()};
            LOGGER.info("Running the command : -- " + command[0] + " " + command[1] + " " + command[2] + " " + command[3]);
            ProcessBuilder processBuilder = new ProcessBuilder(command[0], command[1],command[2],command[3]);
            processBuilder.redirectOutput(new File(MDConfig.getProperty("execute.log-path") + process.getProcessId().toString()));
            LOGGER.info("The output is redirected to " + MDConfig.getProperty("execute.log-path") + process.getProcessId().toString());
            processBuilder.redirectErrorStream(true);
            java.lang.Process osProcess = processBuilder.start();
            try {
                Class<?> cProcessImpl = osProcess.getClass();
                Field fPid = cProcessImpl.getDeclaredField("pid");
                if (!fPid.isAccessible()) {
                    fPid.setAccessible(true);
                }
                executionInfo.setOSProcessId(fPid.getInt(osProcess));
                LOGGER.debug(" OS process Id : "+executionInfo.getOSProcessId() + "executed by " + principal.getName());
            } catch (Exception e) {
                executionInfo.setOSProcessId(-1);
                LOGGER.error(e + " Setting OS Process ID failed " + executionInfo.getOSProcessId());
            }
            restWrapper = new RestWrapper(executionInfo, RestWrapper.OK);
        } catch (Exception e) {
            LOGGER.error( e + " Executing workflow failed " +e.getCause());
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc CloneProcess and adds a clone of process id passed in process table .It also validates the values passed.
     *
     * @param processId
     * @return restWrapper It contains an instance of Process newly added.
     */
    @RequestMapping(value = {"/clone/{id}", "/clone/{id}/"}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insertClone(@PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;

        try {
            com.wipro.ats.bdre.md.dao.jpa.Process clonedDaoProcess = processDAO.cloneProcess(processId);
            Process processes = new Process();
            processes.setProcessId(clonedDaoProcess.getProcessId());
            processes.setBusDomainId(clonedDaoProcess.getBusDomain().getBusDomainId());
            if (clonedDaoProcess.getWorkflowType() != null) {
                processes.setWorkflowId(clonedDaoProcess.getWorkflowType().getWorkflowId());
            }
            processes.setDescription(clonedDaoProcess.getDescription());
            processes.setProcessName(clonedDaoProcess.getProcessName());
            processes.setProcessTypeId(clonedDaoProcess.getProcessType().getProcessTypeId());
            if (clonedDaoProcess.getProcess() != null) {
                processes.setParentProcessId(clonedDaoProcess.getProcess().getProcessId());
            }
            processes.setCanRecover(clonedDaoProcess.getCanRecover());
            if (clonedDaoProcess.getProcessTemplate() != null) {
                processes.setProcessTemplateId(clonedDaoProcess.getProcessTemplate().getProcessTemplateId());
            }
            processes.setEnqProcessId(clonedDaoProcess.getEnqueuingProcessId());
            processes.setNextProcessIds(clonedDaoProcess.getNextProcessId());
            if (clonedDaoProcess.getBatchCutPattern() != null) {
                processes.setBatchPattern(clonedDaoProcess.getBatchCutPattern());
            }
            processes.setTableAddTS(DateConverter.dateToString(clonedDaoProcess.getAddTs()));
            processes.setTableEditTS(DateConverter.dateToString(clonedDaoProcess.getEditTs()));
            processes.setDeleteFlag(clonedDaoProcess.getDeleteFlag());


            restWrapper = new RestWrapper(processes, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processes.getProcessId() + " inserted in Process by User:" + principal.getName() + processes);
        } catch (MetadataException e) {
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
