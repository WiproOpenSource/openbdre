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
import com.wipro.ats.bdre.md.beans.table.PropertiesTemplate;
import com.wipro.ats.bdre.md.dao.PropertiesTemplateDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
@RequestMapping("/propertiestemplate")


public class PropertiesTemplateAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(PropertiesTemplateAPI.class);

    /**
     * This method calls proc DeleteProperties and deletes  records from Properties table
     * corresponding to processId passed.
     *
     * @param processTemplateId
     * @param model
     * @return nothing.
     */
    @Autowired
    PropertiesTemplateDAO propertiesTemplateDAO;

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(@PathVariable("id") Integer processTemplateId, Principal principal,
                       ModelMap model) {

        RestWrapper restWrapper = null;
        try {
            // s.delete("call_procedures.DeletePropertiesTemplate", propertiesTemplate);
            propertiesTemplateDAO.deletePropertiesTemplate(processTemplateId);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + " deleted from PropertiesTemplate by User:" + principal.getName());

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
            // List<PropertiesTemplate> getPropertiesTemplate = s.selectList("call_procedures.ListPropertyTemplate", propertiesTemplate);
            List<PropertiesTemplate> getPropertiesTemplate = propertiesTemplateDAO.listPropertyTemplate(startPage, pageSize);
            restWrapper = new RestWrapper(getPropertiesTemplate, RestWrapper.OK);
            LOGGER.info("All records listed from PropertiesTemplate by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProperty and deletes an entry corresponding to  particular processId and key passed.
     *
     * @param processTemplateId
     * @param key
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}/{k}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer processTemplateId,
            @PathVariable("k") String key,
            ModelMap model, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            PropertiesTemplate propertiesTemplate = new PropertiesTemplate();
            propertiesTemplate.setProcessTemplateId(processTemplateId);
            propertiesTemplate.setKey(key);
            // propertiesTemplate = s.selectOne("call_procedures.DeletePropertyTemplate", propertiesTemplate);
            propertiesTemplateDAO.deletePropertyTemplate(processTemplateId, key);
            restWrapper = new RestWrapper(propertiesTemplate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + "," + key + " deleted from PropertiesTemplate by User:" + principal.getName());

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
    RestWrapper list(@PathVariable("id") Integer processTemplateId, Principal principal) {
        RestWrapper restWrapper = null;
        try {

            PropertiesTemplate propertiesTemplate = new PropertiesTemplate();
            propertiesTemplate.setProcessTemplateId(processTemplateId);
            //    List<Properties> propertiesTemplateList = s.selectList("call_procedures.ListPropertiesOfProcessTemplate", propertiesTemplate);
            List<PropertiesTemplate> propertiesTemplateList = propertiesTemplateDAO.listPropertiesTemplateBean(processTemplateId);
            restWrapper = new RestWrapper(propertiesTemplateList, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processTemplateId + " selected from PropertiesTemplate by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateProperties and updates the values passed. It also validates the values passed.
     *
     * @param propertiesTemplate Instance of Properties.
     * @param bindingResult
     * @return restWrapper It contains updated instance of Properties.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("propertiesTemplate")
                       @Valid PropertiesTemplate propertiesTemplate, BindingResult bindingResult, Principal principal) {
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

            //    PropertiesTemplate propertiesTemplateUpdate = s.selectOne("call_procedures.UpdatePropertiesTemplate", propertiesTemplate);
            PropertiesTemplate propertiesTemplateUpdate = propertiesTemplateDAO.updateProcessTemplate(propertiesTemplate);
            restWrapper = new RestWrapper(propertiesTemplateUpdate, RestWrapper.OK);
            LOGGER.info("Record with ID:" + propertiesTemplateUpdate.getProcessTemplateId() + " updated in PropertiesTemplate by User:" + principal.getName() + propertiesTemplateUpdate);

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProperties and adds a record in properties table. It also validates the
     * values passed.
     *
     * @param propertiesTemplate Instance of properties.
     * @param bindingResult
     * @return restWrapper It contains instance of Properties passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("propertiesTemplate")
                       @Valid PropertiesTemplate propertiesTemplate, BindingResult bindingResult, Principal principal) {

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

            //  PropertiesTemplate propertyTemplateInsert = s.selectOne("call_procedures.InsertPropertiesTemplate", propertiesTemplate);
            PropertiesTemplate propertyTemplateInsert = propertiesTemplateDAO.insertProcessTemplate(propertiesTemplate);
            restWrapper = new RestWrapper(propertyTemplateInsert, RestWrapper.OK);
            LOGGER.info("Record with ID:" + propertyTemplateInsert.getProcessTemplateId() + " inserted in PropertiesTemplate by User:" + principal.getName() + propertyTemplateInsert);

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
