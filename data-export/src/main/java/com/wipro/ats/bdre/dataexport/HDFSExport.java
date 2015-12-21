/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.dataexport;

import com.cloudera.sqoop.SqoopOptions;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.ProcessLog;
import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;
import org.apache.sqoop.tool.ExportTool;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * Created by MI294210 on 05-02-2015.
 */


public class HDFSExport extends Configured implements Tool {
    private static final Logger LOGGER = Logger.getLogger(HDFSExport.class);

    private Properties commonProperties;
    private String processId;
    private String batchId;
    private String instanceExecId;
    private String env;
    private String tableName;


    public HDFSExport(Properties commonProperties) {
        this.commonProperties = commonProperties;
    }

    public int run(String[] param) throws Exception {

        processId = param[0];
        batchId = param[1];
        instanceExecId = param[2];
        env = param[3];

        tableName = commonProperties.getProperty("table");
        String driver = commonProperties.getProperty("driver");
        Class.forName(driver).newInstance();


        /*// Sqoop connection parameters
        Configuration config = new Configuration();
        config.addResource("/etc/hadoop/conf/core-site.xml");
        config.addResource("/etc/hadoop/conf/hdfs-site.xml");
*/


        try {
            SqoopOptions options = new SqoopOptions();
            options.setDriverClassName(driver);

            //reading properties from IMConfig file
            String jarOutputDir = IMConfig.getProperty("data-export.jar-output-dir", env) + "/" + processId + "/" + batchId;
            String hadoopHome = IMConfig.getProperty("data-export.hadoop-home", env);

            //setting the parameters of sqoopOption
            options.setHadoopHome(hadoopHome);
			//options.setHadoopMapRedHome(hadoopHome);
            options.setJarOutputDir(jarOutputDir);
            options.setConnManagerClassName(commonProperties.getProperty("con.mgr.class"));
            options.setConnectString(commonProperties.getProperty("db"));
            options.setUsername(commonProperties.getProperty("username"));
            options.setPassword(commonProperties.getProperty("password"));
            options.setTableName(tableName);
            String exportDir=commonProperties.getProperty("export.dir");
            options.setExportDir(exportDir);
            int mappers = Integer.parseInt(commonProperties.getProperty("mappers"));
            options.setNumMappers(mappers);
            options.setJobName("exportJob");

            //running the import job
            int ret = new ExportTool().run(options);
            if (ret == 0) {

                LOGGER.info("Exported successfully.");

                //adding the process log
                ProcessLog processLog = new ProcessLog();
                ProcessLogInfo processLogInfo = new ProcessLogInfo();
                processLogInfo.setProcessId(Integer.parseInt(processId));
                processLogInfo.setAddTs(new Timestamp(new Date().getTime()));
                processLogInfo.setInstanceRef(Long.parseLong(instanceExecId));
                processLogInfo.setLogCategory("Export");
                processLogInfo.setMessage(tableName);
                processLogInfo.setMessageId("Exported table");
                processLog.log(processLogInfo);

            }

        } catch (Exception e) {
            throw new ETLException(e);
        }
        return 0;
    }
}

