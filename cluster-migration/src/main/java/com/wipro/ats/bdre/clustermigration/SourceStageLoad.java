/*
 *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.wipro.ats.bdre.clustermigration;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by cloudera on 4/12/16.
 */
public class SourceStageLoad extends BaseStructure{

    private static final Logger LOGGER = Logger.getLogger(MigrationPreprocessor.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"stg-db", "src-stage-db", "Source stage database"},
            {"stg-tbl", "src-stg-table", " Source stage table"},
            {"stg-pts", "stg-part-cols", "Source Stg partition columns"},
            {"src-cols", "src-reg-cols", "Source stage regular columns"},
            {"stg-bp", "stg-bp-cols", " Business partition columns"},
            {"exec-id", "instance-exec-id", "Instance execution id"},
            {"src-db", "source-db", " Source database"},
            {"src-tbl", "source-table", "Source table"},
            {"fil-con", "filter-condition", "Filter condition"},
            {"ppid", "parent-process-id", "Parent Process Id"}
    };

    protected static Connection getHiveJDBCConnection(String dbName, String hiveConnection) throws Exception {
        try {
            Class.forName(IMConstant.HIVE_DRIVER_NAME);
            Connection con = DriverManager.getConnection(hiveConnection + "/" + dbName, null, null);
            con.createStatement().execute("set hive.exec.dynamic.partition.mode=nonstrict");
            con.createStatement().execute("set hive.exec.dynamic.partition=true");
            con.createStatement().execute("set hive.exec.max.dynamic.partitions.pernode=1000");
            return con;
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    public void execute(String[] params) throws Exception{

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String sourceStageDb=commandLine.getOptionValue("src-stage-db");
        String sourceStageTable = commandLine.getOptionValue("src-stg-table");
        String stagePartitionCols = commandLine.getOptionValue("stg-part-cols");
        String sourceRegCols=commandLine.getOptionValue("src-reg-cols");
        String sourceBusinessPartitions = commandLine.getOptionValue("stg-bp-cols");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        String sourceDb=commandLine.getOptionValue("source-db");
        String sourceTable = commandLine.getOptionValue("source-table");
        String filterCondition = commandLine.getOptionValue("filter-condition");
        String parentProcessId=commandLine.getOptionValue("parent-process-id");
        String sourceHiveConnection = getSourceHiveConnection(parentProcessId,"hive-migration");

        Connection conn = getHiveJDBCConnection(sourceDb,sourceHiveConnection);
        Statement st = conn.createStatement();
        String sourceStgLoadHQL="insert overwrite table "+sourceStageDb+"."+sourceStageTable+" partition("+stagePartitionCols+") select "+sourceRegCols+","+sourceBusinessPartitions+","+instanceExecId+" from "+sourceDb+"."+sourceTable+" where "+filterCondition;
        st.executeUpdate(sourceStgLoadHQL);
        st.close();
        conn.close();
        return ;
    }
    public String getSourceHiveConnection(String pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(pid, configGroup);
        String sourceHiveConnection=listForParams.get("src-hive").toString();
        return sourceHiveConnection;
    }


}
