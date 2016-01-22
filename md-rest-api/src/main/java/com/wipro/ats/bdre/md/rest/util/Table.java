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


import org.apache.derby.impl.jdbc.LOBInputStream;

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
     * @return String of Raw table columns and data types.
     */
    public String getRawTableColumnAndDataType() {

        Map<String, String> columnMap = new TreeMap<String, String>();
        String rawColumnName = "";
        for (Column columnObj : columns.values()) {

            columnMap.put(columnObj.getSrcColumnIndex(), columnObj.getDestColumnName() + " " + columnObj.getDestDataType());
        }

        for (String rawString : columnMap.values()) {
            rawColumnName += rawString;
            rawColumnName += ",";
        }
        rawColumnName = rawColumnName.substring(0, rawColumnName.length() - 1);
        return rawColumnName;
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
