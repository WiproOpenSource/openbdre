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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.InitStepInfo;
import com.wipro.ats.bdre.md.dao.StepDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by arijit on 12/8/14.
 */
public class InitStep extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(InitStep.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "sub-process-id", "Sub Process id of the step"}
    };
    @Autowired
    private StepDAO stepDAO;

    public InitStep() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * This method call InitStep proc and returns instance-exec-id of sub process which is running.This makes
     * an entry in instance_exec table.
     *
     * @param params String array having environment and sub-process-id with their command line notations
     * @return returns sub-process-instance-exec-id.
     */

    @Override
    public InitStepInfo execute(String[] params) {

        try {
            InitStepInfo initStepInfo = new InitStepInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String subPid = commandLine.getOptionValue("sub-process-id");
            LOGGER.debug("subPid is " + subPid);
            initStepInfo.setSubProcessId(Integer.parseInt(subPid));

            stepDAO.initStep(initStepInfo.getSubProcessId());
            return initStepInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}
