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
import com.wipro.ats.bdre.md.beans.table.PluginDependency;
import com.wipro.ats.bdre.md.dao.InstalledPluginsDAO;
import com.wipro.ats.bdre.md.dao.PluginDependencyDAO;
import com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins;
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
 * Created by PR324290 on 3/8/2016.
 */
@Controller
@RequestMapping("/plugindependency")
public class PluginDependencyAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(PluginDependencyAPI.class);
    private static final String RECORDWITHID = "Record with ID:";
    @Autowired
    PluginDependencyDAO pluginDependencyDAO;
    @Autowired
    InstalledPluginsDAO installedPluginsDAO;

    /**
     * This method calls proc GetDeployStatus and fetches a record from DeployStatus table corresponding
     * to deployStatusId passed.
     *
     * @param
     * @return restWrapper It contains an instance of pluginDependency corresponding to deployStatusId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper get(
            @PathVariable("id") int dependencyId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.PluginDependency jpaPluginDependency = pluginDependencyDAO.get(dependencyId);
            PluginDependency pluginDependency = new PluginDependency();
            if (jpaPluginDependency != null) {
                pluginDependency.setDependencyId(jpaPluginDependency.getDependencyId());
                pluginDependency.setDependentPluginUniqueId(jpaPluginDependency.getInstalledPluginsByDependentPluginUniqueId().getPluginUniqueId());
                pluginDependency.setPluginUniqueId(jpaPluginDependency.getInstalledPluginsByPluginUniqueId().getPluginUniqueId());
            }
            restWrapper = new RestWrapper(pluginDependency, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + dependencyId + " selected from AppDeploymentQueueStatus by User:" + principal.getName());
        }catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;

    }

    /**
     * This method calls DeleteDeployStatus and fetches a record corresponding to the deployStatusId passed.
     *
     * @param dependencyId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody public
    RestWrapper delete(
            @PathVariable("id") Integer dependencyId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            pluginDependencyDAO.delete(dependencyId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + dependencyId + " deleted from AppDeploymentQueueStatus by User:" + principal.getName());
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
            Integer counter=pluginDependencyDAO.totalRecordCount().intValue();
            List<com.wipro.ats.bdre.md.dao.jpa.PluginDependency> jpaPluginDependencies = pluginDependencyDAO.list(startPage, pageSize);
            List<PluginDependency> pluginDependencies = new ArrayList<PluginDependency>();

            for (com.wipro.ats.bdre.md.dao.jpa.PluginDependency pluginDependency : jpaPluginDependencies) {
                PluginDependency pluginDependency1 = new PluginDependency();
                pluginDependency1.setDependencyId(pluginDependency.getDependencyId());
                pluginDependency1.setDependentPluginUniqueId(pluginDependency.getInstalledPluginsByDependentPluginUniqueId().getPluginUniqueId());
                pluginDependency1.setPluginUniqueId(pluginDependency.getInstalledPluginsByPluginUniqueId().getPluginUniqueId());
                pluginDependency1.setCounter(counter);
                pluginDependencies.add(pluginDependency1);
            }
            restWrapper = new RestWrapper(pluginDependencies, RestWrapper.OK);
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
     * @param pluginDependency  Instance of DeployStatus.
     * @param bindingResult
     * @return restWrapper The updated instance of DeployStatus.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper update(@ModelAttribute("plugindependency")
                       @Valid PluginDependency pluginDependency, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into update for adq_status table");
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.PluginDependency jpaPluginDependency = new com.wipro.ats.bdre.md.dao.jpa.PluginDependency();
            InstalledPlugins installedPluginsDependent=installedPluginsDAO.get(pluginDependency.getDependentPluginUniqueId());
            jpaPluginDependency.setInstalledPluginsByDependentPluginUniqueId(installedPluginsDependent);
            InstalledPlugins installedPlugins=installedPluginsDAO.get(pluginDependency.getPluginUniqueId());
            jpaPluginDependency.setInstalledPluginsByPluginUniqueId(installedPlugins);
            pluginDependencyDAO.update(jpaPluginDependency);
            LOGGER.debug("Exiting from update for deploy_status table");
            restWrapper = new RestWrapper(jpaPluginDependency, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + pluginDependency.getDependencyId() + " updated in AppDeploymentQueueStatus by User:" + principal.getName() );
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
     * @param pluginDependency  Instance of DeployStatus.
     * @param bindingResult
     * @return restWrapper Instance of DeployStatus passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(@ModelAttribute("plugindependency")
                       @Valid PluginDependency pluginDependency, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Entering into insert for adq_status table");
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }

        try {
            com.wipro.ats.bdre.md.dao.jpa.PluginDependency jpaPluginDependency = new com.wipro.ats.bdre.md.dao.jpa.PluginDependency();
            InstalledPlugins installedPluginsDependent=installedPluginsDAO.get(pluginDependency.getDependentPluginUniqueId());
            jpaPluginDependency.setInstalledPluginsByDependentPluginUniqueId(installedPluginsDependent);
            InstalledPlugins installedPlugins=installedPluginsDAO.get(pluginDependency.getPluginUniqueId());
            jpaPluginDependency.setInstalledPluginsByPluginUniqueId(installedPlugins);
            LOGGER.debug("Exiting from insert for adq_status table");
            restWrapper = new RestWrapper(pluginDependency, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + " inserted in AppDeploymentQueueStatus by User:" + principal.getName() );
        } catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }
    @Override
    public Object execute(String[] params) {
        return null;
    }

}
