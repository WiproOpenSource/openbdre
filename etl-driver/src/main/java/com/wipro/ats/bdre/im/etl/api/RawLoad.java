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
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by vishnu on 12/14/14.
 * Modified by Arijit
 */
public class RawLoad extends ETLBase {
    private static final Logger LOGGER = Logger.getLogger(RawLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"ied", "instance-exec-id", " instance exec id"},
            {"lof", "list-of-files", " List of files"},
            {"lob", "list-of-file-batchIds", "List of batch Ids corresponding to above files "}
    };

    public void execute(String[] params) {
        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");
        String listOfFiles = commandLine.getOptionValue("list-of-files");
        String listOfBatches = commandLine.getOptionValue("list-of-file-batchIds");
        CreateRawBaseTables createRawBaseTables =new CreateRawBaseTables();
        String[] createTablesArgs={"-p",processId,"-instExecId",instanceExecId };
        createRawBaseTables.execute(createTablesArgs);
        init(processId);
        //Getting raw table information
        String rawTableName = rawTable;
        String rawDbName = rawDb;
        //Now load file to table
        loadRawLoadTable(rawDbName, rawTableName, listOfFiles, listOfBatches);

    }

    private void loadRawLoadTable(String dbName, String tableName, String listOfFiles, String listOfBatches) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            String[] files = listOfFiles.split(IMConstant.FILE_FIELD_SEPERATOR);
            String[] tempFiles = createTempCopies(files);
            String[] correspondingBatchIds = listOfBatches.split(IMConstant.FILE_FIELD_SEPERATOR);
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();

            LOGGER.debug("Inserting data into the table");

            for (int i=0; i<tempFiles.length; i++) {
                String query = "LOAD DATA INPATH '" + tempFiles[i] + "' INTO TABLE " + tableName
                + " PARTITION (batchid='" + correspondingBatchIds[i] + "')";
                LOGGER.info("Raw load query " + query);
                stmt.executeUpdate(query);
            }
            stmt.close();
            con.close();
            LOGGER.info("Raw load completed.");

        } catch (Exception e) {
            LOGGER.error("Error In RawLoad" + e);
            throw new ETLException(e);
        }

    }

    private String[] createTempCopies(String[] files){
        String outputFileList[]= new String[files.length];
        try {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name"));
        FileSystem fs = FileSystem.get(conf);
        for(int i=0;i<files.length;i++) {
            Path srcPath = new Path(files[i]);
            Path destPath = new Path(files[i]+"_tmp");
            FileUtil.copy(fs, srcPath, fs, destPath, false, conf);
            outputFileList[i]=files[i]+"_tmp";
        }

    } catch(Exception e){
            LOGGER.error("error occured ="+ e);
            throw new ETLException(e);
        }
        return outputFileList;
}
}