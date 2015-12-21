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
import com.wipro.ats.bdre.md.beans.GetETLDriverInfo;
import com.wipro.ats.bdre.md.dao.GetETLInfoDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by arijit on 12/8/14.
 */
public class GetETLDriver extends MetadataAPIBase {
    public GetETLDriver() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(GetETLDriver.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"minB", "min-batch-id", "minimum batch id"},
            {"maxB", "max-batch-id", "maximum batch id"}
    };

    /**
     * This method runs GetETLInfo proc and retrieves information regarding files available for batches mentioned.
     *
     * @param params String array having minimum-batch-id, maximum-batch-id, environment with their command line notations.
     * @return This method returns minimum-batch-id, maximum-batch-id and information regarding file between these
     * batch-ids, and retained by vm till runtime.
     */

    @Autowired
    private GetETLInfoDAO getETLInfoDAO;

    public GetETLDriverInfo execute(String[] params) {

        try {
            GetETLDriverInfo getETLDriverInfo = new GetETLDriverInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String minBId = commandLine.getOptionValue("min-batch-id");
            LOGGER.debug("minimum-batch-id is " + minBId);
            String maxBId = commandLine.getOptionValue("max-batch-id");
            LOGGER.debug("maximum-batch-id is " + maxBId);


//            s.selectOne("call_procedures.GetETLInfo", getETLDriverInfo);
            getETLInfoDAO.getETLInfo(Long.parseLong(minBId), Long.parseLong(maxBId));
            return getETLDriverInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
