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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.IntermediateInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.dao.HistoryDataImportDAO;
import com.wipro.ats.bdre.md.dao.IntermediateDAO;
import com.wipro.ats.bdre.md.dao.jpa.Intermediate;
import com.wipro.ats.bdre.md.dao.jpa.IntermediateId;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.Column;
import com.wipro.ats.bdre.md.rest.util.Table;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by arijit on 4/4/15.
 */

/**
 * This class fetches the parameters required for data import from UI and then stores the parameters in the
 * intermediate table and also fetches ddls required for the Data Load.
 */
@Controller
@RequestMapping("/dataimport")
@Scope("session")
public class DataImportAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(DataImportAPI.class);
    private static final String INGESTONLY = "ingestOnly_";
    private static final String INCREMENTTYPE = "incrementType_";
    private static final String INCREMENTCOLUMN = "incrementColumn_";
    private static final String PRIMARYKEYCOLUMN = "primaryKeyColumn_";
    private static final String ESCAPESEQ = "\\..+";
    private Connection conn;
    @Autowired
    private HistoryDataImportDAO historyDataImportDAO;
    @Autowired
    private IntermediateDAO intermediateDAO;

    @RequestMapping(value = "/createjobs", method = {RequestMethod.POST})
    @ResponseBody public
    RestWrapper createJobs(HttpServletRequest request, Principal principal) {
        String rawDBName = request.getParameter("common_rawDBHive");
        String baseDBName = request.getParameter("common_baseDBHive");
        String dbURL = request.getParameter("common_dbURL");
        String driverName = request.getParameter("common_dbDriver");
        String dbUser = request.getParameter("common_dbUser");
        String dbPassword = request.getParameter("common_dbPassword");
        String dbSchema = request.getParameter("common_dbSchema");
        String busDomainId = request.getParameter("common_busDomainId");
        String workflowTypeId = request.getParameter("common_workflowTypeId");
        String processName = request.getParameter("common_processName");
        String processDescription = request.getParameter("common_processDescription");

        String uuid = UUID.randomUUID().toString();
        //this goes to hive_tables , same for all tables being imported
        pushToIntermediate(uuid, "baseHiveDB", baseDBName);
        pushToIntermediate(uuid,"rawHiveDB",rawDBName);
        //To be pushed directly into properties table

        pushToIntermediate(uuid, "db", dbURL);
        pushToIntermediate(uuid, "driver", driverName);
        pushToIntermediate(uuid, "username", dbUser);
        pushToIntermediate(uuid, "password", dbPassword);
        pushToIntermediate(uuid, "dbSchema", dbSchema);
        pushToIntermediate(uuid, "busdomainid", busDomainId);
        pushToIntermediate(uuid, "workflowTypeId", workflowTypeId);
        pushToIntermediate(uuid, "processName",processName);
        pushToIntermediate(uuid, "processDescription",processDescription);


        //pushing other non variable request params

        Map<String, Table> tables = buildTablesFromMap(request.getParameterMap());
        Integer count = 0;

        for (Table table : tables.values()) {
            count++;

            pushToIntermediate(uuid, "rawColumnsAndDataTypes_" + count, table.getRawTableColumnAndDataType());
            LOGGER.info("column values are " + table.getRawTableColumnAndDataType());
            pushToIntermediate(uuid, "baseTableName_" + count, table.getDestTableName() );
            pushToIntermediate(uuid, "rawTableName_" + count, table.getSrcTableName());
            pushToIntermediate(uuid, "columnList_" + count, table.getColumnList());

            LOGGER.info("value is " + table.getIngestOrNot());
            pushToIntermediate(uuid, INGESTONLY + count, table.getIngestOrNot());
            pushToIntermediate(uuid, INCREMENTTYPE + count, table.getIncrementType());
            pushToIntermediate(uuid, INCREMENTCOLUMN + count, table.getIncrementColumn());
            pushToIntermediate(uuid, PRIMARYKEYCOLUMN + count, table.getPrimaryKeyColumn());


        }
        pushToIntermediate(uuid, "numberOfTables" , count.toString());

        RestWrapper restWrapper = null;
        try {
            IntermediateInfo intermediateInfo = new IntermediateInfo();
            intermediateInfo.setUuid(uuid);
            LOGGER.info("uuid is = " + uuid);
            //Calling proc HistoryDataImport which creates the data import job and data load job
            List<Process> process = historyDataImportDAO.historyDataImport(intermediateInfo,principal.getName());
            LOGGER.debug("process ids are :" + process.size());
            restWrapper = new RestWrapper(process, RestWrapper.OK);

        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    //Fetching all the tables from connected database and populating the  RdbmsEntity class
    @RequestMapping(value = "/tables", method = {RequestMethod.GET})
    @ResponseBody public
    RestWrapper getTableList(@RequestParam("common_dbURL") String connectionURL,
                             @RequestParam("common_dbUser") String dbUser,
                             @RequestParam("common_dbPassword") String dbPassword,
                             @RequestParam("common_dbDriver") String driverClass,
                             @RequestParam("common_dbSchema") String dbSchema
                             ) {
        RestWrapper restWrapper = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(connectionURL, dbUser, dbPassword);

            ResultSet result = conn.getMetaData().getTables(null,dbSchema, null, new String[]{"TABLE","VIEW"} );

            List<RdbmsEntity> rdbmsEntities = new ArrayList<RdbmsEntity>();

            while (result.next()) {
                RdbmsEntity rdbmsEntity = new RdbmsEntity();
                rdbmsEntity.setEntityName(result.getString(3));
                LOGGER.debug("table is " + result.getString(3));
                rdbmsEntity.setTable(true);
                String primaryKey = "";
                ResultSet primaryKeys = conn.getMetaData().getPrimaryKeys(null, null, result.getString(3));
                while (primaryKeys.next()) {
                    primaryKey = primaryKeys.getString(4);
                    LOGGER.debug("primary key is " + primaryKey);
                }
                rdbmsEntity.setPrimaryKeyColumn(primaryKey);
                rdbmsEntities.add(rdbmsEntity);
                primaryKeys.close();
            }
            restWrapper = new RestWrapper(rdbmsEntities, RestWrapper.OK);
            result.close();

        } catch (Exception e) {
            LOGGER.error("error occured :" + e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    //Fetching all the columns of a particular table from connected database and populating the  RdbmsEntity class
    @RequestMapping(value = "/tables/{tableName}", method = {RequestMethod.GET})
    @ResponseBody public
    List<RdbmsEntity> getColumnsForTable(
            @PathVariable("tableName") String tableName
    ) {
        try {
            ResultSet result = conn.getMetaData().getColumns(null, null, tableName, null);
            List<RdbmsEntity> columns = new ArrayList<RdbmsEntity>();


            while (result.next()) {
                RdbmsEntity column = new RdbmsEntity();
                column.setParentEntityName(tableName);
                column.setEntityName(result.getString(4));
                column.setDtype(result.getString("TYPE_NAME"));
                column.setColumnId(result.getString("ORDINAL_POSITION"));
                columns.add(column);
            }

            return columns;
        } catch (SQLException e) {
            LOGGER.error("error occured :" + e);
            return Collections.emptyList();
        }
    }



    public class RdbmsEntity {

        private String entityName;
        private boolean table;
        private String columnId;
        private String dtype;
        private String parentEntityName;
        private String primaryKeyColumn;

        @JsonProperty("primarykey")
        public String getPrimaryKeyColumn() {
            return primaryKeyColumn;
        }

        public void setPrimaryKeyColumn(String primaryKeyColumn) {
            this.primaryKeyColumn = primaryKeyColumn;
        }

        @JsonProperty("title")
        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        @JsonProperty("folder")
        public boolean isTable() {
            return table;
        }

        public void setTable(boolean table) {
            this.table = table;
        }

        public String getKey() {
            if (table) {
                return entityName;
            } else {
                return parentEntityName + "." + entityName;
            }
        }

        public String getColumnId() {
            return columnId;
        }

        public void setColumnId(String columnId) {
            this.columnId = columnId;
        }

        public boolean isLazy() {
            return table;

        }

        public String getDtype() {
            return dtype;
        }

        public void setDtype(String dtype) {
            this.dtype = dtype;
        }

        public void setParentEntityName(String parentEntityName) {
            this.parentEntityName = parentEntityName;
        }
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }

    /**
     * This method is used to build instance of Table from the Map of key value pairs.
     *
     * @param params Map of key as String and Value as array of String.
     * @return Map of Tables
     */
    // Default visibility is set for the test class
    Map<String, Table> buildTablesFromMap(Map<String, String[]> params) {
        Map<String, Table> tables = new TreeMap<String, Table>();

        List<String> keyList = new ArrayList<String>();
        keyList.addAll(params.keySet());

        Collections.sort(keyList);
        Collections.reverse(keyList);

        // build table

        for (String key : keyList) {
            String value = params.get(key)[0];
            LOGGER.debug("key = " + key + " value=" + value);
            if (key.startsWith("srcTableName_")) {
                Table table = new Table(value);
                tables.put(table.getSrcTableName(), table);
            } else if (key.startsWith(INGESTONLY)) {
                String srcTableName = key.replaceAll(INGESTONLY, "");
                Table table = tables.get(srcTableName);
                table.setIngestOrNot(value);
            } else if (key.startsWith(PRIMARYKEYCOLUMN)) {
                String srcTableName = key.replaceAll(PRIMARYKEYCOLUMN, "");
                Table table = tables.get(srcTableName);
                table.setPrimaryKeyColumn(value);
            } else if (key.startsWith(INCREMENTTYPE)) {
                String srcTableName = key.replaceAll(INCREMENTTYPE, "");
                Table table = tables.get(srcTableName);
                table.setIncrementType(value);
            } else if (key.startsWith(INCREMENTCOLUMN)) {
                String srcTableName = key.replaceAll(INCREMENTCOLUMN, "");
                Table table = tables.get(srcTableName);
                table.setIncrementColumn(value);
            } else if (key.startsWith("destTableName_")) {
                String srcTableName = key.replaceAll("destTableName_", "");
                Table table = tables.get(srcTableName);
                table.setDestTableName(value);
            } else if (key.startsWith("srcColumnName_")) {
                String srcColName = value;
                String srcTableName = key.replaceAll("srcColumnName_", "").replaceAll(ESCAPESEQ, "");
                Column column = new Column(srcColName);
                Table table = tables.get(srcTableName);
                table.addColumn(column);
            } else if (key.startsWith("srcColumnDType_")) {
                String srcColDType = value;
                String srcTableName = key.replaceAll("srcColumnDType_", "").replaceAll(ESCAPESEQ, "");
                String srcColName = key.replaceAll("srcColumnDType_.+\\.", "");
                Table table = tables.get(srcTableName);
                Column column = table.getColumnByName(srcColName);
                column.setSrcDataType(srcColDType);

            } else if (key.startsWith("srcColumnIndex_")) {
                String srcColIndex = value;
                String srcTableName = key.replaceAll("srcColumnIndex_", "").replaceAll(ESCAPESEQ, "");
                String srcColName = key.replaceAll("srcColumnIndex_.+\\.", "");
                Table table = tables.get(srcTableName);
                Column column = table.getColumnByName(srcColName);
                column.setSrcColumnIndex(srcColIndex);
            } else if (key.startsWith("hiveDataType_")) {
                String destColDType = value;
                String srcTableName = key.replaceAll("hiveDataType_", "").replaceAll(ESCAPESEQ, "");
                String srcColName = key.replaceAll("hiveDataType_.+\\.", "");
                Table table = tables.get(srcTableName);
                Column column = table.getColumnByName(srcColName);
                column.setDestDataType(destColDType);
            } else if (key.startsWith("destColumnName_")) {
                String destColName = value;
                String srcTableName = key.replaceAll("destColumnName_", "").replaceAll(ESCAPESEQ, "");
                String srcColName = key.replaceAll("destColumnName_.+\\.", "");
                Table table = tables.get(srcTableName);
                Column column = table.getColumnByName(srcColName);
                column.setDestColumnName(destColName);
            }

        }

        return tables;
    }

    /**
     * This method adds an entry  in the intermediate table.
     *
     * @param uuid Unique id given to a particular set of properties.
     * @param key  String charaterizing the property.
     * @param val  String representing the value of the property.
     * @return nothing.
     */
    private void pushToIntermediate(String uuid, String key, String val) {
        try {

            Intermediate intermediate = new Intermediate();
            IntermediateId intermediateId = new IntermediateId();
            intermediateId.setInterKey(key);
            intermediateId.setUuid(uuid);
            intermediate.setId(intermediateId);
            intermediate.setInterValue(val);

            LOGGER.debug(key + ":" + val);
            intermediateDAO.insert(intermediate);

        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
