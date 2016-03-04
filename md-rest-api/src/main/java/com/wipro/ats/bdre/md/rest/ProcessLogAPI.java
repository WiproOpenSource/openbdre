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

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.dao.ProcessLogDAO;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage, @RequestParam(value = "size", defaultValue = "10") int pageSize, @RequestParam(value = "pid", defaultValue = "0") Integer pid, Principal principal) {
        RestWrapper restWrapper = null;
        Integer processId=pid;
        try {
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            if (processId == 0) {
                processId = null;
            }
            processLogInfo.setProcessId(processId);
            processLogInfo.setPage(startPage);
            processLogInfo.setPageSize(pageSize);

            List<ProcessLogInfo> listLog = processLogDAO.listLog(processLogInfo);
            restWrapper = new RestWrapper(listLog, RestWrapper.OK);
            LOGGER.info("All records listed from ProcessLog by User:" + principal.getName());
        } catch (Exception e) {
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


    @ResponseBody
    public RestWrapper list(@PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            ProcessLogInfo processLogInfo = new ProcessLogInfo();
            processLogInfo.setProcessId(processId);
            List<ProcessLogInfo> processLogList = processLogDAO.getProcessLog(processLogInfo);
            for(ProcessLogInfo processLogInfo1:processLogList){
                processLogInfo1.setTableAddTs(DateConverter.dateToString(processLogInfo1.getAddTs()));
            }
            restWrapper = new RestWrapper(processLogList, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " selected from ProcessLog by User:" + principal.getName());

        } catch (Exception e) {
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



