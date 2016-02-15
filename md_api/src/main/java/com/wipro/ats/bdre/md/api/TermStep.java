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
import com.wipro.ats.bdre.md.beans.TermStepInfo;
import com.wipro.ats.bdre.md.dao.StepDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by arijit on 12/8/14.
 */
public class TermStep extends MetadataAPIBase {
    public TermStep() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(TermStep.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "sub-process-id", "Sub Process id of the step"}
    };

    /**
     * This method calls TermStep proc which updates instance_exec and batch_consump_queue table.
     *
     * @param params String array having environment and process-id with their command line notations.
     * @return nothing.
     */
    @Autowired
    private StepDAO stepDAO;

    public TermStepInfo execute(String[] params) {
        try {
            TermStepInfo termStepInfo = new TermStepInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String subPid = commandLine.getOptionValue("sub-process-id");
            LOGGER.debug("subPid is " + subPid);


            termStepInfo.setSubProcessId(Integer.parseInt(subPid));
            //calling proc TermStep
            //  s.selectOne("call_procedures.TermStep", termStepInfo);
            stepDAO.termStep(Integer.parseInt(subPid));
            return termStepInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }


}
