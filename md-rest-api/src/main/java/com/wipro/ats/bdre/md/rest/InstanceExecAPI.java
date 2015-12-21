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

package com.wipro.ats.bdre.md.rest;

/**
 * Created by kapil on 29-01-2015.
 */

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.InstanceExec;
import com.wipro.ats.bdre.md.dao.InstanceExecDAO;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Created by kapil on 29-01-2015.
 */
@Controller
@RequestMapping("/instanceexec")


public class InstanceExecAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(InstanceExecAPI.class);
    @Autowired
    InstanceExecDAO instanceExecDAO;

    /**
     * This method calls proc GetInstanceExec and fetches a record corresponding to instanceExecId passed.
     *
     * @param instanceExecId
     * @return restWrapper It contains an instance of InstanceExec.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer instanceExecId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.InstanceExec jpaInstanceExec = instanceExecDAO.get((long) instanceExecId);
            InstanceExec instanceExec = new InstanceExec();
            if (jpaInstanceExec != null) {
                instanceExec.setInstanceExecId(jpaInstanceExec.getInstanceExecId().intValue());
                instanceExec.setStartTs(jpaInstanceExec.getStartTs());
                instanceExec.setEndTs(jpaInstanceExec.getEndTs());
                instanceExec.setExecState(jpaInstanceExec.getExecStatus().getExecStateId());
                instanceExec.setProcessId(jpaInstanceExec.getProcess().getProcessId());
            }
            // instanceExec = s.selectOne("call_procedures.GetInstanceExec", instanceExec);
            instanceExec.setTableStartTs(DateConverter.dateToString(instanceExec.getStartTs()));
            instanceExec.setTableEndTs(DateConverter.dateToString(instanceExec.getEndTs()));

            restWrapper = new RestWrapper(instanceExec, RestWrapper.OK);
            LOGGER.info("Record with ID:" + instanceExecId + " selected from InstanceExec by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;

    }

    /**
     * This method calls proc ListInstanceExec and fetches a list of instances of InstanceExec.
     *
     * @param pid
     * @param startPage
     * @return restWrapper It contains a list of instances of InstanceExec.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "pid", defaultValue = "0") Integer pid, @RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        LOGGER.info("pid = " + pid);
        LOGGER.info("startPage = " + startPage);

        RestWrapper restWrapper = null;
        try {

            if (pid == 0) {
                pid = null;
            }
            List<InstanceExec> instanceExecs = instanceExecDAO.list(pid, startPage, pageSize);

            //   List<InstanceExec> instanceExecs = s.selectList("call_procedures.ListInstanceExec", instanceExec);
            for (InstanceExec ie : instanceExecs) {
                ie.setTableStartTs(DateConverter.dateToString(ie.getStartTs()));
                ie.setTableEndTs(DateConverter.dateToString(ie.getEndTs()));
            }

            restWrapper = new RestWrapper(instanceExecs, RestWrapper.OK);
            LOGGER.info("All records listed from InstanceExec by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }

}


