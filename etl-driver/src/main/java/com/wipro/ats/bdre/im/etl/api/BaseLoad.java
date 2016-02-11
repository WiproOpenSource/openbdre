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

package com.wipro.ats.bdre.im.etl.api;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishnu on 12/17/14.
 * Modified by arijit
 */
public class BaseLoad extends ETLBase {

    private static final Logger LOGGER = Logger.getLogger(BaseLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"ied", "instance-exec-id", " Process id of ETLDriver"}
    };


    public void execute(String[] params) {

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);

        String processId = commandLine.getOptionValue("process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        loadBaseHiveTableInfo(processId);
        //Getting core table information
        String baseTableName = baseTable;
        String baseTableDbName = baseDb;
      //  String baseTableDdl = getBaseTable().getDdl();

        processStage(baseTableDbName, baseTableName, instanceExecId);
    }


    private void processStage(String dbName, String baseTableName, String instanceExecId) {
        try {

            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name"));
            FileSystem fs = FileSystem.get(conf);
            String stageTableName =  baseTableName + "_" + instanceExecId;
            //Stage table is the source and base table is the destination

            List<String> stagePartitions = new ArrayList<String>();
            ResultSet resultSet = getHiveJDBCConnection(dbName).createStatement().executeQuery("show partitions " + stageTableName);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                String columnValue = resultSet.getString(1);
                LOGGER.info(columnValue);
                stagePartitions.add(columnValue);
            }
            List<String> basePartitions = new ArrayList<String>();
            String wareHouse = "";
            ResultSet resultSetForWareHouse = getHiveJDBCConnection(dbName).createStatement().executeQuery("set hive.metastore.warehouse.dir");
            ResultSetMetaData rsmdWareHouse = resultSet.getMetaData();
            while (resultSetForWareHouse.next()) {
                wareHouse = resultSetForWareHouse.getString(1);
            }
            LOGGER.info("ware house dir is " + wareHouse.split("=")[1]);

            String stageTableLocation = IMConfig.getProperty("common.default-fs-name")+ wareHouse.split("=")[1] +"/"+dbName.toLowerCase()+".db/"+stageTableName.toLowerCase()+"/";
            LOGGER.info("stageTableLocation = " + stageTableLocation);
            String baseTableLocation = IMConfig.getProperty("common.default-fs-name")+wareHouse.split("=")[1] +"/" +dbName.toLowerCase()+".db/"+baseTableName.toLowerCase()+"/";
            LOGGER.info("baseTableLocation = " + baseTableLocation);
            for (String stagePartition : stagePartitions)
            {
                String relativePartitionPath=stagePartition+"/";
                LOGGER.info("relativePartitionPath = " + relativePartitionPath);
                Path srcPath=new Path(stageTableLocation +stagePartition.toLowerCase() +"/");
                LOGGER.info("srcPath = " + srcPath);
                String basePartition = (stagePartition.lastIndexOf("/")==-1)?"":stagePartition.substring(0,stagePartition.lastIndexOf("/"));
                Path destPath=new Path(baseTableLocation +basePartition.toLowerCase() );
                LOGGER.info("destPath = " + destPath);
                LOGGER.info("Will move partitions from " + srcPath + " to "+destPath);
                //if the parent destination directory(upper level partition) does not exist create it
                fs.mkdirs(destPath);
                //Now do the rename
                fs.rename(srcPath,destPath);
                Connection baseCon = getHiveJDBCConnection(dbName);
                Statement baseConStatement = baseCon.createStatement();
                String query="alter table "+ baseTableName+" add partition("+stagePartition.replace("/",",")+")";
                LOGGER.info("query = " + query);
                baseConStatement.executeUpdate(query);
            }
            //add partitions in the core table.
            //  getMetaStoreClient().add_partitions(basePartitions);

            // LOGGER.info("Deleting stage table");
            //  getMetaStoreClient().dropTable(dbName,stageTableName);
            LOGGER.info("BaseLoad completed for " + baseTableName);
        } catch (Exception e) {
            LOGGER.info("Exception" + e);
            throw new ETLException(e);
        }

    }

}