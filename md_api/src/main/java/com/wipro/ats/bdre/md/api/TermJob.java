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

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.api.util.StatusNotification;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.beans.TermJobInfo;
import com.wipro.ats.bdre.md.dao.JobDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arijit on 12/8/14.
 */
public class TermJob extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(TermJob.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", "Process Id of the process to terminate"},
    };


    public TermJob() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }
    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private ProcessDAO processDAO;


    /**
     * This method calls TermJob proc and updates status of a process as terminated in instance_exec table.
     * It also calls StatusNotification class to send the message of Job termination.
     *
     * @param params String array having environment and process-id with their command line notations.
     * @return nothing.
     */


    @Override
    public TermJobInfo execute(String[] params) {

        try {
            TermJobInfo termJobInfo = new TermJobInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pid = commandLine.getOptionValue("process-id");
            LOGGER.debug("processId is " + pid);

            termJobInfo.setProcessId(Integer.parseInt(pid));

            jobDAO.termJob(termJobInfo.getProcessId());
            ProcessInfo processInfo = new ProcessInfo();
            com.wipro.ats.bdre.md.dao.jpa.Process process = new com.wipro.ats.bdre.md.dao.jpa.Process();
            process.setProcessId(Integer.parseInt(pid));
            process = processDAO.get(Integer.parseInt(pid));
            processInfo.setProcessName(process.getProcessName());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            //message to send to messaging server
            String termMessage = " --processId=" + termJobInfo.getProcessId() + "  --stage=parent" + "  --status=fail" + "  --processName=" + process.getProcessName() + "  --endTs=" + (dateFormat.format(date)).toString();
            //Calling StatusNotification class
            //The TermJob completes even if sending message fails
            try {
                BasicConfigurator.configure();
                StatusNotification statusNotification = new StatusNotification(termMessage, MDConfig.getProperty("status-notification.term-queue"));
                LOGGER.info(statusNotification.toString());
            } catch (Exception e) {
                LOGGER.error("Error occurred while notifying job status", e);
            }
            return termJobInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
