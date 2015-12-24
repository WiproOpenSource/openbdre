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

import com.wipro.ats.bdre.md.api.Intermediate;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.HiveTables;
import com.wipro.ats.bdre.md.dao.HiveTablesDAO;
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
@RequestMapping("/hivetables")


public class HiveTablesAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(HiveTablesAPI.class);
    @Autowired
    HiveTablesDAO hiveTablesDAO;

    /**
     * This method calls proc GetHIveTables and fetches a record corresponding to tableId passed.
     *
     * @param tableId
     * @return restWrapper It contains an instance of HiveTables corresponding to tableId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer tableId, Principal principal
    ) {
        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.HiveTables jpaHiveTables = hiveTablesDAO.get(tableId);
            HiveTables hiveTables = new HiveTables();
            if (jpaHiveTables != null) {
                hiveTables.setTableId(jpaHiveTables.getTableId());
                hiveTables.setTableName(jpaHiveTables.getTableName());
                hiveTables.setType(jpaHiveTables.getType());
                hiveTables.setComments(jpaHiveTables.getComments());
                hiveTables.setDbName(jpaHiveTables.getDbname());
                hiveTables.setBatchIdPartitionCol(jpaHiveTables.getBatchIdPartitionCol());
                hiveTables.setDdl(jpaHiveTables.getDdl());
                hiveTables.setLocationType(jpaHiveTables.getLocationType());

            }
            //hiveTables = s.selectOne("call_procedures.GetHiveTables", hiveTables);

            restWrapper = new RestWrapper(hiveTables, RestWrapper.OK);
            LOGGER.info("Record with ID:" + tableId + " selected from HiveTables by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteHiveTables and deletes a record corresponding to the tableid passed.
     *
     * @param tableId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer tableId, Principal principal,
            ModelMap model) {

        RestWrapper restWrapper = null;
        try {

            hiveTablesDAO.delete(tableId);
            // s.delete("call_procedures.DeleteHiveTables", hiveTables);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + tableId + " deleted from HiveTables by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListHiveTables and fetches a list of instances of HiveTables.
     *
     * @param
     * @return restwrapper It contains a list of instances of HiveTables.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=hiveTablesDAO.totalRecordCount().intValue();
            List<com.wipro.ats.bdre.md.dao.jpa.HiveTables> jpaHiveTablesList = hiveTablesDAO.list(startPage, pageSize);
            List<HiveTables> hiveTablesList = new ArrayList<HiveTables>();

            for (com.wipro.ats.bdre.md.dao.jpa.HiveTables hiveTable : jpaHiveTablesList) {
                HiveTables returnHiveTable = new HiveTables();
                returnHiveTable.setLocationType(hiveTable.getLocationType());
                returnHiveTable.setDdl(hiveTable.getDdl());
                returnHiveTable.setBatchIdPartitionCol(hiveTable.getBatchIdPartitionCol());
                returnHiveTable.setComments(hiveTable.getComments());
                returnHiveTable.setDbName(hiveTable.getDbname());
                returnHiveTable.setTableId(hiveTable.getTableId());
                returnHiveTable.setTableName(hiveTable.getTableName());
                returnHiveTable.setType(hiveTable.getType());
                returnHiveTable.setCounter(counter);
                hiveTablesList.add(returnHiveTable);
            }
            // List<HiveTables> hiveTablesList = s.selectList("call_procedures.ListHiveTables", hiveTables);

            restWrapper = new RestWrapper(hiveTablesList, RestWrapper.OK);
            LOGGER.info("All records listed from HiveTables by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateHiveTables and updates the values. It also validates the values passed.
     *
     * @param hiveTables    Instance of HiveTables.
     * @param bindingResult
     * @return restwrapper It contains the updated instance of HiveTables.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("hivetables")
                       @Valid HiveTables hiveTables, BindingResult bindingResult, Principal principal) {
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
            com.wipro.ats.bdre.md.dao.jpa.HiveTables jpaHiveTables = new com.wipro.ats.bdre.md.dao.jpa.HiveTables();
            jpaHiveTables.setTableId(hiveTables.getTableId());
            jpaHiveTables.setType(hiveTables.getType());
            jpaHiveTables.setTableName(hiveTables.getTableName());
            jpaHiveTables.setBatchIdPartitionCol(hiveTables.getBatchIdPartitionCol());
            jpaHiveTables.setComments(hiveTables.getComments());
            jpaHiveTables.setDbname(hiveTables.getDbName());
            jpaHiveTables.setDdl(hiveTables.getDdl());
            jpaHiveTables.setLocationType(hiveTables.getLocationType());
            hiveTablesDAO.update(jpaHiveTables);

            //HiveTables hiveTablesUpdate = s.selectOne("call_procedures.UpdateHiveTables", hiveTables);

            restWrapper = new RestWrapper(hiveTables, RestWrapper.OK);
            LOGGER.info("Record with ID:" + hiveTables.getTableId() + " updated in HiveTables by User:" + principal.getName() + hiveTables);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * Thsi method calls InsertHiveTables and adds a record of HiveTables. It also validates the values passed.
     *
     * @param hiveTables    Instance of HiveTables.
     * @param bindingResult
     * @return restWrapper It contains an instance of HiveTables just added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("hivetables")
                       @Valid HiveTables hiveTables, BindingResult bindingResult, Principal principal) {

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
            com.wipro.ats.bdre.md.dao.jpa.HiveTables jpaHiveTables = new com.wipro.ats.bdre.md.dao.jpa.HiveTables();
            jpaHiveTables.setTableId(hiveTables.getTableId());
            jpaHiveTables.setType(hiveTables.getType());
            jpaHiveTables.setTableName(hiveTables.getTableName());
            jpaHiveTables.setBatchIdPartitionCol(hiveTables.getBatchIdPartitionCol());
            jpaHiveTables.setComments(hiveTables.getComments());
            jpaHiveTables.setDbname(hiveTables.getDbName());
            jpaHiveTables.setDdl(hiveTables.getDdl());
            jpaHiveTables.setLocationType(hiveTables.getLocationType());
            Integer tableId = hiveTablesDAO.insert(jpaHiveTables);
            hiveTables.setTableId(tableId);
            //HiveTables hiveTablesInsert = s.selectOne("call_procedures.InsertHiveTables", hiveTables);

            restWrapper = new RestWrapper(hiveTables, RestWrapper.OK);
            LOGGER.info("Record with ID:" + hiveTables.getTableId() + " inserted in HiveTables by User:" + principal.getName() + hiveTables);
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
