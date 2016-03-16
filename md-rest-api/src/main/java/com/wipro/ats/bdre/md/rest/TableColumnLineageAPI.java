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


import com.wipro.ats.bdre.ldg.GetDotForTable;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.rest.beans.LineageTabColInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/tabcollineage")
public class TableColumnLineageAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(TableColumnLineageAPI.class);

    /**
     * This method is used to see the relationship between processes linked to a particular table name and/or column name.
     * It also generates the dot string for visualisation of the links.
     *
     * @param lineageTabColInfo
     * @return restWrapper It contains an instance of LineageTabColInfo.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper getByTableCol(@ModelAttribute("lineageTabColInfo")
                              @Valid LineageTabColInfo lineageTabColInfo, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        String colName = lineageTabColInfo.getColName();
        String tableName = lineageTabColInfo.getTableName();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder("<p>Please fix following errors and try again<p><ul>");
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append("<li>");
                errorMessages.append(error.getField());
                errorMessages.append(". Bad value: '");
                errorMessages.append(error.getRejectedValue());
                errorMessages.append("'</li>");
                errorMessages.append("<li>");
                errorMessages.append("Relation corresponding to the column selected may not exist");
                errorMessages.append("'</li>");
            }
            errorMessages.append("</ul>");
            restWrapper = new RestWrapper(errorMessages.toString(), RestWrapper.ERROR);
            return restWrapper;
        }

            if(colName != null && !("".equals(colName.trim()))) {
                LOGGER.info("ColName given: " + colName + "TableName given: " + tableName);
                String[] args = {colName, tableName};
                String dot = new String();
                GetDotForTable getDotForTable = new GetDotForTable();
                dot = getDotForTable.dotGeneratorWithCol(args);
                LOGGER.debug(dot);
                lineageTabColInfo.setDot(dot.toString());
//                lineageTabColInfo.setTableName(tableName);
//                lineageTabColInfo.setColName(colName);
                restWrapper = new RestWrapper(lineageTabColInfo, RestWrapper.OK);
                LOGGER.info("Getting " + tableName + "for column name: " + colName + " Lineage by User:" + principal.getName());
            } else {
                LOGGER.info("TableName given: " + tableName);
                String[] args = {tableName};
                String dot = new String();
                GetDotForTable getDotForTable = new GetDotForTable();
                dot = getDotForTable.dotGeneratorWithTable(args);
                LOGGER.debug(dot);
                lineageTabColInfo.setDot(dot.toString());
//                lineageTabColInfo.setTableName(tableName);
                restWrapper = new RestWrapper(lineageTabColInfo, RestWrapper.OK);
                LOGGER.info("Getting " + tableName + " Lineage by User:" + principal.getName());
            }

        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }

}
