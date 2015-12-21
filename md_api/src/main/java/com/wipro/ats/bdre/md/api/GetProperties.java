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

package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.GetPropertiesInfo;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class gets settings as key value pair for particular process and configuration type.
 */
public class GetProperties extends MetadataAPIBase {
    public GetProperties() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(GetProperties.class);


    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of the process which requires properties"},
            {"cg", "config-group", "Configuration group of the process to run "}
    };

    /**
     * This method gets settings as key value pair for particular process and configuration type.
     *
     * @param pid         Process id of the process which requires properties.
     * @param configGroup Configuration group of the process to run.
     * @return This method returns key and value from properties table for given process-id and configuration group.
     */

    public Properties getProperties(String pid, String configGroup) {
        Properties savedProperties = new Properties();
        String[] params = {"--process-id", pid, "--config-group", configGroup};
        List<GetPropertiesInfo> getPropInfo = execute(params);
        try {
            for (GetPropertiesInfo info : getPropInfo) {
                savedProperties.setProperty(info.getKey(), info.getValue());
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }
        return savedProperties;
    }

    /**
     * This method calls proc GetPropertiesForConfig and gets settings as key value pair for particular process and configuration type.
     *
     * @param params String array having configuration group, environment and process-id with their
     * command line notations.
     * @return This method returns key and value from properties table for given process-id and configuration group.
     */
    @Autowired
    private PropertiesDAO propertiesDAO;

    public List<GetPropertiesInfo> execute(String[] params) {
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> propertyList;
        try {
            GetPropertiesInfo getPropertiesInfo = new GetPropertiesInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pid = commandLine.getOptionValue("process-id");
            LOGGER.debug("process-id  is " + pid);
            String configGrp = commandLine.getOptionValue("config-group");
            LOGGER.debug("config-group  is " + configGrp);


            getPropertiesInfo.setProcessId(Integer.parseInt(pid));
            getPropertiesInfo.setConfigGroup(configGrp);
            //Calling proc GetPropertiesForConfig
//            propertyList = s.selectList("call_procedures.GetPropertiesForConfig", getPropertiesInfo);
            propertyList = propertiesDAO.getPropertiesForConfig(Integer.parseInt(pid), configGrp);
            List<GetPropertiesInfo> propertiesInfoList = new ArrayList<GetPropertiesInfo>();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties property : propertyList) {
                GetPropertiesInfo propertyInfo = new GetPropertiesInfo();
                propertyInfo.setConfigGroup(property.getConfigGroup());
                propertyInfo.setProcessId(property.getProcess().getProcessId());
                propertyInfo.setKey(property.getId().getPropKey());
                propertyInfo.setValue(property.getPropValue());
                propertiesInfoList.add(propertyInfo);
            }

            if (LOGGER.isDebugEnabled()) {
                for (GetPropertiesInfo info : propertiesInfoList) {
                    LOGGER.debug("Got props from DB: " + info.getKey() + "=" + info.getValue());
                }
            }
            return propertiesInfoList;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}

