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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


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
     * @param tableName, colName
     * @return restWrapper It contains an instance of LineageInfo.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper getByTableCol(@RequestParam(value = "tableName", defaultValue = "null") String tableName, @RequestParam(value = "colName", defaultValue = "null") String colName, Principal principal) {
        RestWrapper restWrapper = null;
        try {

            if(colName != null && !("".equals(colName.trim()))) {
                LOGGER.info("ColName given: " + colName + "TableName given: " + tableName);
                String[] args = {colName, tableName};
                String dot = new String();
                GetDotForTable getDotForTable = new GetDotForTable();
                dot = getDotForTable.dotGeneratorWithCol(args);
                LineageTabColInfo lineageInfo = new LineageTabColInfo();
                LOGGER.debug(dot);
                lineageInfo.setDot(dot.toString());
                lineageInfo.setTableName(tableName);
                lineageInfo.setColName(colName);
                restWrapper = new RestWrapper(lineageInfo, RestWrapper.OK);
                LOGGER.info("Getting " + tableName + "for column name: " + colName + " Lineage by User:" + principal.getName());
            } else {
                LOGGER.info("TableName given: " + tableName);
                String[] args = {tableName};
                String dot = new String();
                GetDotForTable getDotForTable = new GetDotForTable();
                dot = getDotForTable.dotGeneratorWithTable(args);
                LineageTabColInfo lineageInfo = new LineageTabColInfo();
                LOGGER.debug(dot);
                lineageInfo.setDot(dot.toString());
                lineageInfo.setTableName(tableName);
                restWrapper = new RestWrapper(lineageInfo, RestWrapper.OK);
                LOGGER.info("Getting " + tableName + " Lineage by User:" + principal.getName());
            }

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
            LOGGER.info("Error occured in API" + e);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }

    /**
     * This class is used to access the variables of LineageAPI.
     */
    private class LineageTabColInfo {

        private String tableName;
        private String colName;
        private String dot;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public String getDot() {
            return dot;
        }

        public void setDot(String dot) {
            this.dot = dot;
        }

    }

}
