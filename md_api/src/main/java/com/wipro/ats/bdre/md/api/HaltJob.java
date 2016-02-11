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
import com.wipro.ats.bdre.md.beans.HaltJobInfo;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.JobDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arijit on 12/8/14.
 */
public class HaltJob extends MetadataAPIBase {
    public HaltJob() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(HaltJob.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", "Process id of the job"},
            {"batchmarking", "batch-marking", "Batch Marking of the batches enqueued for downstream"}
    };

    /**
     * This process calls HaltJob Proc and updates the instance_exec table, adds entry in batch_consump_queue and
     * archive_consump_queue once process ends successfully.It also calls StatusNotification class to send the
     * message of Job completion.
     *
     * @param params String array having process-id, batch-marking,environment with their command line notations.
     * @return returns nothing.
     */
    @Autowired
    private JobDAO jobDAO;
    @Autowired
    private ProcessDAO processDAO;

    public HaltJobInfo execute(String[] params) {
        try {
            HaltJobInfo haltJobInfo = new HaltJobInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pid = commandLine.getOptionValue("process-id");
            LOGGER.debug("processId is " + pid);
            String batchMarking = commandLine.getOptionValue("batch-marking");
            LOGGER.debug("Batch Marking is " + batchMarking);

            haltJobInfo.setProcessId(Integer.parseInt(pid));
            haltJobInfo.setBatchMarking(batchMarking);
//            s.selectOne("call_procedures.HaltJob", haltJobInfo);
            jobDAO.haltJob(haltJobInfo.getProcessId(), haltJobInfo.getBatchMarking());
            ProcessInfo processInfo = new ProcessInfo();
            com.wipro.ats.bdre.md.dao.jpa.Process process = new com.wipro.ats.bdre.md.dao.jpa.Process();
            process.setProcessId(Integer.parseInt(pid));
//            process = s.selectOne("call_procedures.GetProcess", process);
            process = processDAO.get(Integer.parseInt(pid));
            processInfo.setProcessName(process.getProcessName());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            //message to send to messaging server
            String haltMessage = " --processId=" + haltJobInfo.getProcessId() + "  --stage=parent" + "  --status=success" + "  --processName=" + process.getProcessName() + "  --endTs=" + (dateFormat.format(date)).toString();
            //Calling StatusNotification class
            //The HaltJob completes even if sending message fails
            try {
                BasicConfigurator.configure();
                new StatusNotification(haltMessage, MDConfig.getProperty("status-notification.halt-queue"));
            } catch (Exception e) {
                LOGGER.error("Error occurred while notifying job status", e);

            }
            return haltJobInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}

