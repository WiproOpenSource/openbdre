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
import com.wipro.ats.bdre.md.dao.BatchDAO;
import com.wipro.ats.bdre.md.dao.FileDAO;
import com.wipro.ats.bdre.md.dao.ServersDAO;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.File;
import com.wipro.ats.bdre.md.dao.jpa.FileId;
import com.wipro.ats.bdre.md.dao.jpa.Servers;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by vishnu on 12/14/14.
 * Modified by Arijit
 */
public class RawLoad extends ETLBase {

    @Autowired
    FileDAO fileDAO;
    @Autowired
    BatchDAO batchDAO;
    @Autowired
    ServersDAO serversDAO;

    public RawLoad(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(RawLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"lof", "list-of-files", " List of files"},
            {"lob", "list-of-file-batchIds", "List of batch Ids corresponding to above files "},
            {"ied", "instance-exec-id", " instance exec id"},
    };

    public void execute(String[] params) {

        //values of lof and lob parameters comes as null when filepath is choosen by user.
        //Null parameters are not handled by CommandLine class
        //So changing structure of params when there are null parameters
        int i=0;
        int j=0;
        String dupParams[]= new String[10];
        int index=0;
        for(String param:params){
            dupParams[index]=param;

            if(i==1) {
                if(param==null) dupParams[index]="null batch";
                i=0;
            }
            if(j==1) {
                if(param==null) dupParams[index]="null file";
                j=0;
            }
            if((param!=null) && (param.equals("-lob")||param.equals("--list-of-file-batchIds"))) i++;
            if((param!=null) && (param.equals("--list-of-files")||param.equals("-lof"))) j++;
            System.out.println("params[] = " + dupParams[index]);
            index++;
        }
        //Getting raw table information

        CommandLine commandLine = getCommandLine(dupParams, PARAMS_STRUCTURE);


        Option[] options = commandLine.getOptions();
        for (Option opt: options) {
            LOGGER.info("option value "+opt.getValue());
            LOGGER.info("option is "+opt.getOpt());
        }
        String processId = commandLine.getOptionValue("process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");

        loadRawHiveTableInfo(processId);
        String rawTableName = rawTable;
        String rawDbName = rawDb;
        String filePathString = filePath;
        LOGGER.info("filepath "+filePathString);
        LOGGER.info("rawtable "+rawTableName);
        String listOfFiles = "";
        String listOfBatches = "";

        //If user selects enqueueId
        if( "null".equals(filePathString) || filePathString == null) {
            listOfFiles = commandLine.getOptionValue("list-of-files");
            LOGGER.info("list of files "+listOfFiles);
            listOfBatches = commandLine.getOptionValue("list-of-file-batchIds");
            LOGGER.info("list of batches "+listOfBatches);
        }
        //If user select filepath
        else {

            listOfFiles = filePathString;
            LOGGER.info("list of files "+listOfFiles);
            listOfBatches = "0";

            Batch batch = batchDAO.get(0L);
            Servers servers = serversDAO.get(123461);

            String[] files = listOfFiles.split(IMConstant.FILE_FIELD_SEPERATOR);
            for(String fileString: files){
                File file = new File();
                FileId fileId = new FileId();
                fileId.setBatchId(batch.getBatchId());
                fileId.setCreationTs(new Date());
                fileId.setFileSize(1L);
                fileId.setServerId(123461);
                fileId.setFileHash("null");
                fileId.setPath(fileString);

                file.setId(fileId);
                file.setBatch(batch);
                file.setServers(servers);
                fileDAO.insert(file);
                LOGGER.info("file "+fileString+" inserted successfully");

                listOfBatches = listOfBatches+",0";
            }

            LOGGER.info("list of batches "+listOfBatches);

        }

        CreateRawBaseTables createRawBaseTables =new CreateRawBaseTables();
        String[] createTablesArgs={"-p",processId,"-instExecId",instanceExecId };
        createRawBaseTables.executeRawLoad(createTablesArgs);

        //Now load file to table
        loadRawLoadTable(rawDbName, rawTableName, listOfFiles, listOfBatches);

    }

    private void loadRawLoadTable(String dbName, String tableName, String listOfFiles, String listOfBatches) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            LOGGER.info("list of files "+listOfFiles);
            String[] files = listOfFiles.split(IMConstant.FILE_FIELD_SEPERATOR);
            LOGGER.info("files1 are "+ files.toString());
            String[] tempFiles = createTempCopies(files);
            String[] correspondingBatchIds = listOfBatches.split(IMConstant.FILE_FIELD_SEPERATOR);
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();

            LOGGER.debug("Inserting data into the table");

            for (int i=0; i<tempFiles.length; i++) {
                String query = "LOAD DATA INPATH '" + tempFiles[i] + "'OVERWRITE INTO TABLE " + tableName
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
        String[] outputFileList= new String[files.length];
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name"));
            FileSystem fs = FileSystem.get(conf);
            for(int i=0;i<files.length;i++) {
                LOGGER.info("files are "+files.toString());
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