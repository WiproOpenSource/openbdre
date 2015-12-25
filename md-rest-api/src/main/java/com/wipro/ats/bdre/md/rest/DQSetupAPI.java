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
import com.wipro.ats.bdre.md.beans.DQSetupInfo;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.DataQualityDAO;
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
@RequestMapping("/dqsetup")


public class DQSetupAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(DQSetupAPI.class);

    /**
     * This method calls prc DeleteDQSetup and deletes a record corresponding to the
     * processId passed.
     *
     * @param processId
     * @return nothing.
     */
    @Autowired
    DataQualityDAO dataQualityDAO;

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(@PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            DQSetupInfo dqSetupInfo = new DQSetupInfo();
            dqSetupInfo.setSubProcessId(processId);
            //s.delete("call_procedures.DeleteDQSetup",dqSetupInfo);
            dataQualityDAO.deleteDQSetup(processId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from DQSetup by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }


        return restWrapper;
    }

    /**
     * This method calls proc ListDQSetup and lists all the DQSetup records.
     *
     * @param
     * @return restWrapper List of instances of DQSetup.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            DQSetupInfo dqSetupInfo = new DQSetupInfo();
            dqSetupInfo.setPage(startPage);
            dqSetupInfo.setPageSize(pageSize);
            LOGGER.debug("Listing DQ properties on page  " + dqSetupInfo.getPage());
            // List<Properties> propertiesList = s.selectList("call_procedures.ListDQSetup", dqSetupInfo);
            List<Properties> propertiesList = dataQualityDAO.listDQSetup(startPage, pageSize);
            LOGGER.debug("properties contain" + propertiesList.size() + "objects");
            DQSetupInfo dqSetup = new DQSetupInfo();
            List<DQSetupInfo> dqSetups = new ArrayList<DQSetupInfo>();
            int count = 0;
            int i = 0;
            for (Properties properties : propertiesList) {
                //Checking if each process should have 5 properties each
                if (count % 5 == 0) {
                    dqSetups.add(i, new DQSetupInfo());
                }
                if (properties.getKey().equals(dqSetup.getRulesUserName())) {
                    dqSetups.get(i).setRulesUserNameValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getRulesUserNameValue());
                    count++;
                    LOGGER.debug("UN count :" + count);
                } else if (properties.getKey().equals(dqSetup.getRulesPassword())) {
                    dqSetups.get(i).setRulesPasswordValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getRulesPasswordValue());
                    count++;
                    LOGGER.debug("pass count :" + count);

                } else if (properties.getKey().equals(dqSetup.getRulesPackage())) {
                    dqSetups.get(i).setRulesPackageValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getRulesPackageValue());
                    count++;
                    LOGGER.debug("package count :" + count);

                } else if (properties.getKey().equals(dqSetup.getFileDelimiterRegex())) {
                    dqSetups.get(i).setFileDelimiterRegexValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getFileDelimiterRegexValue());
                    count++;
                    LOGGER.debug("delimiter count :" + count);

                } else if (properties.getKey().equals(dqSetup.getMinPassThresholdPercent())) {
                    dqSetups.get(i).setMinPassThresholdPercentValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getMinPassThresholdPercentValue());
                    count++;
                    LOGGER.debug("threshold count :" + count);

                }
                //ensuring each process should have 5 properties each
                if (count % 5 == 0) {
                    LOGGER.debug("The value of if statement is" + count);
                    //adding common properties
                    dqSetups.get(i).setDescription(properties.getDescription());
                    dqSetups.get(i).setParentProcessId(properties.getParentProcessId());
                    dqSetups.get(i).setSubProcessId(properties.getSubProcessId());
                    dqSetups.get(i).setCounter(properties.getCounter());
                    LOGGER.debug("parentprocess id is" + dqSetups.get(i).getSubProcessId() + "," + dqSetups.get(i).getParentProcessId());
                    //  LOGGER.debug("values of dqSetup are" + dqSetups.get(i).getRulesUserNameValue() + dqSetups.get(i).getRulesPasswordValue() + dqSetups.get(i).getRulesPackageValue() + dqSetups.get(i).getProcessId());
                    i++;
                }

            }
            //LOGGER.debug("Listed DQ properties on  " + dqSetups.get(0).getCounter());
            restWrapper = new RestWrapper(dqSetups, RestWrapper.OK);
            LOGGER.info("All records listed from DQSetup by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertDQSetup and inserts the record.
     *
     * @param dqSetupInfo
     * @param bindingResult
     * @return restWrapper Instance of DQSetup.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("dqsetup")
                       @Valid DQSetupInfo dqSetupInfo, BindingResult bindingResult, Principal principal) {
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
            LOGGER.debug("Listing DQ properties on page  " + dqSetupInfo.getPage());
            //List<Properties> propertiesList = s.selectList("call_procedures.InsertDQSetup", dqSetupInfo);
            List<Properties> propertiesList = dataQualityDAO.insertDQSetup(dqSetupInfo);
            LOGGER.debug("properties contain" + propertiesList.size() + "objects");
            DQSetupInfo dqSetup = new DQSetupInfo();
            List<DQSetupInfo> dqSetups = new ArrayList<DQSetupInfo>();
            int count = 0;
            int i = 0;
            for (Properties properties : propertiesList) {
                //Checking if each process should have 5 properties each
                if (count % 5 == 0) {
                    dqSetups.add(i, new DQSetupInfo());
                }
                if (properties.getKey().equals(dqSetup.getRulesUserName())) {
                    dqSetups.get(i).setRulesUserNameValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getRulesUserNameValue());
                    count++;
                    LOGGER.debug("UN count :" + count);
                } else if (properties.getKey().equals(dqSetup.getRulesPassword())) {
                    dqSetups.get(i).setRulesPasswordValue(properties.getValue());
                    LOGGER.debug("pass count :" + dqSetups.get(i).getRulesPasswordValue());
                    count++;
                    LOGGER.debug("pass count :" + count);

                } else if (properties.getKey().equals(dqSetup.getRulesPackage())) {
                    dqSetups.get(i).setRulesPackageValue(properties.getValue());
                    LOGGER.debug("package count :" + dqSetups.get(i).getRulesPackageValue());
                    count++;
                    LOGGER.debug("package count :" + count);

                } else if (properties.getKey().equals(dqSetup.getFileDelimiterRegex())) {
                    dqSetups.get(i).setFileDelimiterRegexValue(properties.getValue());
                    LOGGER.debug("delimiter count :" + dqSetups.get(i).getFileDelimiterRegexValue());
                    count++;
                    LOGGER.debug("delimiter count :" + count);

                } else if (properties.getKey().equals(dqSetup.getMinPassThresholdPercent())) {
                    dqSetups.get(i).setMinPassThresholdPercentValue(properties.getValue());
                    LOGGER.debug("threshold count :" + dqSetups.get(i).getMinPassThresholdPercentValue());
                    count++;
                    LOGGER.debug("threshold count :" + count);

                }
                if (count % 5 == 0) {
                    LOGGER.debug("The value of if statement is" + count);
                    LOGGER.debug("properties has process id as ppid" + properties.getParentProcessId());
                    //adding common properties
                    dqSetups.get(i).setDescription(properties.getDescription());
                    dqSetups.get(i).setParentProcessId(properties.getParentProcessId());
                    dqSetups.get(i).setSubProcessId(properties.getSubProcessId());
                    dqSetups.get(i).setCounter(properties.getCounter());
                    LOGGER.debug("values of dqSetup are" + dqSetups.get(i).getRulesUserNameValue() + dqSetups.get(i).getRulesPasswordValue() + dqSetups.get(i).getRulesPackageValue() + dqSetups.get(i).getParentProcessId());
                    i++;
                }

            }
            restWrapper = new RestWrapper(dqSetups, RestWrapper.OK);
            LOGGER.info("Record with ID:" + dqSetupInfo.getParentProcessId() + " inserted in DQSetup by User:" + principal.getName() + dqSetupInfo);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls UpdateDQSetup and updates the values of the record passed.It also validates the
     * values passed.
     *
     * @param dqSetupInfo
     * @param bindingResult
     * @return restWrapper Updated instance of DQSetup instance.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("dqsetup")
                       @Valid DQSetupInfo dqSetupInfo, BindingResult bindingResult, Principal principal) {
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
            LOGGER.debug("Listing DQ properties on page  " + dqSetupInfo.getPage());
            // List<Properties> propertiesList = s.selectList("call_procedures.UpdateDQSetup", dqSetupInfo);
            List<Properties> propertiesList = dataQualityDAO.updateDQSetup(dqSetupInfo);
            LOGGER.debug("properties contain" + propertiesList.size() + "objects");
            DQSetupInfo dqSetup = new DQSetupInfo();
            List<DQSetupInfo> dqSetups = new ArrayList<DQSetupInfo>();
            int count = 0;
            int i = 0;
            for (Properties properties : propertiesList) {
                //Checking if each process should have 5 properties each
                if (count % 5 == 0) {
                    dqSetups.add(i, new DQSetupInfo());
                }
                if (properties.getKey().equals(dqSetup.getRulesUserName())) {
                    dqSetups.get(i).setRulesUserNameValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getRulesUserNameValue());
                    count++;
                    LOGGER.debug("UN count :" + count);
                } else if (properties.getKey().equals(dqSetup.getRulesPassword())) {
                    dqSetups.get(i).setRulesPasswordValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getRulesPasswordValue());
                    count++;
                    LOGGER.debug("pass count :" + count);

                } else if (properties.getKey().equals(dqSetup.getRulesPackage())) {
                    dqSetups.get(i).setRulesPackageValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getRulesPackageValue());
                    count++;
                    LOGGER.debug("package count :" + count);

                } else if (properties.getKey().equals(dqSetup.getFileDelimiterRegex())) {
                    dqSetups.get(i).setFileDelimiterRegexValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getFileDelimiterRegexValue());
                    count++;
                    LOGGER.debug("delimiter count :" + count);

                } else if (properties.getKey().equals(dqSetup.getMinPassThresholdPercent())) {
                    dqSetups.get(i).setMinPassThresholdPercentValue(properties.getValue());
                    LOGGER.debug("UN count :" + dqSetups.get(i).getMinPassThresholdPercentValue());
                    count++;
                    LOGGER.debug("threshold count :" + count);

                }
                if (count % 5 == 0) {
                    LOGGER.debug("The value of if statement is" + count);
                    //adding common properties
                    dqSetups.get(i).setDescription(properties.getDescription());
                    dqSetups.get(i).setParentProcessId(properties.getParentProcessId());
                    dqSetups.get(i).setSubProcessId(properties.getSubProcessId());
                    dqSetups.get(i).setCounter(properties.getCounter());
                    //      LOGGER.debug("values of dqSetup are" + dqSetups.get(i).getRulesUserNameValue() + dqSetups.get(i).getRulesPasswordValue() + dqSetups.get(i).getRulesPackageValue() + dqSetups.get(i).getProcessId());
                    i++;
                }

            }
            restWrapper = new RestWrapper(dqSetups, RestWrapper.OK);
            LOGGER.info("Record with ID:" + dqSetupInfo.getParentProcessId() + " updated in DQSetup by User:" + principal.getName() + dqSetupInfo);
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
