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
import com.wipro.ats.bdre.md.beans.MQImportInfo;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.MQImportSetupDAO;
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
 * Created by MI294210 on 22-05-2015.
 */
@Controller
@RequestMapping("/mqimportsetup")


public class MQImportSetupAPI extends MetadataAPIBase {
    @Autowired
    MQImportSetupDAO mqImportSetupDAO;
    private static final Logger LOGGER = Logger.getLogger(MQImportSetupAPI.class);

    /**
     * This method calls proc DeleteMQImportSetup and deletes a record from MQImportSetup
     * corresponding to processId passed.
     *
     * @param processId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(@PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;
        try {

            // s.delete("call_procedures.DeleteMQImportSetup",mqImportInfo);
            mqImportSetupDAO.delete(processId);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from MQImportSetup by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;
    }


    /**
     * This method calls proc ListMQImportSetup and lists all the MQImportSetup records.
     *
     * @param
     * @return restWrapper List of instances of MQImportSetup.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {

            MQImportInfo mqImportInfo = new MQImportInfo();
            LOGGER.debug("config group is " + mqImportInfo.getConfigGroup());
            mqImportInfo.setPage(startPage);
            mqImportInfo.setPageSize(pageSize);
            LOGGER.debug("Listing mq import properties on page  " + mqImportInfo.getPage());
            //     List<Properties> propertiesList = s.selectList("call_procedures.ListMQImportSetup", mqImportInfo);
            List<Properties> propertiesList = mqImportSetupDAO.list(startPage, pageSize);
            //      LOGGER.debug("config group is in propertylist " + propertiesList.get(0).getConfigGroup());

            LOGGER.debug("properties contain" + propertiesList.size() + "objects");
            MQImportInfo mqImportInfo1 = new MQImportInfo();
            List<MQImportInfo> mqImportInfos = new ArrayList<MQImportInfo>();
            int count = 0;
            int i = 0;
            for (Properties properties : propertiesList) {
                //Checking if each process should have 4 properties each
                if (count % 4 == 0) {
                    mqImportInfos.add(i, new MQImportInfo());
                }
                if (properties.getKey().equals(mqImportInfo1.getBrokerUrl())) {
                    mqImportInfos.get(i).setBrokerUrlValue(properties.getValue());
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getBrokerUrlValue());
                    count++;
                    LOGGER.debug("broker url count :" + count);

                } else if (properties.getKey().equals(mqImportInfo1.getQueueName())) {
                    mqImportInfos.get(i).setQueueNameValue(properties.getValue());
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getQueueNameValue());
                    count++;
                    LOGGER.debug("queue count :" + count);

                } else if (properties.getKey().equals(mqImportInfo1.getNumSpouts())) {
                    mqImportInfos.get(i).setNumSpoutsValue(Integer.parseInt(properties.getValue()));
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getNumSpoutsValue());
                    count++;
                    LOGGER.debug("spout count :" + count);

                } else if (properties.getKey().equals(mqImportInfo1.getNumBolts())) {
                    mqImportInfos.get(i).setNumBoltsValue(Integer.parseInt(properties.getValue()));
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getNumBoltsValue());
                    count++;
                    LOGGER.debug("bolt count :" + count);

                }

                if (count % 4 == 0) {
                    LOGGER.debug("The value of if statement is" + count);
                    LOGGER.debug("config group in properties is " + properties.getConfigGroup());
                    //adding common properties
                    mqImportInfos.get(i).setConfigGroup(properties.getConfigGroup());
                    mqImportInfos.get(i).setDescription(properties.getDescription());
                    mqImportInfos.get(i).setParentProcessId(properties.getParentProcessId());
                    mqImportInfos.get(i).setSubProcessId(properties.getSubProcessId());
                    mqImportInfos.get(i).setCounter(properties.getCounter());
                    LOGGER.debug("parentprocess id is" + mqImportInfos.get(i).getSubProcessId() + "," + mqImportInfos.get(i).getParentProcessId());
                    LOGGER.debug("queue id is" + mqImportInfos.get(i).getQueueNameValue() + "," + mqImportInfos.get(i).getBrokerUrlValue());

                    i++;
                }

            }
            restWrapper = new RestWrapper(mqImportInfos, RestWrapper.OK);
            LOGGER.info("All records listed from MQImportSetup by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertMQImportSetup and inserts the record.
     *
     * @param mqImportInfo
     * @param bindingResult
     * @return restWrapper Instance of MQImportSetup.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("mqimportsetup")
                       @Valid MQImportInfo mqImportInfo, BindingResult bindingResult, Principal principal) {
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

            LOGGER.debug("Listing MQ Import properties on page  " + mqImportInfo.getPage());
            //   List<Properties> propertiesList = s.selectList("call_procedures.InsertMQImportSetup", mqImportInfo);
            List<Properties> propertiesList = mqImportSetupDAO.insert(mqImportInfo);
            LOGGER.debug("properties contain" + propertiesList.size() + "objects");
            MQImportInfo mqImportInfo1 = new MQImportInfo();
            List<MQImportInfo> mqImportInfos = new ArrayList<MQImportInfo>();
            int count = 0;
            int i = 0;
            for (Properties properties : propertiesList) {
                //Checking if each process should have 4 properties each
                if (count % 4 == 0) {
                    mqImportInfos.add(i, new MQImportInfo());
                }

                if (properties.getKey().equals(mqImportInfo1.getBrokerUrl())) {
                    mqImportInfos.get(i).setBrokerUrlValue(properties.getValue());
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getBrokerUrlValue());
                    count++;
                    LOGGER.debug("broker url count :" + count);


                } else if (properties.getKey().equals(mqImportInfo1.getQueueName())) {
                    mqImportInfos.get(i).setQueueNameValue(properties.getValue());
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getQueueNameValue());
                    count++;
                    LOGGER.debug("rotation filesize count :" + count);
                } else if (properties.getKey().equals(mqImportInfo1.getNumSpouts())) {
                    mqImportInfos.get(i).setNumSpoutsValue(Integer.parseInt(properties.getValue()));
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getNumSpoutsValue());
                    count++;
                    LOGGER.debug("spout count :" + count);

                } else if (properties.getKey().equals(mqImportInfo1.getNumBolts())) {
                    mqImportInfos.get(i).setNumBoltsValue(Integer.parseInt(properties.getValue()));
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getNumBoltsValue());
                    count++;
                    LOGGER.debug("bolt count :" + count);

                }

                if (count % 4 == 0) {
                    LOGGER.debug("The value of if statement is" + count);
                    LOGGER.debug("properties has process id as ppid" + properties.getParentProcessId());
                    //adding common properties
                    mqImportInfos.get(i).setDescription(properties.getDescription());
                    mqImportInfos.get(i).setParentProcessId(properties.getParentProcessId());
                    mqImportInfos.get(i).setSubProcessId(properties.getSubProcessId());
                    mqImportInfos.get(i).setCounter(properties.getCounter());
                    LOGGER.debug("values of mq import Setup are" + mqImportInfos.get(i).getQueueNameValue() + mqImportInfos.get(i).getBrokerUrlValue() + mqImportInfos.get(i).getBrokerUrlValue() + mqImportInfos.get(i).getParentProcessId());
                    i++;
                }

            }
            restWrapper = new RestWrapper(mqImportInfos, RestWrapper.OK);
            LOGGER.info("Record with ID:" + mqImportInfo1.getParentProcessId() + " inserted in MQImportSetup by User:" + principal.getName() + mqImportInfo1);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls UpdateMQImportSetup and updates the values of the record passed.It also validates the
     * values passed.
     *
     * @param mqImportInfo
     * @param bindingResult
     * @return restWrapper Updated instance of MQImportSetup instance.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("mqimportsetup")
                       @Valid MQImportInfo mqImportInfo, BindingResult bindingResult, Principal principal) {
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

            LOGGER.debug("Listing mq import properties on page  " + mqImportInfo.getPage());
            //   List<Properties> propertiesList = s.selectList("call_procedures.UpdateMQImportSetup", mqImportInfo);
            List<Properties> propertiesList = mqImportSetupDAO.update(mqImportInfo);
            LOGGER.debug("properties contain" + propertiesList.size() + "objects");
            MQImportInfo mqImportInfo1 = new MQImportInfo();
            List<MQImportInfo> mqImportInfos = new ArrayList<MQImportInfo>();
            int count = 0;
            int i = 0;
            for (Properties properties : propertiesList) {
                //Checking if each process should have 4 properties each
                if (count % 4 == 0) {
                    mqImportInfos.add(i, new MQImportInfo());
                }

                if (properties.getKey().equals(mqImportInfo1.getBrokerUrl())) {
                    mqImportInfos.get(i).setBrokerUrlValue(properties.getValue());
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getBrokerUrlValue());
                    count++;
                    LOGGER.debug("broker url count :" + count);

                } else if (properties.getKey().equals(mqImportInfo1.getQueueName())) {
                    mqImportInfos.get(i).setQueueNameValue(properties.getValue());
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getQueueNameValue());
                    count++;
                    LOGGER.debug("queue count :" + count);

                } else if (properties.getKey().equals(mqImportInfo1.getNumSpouts())) {
                    mqImportInfos.get(i).setNumSpoutsValue(Integer.parseInt(properties.getValue()));
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getNumSpoutsValue());
                    count++;
                    LOGGER.debug("spout count :" + count);

                } else if (properties.getKey().equals(mqImportInfo1.getNumBolts())) {
                    mqImportInfos.get(i).setNumBoltsValue(Integer.parseInt(properties.getValue()));
                    LOGGER.debug("BN count :" + mqImportInfos.get(i).getNumBoltsValue());
                    count++;
                    LOGGER.debug("bolt count :" + count);

                }


                if (count % 4 == 0) {
                    LOGGER.debug("The value of if statement is" + count);
                    //adding common properties
                    mqImportInfos.get(i).setDescription(properties.getDescription());
                    mqImportInfos.get(i).setParentProcessId(properties.getParentProcessId());
                    mqImportInfos.get(i).setSubProcessId(properties.getSubProcessId());
                    mqImportInfos.get(i).setCounter(properties.getCounter());
                    i++;
                }

            }
            restWrapper = new RestWrapper(mqImportInfos, RestWrapper.OK);
            LOGGER.info("Record with ID:" + mqImportInfo1.getParentProcessId() + " updated in MQImportSetup by User:" + principal.getName() + mqImportInfo1);
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

