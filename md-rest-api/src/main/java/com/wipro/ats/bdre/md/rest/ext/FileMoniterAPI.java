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

package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.beans.FileMonitorInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PR324290 on 12/22/2015.
 */
@Controller
@RequestMapping("/filemonitor")
@Scope("session")
public class FileMoniterAPI {
    private static final Logger LOGGER = Logger.getLogger(FileMoniterAPI.class);


    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    private PropertiesDAO propertiesDAO;


    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapper createFileMonitorProperties(@ModelAttribute("fileMonitorInfo")
                                     @Valid FileMonitorInfo fileMonitorInfo, BindingResult bindingResult, Principal principal) {

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


        com.wipro.ats.bdre.md.beans.table.Process parentProcess = new com.wipro.ats.bdre.md.beans.table.Process();
        com.wipro.ats.bdre.md.beans.table.Process childProcess = new Process();
        //making process
        parentProcess = insertProcess(26, null, "FIlemon", "File Monitoring", 2, principal);
        childProcess = insertProcess(27, parentProcess.getProcessId(), "child of " + "FIlemon", "child of " + "File Monitoring", 0, principal);
        parentProcess = updateProcess(parentProcess);
        //inserting in properties table
        insertProperties(childProcess.getProcessId(), "fileMon", "deleteCopiedSrc", fileMonitorInfo.getDeleteCopiedSource(), "Delete copied source");
        insertProperties(childProcess.getProcessId(), "fileMon", "filePattern", fileMonitorInfo.getFilePattern(), "pattern of file");
        insertProperties(childProcess.getProcessId(), "fileMon", "hdfsUploadDir",fileMonitorInfo.getHdfsUploadDir(), "hdfc upload dir");
        insertProperties(childProcess.getProcessId(), "fileMon", "monitoredDirName", fileMonitorInfo.getMonitoredDirName(), "file monitored dir");
        insertProperties(childProcess.getProcessId(), "fileMon", "sleepTime",Integer.toString(fileMonitorInfo.getSleepTime()), "sleeptime of thread");

        List<Process> processList = new ArrayList<Process>();
        parentProcess.setCounter(2);
        childProcess.setCounter(2);
        processList.add(parentProcess);
        processList.add(childProcess);
        restWrapper = new RestWrapper(processList, RestWrapper.OK);
        return restWrapper;
    }



    private Process insertProcess(Integer ptId, Integer ppId, String name, String desc, Integer wfId, Principal principal) {
        Process process = new Process();
        process.setBusDomainId(1);
        process.setProcessTypeId(ptId);
        process.setDescription(desc);
        process.setParentProcessId(ppId);
        if (ppId != null) {
            process.setNextProcessIds(ppId.toString());
        } else {
            process.setNextProcessIds("0");
        }

        process.setProcessName(name);
        process.setWorkflowId(wfId);
        process.setEnqProcessId(0);
        process.setAddTS(DateConverter.stringToDate(process.getTableAddTS()));
        process.setCanRecover(true);
        process.setDeleteFlag(false);
        com.wipro.ats.bdre.md.dao.jpa.Process insertDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
        daoProcessType.setProcessTypeId(ptId);
        insertDaoProcess.setProcessType(daoProcessType);
        if (wfId != null) {
            com.wipro.ats.bdre.md.dao.jpa.WorkflowType daoWorkflowType = new com.wipro.ats.bdre.md.dao.jpa.WorkflowType();
            daoWorkflowType.setWorkflowId(wfId);
            insertDaoProcess.setWorkflowType(daoWorkflowType);
        }
        com.wipro.ats.bdre.md.dao.jpa.BusDomain daoBusDomain = new com.wipro.ats.bdre.md.dao.jpa.BusDomain();
        daoBusDomain.setBusDomainId(1);
        insertDaoProcess.setBusDomain(daoBusDomain);
        com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate daoProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
        daoProcessTemplate.setProcessTemplateId(0);
        insertDaoProcess.setProcessTemplate(daoProcessTemplate);

        if (ppId != null) {
            com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            parentProcess.setProcessId(ppId);
            insertDaoProcess.setProcess(parentProcess);
            insertDaoProcess.setNextProcessId("0");
        } else {
            insertDaoProcess.setNextProcessId("0");
        }
        insertDaoProcess.setDescription(desc);
        insertDaoProcess.setProcessName(name);
        insertDaoProcess.setCanRecover(true);
        insertDaoProcess.setDeleteFlag(false);
        insertDaoProcess.setEnqueuingProcessId(0);
        insertDaoProcess.setAddTs(DateConverter.stringToDate(process.getTableAddTS()));
        try {
            LOGGER.debug("Process" + name + " is going to be inserted " + process.getProcessTypeId());
//            process = s.selectOne("call_procedures.InsertProcess", process);
            Integer processId = processDAO.insert(insertDaoProcess);
            process.setProcessId(processId);
            process.setTableAddTS(DateConverter.dateToString(insertDaoProcess.getAddTs()));
            process.setTableEditTS(DateConverter.dateToString(insertDaoProcess.getEditTs()));
            LOGGER.info("Record with ID:" + process.getProcessId() + " inserted in Process by User:" + principal.getName() + process);
        } catch (Exception e) {
            LOGGER.debug("Error Occurred");
        }
        return process;
    }


    private Process updateProcess(Process process) {
        Integer npid = process.getProcessId() + 1;
        process.setNextProcessIds(npid.toString());
        try {
            com.wipro.ats.bdre.md.dao.jpa.Process updateDaoProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            updateDaoProcess.setProcessId(process.getProcessId());
            com.wipro.ats.bdre.md.dao.jpa.ProcessType daoProcessType = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
            daoProcessType.setProcessTypeId(process.getProcessTypeId());
            updateDaoProcess.setProcessType(daoProcessType);
            if (process.getWorkflowId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.WorkflowType daoWorkflowType = new com.wipro.ats.bdre.md.dao.jpa.WorkflowType();
                daoWorkflowType.setWorkflowId(process.getWorkflowId());
                updateDaoProcess.setWorkflowType(daoWorkflowType);
            }
            com.wipro.ats.bdre.md.dao.jpa.BusDomain daoBusDomain = new com.wipro.ats.bdre.md.dao.jpa.BusDomain();
            daoBusDomain.setBusDomainId(process.getBusDomainId());
            updateDaoProcess.setBusDomain(daoBusDomain);
            if (process.getProcessTemplateId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate daoProcessTemplate = new com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate();
                daoProcessTemplate.setProcessTemplateId(process.getProcessTemplateId());
                updateDaoProcess.setProcessTemplate(daoProcessTemplate);
            }
            if (process.getParentProcessId() != null) {
                com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
                parentProcess.setProcessId(process.getParentProcessId());
                updateDaoProcess.setProcess(parentProcess);
            }
            updateDaoProcess.setDescription(process.getDescription());
            updateDaoProcess.setAddTs(DateConverter.stringToDate(process.getTableAddTS()));
            updateDaoProcess.setProcessName(process.getProcessName());
            updateDaoProcess.setCanRecover(process.getCanRecover());
            updateDaoProcess.setEnqueuingProcessId(process.getEnqProcessId());
            if (process.getBatchPattern() != null) {
                updateDaoProcess.setBatchCutPattern(process.getBatchPattern());
            }
            updateDaoProcess.setNextProcessId(process.getNextProcessIds());
            if (process.getDeleteFlag() != null) {
                updateDaoProcess.setDeleteFlag(process.getDeleteFlag());
            }
            updateDaoProcess.setEditTs(DateConverter.stringToDate(process.getTableEditTS()));
//            Process processes = s.selectOne("call_procedures.UpdateProcess", process);
            updateDaoProcess = processDAO.update(updateDaoProcess);
            process.setTableAddTS(DateConverter.dateToString(updateDaoProcess.getAddTs()));
            process.setTableEditTS(DateConverter.dateToString(updateDaoProcess.getEditTs()));

//        process = s.selectOne("call_procedures.UpdateProcess", process);
        } catch (Exception e) {
            LOGGER.debug("Error Occurred");
        }
        return process;
    }



    private void insertProperties(Integer pid, String configGrp, String key, String value, String desc) {

        try {
            Properties properties = new Properties();
            properties.setProcessId(pid);
            properties.setConfigGroup(configGrp);
            properties.setKey(key);
            properties.setValue(value);
            properties.setDescription(desc);
//            s.selectOne("call_procedures.InsertProperties", properties);

            com.wipro.ats.bdre.md.dao.jpa.Properties insertProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setPropKey(properties.getKey());
            propertiesId.setProcessId(properties.getProcessId());
            insertProperties.setId(propertiesId);
            com.wipro.ats.bdre.md.dao.jpa.Process process = new com.wipro.ats.bdre.md.dao.jpa.Process();
            process.setProcessId(properties.getProcessId());
            insertProperties.setProcess(process);
            insertProperties.setConfigGroup(properties.getConfigGroup());
            insertProperties.setPropValue(properties.getValue());
            insertProperties.setDescription(properties.getDescription());
            propertiesDAO.insert(insertProperties);
        } catch (Exception e) {
            LOGGER.debug("Error Occurred");
        }

    }




}
