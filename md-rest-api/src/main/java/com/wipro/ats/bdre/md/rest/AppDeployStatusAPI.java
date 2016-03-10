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
import com.wipro.ats.bdre.md.beans.table.AppDeploymentQueueStatus;
import com.wipro.ats.bdre.md.dao.AppDeploymentQueueStatusDAO;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by SU324335 on 3/8/2016.
 */
@Controller
@RequestMapping("/appdeploystatus")
public class AppDeployStatusAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(AppDeployStatusAPI.class);
    private static final String RECORDWITHID = "Record with ID:";
    @Autowired
    AppDeploymentQueueStatusDAO appDeploymentQueueStatusDAO;

    /**
     * This method calls proc GetDeployStatus and fetches a record from DeployStatus table corresponding
     * to deployStatusId passed.
     *
     * @param adqState
     * @return restWrapper It contains an instance of DeployStatus corresponding to deployStatusId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper get(
            @PathVariable("id") Short adqState, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus jpaAdqStatus = appDeploymentQueueStatusDAO.get(adqState.shortValue());
            AppDeploymentQueueStatus adqStatus = new AppDeploymentQueueStatus();
            if (jpaAdqStatus != null) {
                adqStatus.setAppDeploymentQueueStatusId( jpaAdqStatus.getAppDeployStatusId());
                adqStatus.setDescription(jpaAdqStatus.getDescription());
            }
            restWrapper = new RestWrapper(adqStatus, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + adqState + " selected from AppDeploymentQueueStatus by User:" + principal.getName());
        }catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;

    }

    /**
     * This method calls DeleteDeployStatus and fetches a record corresponding to the deployStatusId passed.
     *
     * @param adqState
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody public
    RestWrapper delete(
            @PathVariable("id") Integer adqState, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            appDeploymentQueueStatusDAO.delete(adqState.shortValue());
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + adqState + " deleted from AppDeploymentQueueStatus by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListStatusDeploy and fetches a list of DeployStatus records.
     *
     * @param
     * @return restWrapper It contains list of instances of DeployStatus.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Integer counter=appDeploymentQueueStatusDAO.totalRecordCount().intValue();
            List<com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus> jpaAdqStatus = appDeploymentQueueStatusDAO.list(startPage, pageSize);
            List<AppDeploymentQueueStatus> adqStatuses = new ArrayList<AppDeploymentQueueStatus>();

            for (com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus adqStatus : jpaAdqStatus) {
                AppDeploymentQueueStatus returnAdqStatus = new AppDeploymentQueueStatus();
                returnAdqStatus.setDescription(adqStatus.getDescription());
                returnAdqStatus.setCounter(counter);
                adqStatuses.add(returnAdqStatus);
            }
            restWrapper = new RestWrapper(adqStatuses, RestWrapper.OK);
            LOGGER.info("All records listed from DeployStatus by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls UpdateDeployStatus and updates the record passed. It also validates
     * the values of the record passed.
     *
     * @param adqStatus  Instance of DeployStatus.
     * @param bindingResult
     * @return restWrapper The updated instance of DeployStatus.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper update(@ModelAttribute("adqstatus")
                       @Valid AppDeploymentQueueStatus adqStatus, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into update for adq_status table");
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus jpaAdqStatus = new com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus();
            jpaAdqStatus.setDescription(adqStatus.getDescription());
            appDeploymentQueueStatusDAO.update(jpaAdqStatus);
            LOGGER.debug("Updating Adq Status Id" + jpaAdqStatus.getAppDeployStatusId());
            LOGGER.debug("Exiting from update for deploy_status table");
            restWrapper = new RestWrapper(adqStatus, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + adqStatus.getAppDeploymentQueueStatusId() + " updated in AppDeploymentQueueStatus by User:" + principal.getName() + adqStatus);
        } catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertDeployStatus and adds a new record in the database. It also validates the
     * values passed.
     *
     * @param adqStatus  Instance of DeployStatus.
     * @param bindingResult
     * @return restWrapper Instance of DeployStatus passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(@ModelAttribute("adqstatus")
                       @Valid AppDeploymentQueueStatus adqStatus, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into insert for adq_status table");
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }

        try {
            com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus jpaAdqStatus = new com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus();
            jpaAdqStatus.setDescription(adqStatus.getDescription());
            Short adqState = appDeploymentQueueStatusDAO.insert(jpaAdqStatus);
            adqStatus.setAppDeploymentQueueStatusId(adqState);
            LOGGER.debug("Exiting from insert for adq_status table");
            restWrapper = new RestWrapper(adqStatus, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + " inserted in AppDeploymentQueueStatus by User:" + principal.getName() + adqStatus);
        } catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * @return
     */

    @RequestMapping(value = {"/options", "/options/"}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapperOptions listOptions() {

        RestWrapperOptions restWrapperOptions = null;
        try {
            List<com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus> jpaAdqStatus = appDeploymentQueueStatusDAO.list(0, 0);
            List<AppDeploymentQueueStatus> adqStatuses = new ArrayList<AppDeploymentQueueStatus>();
            for (com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus adqStatus : jpaAdqStatus) {
                AppDeploymentQueueStatus returnAdqStatus = new AppDeploymentQueueStatus();
                returnAdqStatus.setAppDeploymentQueueStatusId(adqStatus.getAppDeployStatusId());
                returnAdqStatus.setDescription(adqStatus.getDescription());
                returnAdqStatus.setCounter(jpaAdqStatus.size());
                adqStatuses.add(returnAdqStatus);
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (AppDeploymentQueueStatus deploy : adqStatuses) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(deploy.getDescription(), deploy.getAppDeploymentQueueStatusId());
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapperOptions(e.getMessage(), RestWrapperOptions.ERROR);
        }
        return restWrapperOptions;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }

}
