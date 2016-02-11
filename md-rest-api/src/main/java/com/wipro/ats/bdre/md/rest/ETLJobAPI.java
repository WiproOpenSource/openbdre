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
import com.wipro.ats.bdre.md.beans.ETLJobInfo;
import com.wipro.ats.bdre.md.dao.ETLStepDAO;
import com.wipro.ats.bdre.md.rest.beans.ColumnInfo;
import com.wipro.ats.bdre.md.rest.beans.SerdeProperties;
import com.wipro.ats.bdre.md.rest.beans.TableProperties;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/etl")


public class ETLJobAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ETLJobAPI.class);
    @Autowired
    private ETLStepDAO etlStepDAO;

    /**
     * This method calls proc ETLJob which takes an uuid and creates a parent job and three sub processes namely,
     * file2Raw,Raw2Stage and Stage2Base. The DDLs are populated in HiveTables and finally the tableIds are populated
     * in ETLDriver table.
     *
     * @param etlJobInfo    instance of ETLJobInfo.
     * @param bindingResult
     * @return restWrapper It contains the processIds created.
     */
    @RequestMapping(value = {"/publishetl", "/publishetl/"}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper publish(@ModelAttribute("eTLJob")
                        @Valid ETLJobInfo etlJobInfo, BindingResult bindingResult, Principal principal) {
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
            ETLJobInfo etlJobInfoPublish = new ETLJobInfo();
            ETLJobInfo etlJobInfoWithRowData = etlStepDAO.etlJob(etlJobInfo);
            // etlJobInfoWithRowData is populated with complete row data of ETL Step by calling GetETLStep, whereas etlJobInfo has only UUID and a few other columns like busDomainId, processName etc.
//            etlJobInfoPublish = s.selectOne("call_procedures.publishETL", etlJobInfo);
            LOGGER.debug("job id = " + etlJobInfoWithRowData.getProcessId());

            restWrapper = new RestWrapper(etlJobInfoWithRowData, RestWrapper.OK);
            LOGGER.info("Record with ID:" + etlJobInfoPublish.getUuid() + " published from ETLJob by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls UpdateETLJob and updates the values passed. It also validates the values passed.
     *
     * @param etlJobInfo    Instance of ETLJobInfo.
     * @param bindingResult
     * @return restWrapper Updated instance of ETLJobInfo.
     */
    @RequestMapping(value = {"/main/"}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper updateMain(@ModelAttribute("eTLJob")
                           @Valid ETLJobInfo etlJobInfo, BindingResult bindingResult, Principal principal) {
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
//            List<ETLJobInfo> etlJobInfos = s.selectList("call_procedures.UpdateETLJob", etlJobInfo);
            List<ETLJobInfo> etlJobInfos = etlStepDAO.updateETLJob(etlJobInfo);
            LOGGER.debug(etlJobInfos.get(0).getUuid());
            restWrapper = new RestWrapper(etlJobInfos, RestWrapper.OK);
            LOGGER.info("Record with ID:" + etlJobInfo.getUuid() + " updateMain in ETLJob by User:" + principal.getName() + etlJobInfo);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;
    }

    /**
     * This method calls proc InsertETLJob which adds a record in ETLDriver table as main Job.
     *
     * @param etlJobInfo    Instance of ETLJobInfo.
     * @param bindingResult
     * @return restWrapper It contains updated instance of ETLJobInfo.
     */
    @RequestMapping(value = {"/main/"}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insertMain(@ModelAttribute("eTLJob")
                           @Valid ETLJobInfo etlJobInfo, BindingResult bindingResult, Principal principal) {
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
            UUID idOne = UUID.randomUUID();
            String uuid = idOne.toString();
            etlJobInfo.setUuid(uuid);
//            ETLJobInfo etlJobInfo1 = s.selectOne("call_procedures.InsertETLJob", etlJobInfo);
            ETLJobInfo etlJobInfo1 = etlStepDAO.insertETLJob(etlJobInfo);
            LOGGER.debug(etlJobInfo1.getUuid());
            restWrapper = new RestWrapper(etlJobInfo1, RestWrapper.OK);
            LOGGER.info("Record with ID:" + etlJobInfo1.getUuid() + " InsertMain in ETLJob by User:" + principal.getName() + etlJobInfo1);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteETLJob and deletes a record corresponding to uuid passed.
     *
     * @param uuid
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = {"/main/{uuid}/", "/main/{uuid}"}, method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper deleteMain(@PathVariable("uuid") String uuid, Principal principal,
                           ModelMap model) {
        LOGGER.debug("Inside function " + uuid);
        RestWrapper restWrapper = null;
        try {
            ETLJobInfo etlJobInfo = new ETLJobInfo();
            etlJobInfo.setUuid(uuid);
//            s.delete("call_procedures.DeleteETLJob", etlJobInfo);
            etlStepDAO.deleteETLJob(etlJobInfo);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + etlJobInfo.getUuid() + " deleteMain from ETLJob by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;
    }

    /**
     * This method calls proc GetETLJob and fetches a list of instances of ETLJob.
     *
     * @param
     * @return restWrapper It contains a list of instances of ETLJobInfo.
     */
    @RequestMapping(value = {"/main/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper listMain(@RequestParam(value = "page", defaultValue = "0") int startPage,
                         @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            ETLJobInfo etlJobInfo = new ETLJobInfo();
            etlJobInfo.setPage(startPage);
            etlJobInfo.setPageSize(pageSize);
//            List<ETLJobInfo> etlJobInfos = s.selectList("call_procedures.GetETLJob", etlJobInfo);
            List<ETLJobInfo> etlJobInfos = etlStepDAO.getETLJob(etlJobInfo);
            restWrapper = new RestWrapper(etlJobInfos, RestWrapper.OK);
            LOGGER.info("All records listed from ETLJob by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateETLStep and updates the values passed. It also validates the
     * values passed.
     *
     * @param eTLJobInfo
     * @param bindingResult
     * @return restWrapper Updated instance of ETLJobInfo.
     */
    @RequestMapping(value = {"/sub/"}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper updateSub(@ModelAttribute("eTLStep")
                          @Valid ETLJobInfo eTLJobInfo, BindingResult bindingResult, Principal principal) {
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
            SerdeProperties serdeproperties = new ObjectMapper().readValue(eTLJobInfo.getSerdeProperties(), SerdeProperties.class);
            TableProperties tableproperties = new ObjectMapper().readValue(eTLJobInfo.getTableProperties(), TableProperties.class);
            ColumnInfo columnInfo = new ObjectMapper().readValue(eTLJobInfo.getColumnInfo(), ColumnInfo.class);
            LOGGER.debug("Col props 2 are " + columnInfo.getData()[0].get(0));
            LOGGER.debug("Col props 2 are " + columnInfo.getData()[0].get(1));
            LOGGER.debug("Col props 2 are " + columnInfo.getData()[0].get(2));
            LOGGER.debug("Tab props 2 are " + tableproperties.getData()[0].get(0));
            LOGGER.debug("Tab props 2 are " + tableproperties.getData()[0].get(1));
            LOGGER.debug("Input format" + eTLJobInfo.getInputFormat());
            String cList = "";
            for (int i = 0; i < columnInfo.getData().length; i++) {
                if (("yes").equals(columnInfo.getData()[i].get(2))) {
                    cList += columnInfo.getData()[i].get(0) + " " + columnInfo.getData()[i].get(1) + ",";
                }
            }
            String columnList = cList.substring(0, cList.length() - 1);
            LOGGER.debug("columnList= " + columnList);


            String tList = "";

            for (List<String> t_iterator : tableproperties.getData()) {
                if (t_iterator.get(0) != null && t_iterator.get(1) != null) {
                    tList += "\"" + t_iterator.get(0) + "\" = \"" + t_iterator.get(1) + "\",";
                }
            }
            String vList = "";

            for (int i = 0; i < columnInfo.getData().length; i++) {
                if (("yes").equals(columnInfo.getData()[i].get(2))) {
                    vList += columnInfo.getData()[i].get(0) + ",";
                }
            }
            String viewColumnList = vList.substring(0, vList.length() - 1);

            String rawDDL = "";
            String rawViewDDL = "";
            String baseDDL = "";
            LOGGER.info("inpt format " + eTLJobInfo.getInputFormat());
            if (eTLJobInfo.getInputFormat() == 0) { //TextInputFormat
                String sList = "";
                for (List<String> s_iterator : serdeproperties.getData()) {
                    if (s_iterator.get(0) != null && s_iterator.get(1) != null) {
                        sList += "'" + s_iterator.get(0) + "' = '" + s_iterator.get(1) + "',";
                    }
                }
                String serdePropertyList = sList.substring(0, sList.length() - 1);
                LOGGER.debug("serdePropertyList = " + serdePropertyList);
                rawDDL += "CREATE TABLE IF NOT EXISTS " + eTLJobInfo.getRawDBName() + "." + eTLJobInfo.getRawTableName() + " ( " + columnList + " ) " +
                        " partitioned by ( " + eTLJobInfo.getRawPartitionCol() + " bigint) ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'  WITH SERDEPROPERTIES (" + serdePropertyList + " ) STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat'" +
                        " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'";

                LOGGER.debug("rawDDL= " + rawDDL);
                rawViewDDL += "CREATE VIEW IF NOT EXISTS " + eTLJobInfo.getRawDBName() + "." + eTLJobInfo.getRawViewName() + " as select " + viewColumnList + "," + eTLJobInfo.getRawPartitionCol() + " from " + eTLJobInfo.getRawDBName() + "." + eTLJobInfo.getRawTableName();
                LOGGER.debug("rawViewDDL= " + rawViewDDL);

                baseDDL += "CREATE TABLE IF NOT EXISTS " + eTLJobInfo.getBaseDBName() + "." + eTLJobInfo.getBaseTableName() + " (" + columnList + ") partitioned by (instanceexecid bigint) stored as orc";
                LOGGER.debug("baseDDL= " + baseDDL);

            } else if (eTLJobInfo.getInputFormat() == 1) { //XMLInputFormat
                String sList = "";
                for (List<String> s_iterator : serdeproperties.getData()) {
                    if (s_iterator.get(0) != null && s_iterator.get(1) != null) {
                        sList += "\"" + s_iterator.get(0) + "\" = \"" + s_iterator.get(1) + "\",";
                    }
                }
                String serdePropertyList = sList.substring(0, sList.length() - 1);
                LOGGER.debug("serdePropertyList = " + serdePropertyList);
                String tablePropertyList = tList.substring(0, tList.length() - 1);
                LOGGER.debug("tablePropertyList= " + tablePropertyList);
                rawDDL += "CREATE TABLE IF NOT EXISTS " + eTLJobInfo.getRawDBName() + "." + eTLJobInfo.getRawTableName() + " ( " + columnList + " )" +
                        " partitioned by ( " + eTLJobInfo.getRawPartitionCol() + " bigint) ROW FORMAT SERDE 'com.wipro.ats.bdre.io.xml.XmlSerDe' WITH SERDEPROPERTIES " +
                        " ( " + serdePropertyList + " ) STORED AS INPUTFORMAT 'com.wipro.ats.bdre.io.xml.XmlInputFormat' OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat' " +
                        "TBLPROPERTIES ( " + tablePropertyList + " )";
                LOGGER.debug("rawDDL= " + rawDDL);
                rawViewDDL += "CREATE VIEW IF NOT EXISTS " + eTLJobInfo.getRawDBName() + "." + eTLJobInfo.getRawViewName() + " as select " + viewColumnList + "," + eTLJobInfo.getRawPartitionCol() + " from " + eTLJobInfo.getRawDBName() + "." + eTLJobInfo.getRawTableName();
                LOGGER.debug("rawViewDDL= " + rawViewDDL);

                baseDDL += "CREATE TABLE IF NOT EXISTS " + eTLJobInfo.getBaseDBName() + "." + eTLJobInfo.getBaseTableName() + " (" + columnList + ") partitioned by (instanceexecid bigint) stored as orc";
                LOGGER.debug("baseDDL= " + baseDDL);
            }
            LOGGER.debug("are ehaan pahunche ki naahin");

            eTLJobInfo.setRawTableDDL(rawDDL);
            eTLJobInfo.setRawViewDDL(rawViewDDL);
            eTLJobInfo.setBaseTableDDL(baseDDL);

//            ETLJobInfo eTLJobInfoWithDDLs = s.selectOne("call_procedures.UpdateETLStep", eTLJobInfo);
            etlStepDAO.update(eTLJobInfo);

            LOGGER.debug("job = " + eTLJobInfo.getUuid());
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + eTLJobInfo.getUuid() + " updateSUb in ETLJob by User:" + principal.getName() + eTLJobInfo);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertETLStep and adds a record in ETLStep Table with uuid,busDomainId,processName,description,baseTableName,rawTableName,rawViewName,baseDBName,rawDBName,columnInfo,
     * serdeProperties,tableProperties,rawTableDDL,baseTableDDL,rawViewDDL,rawPartitionCol,dropRaw,enqId,inputFormat.
     *
     * @param eTLJobInfo
     * @param bindingResult
     * @return restWrapper It contains an instance of  ETLJobInfo just added.
     */
    @RequestMapping(value = {"/sub/"}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insertSub(@ModelAttribute("eTLJob")
                          @Valid ETLJobInfo eTLJobInfo, BindingResult bindingResult, Principal principal) {
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


//            ETLJobInfo eTLJobInfos = s.selectOne("call_procedures.InsertETLStep", eTLJobInfo);
            ETLJobInfo eTLJobInfos = etlStepDAO.insert(eTLJobInfo);
            restWrapper = new RestWrapper(eTLJobInfos, RestWrapper.OK);
            LOGGER.info("Record with ID:" + eTLJobInfos.getUuid() + " insertSub in ETLJob by User:" + principal.getName() + eTLJobInfos);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteETLStep and deletes a record corresponding to uuid and
     * serialNumber passed.
     *
     * @param uuid
     * @param serialNumber
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/sub/{uuid}/{serialNumber}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(@PathVariable("uuid") String uuid, @PathVariable("serialNumber") Long serialNumber, Principal principal,
                       ModelMap model) {
        RestWrapper restWrapper = null;
        try {
            ETLJobInfo eTLJobInfo = new ETLJobInfo();
            eTLJobInfo.setSerialNumber(serialNumber);
            eTLJobInfo.setUuid(uuid);
//            s.delete("call_procedures.DeleteETLStep", eTLJobInfo);
            etlStepDAO.delete(eTLJobInfo);

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + eTLJobInfo.getUuid() + " deleted from ETLJob by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetETLStep and fetches a record with uuid and serialNumber.
     *
     * @param
     * @return restWrapper An instance of ETLJobInfo corresponding to uuid nad serialNumber.
     */
    @RequestMapping(value = {"/sub/{uuid}/{serialNumber}/", "/sub/{uuid}/{serialNumber}"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper listSub(@PathVariable("uuid") String uuid, @PathVariable("serialNumber") Long serialNumber, @RequestParam(value = "page", defaultValue = "0") int startPage, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            ETLJobInfo eTLJobInfo = new ETLJobInfo();
            eTLJobInfo.setUuid(uuid);
            eTLJobInfo.setPage(startPage);
            eTLJobInfo.setSerialNumber(serialNumber);
//            eTLJobInfo = s.selectOne("call_procedures.GetETLStep", eTLJobInfo);
            eTLJobInfo = etlStepDAO.get(eTLJobInfo).get(0);
            restWrapper = new RestWrapper(eTLJobInfo, RestWrapper.OK);
            LOGGER.info("All records listed from ETLJob by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetETLStep and fetches a list of instances of ETLJob corresponding to uuid
     * passed.
     *
     * @param uuid
     * @param startPage
     * @return restWrapper It contains a list of instances of  ETLJobInfo which are sub porcesses.
     */
    @RequestMapping(value = {"/sub/{uuid}/", "/sub/{uuid}"}, method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper listSub(@PathVariable("uuid") String uuid, @RequestParam(value = "page", defaultValue = "0") int startPage, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            ETLJobInfo eTLJobInfo = new ETLJobInfo();
            eTLJobInfo.setUuid(uuid);
            eTLJobInfo.setPage(startPage);
            eTLJobInfo.setSerialNumber(null);
//            List<ETLJobInfo> eTLJobInfos = s.selectList("call_procedures.GetETLStep", eTLJobInfo);
            List<ETLJobInfo> eTLJobInfos = etlStepDAO.get(eTLJobInfo);
            restWrapper = new RestWrapper(eTLJobInfos, RestWrapper.OK);
            LOGGER.info("All records listed from ETLJob by User:" + principal.getName());
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




