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
import com.wipro.ats.bdre.md.api.GetHiveTables;
import com.wipro.ats.bdre.md.beans.GetHiveTablesInfo;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by arijit on 12/28/14.
 */
public abstract class ETLBase extends BaseStructure{
    private static final Logger LOGGER = Logger.getLogger(ETLBase.class);


    private GetHiveTablesInfo rawTable;
    private GetHiveTablesInfo baseTable;
    private GetHiveTablesInfo rawView;
    private String processId;

    protected void init(String processId){
        loadHiveTableInfo(processId);
    }
    private void loadHiveTableInfo(String processId){
        String[] hiveTableParams = {"-p", processId};
        GetHiveTables getHiveTables = new GetHiveTables();
        List<GetHiveTablesInfo> hiveTablesInfos = getHiveTables.execute(hiveTableParams);
       //TODO: THIS logic is wrong. The stageTable , view and coreTable may not be in order.
        rawTable =hiveTablesInfos.get(0);
        rawView =hiveTablesInfos.get(2);
        baseTable =hiveTablesInfos.get(1);
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
    HiveMetaStoreClient hclient =null;
    protected HiveMetaStoreClient getMetaStoreClient()
    {
        if(hclient ==null) {
            try {
                HiveConf hiveConf = new HiveConf();
                hiveConf.set("hive.metastore.uris", IMConfig.getProperty("etl.hive-metastore-uris"));
                hiveConf.set("hive.exec.dynamic.partition.mode", "nonstrict");
                hiveConf.set("hive.exec.dynamic.partition", "true");
                hiveConf.set("hive.exec.max.dynamic.partitions.pernode", "1000");
                hclient = new HiveMetaStoreClient(hiveConf);

            } catch (MetaException e) {
                LOGGER.error(e);
                throw new ETLException(e);
            }
        }
        return hclient;
    }
    protected GetHiveTablesInfo getRawTable() {

        return rawTable;
    }


    protected GetHiveTablesInfo getBaseTable() {
        return baseTable;
    }


    protected GetHiveTablesInfo getRawView() {
        return rawView;
    }


}
