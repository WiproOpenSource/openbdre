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

import com.wipro.ats.bdre.md.rest.util.Table;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jayabroto on 06-04-2015.
 */
public class DataImportAPITest {

    @org.junit.Test
    public void testBuildTablesFromMap() throws Exception {
        Map<String, String[]> params = new HashMap<String, String[]>();
        String[] driver = {"com.mysql.Driver"};
        String[] base = {"base"};
        String[] password = {"zaq1xsw2"};
        String[] url = {"mysql://xyz"};
        String[] user = {"admin"};
        String[] t0hivecol0 = {"hivecol0"};
        String[] t0col1 = {"col1"};
        String[] t0col2 = {"col2"};
        String[] t1col0 = {"col0"};
        String[] t0name = {"MyHiveTable0"};
        String[] t1name = {"MyTable1"};
        String[] t2name = {"MyTable2"};
        String[] intType = {"int"};
        String[] varType = {"VARCHAR"};
        String[] t0names = {"MyTable0"};
        String[] one = {"1"};
        String[] two = {"2"};
        String[] three = {"3"};


        params.put("common_dbDriver", driver);
        params.put("common_dbHive", base);
        params.put("common_dbPassword", password);
        params.put("common_dbURL", url);
        params.put("common_dbUser", user);
        params.put("destColumnName_MyTable0.col0", t0hivecol0);
        params.put("destColumnName_MyTable0.col1", t0col1);
        params.put("destColumnName_MyTable0.col2", t0col2);
        params.put("destColumnName_MyTable1.col0", t1col0);
        params.put("destColumnName_MyTable1.col1", t0col1);
        params.put("destColumnName_MyTable1.col2", t0col2);
        params.put("destColumnName_MyTable2.col2", t0col2);
        params.put("destTableName_MyTable0", t0name);
        params.put("destTableName_MyTable1", t1name);
        params.put("destTableName_MyTable2", t2name);
        params.put("hiveDataType_MyTable0.col0", intType);
        params.put("hiveDataType_MyTable0.col1", intType);
        params.put("hiveDataType_MyTable0.col2", intType);
        params.put("hiveDataType_MyTable1.col0", intType);
        params.put("hiveDataType_MyTable1.col1", intType);
        params.put("hiveDataType_MyTable1.col2", intType);
        params.put("hiveDataType_MyTable2.col2", intType);
        params.put("srcColumnDType_MyTable0.col0", varType);
        params.put("srcColumnDType_MyTable0.col1", varType);
        params.put("srcColumnDType_MyTable0.col2", varType);
        params.put("srcColumnDType_MyTable1.col0", varType);
        params.put("srcColumnDType_MyTable1.col1", varType);
        params.put("srcColumnDType_MyTable1.col2", varType);
        params.put("srcColumnDType_MyTable2.col2", varType);
        params.put("srcColumnName_MyTable0.col0", t1col0);
        params.put("srcColumnIndex_MyTable0.col0", two);
        params.put("srcColumnName_MyTable0.col1", t0col1);
        params.put("srcColumnIndex_MyTable0.col1", one);
        params.put("srcColumnName_MyTable0.col2", t0col2);
        params.put("srcColumnIndex_MyTable0.col2", three);
        params.put("srcColumnName_MyTable1.col0", t1col0);
        params.put("srcColumnIndex_MyTable1.col0", one);
        params.put("srcColumnName_MyTable1.col1", t0col1);
        params.put("srcColumnIndex_MyTable1.col1", two);
        params.put("srcColumnName_MyTable1.col2", t0col2);
        params.put("srcColumnIndex_MyTable1.col2", three);
        params.put("srcColumnName_MyTable2.col2", t0col2);
        params.put("srcColumnIndex_MyTable2.col2", one);
        params.put("srcTableName_MyTable0", t0names);
        params.put("srcTableName_MyTable1", t1name);
        params.put("srcTableName_MyTable2", t2name);

        DataImportAPI dataImportAPI = new DataImportAPI();
        Map<String, Table> tables = dataImportAPI.buildTablesFromMap(params);
        for (Table table : tables.values()) {
            System.out.println("table.getBaseTableDDL() = " + table.getColumnList());
            System.out.println("table.getBaseTableDDL() = " + table.getRawTableColumnAndDataType());

            System.out.println("table.getColumns().values().getSrcColumnName() = " + table.getColumnList());

        }
    }
}