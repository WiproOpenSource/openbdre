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

import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.dao.GeneralConfigDAO;
import com.wipro.ats.bdre.md.dao.jpa.GeneralConfigId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public
    @ResponseBody
    RestWrapper listUsingRequired(@PathVariable("cg") String configGroup, @RequestParam(value = "required", defaultValue = "2") Integer required, Principal principal) {

        RestWrapper restWrapper = null;
        GetGeneralConfig generalConfigs = new GetGeneralConfig();
        List<GeneralConfig> generalConfigList = generalConfigs.byConigGroupOnly(configGroup, required);
        if (generalConfigList.size() > 0) {
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
     * This method calls proc GetGenConfigProperty and fetches a record from GeneralConfig table corresponding to
     * Config group and key passed.
     *
     * @param
     * @return restWrapper It contains an instance of GeneralConfig corresponding to config group and key passed.
     */
    @RequestMapping(value = {"/{cg}/{k}", "{cg}/{k}/"}, method = RequestMethod.GET)
    public
    @ResponseBody
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
    public
    @ResponseBody
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
                //generalConfigUpdate = s.selectOne("call_procedures.UpdateGeneralConfig", generalConfig);
                generalConfigUpdate = generalConfig;
            }
            restWrapper = new RestWrapper(generalConfigUpdate, RestWrapper.OK);
            LOGGER.info(" Record with key:" + generalConfigUpdate.getKey() + " and config group:" + generalConfigUpdate.getConfigGroup() + " updated in general_config by User:" + principal.getName());

        } catch (Exception e) {
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
    public
    @ResponseBody
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
            //Calling delete method of generalConfigDAO
            generalConfigDAO.delete(jpaGeneralConfigId);
            //s.delete("call_procedures.DeleteGeneralConfig", generalConfig);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with key:" + key + " deleted from general_config by User:" + principal.getName());

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
