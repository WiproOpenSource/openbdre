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

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by AS294216 on 13-10-2015.
 */
@Controller
@RequestMapping("/datagenproperties")
public class DataGenAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(DataGenAPI.class);
    @Autowired
    private PropertiesDAO propertiesDAO;
    @Autowired
    private ProcessDAO processDAO;

    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapper list(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;
        com.wipro.ats.bdre.md.beans.table.Process parentProcess = new com.wipro.ats.bdre.md.beans.table.Process();
        Process childProcess = new Process();
        parentProcess = insertProcess(18, null, "Data gen Parent", "Data Generation Parent", 1, principal);
        childProcess = insertProcess(14, parentProcess.getProcessId(), "child of Data gen Parent", "child of Data Generation Parent", 0, principal);
        parentProcess = updateProcess(parentProcess,childProcess.getProcessId());

        StringBuffer tableSchema = new StringBuffer("");
        //to handle argument id's in sequence if rows are deleted and added in UI
        int fieldArgCounter = 1;
        int fieldTypeCounter = 0;
        int fieldCounter = 1;
        String[] dateContent;
        StringBuffer unifiedDate = new StringBuffer("");
        Date date = null, date2 = null;
        for (String string : map.keySet()) {
            LOGGER.debug("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            Integer splitIndex = string.lastIndexOf("_");
            String key = string.substring(splitIndex + 1, string.length());
            LOGGER.debug("key is " + key);
            if (string.startsWith("type_genArg")) {
//                String keySplit[] = key.split(".");
//                LOGGER.debug("keySplit length"+keySplit.length);
                fieldTypeCounter = Integer.parseInt(string.substring((string.lastIndexOf(".") + 1), string.length()));
                LOGGER.debug("genArg key Index" + fieldTypeCounter);
                if ((dateContent = map.get(string).split(",")).length == 3) {
                    DateFormat dF = new SimpleDateFormat(dateContent[2]);
                    try {
                        date = dF.parse(dateContent[0]);
                        date2 = dF.parse(dateContent[1]);
                    } catch (ParseException e) {
                        LOGGER.debug("error in Date entry");
                        e.printStackTrace();
                    }
                    unifiedDate.append(date.getTime() + "," + date2.getTime() + "," + dateContent[2]);
                    insertProperties(childProcess.getProcessId(), "data", "args." + fieldArgCounter, unifiedDate.toString(), "Generated Argument");
                    insertProperties(childProcess.getProcessId(), "data", "data-gen-id." + fieldArgCounter, map.get("type_generatedType." + fieldTypeCounter), "Generated Type");
                } else {
                    insertProperties(childProcess.getProcessId(), "data", "args." + fieldArgCounter, map.get(string), "Generated Argument");
                    insertProperties(childProcess.getProcessId(), "data", "data-gen-id." + fieldArgCounter, map.get("type_generatedType." + fieldTypeCounter), "Generated Type");
                }
                fieldArgCounter++;
            } else if (string.startsWith("type_fieldName")) {
                LOGGER.debug("type_fieldName" + tableSchema);
                tableSchema.append(map.get(string) + ":" + fieldCounter++ + ",");
            } else if (string.startsWith("other_numRows")) {
                LOGGER.debug("other_numRows" + map.get(string));
                insertProperties(childProcess.getProcessId(), "data", key, map.get(string), "number of rows");
            } else if (string.startsWith("other_numSplits")) {
                LOGGER.debug("other_numSplits" + map.get(string));
                insertProperties(childProcess.getProcessId(), "data", key, map.get(string), "number of splits");
            } else if (string.startsWith("other_tableName")) {
                LOGGER.debug("other_tableName" + map.get(string));
                insertProperties(childProcess.getProcessId(), "table", key, map.get(string), "Table Name");
            } else if (string.startsWith("other_separator")) {
                LOGGER.debug("other_separator" + map.get(string));
                insertProperties(childProcess.getProcessId(), "table", key, map.get(string), "Separator");
            }
        }
        //remove last : in tableSchema String
        tableSchema.deleteCharAt(tableSchema.length() - 1);
        insertProperties(childProcess.getProcessId(), "table", "tableSchema", tableSchema.toString(), "Table Schema");

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
        process.setProcessTemplateId(0);

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
        LOGGER.info("ppId is" + ppId);

        if (ppId != null) {
            com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            parentProcess.setProcessId(ppId);
            insertDaoProcess.setProcess(parentProcess);
            insertDaoProcess.setNextProcessId(ppId.toString());

        } else {
            insertDaoProcess.setNextProcessId("0");
        }
        insertDaoProcess.setDeleteFlag(false);
        insertDaoProcess.setDescription(desc);
        insertDaoProcess.setProcessName(name);
        insertDaoProcess.setCanRecover(true);
        insertDaoProcess.setEnqueuingProcessId(0);
        insertDaoProcess.setDeleteFlag(false);
        insertDaoProcess.setAddTs(DateConverter.stringToDate(process.getTableAddTS()));
        try {
            LOGGER.info("Process" + name + " is going to be inserted " + process.getProcessTypeId());
//            process = s.selectOne("call_procedures.InsertProcess", process);
            Integer processId = processDAO.insert(insertDaoProcess);
            process.setProcessId(processId);
            process.setTableAddTS(DateConverter.dateToString(insertDaoProcess.getAddTs()));
            process.setTableEditTS(DateConverter.dateToString(insertDaoProcess.getEditTs()));
            LOGGER.debug("Process" + name + " is going to be inserted " + process.getProcessTypeId());
//            process = s.selectOne("call_procedures.InsertProcess", process);
            LOGGER.info("Record with ID:" + process.getProcessId() + " inserted in Process by User:" + principal.getName() + process);
        } catch (Exception e) {
            LOGGER.debug("Error Occurred");
        }

        return process;
    }

    private Process updateProcess(Process process, Integer npid) {
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
            updateDaoProcess.setDeleteFlag(false);
            updateDaoProcess.setEditTs(DateConverter.stringToDate(process.getTableEditTS()));
//            Process processes = s.selectOne("call_procedures.UpdateProcess", process);
            updateDaoProcess = processDAO.update(updateDaoProcess);
            process.setTableAddTS(DateConverter.dateToString(updateDaoProcess.getAddTs()));
            process.setTableEditTS(DateConverter.dateToString(updateDaoProcess.getEditTs()));
        }

//            process = s.selectOne("call_procedures.UpdateProcess", process);
        catch (Exception e) {
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

    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("generalconfig")
                       @Valid GeneralConfig generalConfig, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Updating jtable for new advanced config");


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

            restWrapper = new RestWrapper(generalConfig, RestWrapper.OK);
            LOGGER.info("Record with configGroup:" + generalConfig.getConfigGroup() + " inserted in Jtable by User:" + principal.getName() + generalConfig);
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