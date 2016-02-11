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
import com.wipro.ats.bdre.md.beans.table.ExecStatus;
import com.wipro.ats.bdre.md.dao.ExecStatusDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/execstatus")


public class ExecStatusAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ExecStatusAPI.class);
    /**
     * This method calls proc GetExecStatus and fetches a record corresponding to execStateId passed.
     *
     * @param execStateId
     * @return restWrapper It contains an instance of ExecStatus corresponding to execStateId passed.
     */


    @Autowired
    ExecStatusDAO execStatusDAO;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer execStateId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {

            ExecStatus execStatus = new ExecStatus();
            com.wipro.ats.bdre.md.dao.jpa.ExecStatus jpaExecStatus = execStatusDAO.get(execStateId);
            if (jpaExecStatus != null) {
                execStatus.setExecStateId(jpaExecStatus.getExecStateId());
                execStatus.setDescription(jpaExecStatus.getDescription());
            }
            // execStatus = s.selectOne("call_procedures.GetExecStatus", execStatus);

            restWrapper = new RestWrapper(execStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + execStateId + " selected from ExecStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteExecstatus and deletes a record corresponding to execStateId passed.
     *
     * @param execStateId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer execStateId, Principal principal,
            ModelMap model) {
        RestWrapper restWrapper = null;
        try {
            execStatusDAO.delete(execStateId);
            //s.delete("call_procedures.DeleteExecStatus", execStatus);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + execStateId + " deleted from ExecStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListExecStatus and fetches a list of instances of ExecStatus.
     *
     * @param
     * @return resWrapper It contains a list of instances of ExecStatus.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            // execStatus.setPage(startPage);
            // List<ExecStatus> execStatuses = s.selectList("call_procedures.ListExecStatus", execStatus);
            Integer counter=execStatusDAO.totalRecordCount().intValue();
            List<ExecStatus> execStatuses = new ArrayList<ExecStatus>();
            List<com.wipro.ats.bdre.md.dao.jpa.ExecStatus> jpaExecStatuses = execStatusDAO.list(startPage, pageSize);

            for (com.wipro.ats.bdre.md.dao.jpa.ExecStatus execStatus : jpaExecStatuses) {
                ExecStatus returnExecStatus = new ExecStatus();
                returnExecStatus.setExecStateId(execStatus.getExecStateId());
                returnExecStatus.setDescription(execStatus.getDescription());
                returnExecStatus.setCounter(counter);
                execStatuses.add(returnExecStatus);

            }
            restWrapper = new RestWrapper(execStatuses, RestWrapper.OK);
            LOGGER.info("All records listed from ExecStatus by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateExecStatus and updates the values passed. It also validates the values passed.
     *
     * @param execStatus    Instance of ExecStatus.
     * @param bindingResult
     * @return restWrapper It contains the updated instance of ExecStatus.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("execstatus")
                       @Valid ExecStatus execStatus, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder("<p>Please fix following errors and try again<p><ul>");
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append("<li>");
                errorMessages.append(error.getField());
                errorMessages.append(". Bad value: '");
                errorMessages.append(error.getRejectedValue());
                errorMessages.append("'</li>");
            }
            errorMessages.append("</ul>");
            restWrapper = new RestWrapper(errorMessages.toString(), RestWrapper.ERROR);
            return restWrapper;
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.ExecStatus jpaExecStatus = new com.wipro.ats.bdre.md.dao.jpa.ExecStatus();
            jpaExecStatus.setExecStateId(execStatus.getExecStateId());
            jpaExecStatus.setDescription(execStatus.getDescription());
            execStatusDAO.update(jpaExecStatus);
            //ExecStatus execStatuses = s.selectOne("call_procedures.UpdateExecStatus", execStatus);

            restWrapper = new RestWrapper(execStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + execStatus.getExecStateId() + " updated in ExecStatus by User:" + principal.getName() + execStatus);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
            LOGGER.info("exception occured");
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertExecStatus and adds a record in database. It also validates the values passed.
     *
     * @param execStatus    Instance of ExecStatus.
     * @param bindingResult
     * @return restWrapper It contains an instance of ExecStatus just added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("execstatus")
                       @Valid ExecStatus execStatus, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder("<p>Please fix following errors and try again<p><ul>");
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append("<li>");
                errorMessages.append(error.getField());
                errorMessages.append(". Bad value: '");
                errorMessages.append(error.getRejectedValue());
                errorMessages.append("'</li>");
            }
            errorMessages.append("</ul>");
            restWrapper = new RestWrapper(errorMessages.toString(), RestWrapper.ERROR);
            return restWrapper;
        }
        try {

            // ExecStatus execStatuses = s.selectOne("call_procedures.InsertExecStatus", execStatus);
            com.wipro.ats.bdre.md.dao.jpa.ExecStatus jpaExecStatus = new com.wipro.ats.bdre.md.dao.jpa.ExecStatus();
            jpaExecStatus.setExecStateId(execStatus.getExecStateId());
            jpaExecStatus.setDescription(execStatus.getDescription());
            Integer execStateId = execStatusDAO.insert(jpaExecStatus);
            jpaExecStatus.setExecStateId(execStateId);
            restWrapper = new RestWrapper(execStatus, RestWrapper.OK);
            LOGGER.info("Record with ID:" + execStatus.getExecStateId() + " inserted in ExecStatus by User:" + principal.getName() + execStatus);
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
