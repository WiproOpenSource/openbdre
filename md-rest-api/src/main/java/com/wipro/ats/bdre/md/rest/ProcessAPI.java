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
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

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
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer processId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {

            Process process = new Process();
            process.setProcessId(processId);
//          process = s.selectOne("call_procedures.GetProcess", process);
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
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;

    }

    /**
     * This method calls proc DeleteProcess and deletes a record corresponding to processId passed.
     *
     * @param processId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer processId, Principal principal,
            ModelMap model) {
        RestWrapper restWrapper = null;
        try {
            processDAO.delete(processId);
//          s.delete("call_procedures.DeleteProcess", process);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from Process by User:" + principal.getName());
        } catch (Exception e) {
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

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize,
                     @RequestParam(value = "pid", defaultValue = "0") Integer pid, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            if (pid == 0) {
                pid = null;
            }
            Integer counter=processDAO.totalRecordCount(pid);
            List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.list(pid, startPage, pageSize);
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
            // List<Process> processes = s.selectList("call_procedures.GetProcesses", process);

            restWrapper = new RestWrapper(processes, RestWrapper.OK);
            LOGGER.info("All records listed from Process by User:" + principal.getName());
        } catch (Exception e) {
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
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("process")
                       @Valid Process process, BindingResult bindingResult, Principal principal) {

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
//            Process processes = s.selectOne("call_procedures.UpdateProcess", process);
            updateDaoProcess = processDAO.update(updateDaoProcess);
            process.setTableAddTS(DateConverter.dateToString(updateDaoProcess.getAddTs()));
            process.setTableEditTS(DateConverter.dateToString(updateDaoProcess.getEditTs()));
            restWrapper = new RestWrapper(process, RestWrapper.OK);
            LOGGER.info("Record with ID:" + process.getProcessId() + " updated in Process by User:" + principal.getName() + process);
        } catch (Exception e) {
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
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("process")
                       @Valid Process process, BindingResult bindingResult, Principal principal) {
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

//            Process processes = s.selectOne("call_procedures.InsertProcess", process);
            restWrapper = new RestWrapper(process, RestWrapper.OK);
            LOGGER.info("Record with ID:" + process.getProcessId() + " inserted in Process by User:" + principal.getName() + process);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/export/{id}", "/export/{id}/"}, method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper export(HttpServletResponse resp,
                       @PathVariable("id") Integer processId
    ) {
        RestWrapper restWrapper = null;
        //resp.setHeader("Content-Disposition", "attachment; filename=" + processId + ".json");
        try {
            Process process = new Process();
            process.setProcessId(processId);
//            List<Process> processList = s.selectList("call_procedures.select-parent-sub-process-list", process);
//            List<Properties> propertiesList = s.selectList("call_procedures.select-properties-list", process);
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
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }




    @RequestMapping(value = {"/zippedexport/{id}", "/zippedexport/{id}/"}, method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper zippedexport(HttpServletResponse resp,
                       @PathVariable("id") Integer processId
    ) {
        RestWrapper restWrapper = null;
        ProcessExport processExport = new ProcessExport();
        //resp.setHeader("Content-Disposition", "attachment; filename=" + processId + ".json");
        try {
            Process process = new Process();
            process.setProcessId(processId);
//            List<Process> processList = s.selectList("call_procedures.select-parent-sub-process-list", process);
//            List<Properties> propertiesList = s.selectList("call_procedures.select-properties-list", process);
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
                LOGGER.info("processCode during export is "+tableProcess.getProcessCode());
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




            String fileName = "";
            String fileType = "";
            // Find this file id in database to get file name, and file type

            // You must tell the browser the file type you are going to send
            // for example application/pdf, text/plain, text/html, image/jpg
            resp.setContentType("application/zip");

            // Make sure to show the download dialog
            resp.setHeader("Content-Disposition", "attachment; filename=" + processId + ".zip");

            // Assume file name is retrieved from database
            // For example D:\\file\\test.pdf

            File my_file = new File(zippedFileLocatin);

            // This should send the file to browser
            OutputStream out = resp.getOutputStream();
            FileInputStream in = new FileInputStream(my_file);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            in.close();
                out.flush();
            restWrapper = new RestWrapper(processExport, RestWrapper.OK);
            }
        catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }





    @RequestMapping(value = {"/import", "/import/"}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper importData(@ModelAttribute("fileString")
                           @Valid String uploadedFileName, BindingResult bindingResult) {
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            LOGGER.info("uploaded file name is "+uploadedFileName);
            String homeDir = System.getProperty("user.home");
            Import pimport=new Import();
            String zippedFileLocation=homeDir+"/bdre-wfd/zip/"+uploadedFileName;
            String outputDir=homeDir+"/bdre-wfd/"+uploadedFileName.substring(0,uploadedFileName.length()-4);
            LOGGER.info("zipped file location is "+zippedFileLocation);
            LOGGER.info("output dir is "+outputDir);
            String fileString=pimport.unZipIt(zippedFileLocation,outputDir);
            LOGGER.info("filestring in processAPI is "+fileString);
            ProcessExport processExport = mapper.readValue(fileString, ProcessExport.class);
            LOGGER.info(processExport.getProcessList());
            LOGGER.info(processExport.getPropertiesList());
            for (Process process : processExport.getProcessList()) {
                process.setProcessTemplateId(0);
            }
            Hashtable<String,String> importedTable=new Hashtable<String,String>();
            List<Process>   allImportedProcessList=processExport.getProcessList();
            for (Process process : allImportedProcessList)
            {
                importedTable.put(process.getProcessCode(),process.getProcessId().toString());
                //LOGGER.info(process.toString());

            }
            Process parentProcess = processExport.getProcessList().get(0);
//            List<Process> dbList = s.selectList("call_procedures.select-parent-sub-process-list", parentProcess);
            List<Process> dbList = new ArrayList<Process>();
            LOGGER.info("processCode searched for "+parentProcess.getProcessCode());
            com.wipro.ats.bdre.md.dao.jpa.Process dbParentProcess=processDAO.returnProcess(parentProcess.getProcessCode());
            LOGGER.info(dbParentProcess);
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
                LOGGER.info("parent process is "+insertDaoProcess.getProcess());
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
                LOGGER.info(parentProcess.toString());
                parentProcessId = processDAO.insert(insertDaoProcess);
                LOGGER.info("process id of  inserted  process is is "+parentProcessId);
                parentProcess.setProcessId(parentProcessId);
                parentProcess.setTableAddTS(DateConverter.dateToString(insertDaoProcess.getAddTs()));
                parentProcess.setTableEditTS(DateConverter.dateToString(insertDaoProcess.getEditTs()));
            }


            List<com.wipro.ats.bdre.md.dao.jpa.Process> daoProcessList = processDAO.selectProcessList(parentProcess.getProcessCode());
            LOGGER.info("daoProcessList size= "+daoProcessList);

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
                LOGGER.info("dblist size ="+dbList.size());
            }
            List<Integer> dbProcessIdList = new ArrayList<Integer>();
            List<Integer> importProcessIdList = new ArrayList<Integer>();
            List<String> dbProcessCodeList=new ArrayList<>();
            List<String> importedProcessCodeList=new ArrayList<>();
            List<String> commonPCodeList = new ArrayList<>();
            List<String> diffPCodeList = new ArrayList<>();
            List<String> toDeletePCodeList = new ArrayList<String>();



            for (Process p : dbList) {
                LOGGER.info(p.getProcessCode());
                dbProcessIdList.add(p.getProcessId());
                dbProcessCodeList.add(p.getProcessCode());

            }

               LOGGER.info("dbprocessidlist is  "+dbProcessIdList);
               LOGGER.info("dbprocesscodelist is"+dbProcessCodeList);



            for (Process p : processExport.getProcessList()) {
                importProcessIdList.add(p.getProcessId());
                importedProcessCodeList.add(p.getProcessCode());

            }

            LOGGER.info("importedprocessidlist is  "+importProcessIdList);
            LOGGER.info("importedprocesscodelist is"+importedProcessCodeList);


            HashSet<String> set = new HashSet<String>();
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
            HashSet<String> setForDelete = new HashSet<String>();
            for (String i : importedProcessCodeList) {
                setForDelete.add(i);
            }
            for (String i : dbProcessCodeList) {
                if (!setForDelete.contains(i)) {
                    toDeletePCodeList.add(i);
                }
            }

            LOGGER.info("diffPcodeList is "+diffPCodeList);
            LOGGER.info("CommonPcodeLIst is "+commonPCodeList);
            LOGGER.info("tobedeletedpcodelist is"+toDeletePCodeList);
            Process pIdUpdate = new Process();


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
                        LOGGER.info("parent processId is "+dbParentProcess.getProcessId());
                        insertDaoProcess.setProcess(dbParentProcess);
                    }
                    else
                    {

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
                    LOGGER.info(process.toString());
                    LOGGER.info("process Code to be added = " + process.getProcessCode());
                    LOGGER.info("process id to be inserted is "+process.getProcessId());
                    Integer processId = processDAO.insert(insertDaoProcess);
                    LOGGER.info("process id of  inserted  process is is "+processId);
                    process.setProcessId(processId);
                    process.setTableAddTS(DateConverter.dateToString(insertDaoProcess.getAddTs()));
                    process.setTableEditTS(DateConverter.dateToString(insertDaoProcess.getEditTs()));
                    //Process addedProcess = s.selectOne("call_procedures.InsertProcess", process);
                 //pIdUpdate.setProcessId(process.getProcessId());
                  //  pIdUpdate.setProcessTemplateId(process.getProcessTemplateId());
                  /// LOGGER.debug("before updating pid = " + pIdUpdate.getProcessId() + " replacer id= " + pIdUpdate.getProcessTemplateId());
//                  //  s.selectOne("call_procedures.UpdateProcessId", pIdUpdate);
                   // processDAO.updateProcessId(pIdUpdate.getProcessId(), pIdUpdate.getProcessTemplateId());
                }
            }

            for (Process process : processExport.getProcessList()) {
                if (commonPCodeList.contains(process.getProcessCode())) {
                    LOGGER.debug("updating existing processes,CODE= " + process.getProcessCode());

                    LOGGER.debug("before updating next process id= " + process.getNextProcessIds());
//                    s.selectOne("call_procedures.UpdateProcess", process);
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
                        LOGGER.info("parent processId is "+dbParentProcess.getProcessId());
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
//            Process processes = s.selectOne("call_procedures.UpdateProcess", process);
                    updateDaoProcess = processDAO.update(updateDaoProcess);
                    process.setTableAddTS(DateConverter.dateToString(updateDaoProcess.getAddTs()));
                    process.setTableEditTS(DateConverter.dateToString(updateDaoProcess.getEditTs()));
                }

            }

            for (Process process : dbList) {
                if (toDeletePCodeList.contains(process.getProcessCode())) {
                    LOGGER.debug("deleting missing processes, CODE= " + process.getProcessCode());
//                    s.delete("call_procedures.DeleteProcess", process);
                    processDAO.delete(process.getProcessId());
                }

            }
            LOGGER.info("process completed");
            Hashtable<String,String> table = new Hashtable<String,String>();
            List<com.wipro.ats.bdre.md.dao.jpa.Process> allDaoProcessList = processDAO.selectProcessList(parentProcess.getProcessCode());
             for (com.wipro.ats.bdre.md.dao.jpa.Process dbInsertedProcess : allDaoProcessList)
             {

                     table.put(importedTable.get(dbInsertedProcess.getProcessCode()),dbInsertedProcess.getProcessId().toString());
                     LOGGER.info("Key : "+importedTable.get(dbInsertedProcess.getProcessCode())+" Value : "+dbInsertedProcess.getProcessId());

             }



            for (com.wipro.ats.bdre.md.dao.jpa.Process dbInsertedProcess : allDaoProcessList)
            {
                String nextProcessIds=dbInsertedProcess.getNextProcessId();
                LOGGER.info(dbInsertedProcess.getNextProcessId());
                String updatedNextProcessIds="";
                String[] temp=nextProcessIds.split(",");
                LOGGER.info(temp.toString());
                for (int i=0;i<temp.length;i++)
                {
                    LOGGER.info("temp[i] "+temp[i]);
                    updatedNextProcessIds=updatedNextProcessIds+table.get(temp[i])+",";
                }
                LOGGER.info("updatedNextProcessIds :"+updatedNextProcessIds);
                dbInsertedProcess.setNextProcessId(updatedNextProcessIds.substring(0,updatedNextProcessIds.length()-1));
                processDAO.update(dbInsertedProcess);
                propertiesDAO.deleteByProcessId(dbInsertedProcess);

            }
                  LOGGER.info("after deleting properties");

            for (Properties properties : processExport.getPropertiesList()) {
                LOGGER.info(properties.toString());
                Integer updatedProcessId=Integer.valueOf(table.get(properties.getProcessId().toString()));
                properties.setProcessId(updatedProcessId);
                LOGGER.info(properties.toString());
                LOGGER.info("Inserting all properties after delete, id= " + properties.getConfigGroup() + properties.getKey());
//              s.selectOne("call_procedures.InsertProperties", properties);
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
            File oldDir = new File(homeDir+"/bdre-wfd/"+uploadedFileName.substring(0,uploadedFileName.length()-4));
            File newDir=new File(homeDir+"/bdre-wfd/"+parentProcessInserted.getProcessId());
            if (newDir.exists())
            {
                LOGGER.info("dir deleted is "+newDir);
                newDir.delete();
            }
            if ( oldDir.isDirectory() ) {
                oldDir.renameTo(newDir);
            } else {
                oldDir.mkdir();
                oldDir.renameTo(newDir);
            }
            //File oldJsonFile = new File(homeDir+"/bdre-wfd/"+uploadedFileName.substring(0,uploadedFileName.length()-4)+"/"+uploadedFileName.substring(0,uploadedFileName.length()-4)+".json");

            File oldJsonFile = new File(homeDir+"/bdre-wfd/"+parentProcessInserted.getProcessId()+"/"+uploadedFileName.substring(0,uploadedFileName.length()-4)+".json");
            File newJsonFile = new File(homeDir+"/bdre-wfd/"+parentProcessInserted.getProcessId()+"/"+parentProcessInserted.getProcessId()+".json");
            if(oldJsonFile.renameTo(newJsonFile)) {
                System.out.println("renamed");
            } else {
                System.out.println("Error");
            }

            restWrapper = new RestWrapper(processExport, RestWrapper.OK);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/execute", "/execute/"}, method = RequestMethod.POST)
    public
    @ResponseBody
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
                LOGGER.debug("Setting OS process Id"+executionInfo.getOSProcessId());
            } catch (Exception e) {
                executionInfo.setOSProcessId(-1);
                LOGGER.error("Setting OS Process ID failed " + executionInfo.getOSProcessId());
            }
            restWrapper = new RestWrapper(executionInfo, RestWrapper.OK);
        } catch (Exception e) {
            LOGGER.error("Executing workflow failed " +e.getCause());
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
    public
    @ResponseBody
    RestWrapper insertClone(@PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;

        try {
//            Process processes = s.selectOne("call_procedures.CloneProcess", process);
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
