/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
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
@RequestMapping("/properties")


public class PropertiesAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(PropertiesAPI.class);
    @Autowired
    private PropertiesDAO propertiesDAO;

    /**
     * This method calls proc DeleteProperties and deletes  records from Properties table
     * corresponding to processId passed.
     *
     * @param processId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(@PathVariable("id") Integer processId,
                       ModelMap model, Principal principal) {

        RestWrapper restWrapper = null;
        try {


            com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
            process.setProcessId(processId);
            propertiesDAO.deleteByProcessId(process);
            //s.delete("call_procedures.DeleteProperties", properties);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from Properties by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListProperty and fetches a list of instances of Properties.
     *
     * @param
     * @return restWrapper It contains a list of instances of Properties.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=propertiesDAO.totalRecordCount();
            List<Properties> getProperties = new ArrayList<Properties>();
            //List<Properties> getProperties = s.selectList("call_procedures.ListProperty", properties);

            for (Integer processId : propertiesDAO.list(startPage, pageSize)) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(processId);
                returnProperties.setCounter(counter);
                getProperties.add(returnProperties);
            }


            restWrapper = new RestWrapper(getProperties, RestWrapper.OK);
            LOGGER.info("All records listed from Properties by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProperty and deletes an entry corresponding to  particular processId and key passed.
     *
     * @param processId
     * @param key
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}/{k}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer processId,
            @PathVariable("k") String key,
            ModelMap model, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            Properties properties = new Properties();

            com.wipro.ats.bdre.md.dao.jpa.PropertiesId propertiesId = new com.wipro.ats.bdre.md.dao.jpa.PropertiesId();
            propertiesId.setProcessId(processId);
            propertiesId.setPropKey(key);
            propertiesDAO.delete(propertiesId);

            // properties = s.selectOne("call_procedures.DeleteProperty", properties);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + "," + key + " deleted from Properties by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListProperties and fetches a list of records from properties table corresponding to
     * processId passed.
     *
     * @param
     * @return restWrapper It contains list of instances of properties corresponding to processId passed.
     */
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@PathVariable("id") Integer processId, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            List<Properties> propertiesList = new ArrayList<Properties>();
            Process process = new Process();
            process.setProcessId(processId);

            List<com.wipro.ats.bdre.md.dao.jpa.Properties> propertiesList1=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Properties>();
                    propertiesList1=propertiesDAO.getByProcessId(process);
            Integer counter=propertiesList1.size();
            // List<Properties> propertiesList = s.selectList("call_procedures.ListPropertiesOfProcess", properties);
            for (com.wipro.ats.bdre.md.dao.jpa.Properties properties : propertiesList1) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(properties.getProcess().getProcessId());
                returnProperties.setConfigGroup(properties.getConfigGroup());
                returnProperties.setKey(properties.getId().getPropKey());
                returnProperties.setValue(properties.getPropValue());
                returnProperties.setDescription(properties.getDescription());
                returnProperties.setCounter(counter);
                propertiesList.add(returnProperties);
            }

            restWrapper = new RestWrapper(propertiesList, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + "selected from Properties by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method calls proc ListProperties and fetches a list of records from properties table corresponding to
     * processId passed.
     *
     * @param
     * @return restWrapper It contains list of instances of properties corresponding to processId passed.
     */
    @RequestMapping(value = {"/{id}/{cg}", "/{id}/{cg}/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper listConfigGroup(@PathVariable("id") Integer processId,
                                @PathVariable("cg") String configGroup,
                                Principal principal) {

        RestWrapper restWrapper = null;
        try {
            List<Properties> propertiesList = new ArrayList<Properties>();
            //List<Properties> propertiesList = s.selectList("call_procedures.ListConfigGroup", properties);
            List<com.wipro.ats.bdre.md.dao.jpa.Properties>jpaPropertiesList=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Properties>();
                    jpaPropertiesList=propertiesDAO.getPropertiesForConfig(processId, configGroup);
            Integer counter=jpaPropertiesList.size();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties properties : jpaPropertiesList) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(properties.getProcess().getProcessId());
                returnProperties.setConfigGroup(properties.getConfigGroup());
                returnProperties.setKey(properties.getId().getPropKey());
                returnProperties.setValue(properties.getPropValue());
                returnProperties.setDescription(properties.getDescription());
                returnProperties.setCounter(counter);
                propertiesList.add(returnProperties);
            }
            restWrapper = new RestWrapper(propertiesList, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + "and config group" + configGroup + "selected from Properties by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateProperties and updates the values passed. It also validates the values passed.
     *
     * @param properties    Instance of Properties.
     * @param bindingResult
     * @return restWrapper It contains updated instance of Properties.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("properties")
                       @Valid Properties properties, BindingResult bindingResult, Principal principal) {

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

//            Properties propertiesUpdate = s.selectOne("call_procedures.UpdateProperties", properties);
            com.wipro.ats.bdre.md.dao.jpa.Properties updateProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setPropKey(properties.getKey());
            propertiesId.setProcessId(properties.getProcessId());
            updateProperties.setId(propertiesId);
            Process process = new Process();
            process.setProcessId(properties.getProcessId());
            updateProperties.setProcess(process);
            updateProperties.setConfigGroup(properties.getConfigGroup());
            updateProperties.setPropValue(properties.getValue());
            updateProperties.setDescription(properties.getDescription());
            propertiesDAO.update(updateProperties);
            restWrapper = new RestWrapper(properties, RestWrapper.OK);
            LOGGER.info("Record with ID:" + properties.getProcessId() + " updated in Properties by User:" + principal.getName() + properties);

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProperties and adds a record in properties table. It also validates the
     * values passed.
     *
     * @param properties    Instance of properties.
     * @param bindingResult
     * @return restWrapper It contains instance of Properties passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("properties")
                       @Valid Properties properties, BindingResult bindingResult, Principal principal) {

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
//              Properties propertyInsert = s.selectOne("call_procedures.InsertProperties", properties);
            com.wipro.ats.bdre.md.dao.jpa.Properties insertProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setPropKey(properties.getKey());
            propertiesId.setProcessId(properties.getProcessId());
            insertProperties.setId(propertiesId);
            Process process = new Process();
            process.setProcessId(properties.getProcessId());
            insertProperties.setProcess(process);
            insertProperties.setConfigGroup(properties.getConfigGroup());
            insertProperties.setPropValue(properties.getValue());
            insertProperties.setDescription(properties.getDescription());
            propertiesDAO.insert(insertProperties);
            restWrapper = new RestWrapper(properties, RestWrapper.OK);
            LOGGER.info("Record with ID:" + properties.getProcessId() + " inserted in Properties by User:" + principal.getName() + properties);

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProperties and deletes  records from Properties table
     * corresponding to processId passed.
     *
     * @param parentProcessId
     * @return
     */
    @RequestMapping(value = "/all/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper getAll(@PathVariable("id") Integer parentProcessId, Principal principal) {

        RestWrapper restWrapper = null;
        try {

//            Properties properties = new Properties();
//            properties.setProcessId(parentProcessId);
//            List<Properties> allProperties = s.selectList("call_procedures.GetPropertiesOfProcess", properties);

            List<Properties> propertiesList = new ArrayList<Properties>();
            Process process = new Process();
            process.setProcessId(parentProcessId);
            List<com.wipro.ats.bdre.md.dao.jpa.Properties>jpaPropertiesList=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Properties>();
            jpaPropertiesList=propertiesDAO.getByProcessId(process);
            Integer counter=jpaPropertiesList.size();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties properties :jpaPropertiesList ) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(properties.getProcess().getProcessId());
                returnProperties.setConfigGroup(properties.getConfigGroup());
                returnProperties.setKey(properties.getId().getPropKey());
                returnProperties.setValue(properties.getPropValue());
                returnProperties.setDescription(properties.getDescription());
                returnProperties.setCounter(counter);
                propertiesList.add(returnProperties);
            }

            restWrapper = new RestWrapper(propertiesList, RestWrapper.OK);
            LOGGER.debug("Records fetched:" + propertiesList);
            LOGGER.info("All records with parent process ID:" + parentProcessId + " selected from Properties by User:" + principal.getName());

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
