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

package com.wipro.ats.bdre.md.rest.util;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jayabroto on 06-04-2015.
 */

/**
 * This class is used to access the tables for import and dataload.All the setter and getter methods are
 * present. It also has methods which returns ddls for data load.
 */
public class Table {
    private String srcTableName;
    private String destTableName;
    private String ingestOrNot;
    private String primaryKeyColumn;

    public String getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public void setPrimaryKeyColumn(String primaryKeyColumn) {
        this.primaryKeyColumn = primaryKeyColumn;
    }

    public String getIncrementType() {
        return incrementType;
    }

    public void setIncrementType(String incrementType) {
        this.incrementType = incrementType;
    }

    private String incrementType;
    private Map<String, Column> columns = new LinkedHashMap<String, Column>();


    public String getIngestOrNot() {
        return ingestOrNot;
    }

    public void setIngestOrNot(String ingestOrNot) {
        this.ingestOrNot = ingestOrNot;
    }

    public Table(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    public String getSrcTableName() {
        return srcTableName;
    }

    public void setSrcTableName(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    public String getDestTableName() {
        return destTableName;
    }

    public void setDestTableName(String destTableName) {
        this.destTableName = destTableName;
    }

    public Map<String, Column> getColumns() {
        return columns;
    }

    public Column getColumnByName(String srcColName) {
        return columns.get(srcColName);
    }

    public void addColumn(Column column) {
        columns.put(column.getSrcColumnName(), column);
    }

    /**
     * @return String of Raw table ddl.
     */
    public String getRawTableDDL() {
        String rawTableQuery1 = "CREATE TABLE if not exists %rawTable (";
        String rawTableQuery2 = ") partitioned by (batchid bigint) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE";
        String rawTableQuery = rawTableQuery1;

        String rawTableName = srcTableName;

        rawTableQuery = rawTableQuery.replace("%rawTable", rawTableName);
        Map<String, String> columnMap = new TreeMap<String, String>();

        for (Column columnObj : columns.values()) {

            columnMap.put(columnObj.getSrcColumnIndex(), columnObj.getDestColumnName() + " " + columnObj.getDestDataType());
        }

        for (String rawString : columnMap.values()) {
            rawTableQuery += rawString;
            rawTableQuery += ",";
        }
        rawTableQuery = rawTableQuery.substring(0, rawTableQuery.length() - 1);
        rawTableQuery += rawTableQuery2;
        return rawTableQuery;
    }

    /**
     * @return String of Raw view ddl.
     */
    public String getRawViewDDL() {
        String rawViewQuery1 = "create view if not exists %rawView as select ";
        String rawViewQuery2 = "batchid from %rawTable";
        String rawViewQuery = rawViewQuery1;
        String rawViewName = srcTableName + "_view";

        Map<String, String> columnMap = new TreeMap<String, String>();

        for (Column columnObj : columns.values()) {

            columnMap.put(columnObj.getSrcColumnIndex(), columnObj.getSrcColumnName());
        }

        for (String viewString : columnMap.values()) {
            rawViewQuery += viewString;
            rawViewQuery += ",";
        }
        rawViewQuery += rawViewQuery2;
        rawViewQuery = rawViewQuery.replace("%rawView", rawViewName);
        rawViewQuery = rawViewQuery.replace("%rawTable", srcTableName);
        return rawViewQuery;
    }

    /**
     * @return String of Base table ddl.
     */
    public String getBaseTableDDL() {

        String baseTableQuery1 = "CREATE TABLE if not exists %baseTable (";
        String baseTableQuery2 = "batchid bigint) partitioned by (instanceexecid bigint) stored as orc";
        String baseTableQuery = baseTableQuery1;

        String baseTableName = destTableName + "_base";

        baseTableQuery = baseTableQuery.replace("%baseTable", baseTableName);
        Map<String, String> columnMap = new TreeMap<String, String>();

        for (Column columnObj : columns.values()) {

            columnMap.put(columnObj.getSrcColumnIndex(), columnObj.getDestColumnName() + " " + columnObj.getDestDataType());
        }

        for (String baseString : columnMap.values()) {
            baseTableQuery += baseString;
            baseTableQuery += ",";
        }

        baseTableQuery += baseTableQuery2;
        return baseTableQuery;

    }

    /**
     * @return String of comma separated column list.
     */
    public String getColumnList() {
        Map<String, String> columnMap = new TreeMap<String, String>();
        String columnList = "";
        for (Column columnObj : columns.values()) {

            columnMap.put(columnObj.getSrcColumnIndex(), columnObj.getDestColumnName());
        }

        for (String columnString : columnMap.values()) {
            columnList += columnString;
            columnList += ",";
        }

        return columnList = columnList.substring(0, columnList.length() - 1);

    }

}
