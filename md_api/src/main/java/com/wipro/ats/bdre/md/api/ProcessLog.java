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
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.dao.ProcessLogDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Timestamp;

/**
 * Created by IshitaParekh on 11-03-2015.
 */
public class ProcessLog extends MetadataAPIBase {
    public ProcessLog() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(ProcessLog.class);

    @Autowired
    ProcessLogDAO processLogDAO;


    private static final String[][] PARAMS_STRUCTURE = {
            {"processId", "process-id", "process id"},
            {"addTs", "add-ts", "add timestamp"},
            {"logCategory", "log-category", "log category"},
            {"message", "message", "message"},
            {"messageId", "message-id", "message id"},
            {"instanceRef", "instance-ref", "instance reference / instance exec id"}

    };

    /**
     * This method is used by DataImport module to get the LastValue for incremental import.This method runs
     * GetLastValue proc present in AddProcessLogProc  in mysql.
     *
     * @param pid         Process id of data import.
     * @param msgId       message Id is String to identify the message.
     * @param logCategory Log Category is String to categorize the process logs.
     * @return This method returns instance of ProcessLogInfo class containing last value for incremental import.
     */
    public ProcessLogInfo getLastValue(String pid, String msgId, String logCategory) {
        ProcessLogInfo processLogInfo = new ProcessLogInfo();

        try {

            processLogInfo.setProcessId(Integer.parseInt(pid));
            processLogInfo.setLogCategory(logCategory);
            processLogInfo.setMessageId(msgId);
            //Calling proc GetLastValue
            processLogInfo = processLogDAO.getLastValue(processLogInfo);
//            processLogInfo = s.selectOne("call_procedures.GetLastValue", processLogInfo);
            return processLogInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }


    /**
     * This method runs AddProcessLogProc  proc in mysql.
     *
     * @param params String array containing process-id,log-category, message-id, message, instance-execution-id,add-timestamp,
     *               environment with their respective notation on command line.
     * @return This method returns same input data as instance of ProcessLogInfo class.
     */

    public ProcessLogInfo execute(String[] params) {
        try {
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pId = commandLine.getOptionValue("process-id");
            LOGGER.debug("processId is " + pId);
            String logCat = commandLine.getOptionValue("log-category");
            LOGGER.debug("log category is " + logCat);
            String message = commandLine.getOptionValue("message");
            LOGGER.debug("message is " + message);
            String mId = commandLine.getOptionValue("message-id");
            LOGGER.debug("message id is" + mId);
            String iRef = commandLine.getOptionValue("instance-ref");
            LOGGER.debug("instance ref is " + iRef);
            String addTs = commandLine.getOptionValue("add-ts");
            LOGGER.debug("add ts " + addTs);

            processLogInfo.setProcessId(Integer.parseInt(pId));
            processLogInfo.setLogCategory(logCat);
            processLogInfo.setMessage(message);
            processLogInfo.setMessageId(mId);
            processLogInfo.setInstanceRef(Long.parseLong(iRef));
            processLogInfo.setAddTs(Timestamp.valueOf(addTs));

            //  processLogInfo = s.selectOne("call_procedures.AddProcessLog", processLogInfo);

            com.wipro.ats.bdre.md.dao.jpa.ProcessLog processLog = new com.wipro.ats.bdre.md.dao.jpa.ProcessLog();
            processLog.setAddTs(Timestamp.valueOf(addTs));
            processLog.setInstanceRef(Long.parseLong(iRef));
            processLog.setLogCategory(logCat);
            processLog.setMessage(message);
            processLog.setMessageId(mId);
            Process process = new Process();
            process.setProcessId(Integer.parseInt(pId));
            processLog.setProcess(process);
            //inserting process log
            Long logId = processLogDAO.insert(processLog);

            processLogInfo.setLogId((int) (long) logId);

            return processLogInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

    /**
     * This method runs AddProcessLogProc proc in mysql and adds process log.
     *
     * @param processLogInfo Instance of ProcessLogInfo class containing process-id,log-category, message-id, message, instance-execution-id,add-timestamp,
     *                       environment.
     * @return nothing.
     */
    public void log(ProcessLogInfo processLogInfo) {
        try {

            // s.selectOne("call_procedures.AddProcessLog", processLogInfo);
            //calling addprocesslog function of Addprocesslogdao
            processLogDAO.log(processLogInfo);

        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }

    }
}


