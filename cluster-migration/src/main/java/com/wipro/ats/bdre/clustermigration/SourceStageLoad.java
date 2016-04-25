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
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by cloudera on 4/12/16.
 */
public class SourceStageLoad extends BaseStructure{

    private static final String[][] PARAMS_STRUCTURE = {
            {"stgDb", "src-stage-db", "Source stage database"},
            {"stgTbl", "src-stg-table", " Source stage table"},
            {"stgPts", "stg-part-cols", "Source Stg partition columns"},
            {"srcCols", "src-reg-cols", "Source stage regular columns"},
            {"stgBp", "stg-bp-cols", " Business partition columns"},
            {"execId", "instance-exec-id", "Instance execution id"},
            {"srcDb", "source-db", " Source database"},
            {"srcTbl", "source-table", "Source table"},
            {"filCon", "filter-condition", "Filter condition"},
            {"ppid", "parent-process-id", "Parent Process Id"}
    };

    protected static Connection getHiveJDBCConnection(String dbName, String hiveConnection) throws SQLException {
        try {
            Class.forName(IMConstant.HIVE_DRIVER_NAME);
            Connection con = DriverManager.getConnection(hiveConnection + "/" + dbName, null, null);
            con.createStatement().execute("set hive.exec.dynamic.partition.mode=nonstrict");
            con.createStatement().execute("set hive.exec.dynamic.partition=true");
            con.createStatement().execute("set hive.exec.max.dynamic.partitions.pernode=1000");
            return con;
        } catch (ClassNotFoundException e) {
            throw new ETLException(e);
        } catch (SQLException e) {
            throw new ETLException(e);
        }
    }

    public void execute(String[] params) throws SQLException{

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
        return  listForParams.get("src-hive").toString();

    }


}
