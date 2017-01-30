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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.dao.GeneralConfigDAO;
import com.wipro.ats.bdre.md.dao.jpa.GeneralConfigId;
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
import java.util.Map;

/**
 * Created by MI294210 on 9/9/2015.
 */
@Controller
@RequestMapping("/genconfig")


public class GeneralConfigAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(GeneralConfigAPI.class);

    /**
     * This method calls proc GetGeneralConfig and fetches a list of instances of GeneralConfig.
     *
     * @param configGroup
     * @return restWrapper It contains a list of instances of GeneralConfig.
     */
    @Autowired
    GeneralConfigDAO generalConfigDAO;

    @RequestMapping(value = {"/{cg}", "/{cg}/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper listUsingRequired(@PathVariable("cg") String configGroup, @RequestParam(value = "required", defaultValue = "2") Integer required, Principal principal) {

        RestWrapper restWrapper = null;
        GetGeneralConfig generalConfigs = new GetGeneralConfig();
        List<GeneralConfig> generalConfigList = generalConfigs.byConigGroupOnly(configGroup, required);
        if (!generalConfigList.isEmpty()) {
            if (generalConfigList.get(0).getRequired() == 2) {
                restWrapper = new RestWrapper("Listing of Records Failed", RestWrapper.ERROR);
            } else {
                restWrapper = new RestWrapper(generalConfigList, RestWrapper.OK);
                LOGGER.info("All records listed with config group :" + configGroup + "from General  Config by User:" + principal.getName());
            }
        } else {
            restWrapper = new RestWrapper(generalConfigList, RestWrapper.OK);

            LOGGER.info("All records listed with config group :" + configGroup + "from General  Config by User:" + principal.getName());
        }
        return restWrapper;

    }


    /**
     * This method calls proc GetGeneralConfig and fetches a list of instances of GeneralConfig.
     *
     * @param configGroup
     * @return restWrapper It contains a list of instances of GeneralConfig.
     */


    @RequestMapping(value = {"/list/{cg}", "/list/{cg}/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper list(@PathVariable("cg") String configGroup, Principal principal) {

        RestWrapper restWrapper = null;
        GetGeneralConfig generalConfigs = new GetGeneralConfig();
        List<GeneralConfig> generalConfigList = generalConfigs.listGeneralConfig(configGroup);
        restWrapper = new RestWrapper(generalConfigList, RestWrapper.OK);
        LOGGER.info("All records listed with config group :" + configGroup + "from General  Config by User:" + principal.getName());

        return restWrapper;

    }


    @RequestMapping(value = {"/OptionList/{cg}", "/OptionList/{cg}/"}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapperOptions listOptions(@PathVariable("cg") String configGroup, Principal principal) {

        RestWrapperOptions restWrapperOptions = null;
        try {
            GetGeneralConfig generalConfigs = new GetGeneralConfig();
            List<GeneralConfig> generalConfigList = generalConfigs.listGeneralConfig(configGroup);
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for(GeneralConfig generalConfig:generalConfigList)
            {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(generalConfig.getKey(),generalConfig.getValue());
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapper.OK);
            LOGGER.info("All records listed with config group :" + configGroup + "from General  Config by User:" + principal.getName());
        }catch (Exception e){
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;

    }
    /**
     * This method calls proc GetGenConfigProperty and fetches a record from GeneralConfig table corresponding to
     * Config group and key passed.
     *
     * @param
     * @return restWrapper It contains an instance of GeneralConfig corresponding to config group and key passed.
     */
    @RequestMapping(value = {"/{cg}/{k}", "{cg}/{k}/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper listUsingKey(@PathVariable("cg") String configGroup, @PathVariable("k") String key, Principal principal) {

        RestWrapper restWrapper = null;
        GetGeneralConfig getGeneralConfig = new GetGeneralConfig();
        GeneralConfig generalConfig = getGeneralConfig.byConigGroupAndKey(configGroup, key);
        if (generalConfig.getRequired() == 2) {
            restWrapper = new RestWrapper("Object with specified config_group and key not found", RestWrapper.ERROR);
        } else {
            restWrapper = new RestWrapper(generalConfig, RestWrapper.OK);
            LOGGER.info("Record with config group: " + configGroup + " and key:" + key + "selected from General Config by User:" + principal.getName());
        }

        return restWrapper;

    }

    /**
     * This method calls proc UpdateGeneralConfig and updates a record in GeneralConfig table corresponding to
     * Config group and key passed.
     *
     * @param map object
     * @return restWrapper It contains an updated instance of GeneralConfig corresponding to config group and key passed.
     */

    @RequestMapping(value = {"/admin/", "/admin"}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper update(@RequestParam Map<String, String> map, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            String configGroup = map.get("configGroup");
            GeneralConfig generalConfigUpdate = new GeneralConfig();
            for (String key : map.keySet()) {
                LOGGER.info("getting into loop" + key );
                if ("configGroup".equals(key)) {
                    continue;
                }
                GeneralConfig generalConfig = new GeneralConfig();
                generalConfig.setConfigGroup(configGroup);
                generalConfig.setKey(key);
                generalConfig.setDefaultVal(map.get(key));
                //initialising values to generalConfigId of dao
                GeneralConfigId jpaGeneralConfigId = new GeneralConfigId();
                jpaGeneralConfigId.setConfigGroup(generalConfig.getConfigGroup());
                jpaGeneralConfigId.setGcKey(generalConfig.getKey());
                //initialising values to generalConfig of dao
                com.wipro.ats.bdre.md.dao.jpa.GeneralConfig jpaGeneralConfig = generalConfigDAO.get(jpaGeneralConfigId);
                jpaGeneralConfig.setDefaultVal(generalConfig.getDefaultVal());
                //Calling Update method of generalConfigDAO
                generalConfigDAO.update(jpaGeneralConfig);
                generalConfigUpdate = generalConfig;
            }
            restWrapper = new RestWrapper(generalConfigUpdate, RestWrapper.OK);
            LOGGER.info(" Record with key:" + generalConfigUpdate.getKey() + " and config group:" + generalConfigUpdate.getConfigGroup() + " updated in general_config by User:" + principal.getName());

        }catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method updates a record in GeneralConfig table corresponding to object passed.
     *
     * @param generalConfig Instance of GeneralConfig
     * @return restWrapper It contains an updated instance of GeneralConfig.
     */

    @RequestMapping(value = {"/admin/update/", "/admin/update"}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper updateOneRecord(@ModelAttribute("generalConfig")
                                    @Valid GeneralConfig generalConfig, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            if(generalConfig.getRequired()<=1) {
                com.wipro.ats.bdre.md.dao.jpa.GeneralConfig jpaGeneralConfigUpdate = new com.wipro.ats.bdre.md.dao.jpa.GeneralConfig();

                GeneralConfigId jpaGeneralConfigId = new GeneralConfigId();
                jpaGeneralConfigId.setConfigGroup(generalConfig.getConfigGroup());
                jpaGeneralConfigId.setGcKey(generalConfig.getKey());
                jpaGeneralConfigUpdate.setId(jpaGeneralConfigId);

                jpaGeneralConfigUpdate.setDefaultVal(generalConfig.getDefaultVal());
                jpaGeneralConfigUpdate.setDescription(generalConfig.getDescription());
                jpaGeneralConfigUpdate.setEnabled(generalConfig.isEnabled());
                jpaGeneralConfigUpdate.setGcValue(generalConfig.getValue());
                if (generalConfig.getRequired() == 1)
                    jpaGeneralConfigUpdate.setRequired(true);
                else
                    jpaGeneralConfigUpdate.setRequired(false);
                jpaGeneralConfigUpdate.setType(generalConfig.getType());

                LOGGER.info(generalConfig);
                generalConfigDAO.update(jpaGeneralConfigUpdate);


                restWrapper = new RestWrapper(generalConfig, RestWrapper.OK);
                LOGGER.info(" Record with key:" + jpaGeneralConfigId.getGcKey() + " and config group:" + jpaGeneralConfigId.getConfigGroup() + " updated in general_config by User:" + principal.getName());

            }else{
                LOGGER.error("Invalid required field's data");
                restWrapper = new RestWrapper("Required field does not accest value other than 0 or 1", RestWrapper.ERROR);
            }

        }catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method adds a record in GeneralConfig table corresponding to object passed.
     *
     * @param generalConfig Instance of GeneralConfig
     * @return restWrapper It contains an added instance of GeneralConfig.
     */

    @RequestMapping(value = {"/admin/add/", "/admin/add"}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper addOneRecord(@ModelAttribute("generalConfig")
                                @Valid GeneralConfig generalConfig, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            if(generalConfig.getRequired()<=1) {
                com.wipro.ats.bdre.md.dao.jpa.GeneralConfig jpaGeneralConfig = new com.wipro.ats.bdre.md.dao.jpa.GeneralConfig();
                GeneralConfigId jpaGeneralConfigId = new GeneralConfigId();
                jpaGeneralConfigId.setConfigGroup(generalConfig.getConfigGroup());
                jpaGeneralConfigId.setGcKey(generalConfig.getKey());
                jpaGeneralConfig.setDefaultVal(generalConfig.getDefaultVal());
                jpaGeneralConfig.setDescription(generalConfig.getDescription());
                jpaGeneralConfig.setEnabled(generalConfig.isEnabled());
                jpaGeneralConfig.setGcValue(generalConfig.getValue());
                jpaGeneralConfig.setId(jpaGeneralConfigId);
                if (generalConfig.getRequired() == 1)
                    jpaGeneralConfig.setRequired(true);
                else
                    jpaGeneralConfig.setRequired(false);
                jpaGeneralConfig.setType(generalConfig.getType());
                GeneralConfigId id=generalConfigDAO.insert(jpaGeneralConfig);
                if(id!=null) {
                    restWrapper = new RestWrapper(generalConfig, RestWrapper.OK);
                    LOGGER.info(" Record with key:" + jpaGeneralConfigId.getGcKey() + " and config group:" + jpaGeneralConfigId.getConfigGroup() + " added in general_config by User:" + principal.getName());
                }else
                    LOGGER.error("Error to insert data");
            }else{
                LOGGER.error("Invalid required field's data");
                restWrapper = new RestWrapper("Required field does not accest value other than 0 or 1", RestWrapper.ERROR);
            }
        }catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method calls proc DeleteGeneralConfig and deletes a record in GeneralConfig table corresponding to
     * Config group and key passed.
     *
     * @param configGroup group and key
     * @return restWrapper with the updated instance of GeneralConfig table.
     */


    @RequestMapping(value = {"/{cg}/{key}", "/{cg}/{key}/"}, method = RequestMethod.DELETE)
    @ResponseBody public
    RestWrapper delete(@PathVariable("cg") String configGroup, @PathVariable("key") String key,
                       Principal principal) {
        RestWrapper restWrapper = null;
        try {
            GeneralConfig generalConfig = new GeneralConfig();
            generalConfig.setConfigGroup(configGroup);
            generalConfig.setKey(key);
            //initialising values to generalConfigId of dao
            GeneralConfigId jpaGeneralConfigId = new GeneralConfigId();
            jpaGeneralConfigId.setConfigGroup(generalConfig.getConfigGroup());
            jpaGeneralConfigId.setGcKey(generalConfig.getKey());
            generalConfigDAO.delete(jpaGeneralConfigId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with key:" + key + " deleted from general_config by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method fetches all distinct record in GeneralConfig table depending on Config group.
     *
     * @return restWrapper with the list of instance of GeneralConfig table.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper getDistinctGeneralConfig(@RequestParam(value = "page", defaultValue = "0") int startPage,
                                                @RequestParam(value = "size", defaultValue = "10") int pageSize,
                                                Principal principal) {
        RestWrapper restWrapper = null;
        try {
            List<com.wipro.ats.bdre.md.beans.table.GeneralConfig> configGroupList=new ArrayList<com.wipro.ats.bdre.md.beans.table.GeneralConfig>();
            configGroupList= generalConfigDAO.getDistinctGenerelConfig(startPage, pageSize);

            restWrapper = new RestWrapper(configGroupList, RestWrapper.OK);
            LOGGER.info("Size of distinct config group:"+configGroupList.size()+" by User:"+principal.getName());
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method deletes the records from Properties table corresponding to the configGroup passed
     * corresponding to processId passed.
     *
     * @param configGroup
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)

    @ResponseBody
    public RestWrapper delete(@PathVariable("id") String configGroup, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            generalConfigDAO.deleteByConfigGroup(configGroup);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with configGroup:" + configGroup + " deleted from generalConfig by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
