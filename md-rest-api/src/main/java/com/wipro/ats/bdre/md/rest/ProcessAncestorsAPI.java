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
import com.wipro.ats.bdre.md.beans.ProcessAncestorsInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.dao.ProcessAncestorsDAO;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MI294210 on 9/10/2015.
 */
@Controller
@RequestMapping("/ancestors")

public class ProcessAncestorsAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessAncestorsAPI.class);
    @Autowired
    ProcessAncestorsDAO processAncestorsDAO;

    /**
     * This method calls proc FetchDetails and proc ListU and fetches a record with upstreams and deploy details corresponding to the processId passed.
     *
     * @param processId
     * @return restWrapper instance of ProcessAncestorsInfo corresponding to processId passed.
     */


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper get(
            @PathVariable("id") Integer processId, Principal principal
    ) {

        RestWrapper restWrapper = null;

        try {

            ProcessAncestorsInfo ancestorsInfo = new ProcessAncestorsInfo();
            ancestorsInfo = fetchAncestors(processId).get(0);
            ancestorsInfo.setProcessAncestorsInfoList(fetchAncestors(processId));
            LOGGER.debug("Final ancestor bean of process:" + processId + " is " + ancestorsInfo);

            restWrapper = new RestWrapper(ancestorsInfo, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " and ancestor details fetched by User:" + principal.getName());
        }catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;

    }

    private List<ProcessAncestorsInfo> fetchAncestors(Integer pid) {
        int i = 0;
        List<ProcessAncestorsInfo> ancestorList = new ArrayList<ProcessAncestorsInfo>();
        List<Integer> ancestors = new ArrayList<Integer>();
        ancestors.add(pid);
        try {
            while (i < ancestors.size()) {


                ProcessAncestorsInfo processAncestorsInfo = new ProcessAncestorsInfo();
                processAncestorsInfo = processAncestorsDAO.fetchDetails(ancestors.get(i));
                processAncestorsInfo.setTableEditTs(DateConverter.dateToString(processAncestorsInfo.getEditTs()));
                processAncestorsInfo.setTableDeployInsertTs(DateConverter.dateToString(processAncestorsInfo.getDeployInsertTs()));
                processAncestorsInfo.setTableDeploySuccessTs(DateConverter.dateToString(processAncestorsInfo.getDeploySuccessTs()));

                LOGGER.info("ProcessAncestor bean :" + processAncestorsInfo);
                List<Process> upstreamProcesses = processAncestorsDAO.listUpstreams(ancestors.get(i));

                processAncestorsInfo.setUpstreamProcess(upstreamProcesses);
                LOGGER.info("ProcessAncestor bean with ancestor process list:" + processAncestorsInfo);
                ancestorList.add(processAncestorsInfo);//adding processAncestorsInfo bean to the list
                for (Process p : upstreamProcesses) {
                    ancestors.add(p.getProcessId());
                }
                i++;
            }
            return ancestorList;
        } catch (MetadataException e) {
            LOGGER.error(e);
            throw new MetadataException();
        }
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
