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

package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.dao.GeneralConfigDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KA294215 on 07-10-2015.
 */


public class GetGeneralConfig extends MetadataAPIBase {
    public GetGeneralConfig() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    GeneralConfigDAO generalConfigDAO;

    private static final Logger LOGGER = Logger.getLogger(GetGeneralConfig.class);

    public List<GeneralConfig> byConigGroupOnly(String configGroup, Integer required) {

        GeneralConfig generalConfig = new GeneralConfig();
        List<GeneralConfig> generalConfigs = new ArrayList<GeneralConfig>();
        try {
            if (required == null) {
                required = 2;
            }
            // generalConfigs = s.selectList("call_procedures.GeneralConfig", generalConfig);
            generalConfigs = generalConfigDAO.getGeneralConfig(configGroup, required);
            LOGGER.info("All records listed with config group" + configGroup);
            LOGGER.info("generalConfigs" + generalConfigs);

        } catch (Exception e) {
            generalConfig.setRequired(2);
            generalConfigs.add(generalConfig);
            LOGGER.error("Listing of Records Failed");
        }
        return generalConfigs;
    }

    public GeneralConfig byConigGroupAndKey(String configGroup, String key) {

        GeneralConfig generalConfig = new GeneralConfig();
        try {

            //generalConfig = s.selectOne("call_procedures.GenConfigProperty", generalConfig);
            generalConfig = generalConfigDAO.GetGenConfigProperty(configGroup, key);

            LOGGER.info("Record with config group:" + configGroup + " and key:" + key + "selected from General Config " + generalConfig);

        } catch (Exception e) {
            generalConfig.setRequired(2);
            LOGGER.error("Object with specified config_group and key not found");

        }
        return generalConfig;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
