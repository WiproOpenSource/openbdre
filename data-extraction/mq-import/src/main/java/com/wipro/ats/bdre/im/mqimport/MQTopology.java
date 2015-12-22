/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */
package com.wipro.ats.bdre.im.mqimport;
/**
 * Created by arijit on 4/25/15.
 */

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * This is a basic example of a Storm topology.
 */
public class MQTopology extends MetadataAPIBase{

    private static final Logger LOGGER = Logger.getLogger(MQTopology.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"pid", "process-id", "Parent Process Id of the mq-import"},
            {"spid", "sub-process-id", "Sub Process Id of the mq-import"},
            {"bm", "batch-marking", "Batch marking for the target batch"}
    };
    static String sPId;
    static String environment;

    public static void main(String[] args) throws Exception {
            MQTopology mq=new MQTopology();
            mq.execute(args);
    }

    public  Object execute(String[] params) {
        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("process-id");
        LOGGER.debug("processId is " + processId);
        String subProcessId = commandLine.getOptionValue("sub-process-id");
        LOGGER.debug("sub process id is " + subProcessId);
        sPId=subProcessId;
        String batchMarking = commandLine.getOptionValue("batch-marking");
        LOGGER.debug("batch marking is " + batchMarking);

        String folderPath=IMConfig.getProperty("mq-import.target-directory");
        String fileSizeUnits=IMConfig.getProperty("mq-import.file-size-units");
        String fSize=IMConfig.getProperty("mq-import.rotation-file-size");
        LOGGER.debug(folderPath+" =folderPath");
        LOGGER.debug(fileSizeUnits+" =fileSizeUnits");
        LOGGER.debug(fSize+" =fSize");
        String spoutName="mySpout";
        String boltName="myBolt";
        String topologyName="myTopology";
        String[] beargs={"-p",processId,"-sId","123461","-path","","-fs",fSize,"-fh","","-cTS",new Timestamp(new Date().getTime()).toString(),"-bid","null","-bm",batchMarking};
        Properties props=new GetProperties().getProperties(subProcessId,"mqimport");

        int numBolts=Integer.parseInt(props.getProperty("num.bolts"));
        int numSpouts=Integer.parseInt(props.getProperty("num.spouts"));

        RecordFormat format = new DelimitedRecordFormat().withFieldDelimiter("|");
        //Synchronize data buffer with the filesystem every 100 tuples
        SyncPolicy syncPolicy = new CountSyncPolicy(100);

        // Rotate data files when they reach given size
        FileRotationPolicy rotationPolicy=null;
        if(fileSizeUnits.toUpperCase().equals("KB")) {
            rotationPolicy = new FileSizeRotationPolicy(Float.parseFloat(fSize), FileSizeRotationPolicy.Units.KB); }
        else if (fileSizeUnits.toUpperCase().equals("MB")) {
            rotationPolicy = new FileSizeRotationPolicy(Float.parseFloat(fSize), FileSizeRotationPolicy.Units.MB); }
        else if (fileSizeUnits.toUpperCase().equals("GB")) {
            rotationPolicy = new FileSizeRotationPolicy(Float.parseFloat(fSize), FileSizeRotationPolicy.Units.GB); }
        else if (fileSizeUnits.toUpperCase().equals("TB")) {
            rotationPolicy = new FileSizeRotationPolicy(Float.parseFloat(fSize), FileSizeRotationPolicy.Units.TB); }


        // Use default, Storm-generated file names
        FileNameFormat fileNameFormat = new DefaultFileNameFormat().withPath(folderPath);
        // Instantiate the HdfsBolt
        HdfsBolt bolt = new HdfsBolt()
                .withFsUrl(IMConfig.getProperty("common.default-fs-name"))
                .withFileNameFormat(fileNameFormat)
                .withRecordFormat(format)
                .withRotationPolicy(rotationPolicy)
                .withSyncPolicy(syncPolicy)
                .addRotationAction(new RegisterFileAction().registerAndEnqueue(beargs));
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(spoutName, new com.wipro.ats.bdre.im.mqimport.MQSpout(), numSpouts);
        builder.setBolt(boltName, bolt, numBolts).shuffleGrouping(spoutName);

        Config conf = new Config();
        conf.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(topologyName, conf, builder.createTopology());
        Utils.sleep(100000);

        return null;
    }
}
