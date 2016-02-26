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

package com.wipro.ats.bdre.im.etl.api.base;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by arijit on 12/28/14.
 */
public abstract class ETLBase extends BaseStructure{
    private static final Logger LOGGER = Logger.getLogger(ETLBase.class);
    private static final String TABLEDB = "table_db";
    private static final String TABLENAME = "table_name";


    protected String rawLoad;
    protected String stgLoad;
    protected String baseLoad;
    protected String rawTable;
    protected String rawDb;
    protected String stgView;
    protected String stgDb;
    protected String baseTable;
    protected String baseDb;


    protected void loadRawHiveTableInfo(String processId){
        rawLoad = processId;
        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(rawLoad, "raw-table");
        rawTable = rawPropertiesOfTable.getProperty(TABLENAME);
        rawDb = rawPropertiesOfTable.getProperty(TABLEDB);
    }
    protected void loadStageHiveTableInfo(String processId){
        stgLoad = processId;
        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(stgLoad, "raw-table");
        stgView=rawPropertiesOfTable.getProperty("table_name_raw")+"_view";
        stgDb=rawPropertiesOfTable.getProperty("table_db_raw");
        java.util.Properties basePropertiesOfTable = getPropertiesOfRawTable.getProperties(stgLoad, "base-table");
        baseTable = basePropertiesOfTable.getProperty(TABLENAME);
        baseDb = basePropertiesOfTable.getProperty(TABLEDB);
    }
    protected void loadBaseHiveTableInfo(String processId){
        baseLoad = processId;
        GetProperties getPropertiesOfBaseTable = new GetProperties();
        java.util.Properties basePropertiesOfTable = getPropertiesOfBaseTable.getProperties(baseLoad, "base-table");
        baseTable = basePropertiesOfTable.getProperty(TABLENAME);
        baseDb = basePropertiesOfTable.getProperty(TABLEDB);
    }
    protected Connection getHiveJDBCConnection(String dbName){
        try {
            Class.forName(IMConstant.HIVE_DRIVER_NAME);
            String hiveConnection = IMConfig.getProperty("etl.hive-connection");
            String hiveUser = IMConfig.getProperty("etl.hive-jdbcuser");
            String hivePassword = IMConfig.getProperty("etl.hive-jdbcpassword");
            Connection con = DriverManager.getConnection(hiveConnection + "/" + dbName, hiveUser, hivePassword);
            con.createStatement().execute("set hive.exec.dynamic.partition.mode=nonstrict");
            con.createStatement().execute("set hive.exec.dynamic.partition=true");
            con.createStatement().execute("set hive.exec.max.dynamic.partitions.pernode=1000");
            return con;
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
            throw new ETLException(e);
        } catch (SQLException e) {
            LOGGER.error(e);
            throw new ETLException(e);
        }

    }
    HiveMetaStoreClient hClient =null;
    protected HiveMetaStoreClient getMetaStoreClient()
    {
        if(hClient ==null) {
            try {
                HiveConf hiveConf = new HiveConf();
                hiveConf.set("hive.metastore.uris", IMConfig.getProperty("etl.hive-metastore-uris"));
                hiveConf.set("hive.exec.dynamic.partition.mode", "nonstrict");
                hiveConf.set("hive.exec.dynamic.partition", "true");
                hiveConf.set("hive.exec.max.dynamic.partitions.pernode", "1000");
                hClient = new HiveMetaStoreClient(hiveConf);

            } catch (MetaException e) {
                LOGGER.error(e);
                throw new ETLException(e);
            }
        }
        return hClient;
    }


}
