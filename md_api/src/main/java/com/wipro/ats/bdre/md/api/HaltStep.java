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
import com.wipro.ats.bdre.md.beans.HaltStepInfo;
import com.wipro.ats.bdre.md.dao.StepDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by arijit on 12/8/14.
 */
public class HaltStep extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(HaltStep.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "sub-process-id", "Sub Process id of the step"}
    };

    @Autowired
    private StepDAO stepDAO;

    public HaltStep() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * This process calls HaltStep Proc and updates instance_exec table and batch_consump_queue.
     *
     * @param params String array having sub-process-id, environment with their command line notations.
     * @return returns nothing.
     */
    @Override
    public HaltStepInfo execute(String[] params) {
        try {
            HaltStepInfo haltStepInfo = new HaltStepInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String subPids = commandLine.getOptionValue("sub-process-id");
            LOGGER.debug("subPid is " + subPids);

            String[] subPidList = subPids.split(",");

            for (String subPid : subPidList) {
                //Calling proc HaltStep
                haltStepInfo.setSubProcessId(Integer.parseInt(subPid));
                stepDAO.haltStep(haltStepInfo.getSubProcessId());
            }
            return haltStepInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}
