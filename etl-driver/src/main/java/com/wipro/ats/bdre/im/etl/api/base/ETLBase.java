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
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
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


    protected ProcessInfo rawLoad;
    protected ProcessInfo stgLoad;
    protected ProcessInfo baseLoad;
    protected String rawTable;
    protected String rawDb;
    protected String stgView;
    protected String stgDb;
    protected String baseTable;
    protected String baseDb;
    private String processId;

    protected void init(String processId){
        loadHiveTableInfo(processId);
    }
    private void loadHiveTableInfo(String processId){
        String[] processParams = {"-p", processId};
        GetProcess getProcess = new GetProcess();
        List<ProcessInfo> subProcessList=getProcess.getSubProcesses(processParams);
       for(ProcessInfo subprocess:subProcessList){
           if(subprocess.getProcessTypeId()==6) rawLoad = subprocess;
           else if(subprocess.getProcessTypeId()==7) stgLoad = subprocess;
           else if (subprocess.getProcessTypeId()==8) baseLoad = subprocess;
           else throw new ETLException("Not a valid hive load process");
       }
        GetProperties getPropertiesOfRawTable = new GetProperties();
        java.util.Properties rawPropertiesOfTable = getPropertiesOfRawTable.getProperties(rawLoad.getProcessId().toString(), "raw-table");
        rawTable = rawPropertiesOfTable.getProperty("table-name");
        rawDb = rawPropertiesOfTable.getProperty("table-db");
        stgView = rawTable+"_view";
        stgDb=rawDb;

        GetProperties getPropertiesOfBaseTable = new GetProperties();
        java.util.Properties basePropertiesOfTable = getPropertiesOfBaseTable.getProperties(baseLoad.getProcessId().toString(), "base-table");
        baseTable = basePropertiesOfTable.getProperty("table-name");
        baseDb = basePropertiesOfTable.getProperty("table-db");
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
 /*   protected GetHiveTablesInfo getRawTable() {

        return rawTable;
    }


    protected GetHiveTablesInfo getBaseTable() {
        return baseTable;
    }


    protected GetHiveTablesInfo getRawView() {
        return rawView;
    }*/


}
