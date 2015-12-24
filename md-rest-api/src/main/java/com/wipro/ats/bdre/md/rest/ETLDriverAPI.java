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

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.ETLDriver;
import com.wipro.ats.bdre.md.dao.ETLDriverDAO;
import com.wipro.ats.bdre.md.dao.jpa.EtlDriver;
import com.wipro.ats.bdre.md.dao.jpa.HiveTables;
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
@RequestMapping("/etldriver")


public class ETLDriverAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ETLDriverAPI.class);
    @Autowired
    ETLDriverDAO etlDriverDAO;

    /**
     * This method calls proc GetETLDriver which fetches the  raw table,raw view and base table ids.
     * corresponding to eTLProcessId.
     *
     * @param eTLProcessId
     * @return restWrapper It contains an instance of ETLDriver.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer eTLProcessId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            EtlDriver jpaETLDriver = etlDriverDAO.get(eTLProcessId);
            ETLDriver eTLDriver = new ETLDriver();
            if (jpaETLDriver != null) {
                eTLDriver.seteTLProcessId(jpaETLDriver.getEtlProcessId());
                if (jpaETLDriver.getHiveTablesByBaseTableId() != null) {
                    eTLDriver.setBaseTableId(jpaETLDriver.getHiveTablesByBaseTableId().getTableId());
                }
                eTLDriver.setRawTableId(jpaETLDriver.getHiveTablesByRawTableId().getTableId());
                eTLDriver.setRawViewId(jpaETLDriver.getHiveTablesByRawViewId().getTableId());
                if (jpaETLDriver.getInsertType() != null) {
                    eTLDriver.setInsertType(jpaETLDriver.getInsertType().intValue());
                }
                if (jpaETLDriver.getDropRaw()) {
                    eTLDriver.setDropRaw(1);
                } else {
                    eTLDriver.setDropRaw(0);
                }
            }
            //eTLDriver = s.selectOne("call_procedures.GetETLDriver", eTLDriver);

            restWrapper = new RestWrapper(eTLDriver, RestWrapper.OK);
            LOGGER.info("Record with ID:" + eTLProcessId + " selected from ETLDriver by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteETLDriver and deletes a record corresponding to eTLProcessId passed.
     *
     * @param eTLProcessId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Integer eTLProcessId, Principal principal,
            ModelMap model) {

        RestWrapper restWrapper = null;
        try {
            etlDriverDAO.delete(eTLProcessId);
            // s.delete("call_procedures.DeleteETLDriver", eTLDriver);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + eTLProcessId + " deleted from ETLDriver by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListETLDriver which fetches a list of instances of ETLDriver.
     *
     * @param
     * @return restWrapper It contains a list of instances of ETLDriver.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=etlDriverDAO.totalRecordCount().intValue();
            List<EtlDriver> jpaETLDriverList = etlDriverDAO.list(startPage, pageSize);
            List<ETLDriver> eTLDrivers = new ArrayList<ETLDriver>();
            Integer counter=jpaETLDriverList.size();
            for (EtlDriver jpaETLDriver : jpaETLDriverList) {
                ETLDriver eTLDriver = new ETLDriver();
                eTLDriver.seteTLProcessId(jpaETLDriver.getEtlProcessId());
                if (jpaETLDriver.getInsertType() != null) {
                    eTLDriver.setInsertType(jpaETLDriver.getInsertType().intValue());
                }
                if (jpaETLDriver.getHiveTablesByBaseTableId() != null) {
                    eTLDriver.setBaseTableId(jpaETLDriver.getHiveTablesByBaseTableId().getTableId());
                }
                eTLDriver.setRawTableId(jpaETLDriver.getHiveTablesByRawTableId().getTableId());
                eTLDriver.setRawViewId(jpaETLDriver.getHiveTablesByRawViewId().getTableId());
                if (jpaETLDriver.getDropRaw()) {
                    eTLDriver.setDropRaw(1);
                } else {
                    eTLDriver.setDropRaw(0);
                }
                eTLDriver.setCounter(counter);
                eTLDrivers.add(eTLDriver);
            }
            // List<ETLDriver> eTLDrivers = s.selectList("call_procedures.GetETLDrivers", eTLDriver);

            restWrapper = new RestWrapper(eTLDrivers, RestWrapper.OK);
            LOGGER.info("All records listed from ETLDriver by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateETLDriver and updates the values passed. It also validates
     * the values passed.
     *
     * @param eTLDriver     Instance of ETLDriver.
     * @param bindingResult
     * @return restWrapper Updated instance of ETLDriver.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("etldriver")
                       @Valid ETLDriver eTLDriver, BindingResult bindingResult, Principal principal) {

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
            EtlDriver jpaETLDriver = new EtlDriver();
            jpaETLDriver.setEtlProcessId(eTLDriver.geteTLProcessId());
            if (eTLDriver.getBaseTableId() != null) {
                HiveTables baseId = new HiveTables();
                baseId.setTableId(eTLDriver.getBaseTableId());
                jpaETLDriver.setHiveTablesByBaseTableId(baseId);
            }
            if (eTLDriver.getRawTableId() != null) {
                HiveTables rawId = new HiveTables();
                rawId.setTableId(eTLDriver.getRawTableId());
                jpaETLDriver.setHiveTablesByRawTableId(rawId);
            }
            if (eTLDriver.getRawViewId() != null) {
                HiveTables rawViewId = new HiveTables();
                rawViewId.setTableId(eTLDriver.getRawViewId());
                jpaETLDriver.setHiveTablesByRawViewId(rawViewId);
            }

            if (eTLDriver.getInsertType() != null) {
                jpaETLDriver.setInsertType(eTLDriver.getInsertType().shortValue());
            }

            if (eTLDriver.getDropRaw() == 1) {
                jpaETLDriver.setDropRaw(true);
            } else
                jpaETLDriver.setDropRaw(false);

            etlDriverDAO.update(jpaETLDriver);
            eTLDriver.seteTLProcessId(jpaETLDriver.getEtlProcessId());
            // ETLDriver eTLDrivers = s.selectOne("call_procedures.UpdateETLDriver", eTLDriver);

            restWrapper = new RestWrapper(eTLDriver, RestWrapper.OK);
            LOGGER.info("Record with ID:" + eTLDriver.geteTLProcessId() + " updated in ETLDriver by User:" + principal.getName() + eTLDriver);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertETLDriver and adds a record of ETLDriver. It also
     * validates the values passed.
     *
     * @param eTLDriver     Instance of ETLDriver.
     * @param bindingResult
     * @return restWrapper It contains an instance of ETLDriver just added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("etldriver")
                       @Valid ETLDriver eTLDriver, BindingResult bindingResult, Principal principal) {

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
            EtlDriver jpaETLDriver = new EtlDriver();
            jpaETLDriver.setEtlProcessId(eTLDriver.geteTLProcessId());
            LOGGER.info("baseId:" + eTLDriver.getBaseTableId());
            if (eTLDriver.getBaseTableId() != null) {
                HiveTables baseId = new HiveTables();
                baseId.setTableId(eTLDriver.getBaseTableId());
                jpaETLDriver.setHiveTablesByBaseTableId(baseId);
            }
            LOGGER.info("rawId:" + eTLDriver.getRawTableId());
            if (eTLDriver.getRawTableId() != null) {
                HiveTables rawId = new HiveTables();
                rawId.setTableId(eTLDriver.getRawTableId());
                jpaETLDriver.setHiveTablesByRawTableId(rawId);
            }
            LOGGER.info("rawViewId:" + eTLDriver.getRawViewId());
            if (eTLDriver.getRawViewId() != null) {
                HiveTables rawViewId = new HiveTables();
                rawViewId.setTableId(eTLDriver.getRawViewId());
                jpaETLDriver.setHiveTablesByRawViewId(rawViewId);
            }
            LOGGER.info("insertType:" + eTLDriver.getInsertType());
            if (eTLDriver.getInsertType() != null) {
                jpaETLDriver.setInsertType(eTLDriver.getInsertType().shortValue());
            }
            LOGGER.info("drop raw:" + eTLDriver.getDropRaw());

            if (eTLDriver.getDropRaw() == 1) {
                jpaETLDriver.setDropRaw(true);
            } else
                jpaETLDriver.setDropRaw(false);

            etlDriverDAO.insert(jpaETLDriver);
            eTLDriver.seteTLProcessId(jpaETLDriver.getEtlProcessId());
            // ETLDriver eTLDrivers = s.selectOne("call_procedures.InsertETLDriver", eTLDriver);

            restWrapper = new RestWrapper(eTLDriver, RestWrapper.OK);
            LOGGER.info("Record with ID:" + eTLDriver.geteTLProcessId() + " inserted in ETLDriver by User:" + principal.getName() + eTLDriver);
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

