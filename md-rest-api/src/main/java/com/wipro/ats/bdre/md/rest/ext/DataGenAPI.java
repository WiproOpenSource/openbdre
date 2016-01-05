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

package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;

import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by AS294216 on 13-10-2015.
 */
@Controller
@RequestMapping("/datagenproperties")
public class DataGenAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(DataGenAPI.class);
    @Autowired
    private PropertiesDAO propertiesDAO;
    @Autowired
    private ProcessDAO processDAO;

    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapper createJobs(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;
        Process parentProcess = null;
        Process childProcess = null;
        Properties jpaProperties=null;

        parentProcess = Dao2TableUtil.buildJPAProcess(18,"Data gen Parent", "Data Generation Parent", 1);
        childProcess = Dao2TableUtil.buildJPAProcess(14,  "child of Data gen Parent", "child of Data Generation Parent", 0);


        StringBuffer tableSchema = new StringBuffer("");
        //to handle argument id's in sequence if rows are deleted and added in UI
        int fieldArgCounter = 1;
        int fieldTypeCounter = 0;
        int fieldCounter = 1;
        String[] dateContent;
        StringBuffer unifiedDate = new StringBuffer("");
        Date date = null, date2 = null;

        List<Properties> childProps=new ArrayList<>();
        //inserting in properties table
        for (String string : map.keySet()) {
            LOGGER.debug("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            Integer splitIndex = string.lastIndexOf("_");
            String key = string.substring(splitIndex + 1, string.length());
            LOGGER.debug("key is " + key);

            if (string.startsWith("type_genArg")) {
//                String keySplit[] = key.split(".");
//                LOGGER.debug("keySplit length"+keySplit.length);
                fieldTypeCounter = Integer.parseInt(string.substring((string.lastIndexOf(".") + 1), string.length()));
                LOGGER.debug("genArg key Index" + fieldTypeCounter);
                if ((dateContent = map.get(string).split(",")).length == 3) {
                    DateFormat dF = new SimpleDateFormat(dateContent[2]);
                    try {
                        date = dF.parse(dateContent[0]);
                        date2 = dF.parse(dateContent[1]);
                    } catch (ParseException e) {
                        LOGGER.debug("error in Date entry");
                        e.printStackTrace();
                    }
                    unifiedDate.append(date.getTime() + "," + date2.getTime() + "," + dateContent[2]);
                    jpaProperties =Dao2TableUtil.buildJPAProperties("data", "args." + fieldArgCounter, unifiedDate.toString(), "Generated Argument");
                    childProps.add(jpaProperties );
                    jpaProperties  =Dao2TableUtil.buildJPAProperties("data", "data-gen-id." + fieldArgCounter, map.get("type_generatedType." + fieldTypeCounter), "Generated Type");
                    childProps.add(jpaProperties );

                } else {
                    jpaProperties =Dao2TableUtil.buildJPAProperties("data", "args." + fieldArgCounter, map.get(string), "Generated Argument");
                    childProps.add(jpaProperties );
                    jpaProperties =Dao2TableUtil.buildJPAProperties("data", "data-gen-id." + fieldArgCounter, map.get("type_generatedType." + fieldTypeCounter), "Generated Type");
                    childProps.add(jpaProperties );

                }
                fieldArgCounter++;
            } else if (string.startsWith("type_fieldName")) {
                LOGGER.debug("type_fieldName" + tableSchema);
                tableSchema.append(map.get(string) + ":" + fieldCounter++ + ",");
            } else if (string.startsWith("other_numRows")) {
                LOGGER.debug("other_numRows" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties("data", key, map.get(string), "number of rows");
                childProps.add(jpaProperties );
            } else if (string.startsWith("other_numSplits")) {
                LOGGER.debug("other_numSplits" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties("data", key, map.get(string), "number of splits");
                childProps.add(jpaProperties );
            } else if (string.startsWith("other_tableName")) {
                LOGGER.debug("other_tableName" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties("table", key, map.get(string), "Table Name");
                childProps.add(jpaProperties );
            } else if (string.startsWith("other_separator")) {
                LOGGER.debug("other_separator" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties("table", key, map.get(string), "Separator");
                childProps.add(jpaProperties );
            }
        }


        //remove last : in tableSchema String
        tableSchema.deleteCharAt(tableSchema.length() - 1);
        jpaProperties =Dao2TableUtil.buildJPAProperties("table", "tableSchema", tableSchema.toString(), "Table Schema");
        childProps.add(jpaProperties );
        //creating parent and child processes and inserting properties
        List<Process> processList = processDAO.createOneChildJob(parentProcess,childProcess,null,childProps);
        List<com.wipro.ats.bdre.md.beans.table.Process>tableProcessList=Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter=tableProcessList.size();
        for(com.wipro.ats.bdre.md.beans.table.Process process:tableProcessList){
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
        return restWrapper;
    }


    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("generalconfig")
                       @Valid GeneralConfig generalConfig, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Updating jtable for new advanced config");


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

            restWrapper = new RestWrapper(generalConfig, RestWrapper.OK);
            LOGGER.info("Record with configGroup:" + generalConfig.getConfigGroup() + " inserted in Jtable by User:" + principal.getName() + generalConfig);
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