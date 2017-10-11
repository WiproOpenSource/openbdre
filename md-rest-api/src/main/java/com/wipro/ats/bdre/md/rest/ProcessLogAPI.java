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

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.ProcessLogDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/processlog")


public class ProcessLogAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessLogAPI.class);

    /**
     * This method calls proc ListLog and returns a list of instances of ProcessLog.
     *
     * @param startPage
     * @param pid process Id.
     * @return restWrapper It contains list of instances of ProcessLog.
     */
    @Autowired
    ProcessLogDAO processLogDAO;
    @Autowired
    ProcessDAO processDAO;
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage, @RequestParam(value = "size", defaultValue = "10") int pageSize, @RequestParam(value = "pid", defaultValue = "0") Integer pid, Principal principal) {
        RestWrapper restWrapper = null;
        Integer processId = pid;
        try {
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            if (pid == 0) {
                processId = null;
            }
            LOGGER.info("parent processId is " + processId);
            processLogInfo.setParentProcessId(processId);
            processLogInfo.setPage(startPage);
            processLogInfo.setPageSize(pageSize);
            List<ProcessLogInfo> listLog = new ArrayList<>();
            List<ProcessLogInfo> logList = processLogDAO.listLog(processLogInfo);
            LOGGER.info("process log contains before scecurity check " + logList + " " + principal.getName());
            if (processId != null) {
                processDAO.securityCheck(processId, principal.getName(), "read");
                for (ProcessLogInfo log : logList) {
                    if (log.getParentProcessId().equals(processId))
                        listLog.add(log);
                }
            }
            else{
            for (ProcessLogInfo log : logList) {
                String returnValue = "";
                com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = processDAO.get(log.getParentProcessId());
                if (parentProcess.getProcess() != null)
                    returnValue = processDAO.securityCheck(parentProcess.getProcess().getProcessId(), principal.getName(), "read");
                else
                    returnValue = processDAO.securityCheck(log.getParentProcessId(), principal.getName(), "read");
                LOGGER.info(returnValue);
                List<String> values = new ArrayList<>();
                values.add("ACCESS GRANTED");
                values.add("NOT REQUIRED");
                if (values.contains(returnValue))
                    listLog.add(log);

            }}
            LOGGER.info("process log contains "+listLog);
            restWrapper = new RestWrapper(listLog, RestWrapper.OK);
            LOGGER.info("All records listed from ProcessLog by User:" + principal.getName());
        }catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetProcessLog and returns a record corresponding to processId passed.
     *
     * @param processId
     * @return restWrapper It contains instance of ProcessLog corresponding to processId passed.
     */
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper list(@PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Process parentProcess=processDAO.get(processId);
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),"read");
            else
                processDAO.securityCheck(processId,principal.getName(),"read");
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            processLogInfo.setProcessId(processId);
            List<ProcessLogInfo> processLogList = processLogDAO.getProcessLog(processLogInfo);
            for(ProcessLogInfo processLogInfo1:processLogList){
                processLogInfo1.setTableAddTs(DateConverter.dateToString(processLogInfo1.getAddTs()));
            }
            restWrapper = new RestWrapper(processLogList, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " selected from ProcessLog by User:" + principal.getName());

        }catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}



