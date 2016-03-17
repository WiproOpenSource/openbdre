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

package com.wipro.ats.bdre.datagen.util;

import com.wipro.ats.bdre.datagen.Column;
import com.wipro.ats.bdre.datagen.Table;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

public class TableUtil {


    private static final Logger LOGGER = Logger.getLogger(TableUtil.class);

    /**
     * @param pid
     * @return
     * @throws IOException
     */
    public Table formTableFromConfig(String pid) {
        Properties tableProp = Config.getTableProperties(pid);
        String tableName = tableProp.getProperty("tableName");
        String tableSchema = tableProp.getProperty("tableSchema");
        Table table = new Table();
        table.setTableName(tableName);
        table.setDelimiter(tableProp.getProperty("separator"));
        String[] columnArray = tableSchema.split(",");
        int index = 0;
        for (String col : columnArray) {
            Column column = new Column();
            String colName = col.split(":")[0];
            if (colName.indexOf('.') != -1) {
                column.setRefTable(colName.split("\\.")[0]);
                column.setColumnName(colName.split("\\.")[1]);
                column.setRefCol(colName.split("\\.")[1]);
            } else {
                column.setColumnName(colName);
            }
            column.setColumnType(col.split(":")[1]);
            column.setColumnGenId(col.split(":")[2]);
            column.setColumnIndex(index++);
            table.getColumns().add(column);
        }
        return table;
    }


    /**
     * @param table
     * @return
     * @throws IOException
     */
    public String getDelimitedTextRow(Table table, String pid) throws IOException {
        StringBuilder tableData = new StringBuilder();


        List<Column> columns = table.getColumns();
        StringBuilder rowData = new StringBuilder();
        for (Column column : columns) {
            if (column.getRefTable() != null) {
                break;
            }
            rowData.append(getData(column.getColumnGenId(), pid));
            rowData.append(table.getDelimiter());
        }
        rowData.deleteCharAt(rowData.length() - 1);
        tableData.append(rowData);
        return tableData.toString();
    }


    /**
     * @param table
     * @return
     */
    public StringBuilder createTableDefinition(Table table) {
        StringBuilder tableDefBuilder = new StringBuilder();
        tableDefBuilder.append("\n");
        tableDefBuilder.append("DROP TABLE IF EXISTS " + table.getTableName() + ";");
        tableDefBuilder.append("\n\n");
        tableDefBuilder.append("CREATE TABLE " + table.getTableName());
        tableDefBuilder.append("\n");
        tableDefBuilder.append("(");
        tableDefBuilder.append("\n");
        List<Column> columns = table.getColumns();
        int columnCount = columns.size();
        for (int col = 0; col < columnCount; col++) {
            Column column = columns.get(col);
            if (col == columnCount - 1)
                tableDefBuilder.append(column.getColumnName() + " " + column.getColumnType());
            else
                tableDefBuilder.append(column.getColumnName() + " " + column.getColumnType() + ",");
            tableDefBuilder.append("\n");
        }
        tableDefBuilder.append(")");
        tableDefBuilder.append("\n");
        tableDefBuilder.append("ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS TEXTFILE;");
        return tableDefBuilder;
    }


    private String getData(String dataGenId, String pid) {
        String genVal = null;
        try {
            Properties dataProperties = Config.getDataProperties(pid);
            String methodName = dataProperties.getProperty("data-gen-id." + dataGenId);
            String methodArgument = dataProperties.getProperty("args." + dataGenId);
            Method method = RandomValueGenerator.class.getMethod(methodName, String.class);
            genVal = (String) method.invoke(null, methodArgument);
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException",e);
        } catch (NoSuchMethodException e) {
            LOGGER.error("NoSuchMethodException"+e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException"+e);
        }
        return genVal;
    }
}
