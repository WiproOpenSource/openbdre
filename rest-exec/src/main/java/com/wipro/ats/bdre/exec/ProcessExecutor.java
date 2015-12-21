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

package com.wipro.ats.bdre.exec;

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ExecutionInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by arijit on 1/9/15.
 */

@Controller
@RequestMapping("/execute")


public class ProcessExecutor extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessExecutor.class);

    @RequestMapping(value = "/{pid}/{ptid}/{bdid}/{wfid}", method = RequestMethod.GET)
    public
    @ResponseBody
    ExecutionInfo executeProcess(@PathVariable("pid") String processId, @PathVariable("ptid") String processTypeId,
                                 @PathVariable("bdid") String busDomainId, @PathVariable("wfid") String workflowId) {
        ExecutionInfo executionInfo = new ExecutionInfo();

        try {
            String[] command = null;
            if ("1".equals(workflowId)) {
                command = new String[]{MDConfig.getProperty("execute.oozie-script-path"), busDomainId, processTypeId, processId};
                //starting the external program

            } else if ("2".equals(workflowId)) {
                command = new String[]{MDConfig.getProperty("execute.standalone-script-path"), busDomainId, processTypeId, processId};

            }
            LOGGER.info("Running the command : -- " + command[0] + " " + command[1] + " " + command[2] + " " + command[3]);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectOutput(new File(MDConfig.getProperty("execute.log-path") + processId.toString()));
            LOGGER.info("The output is redirected to " + MDConfig.getProperty("execute.log-path") + processId.toString());
            processBuilder.redirectErrorStream(true);
            java.lang.Process osProcess = processBuilder.start();
//            LOGGER.debug(" The output value" + osProcess.exitValue() + "output " + osProcess.getOutputStream().toString());
            try {
                Class<?> cProcessImpl = osProcess.getClass();
                Field fPid = cProcessImpl.getDeclaredField("pid");
                if (!fPid.isAccessible()) {
                    fPid.setAccessible(true);
                }
                executionInfo.setOSProcessId(fPid.getInt(osProcess));
                LOGGER.debug("Setting OS process Id" + executionInfo.getOSProcessId());
            } catch (Exception e) {
                executionInfo.setOSProcessId(-1);
                LOGGER.error("Setting OS Process ID failed " + executionInfo.getOSProcessId());
            }


        } catch (Exception e) {
            executionInfo.setOSProcessId(-1);
            LOGGER.error("Starting OS Process failed " + executionInfo.getOSProcessId());
        }
        LOGGER.info("OS Process started with ID " + executionInfo.getOSProcessId());
        return executionInfo;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
