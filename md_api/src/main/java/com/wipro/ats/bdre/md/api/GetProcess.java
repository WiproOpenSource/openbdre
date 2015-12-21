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
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 12/8/14.
 */
public class GetProcess extends MetadataAPIBase {
    public GetProcess() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(GetProcess.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "parent-process-id", "Parent process id for a given workflow"}
    };
    private static final String[][] PARAMS_STRUCTURE_WITH_EXEC = {
            {"p", "parent-process-id", "Parent process id for a given workflow"},
            {"ieid", "instance-exec-id", "Instance Exec Id for a given run"}

    };


    /**
     * This method executes query on process table for mentioned parent-process-id.
     *
     * @param params String array having environment and process-id with their command line notations.
     * @return This method returns information regarding that parent process and all sub-process of parent process.
     */
    @Autowired
    ProcessDAO processDAO;

    public List<ProcessInfo> execute(String[] params) {
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
        try {

            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String subPid = commandLine.getOptionValue("parent-process-id");
            LOGGER.debug("Pid is " + subPid);

            //Calling proc select-process-list
            List<com.wipro.ats.bdre.md.dao.jpa.Process> jpaProcessList = processDAO.selectProcessList(Integer.parseInt(subPid));
            for (Process process : jpaProcessList) {
                ProcessInfo processInfo = new ProcessInfo();

                processInfo.setProcessId(process.getProcessId());
                processInfo.setBusDomainId(process.getBusDomain().getBusDomainId());
                processInfo.setProcessTypeId(process.getProcessType().getProcessTypeId());
                processInfo.setCanRecover(process.getCanRecover());
                processInfo.setDescription(process.getDescription());
                if (process.getProcess() != null) {
                    processInfo.setParentProcessId(process.getProcess().getProcessId());
                }
                processInfo.setProcessName(process.getProcessName());
                processInfo.setEnqProcessId(process.getEnqueuingProcessId());
                processInfo.setNextProcessIds(process.getNextProcessId());
                processInfo.setWorkflowId(process.getWorkflowType().getWorkflowId());
                processInfo.setBatchCutPattern(process.getBatchCutPattern());
                processInfo.setDeleteFlag(process.getDeleteFlag());

                processInfoList.add(processInfo);

            }

            // List<ProcessInfo> processInfos = s.selectList("call_procedures.select-process-list", processInfo);

            return processInfoList;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }

    }

    public List<ProcessInfo> execInfo(String[] params) {

        try {
            ProcessInfo processInfo = new ProcessInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE_WITH_EXEC);
            String subPid = commandLine.getOptionValue("parent-process-id");
            LOGGER.debug("Pid is " + subPid);
            String ieid = commandLine.getOptionValue("instance-exec-id");
            LOGGER.debug("Ieid is " + ieid);


            //Calling proc select-process-list

            List<ProcessInfo> processInfos = processDAO.selectProcessListWithExec(Integer.parseInt(subPid), Long.parseLong(ieid));
            // List<ProcessInfo> processInfos = s.selectList("call_procedures.select-process-list-with-exec", processInfo);

            return processInfos;

        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}
