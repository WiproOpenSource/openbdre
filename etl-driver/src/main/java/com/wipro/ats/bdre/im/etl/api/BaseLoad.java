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
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.HiveSchemaEvolution;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

/**
 * Created by vishnu on 12/17/14.
 * Modified by arijit
 */
public class BaseLoad extends ETLBase {
    private static final String DEFAULTFSNAME = "common.default-fs-name";
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

        processStage(baseTableDbName, baseTableName, instanceExecId, processId);
    }


    private static String getQuery(String name){
        return "show partitions "+name;
    }
    private void processStage(String dbName, String baseTableName, String instanceExecId, String processId) {
        try {
            checkHiveSchemaEvolution(Integer.parseInt(processId),dbName,baseTableName);
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", IMConfig.getProperty(DEFAULTFSNAME));
            FileSystem fs = FileSystem.get(conf);
            String stageTableName =  baseTableName + "_" + instanceExecId;
            //Stage table is the source and base table is the destination

            List<String> stagePartitions = new ArrayList<String>();
            ResultSet resultSet = getHiveJDBCConnection(dbName).createStatement().executeQuery(BaseLoad.getQuery(stageTableName));
            while (resultSet.next()) {
                String columnValue = resultSet.getString(1);
                LOGGER.info(columnValue);
                stagePartitions.add(columnValue);
            }

            String wareHouse = "";
            ResultSet resultSetForWareHouse = getHiveJDBCConnection(dbName).createStatement().executeQuery("set hive.metastore.warehouse.dir");
            while (resultSetForWareHouse.next()) {
                wareHouse = resultSetForWareHouse.getString(1);
            }
            LOGGER.info("ware house dir is " + wareHouse.split("=")[1]);

            String stageTableLocation = IMConfig.getProperty(DEFAULTFSNAME)+ wareHouse.split("=")[1] +"/"+dbName.toLowerCase()+".db/"+stageTableName.toLowerCase()+"/";
            LOGGER.info("stageTableLocation = " + stageTableLocation);
            String baseTableLocation = IMConfig.getProperty(DEFAULTFSNAME)+wareHouse.split("=")[1] +"/" +dbName.toLowerCase()+".db/"+baseTableName.toLowerCase()+"/";
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

                GetGeneralConfig generalConfig = new GetGeneralConfig();
                String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processId));

                /*String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
                String addSerde = "add jar "+serdePath;
                baseConStatement.execute(addSerde);*/

                String query="alter table "+ baseTableName+" add partition("+stagePartition.replace("/",",")+")";
                LOGGER.info("query = " + query);
                baseConStatement.executeUpdate(query);
            }
            //add partitions in the core table.
            LOGGER.info("BaseLoad completed for " + baseTableName);
        } catch (Exception e) {
            LOGGER.info("Exception" + e);
            throw new ETLException(e);
        }

    }

    private void checkHiveSchemaEvolution(Integer processId,String dbName,String tableName) {
        try {
            LOGGER.info("inside checkHiveSchemaEvolution");
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();
            GetProperties getProperties = new GetProperties();
            Properties appendedColumnProperties = getProperties.getProperties(processId.toString(), "appended-columns");
            Enumeration appendedColumnList = appendedColumnProperties.propertyNames();
            Properties deletedColumnProperties = getProperties.getProperties(processId.toString(), "deleted-columns");
            Enumeration deletedColumnList = deletedColumnProperties.propertyNames();
            List<String> deletedColumns= Collections.list(deletedColumnList);
            System.out.println(appendedColumnProperties.isEmpty());
            System.out.println("Number of columns to be deleted are "+deletedColumnProperties.size());
            System.out.println("size of deletedColumns is "+deletedColumns.size());
            for(String s : deletedColumns){
                System.out.println("hiiieee");
                System.out.println("column in deleted column list is "+s);
            }
            while(deletedColumnList.hasMoreElements()) {
                System.out.println("hiiieee");
                String column = (String) deletedColumnList.nextElement();
                System.out.println("column in deleted column list is"+column);
            }
            if (!appendedColumnProperties.isEmpty()) {
                StringBuilder appendDdl = new StringBuilder("ALTER TABLE " + tableName + " ADD COLUMNS ( ");
                while (appendedColumnList.hasMoreElements()) {
                    String key = (String) appendedColumnList.nextElement();
                    String value = appendedColumnProperties.getProperty(key);
                    LOGGER.info("column name is " + key + " and its data type is " + value);
                    appendDdl.append(key + " " + value + ", ");
                }
                appendDdl.deleteCharAt(appendDdl.length() - 2);
                appendDdl.append(")");
                System.out.println("query is " + appendDdl);
                stmt.executeUpdate(appendDdl.toString());
            }
            if(!deletedColumnProperties.isEmpty()){
                StringBuilder deleteDdl = new StringBuilder("ALTER TABLE " + tableName + " REPLACE COLUMNS ( ");
                ResultSet rs = stmt.executeQuery("select * from " + dbName + "." + tableName +"  limit 1");
                ResultSetMetaData metaData = rs.getMetaData();
                for(int i=1; i<=metaData.getColumnCount();i++) {
                    int flag=0;
                    String columnName = metaData.getColumnLabel(i).replaceFirst(tableName.toLowerCase() + ".", "");
                    String datatype = metaData.getColumnTypeName(i);
                    System.out.println("column in table is " + columnName);
                    for(String column:deletedColumns){
                        System.out.println("I am using list and not enumeration");
                        System.out.println(column);
                      if(columnName.equalsIgnoreCase(column)) {
                          System.out.println("column to be deleted is " + column + "::" + columnName);
                          flag = 1;
                      }
                    }
                    if(flag==0 && !columnName.equalsIgnoreCase("instanceexecid")){
                        LOGGER.info("column name is " + columnName + " and its data type is " + datatype);
                        deleteDdl.append(columnName + " " + datatype + ", ");
                    }
                }
                deleteDdl.deleteCharAt(deleteDdl.length() - 2);
                deleteDdl.append(")");
                System.out.println("query is " + deleteDdl);
                stmt.executeUpdate(deleteDdl.toString());
            }
            new HiveSchemaEvolution().updateBaseTableProperties(processId);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
