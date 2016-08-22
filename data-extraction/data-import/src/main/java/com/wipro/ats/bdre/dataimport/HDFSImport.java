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

package com.wipro.ats.bdre.dataimport;

import com.cloudera.sqoop.SqoopOptions;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;
import org.apache.sqoop.tool.ImportTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * Created by MI294210 on 05-02-2015.
 */


public class HDFSImport extends Configured implements Tool {
    private static final Logger LOGGER = Logger.getLogger(HDFSImport.class);
    private static final String QUERY_STRING = "query";
    private static final String LOG_CATEGORY = "IncrementalImport";
    private Properties commonProperties;
    private String processId;
    private String batchId;
    private String instanceExecId;
    private int size = 0;
    private String[] cols;
    private String tableName;
    private String lastValue;
    private String prevLastValue;
    private String incrementMode;
    private String query;


    public HDFSImport(Properties commonProperties, String[] columns) {
        this.commonProperties = commonProperties;
        if (columns != null) {
            size = columns.length;
            cols = new String[size];
            for (int i = 0; i < size; i++)
                cols[i] = columns[i];
        }
    }
    @Override
    public int run(String[] param) throws Exception {

        processId = param[0];
        batchId = param[1];
        instanceExecId = param[2];

        Configuration conf = getConf();

        tableName = commonProperties.getProperty("table");
        if (commonProperties.getProperty("incr.mode") != null) {
            incrementMode = commonProperties.getProperty("incr.mode");
        }
        if (commonProperties.getProperty(QUERY_STRING) != null) {
            query = commonProperties.getProperty(QUERY_STRING);
        }
        String driver = commonProperties.getProperty("driver");
        Class.forName(driver).newInstance();

        try {
            SqoopOptions options = new SqoopOptions();
            options.setDriverClassName(driver);

            //reading properties from IMConfig file
            String targetDir = IMConfig.getProperty("data-import.target-dir");
            String jarOutputDir = IMConfig.getProperty("data-import.jar-output-dir") + "/" + processId + "/" + batchId;
            String hadoopHome = IMConfig.getProperty("data-import.hadoop-home");
            File jod=new File(jarOutputDir);
            //create if this directory does not exist
            if(!jod.exists())
            {
                LOGGER.info("Creating jar output dir"+jarOutputDir);
                boolean created=jod.mkdirs();
                LOGGER.info("Is directory created?"+created);
            }

            //setting the parameters of sqoopOption
            options.setHadoopHome(hadoopHome);
            options.setJarOutputDir(jarOutputDir);
            String outputDir = targetDir + "/" + processId + "/tmp";
            options.setTargetDir(outputDir);

            options.setConnManagerClassName("org.apache.sqoop.manager.GenericJdbcManager");
            options.setConnectString(commonProperties.getProperty("db"));
            options.setUsername(commonProperties.getProperty("username"));
            options.setPassword(commonProperties.getProperty("password"));

            int mappers = Integer.parseInt(commonProperties.getProperty("mappers"));
            options.setNumMappers(mappers);
            if (mappers > 1) {
                options.setSplitByCol(commonProperties.getProperty("splitbycol"));
            }
            options.setFileLayout(SqoopOptions.FileLayout.valueOf(commonProperties.getProperty("file.layout")));
            options.setJobName("importJob");


            //Checking the condition for importing by table ,columns or query
            if (null != commonProperties.getProperty(QUERY_STRING) && "" != commonProperties.getProperty(QUERY_STRING)) {
                options.setSqlQuery(query);//import using the query

            } else {
                if (null != commonProperties.getProperty("columns") && size != 0) {

                        options.setTableName(tableName);
                        options.setColumns(cols);        //importing table or columns

                }

                if (!("None".equalsIgnoreCase(incrementMode)) && incrementMode != null) {
                    ProcessLog processLog = new ProcessLog();
                    ProcessLogInfo processLogInfo = new ProcessLogInfo();
                    String logCategory;
                    logCategory = LOG_CATEGORY;
                    String msgId = "last value";
                    processLogInfo = processLog.getLastValue(processId, msgId, logCategory);
                    if (processLogInfo != null) {
                        lastValue = processLogInfo.getMessage();
                        prevLastValue = lastValue;
                    }
                    options.setIncrementalMode(SqoopOptions.IncrementalMode.valueOf(incrementMode));
                    options.setIncrementalTestColumn(commonProperties.getProperty("check.col"));
                    options.setIncrementalLastValue(lastValue);

                }

            }


            //running the import job
            int ret = new ImportTool().run(options);
            if (ret == 0) {

                lastValue = options.getIncrementalLastValue();
                LOGGER.debug(lastValue);
                LOGGER.debug(prevLastValue);

                //adding the process log
                ProcessLog processLog = new ProcessLog();
                ProcessLogInfo processLogInfo = new ProcessLogInfo();
                processLogInfo.setProcessId(Integer.parseInt(processId));
                processLogInfo.setAddTs(new Timestamp(new Date().getTime()));
                processLogInfo.setInstanceRef(Long.parseLong(instanceExecId));


                if (prevLastValue != null && prevLastValue.equals(lastValue)) {
                    LOGGER.info("No new records imported.");
                    processLogInfo.setLogCategory(LOG_CATEGORY);
                    processLogInfo.setMessage("0");
                    processLogInfo.setMessageId("Number of imported records");
                    processLog.log(processLogInfo);
                } else {

                    LOGGER.info("Imported sucessfully.The file will be registered. ");

                    Path outputDirPath = new Path(targetDir);
                    FileSystem srcFs = outputDirPath.getFileSystem(getConf());
                    FileSystem destFs = outputDirPath.getFileSystem(getConf());

                    //temporary directory to store multiple partitions
                    Path tmpDir = new Path(outputDir);
                    //merging output files to one target file
                    Path targetFile = new Path(targetDir + "/" + processId + "/" + batchId);
                    if (srcFs.exists(tmpDir)) {
                        FileUtil.copyMerge(srcFs, tmpDir, destFs, targetFile, true, conf, "");
                    }


                    //Return file info oozie params
                    RegisterFileInfo registerFileInfo = new RegisterFileInfo();
                    registerFileInfo.setBatchId(Long.parseLong(batchId));
                    registerFileInfo.setCreationTs(new Timestamp(new Date().getTime()));
                    FileChecksum hdfsChecksum = destFs.getFileChecksum(targetFile);
                    String fileHash = hdfsChecksum == null ? "0" : hdfsChecksum.toString();
                    registerFileInfo.setFileHash(fileHash);
                    registerFileInfo.setFileSize(destFs.getFileStatus(targetFile).getLen());
                    registerFileInfo.setPath(targetFile.toString());
                    registerFileInfo.setSubProcessId(Integer.parseInt(processId));
                    OozieUtil oozieUtil = new OozieUtil();
                    oozieUtil.persistBeanData(registerFileInfo, false);
                    LOGGER.info("register file info "+registerFileInfo.toString());

                    try
                    {
                        String homeDir = System.getProperty("user.home");
                       // String parentProcessId = String.valueOf(Integer.valueOf(processId) - 1);
                        FileWriter fw = new FileWriter(homeDir+"/bdre/airflow/"+processId+"_jobInfo.txt", true);
                        BufferedWriter bw = new BufferedWriter(fw);

                        if(registerFileInfo.getSubProcessId() != null)
                            bw.write("fileInfo.getSubProcessId():"+registerFileInfo.getSubProcessId().toString()+"\n");
                        else
                            bw.write("fileInfo.getSubProcessId():null\n");

                        if(registerFileInfo.getServerId() != null)
                            bw.write("fileInfo.getServerId():"+registerFileInfo.getServerId().toString()+"\n");
                        else
                            bw.write("fileInfo.getServerId():null\n");

                        if(registerFileInfo.getPath() != null)
                            bw.write("fileInfo.getPath():"+registerFileInfo.getPath()+"\n");
                        else
                            bw.write("fileInfo.getPath():null\n");

                        if(registerFileInfo.getFileSize() != null)
                            bw.write("fileInfo.getFileSize():"+registerFileInfo.getFileSize().toString()+"\n");
                        else
                            bw.write("fileInfo.getFileSize():null\n");

                        if(registerFileInfo.getFileHash() != null)
                            bw.write("fileInfo.getFileHash():"+registerFileInfo.getFileHash().toString()+"\n");
                        else
                            bw.write("fileInfo.getFileHash():null\n");

                        if(registerFileInfo.getCreationTs() != null) {
                            String creationTs = registerFileInfo.getCreationTs().toString().replace(" ", "__").replace(":", "zzzz");//Recovered back in RegisterFile.java CreationTs has space(which splits parameter) and :(creates great problem while creating python dictionaries)
                            LOGGER.info("Creation Ts modified is "+creationTs);
                            bw.write("fileInfo.getCreationTs():" + creationTs + "\n");
                        }else
                            bw.write("fileInfo.getCreationTs():null\n");

                        if(registerFileInfo.getBatchId() != null)
                            bw.write("fileInfo.getBatchId():"+registerFileInfo.getBatchId().toString()+"\n");
                        else
                            bw.write("fileInfo.getBatchId():null\n");

                        if(registerFileInfo.getParentProcessId() != null)
                            bw.write("fileInfo.getParentProcessId():"+registerFileInfo.getParentProcessId().toString()+"\n");
                        else
                            bw.write("fileInfo.getParentProcessId():null\n");

                        if(registerFileInfo.getBatchMarking() != null)
                            bw.write("fileInfo.getBatchMarking():"+registerFileInfo.getBatchMarking()+"\n");
                        else
                            bw.write("fileInfo.getBatchMarking():null\n");

                        bw.close();

                    }catch(IOException i)
                    {
                        i.printStackTrace();
                    }

                    if (null != commonProperties.getProperty(QUERY_STRING) && "" != commonProperties.getProperty(QUERY_STRING)) {
                        //adding log for import by query
                        processLogInfo.setLogCategory("ImpQuery");
                        processLogInfo.setMessage(query);
                        processLogInfo.setMessageId(QUERY_STRING);
                        processLog.log(processLogInfo);
                    } else {
                        //adding log for incremental import
                        if (!("None".equalsIgnoreCase(incrementMode)) && incrementMode != null) {
                            processLogInfo.setLogCategory(LOG_CATEGORY);
                            processLogInfo.setMessage(lastValue);
                            processLogInfo.setMessageId("last value");
                            processLog.log(processLogInfo);
                        }
                        //adding log for normal import
                        else {

                            processLogInfo.setLogCategory("Import");
                            processLogInfo.setMessage(tableName);
                            processLogInfo.setMessageId("table name");
                            processLog.log(processLogInfo);

                        }

                    }


                }

            }


        } catch (Exception e) {
            throw new ETLException(e);
        }
        return 0;
    }
}

