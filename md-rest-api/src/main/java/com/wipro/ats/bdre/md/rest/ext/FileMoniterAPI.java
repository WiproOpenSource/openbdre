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
import com.wipro.ats.bdre.md.beans.table.*;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
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
import java.util.Date;
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
        //making process
        Process parentProcess = buildJPAProcess(26, null, "Filemon", "File Monitoring", 2);
        Integer parentPid = processDAO.insert(parentProcess);
        parentProcess.setProcessId(parentPid);
        Process childProcess = buildJPAProcess(27, parentProcess, "child of " + "Filemon", "child of " + "File Monitoring", 0);
        childProcess.setNextProcessId(parentPid.toString());
        Integer childPid = processDAO.insert(childProcess);
        parentProcess.setNextProcessId(childPid.toString());

        childProcess.setProcessId(childPid);
        //inserting in properties table
        Properties jpaProperties = buildJPAProperties(childProcess.getProcessId(), "fileMon", "deleteCopiedSrc", fileMonitorInfo.getDeleteCopiedSource(), "Delete copied source");
        propertiesDAO.insert(jpaProperties);
        jpaProperties = buildJPAProperties(childProcess.getProcessId(), "fileMon", "filePattern", fileMonitorInfo.getFilePattern(), "pattern of file");
        propertiesDAO.insert(jpaProperties);
        jpaProperties = buildJPAProperties(childProcess.getProcessId(), "fileMon", "hdfsUploadDir", fileMonitorInfo.getHdfsUploadDir(), "hdfc upload dir");
        propertiesDAO.insert(jpaProperties);
        jpaProperties = buildJPAProperties(childProcess.getProcessId(), "fileMon", "monitoredDirName", fileMonitorInfo.getMonitoredDirName(), "file monitored dir");
        propertiesDAO.insert(jpaProperties);
        jpaProperties = buildJPAProperties(childProcess.getProcessId(), "fileMon", "sleepTime", Integer.toString(fileMonitorInfo.getSleepTime()), "sleeptime of thread");
        propertiesDAO.insert(jpaProperties);

        List<Process> processList = new ArrayList<Process>();
        //parentProcess.setCounter(2);
        //childProcess.setCounter(2);
        processList.add(parentProcess);
        processList.add(childProcess);
        restWrapper = new RestWrapper(processList, RestWrapper.OK);
        return restWrapper;
    }

    private com.wipro.ats.bdre.md.beans.table.Process jpa2TableProcess(Process jpaProcess) {
        com.wipro.ats.bdre.md.beans.table.Process tableProcess = new com.wipro.ats.bdre.md.beans.table.Process();
        tableProcess.setProcessId(jpaProcess.getProcessId());
        tableProcess.setProcessName(jpaProcess.getProcessName());
        tableProcess.setAddTS(jpaProcess.getAddTs());
        tableProcess.setEditTS(jpaProcess.getEditTs());
        tableProcess.setCanRecover(jpaProcess.getCanRecover());
        tableProcess.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
        tableProcess.setBatchPattern(jpaProcess.getBatchCutPattern());
        tableProcess.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
        tableProcess.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
        tableProcess.setDeleteFlag(jpaProcess.getDeleteFlag());
        tableProcess.setDescription(jpaProcess.getDescription());
        tableProcess.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
        if(jpaProcess.getProcess()!=null)
            tableProcess.setParentProcessId(jpaProcess.getProcess().getProcessId());
        return tableProcess;
    }
    private com.wipro.ats.bdre.md.beans.table.Properties jpa2TableProperties(Properties jpaProperties){
        com.wipro.ats.bdre.md.beans.table.Properties tableProperties=new com.wipro.ats.bdre.md.beans.table.Properties();
        tableProperties.setProcessId(jpaProperties.getProcess().getProcessId());
        tableProperties.setDescription(jpaProperties.getDescription());
        tableProperties.setConfigGroup(jpaProperties.getConfigGroup());
        tableProperties.setKey(jpaProperties.getId().getPropKey());
        tableProperties.setValue(jpaProperties.getPropValue());
        return tableProperties;
    }
    private Process buildJPAProcess(Integer ptId, Process parentProcess, String name, String desc, Integer wfId) {
        Process daoProcess = new Process();
        ProcessType daoProcessType = new ProcessType();
        daoProcessType.setProcessTypeId(ptId);
        daoProcess.setProcessType(daoProcessType);
        if (wfId != null) {
            com.wipro.ats.bdre.md.dao.jpa.WorkflowType daoWorkflowType = new com.wipro.ats.bdre.md.dao.jpa.WorkflowType();
            daoWorkflowType.setWorkflowId(wfId);
            daoProcess.setWorkflowType(daoWorkflowType);
        }
        com.wipro.ats.bdre.md.dao.jpa.BusDomain daoBusDomain = new com.wipro.ats.bdre.md.dao.jpa.BusDomain();
        daoBusDomain.setBusDomainId(1);
        daoProcess.setBusDomain(daoBusDomain);
        ProcessTemplate daoProcessTemplate = new ProcessTemplate();
        daoProcessTemplate.setProcessTemplateId(0);
        daoProcess.setProcessTemplate(daoProcessTemplate);

        if (parentProcess != null) {
            daoProcess.setProcess(parentProcess);
            daoProcess.setNextProcessId("0");
        } else {
            daoProcess.setNextProcessId("0");
        }
        daoProcess.setDescription(desc);
        daoProcess.setProcessName(name);
        daoProcess.setCanRecover(true);
        daoProcess.setDeleteFlag(false);
        daoProcess.setEnqueuingProcessId(0);
        daoProcess.setAddTs(new Date());
        daoProcess.setEditTs(new Date());

        return daoProcess;
    }


    private Properties buildJPAProperties(Integer pid, String configGrp, String key, String value, String desc) {
        Properties properties = new Properties();
        try {
            Process process = new Process();
            process.setProcessId(pid);
            properties.setProcess(process);
            properties.setConfigGroup(configGrp);
            properties.setPropValue(value);
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setProcessId(pid);
            propertiesId.setPropKey(key);
            properties.setId(propertiesId);
            properties.setDescription(desc);

        } catch (Exception e) {
            LOGGER.error(e);
        }
        return properties;
    }
}
