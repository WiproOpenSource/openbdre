/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */
package com.wipro.ats.bdre.dq;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.StatefulKnowledgeSession;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Satish Kumar
 *         <p/>
 *         Mapper Class create the Drool knowledgeBase and Validate the each record and
 *         Emits the record as GOOD or BAD based on rules defined in drool.
 */
public class DQMapper extends Mapper<LongWritable, Text, Text, Text> {

    private static Logger LOGGER = Logger.getLogger(DQMapper.class);
    Text mOutputKey = new Text();
    Text mOutputValue = new Text();
    private int goodRecords;
    private int badRecords;
    private MultipleOutputs<Text, NullWritable> mos;
    private Properties props = null;
    private GetProperties getProperties = new GetProperties();
    private KnowledgeBase knowledgeBase = null;


    @Override
    public void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
        LOGGER.info("START :: DQMapper.setup(Context context)");
        Configuration conf = context.getConfiguration();
        props = getProperties.getProperties(conf.get("dq.process.id"), "dq");
        knowledgeBase = buildKnowledgeBase();
        LOGGER.debug("The Value of props is"+props.toString()+"\n package name is"+props.getProperty("rules.package"));
        mos = new MultipleOutputs<Text, NullWritable>(context);
        goodRecords = 0;
        badRecords = 0;
    }

    private KnowledgeBase buildKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        UrlResource urlResource = new UrlResource(
                IMConfig.getProperty("dq.drools-url-prefix") +
                        props.getProperty("rules.package") + "/binary");
        LOGGER.info("urlResource=" + urlResource);
        urlResource.setBasicAuthentication("enabled");
        System.out.println("props = " + props);
        urlResource.setUsername(props.getProperty("rules.username"));
        urlResource.setPassword(props.getProperty("rules.password"));
        kbuilder.add(urlResource, ResourceType.PKG);
        return kbuilder.newKnowledgeBase();
    }

    private DQDataModel validateRecord(String record) {
        StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();
        try {
            DQDataModel dataModel = new DQDataModel(record, props.getProperty("file.delimiter.regex"));
            session.insert(dataModel);
            session.fireAllRules();
            return dataModel;
        } finally {
            session.dispose();
        }
    }

    @Override
    public void map(LongWritable key, Text value,
                       org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
        DQDataModel dqDataModel = validateRecord(value.toString());
        LOGGER.trace("map() :: " + value.toString() + " = " + dqDataModel.getmInvalidRecordMessage());
        if (dqDataModel.isValidRecord()) {
            goodRecords++;
            mOutputKey.set(dqDataModel.getmRecord());
            mos.write(DQConstants.GOOD_RECORDS_FILE, mOutputKey, NullWritable.get(), DQConstants.INTERMEDIATE_GOOD_RECORD_OUTPUT_DIR);
        } else {
            badRecords++;
            mOutputKey.set(dqDataModel.getmInvalidRecordMessage());
            mOutputValue.set(value.toString());
            mos.write(DQConstants.BAD_RECORDS_FILE, mOutputKey, mOutputValue, DQConstants.INTERMEDIATE_BAD_RECORD_OUTPUT_DIR);
        }
    }

    @Override
    public void cleanup(org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
        try {
            mos.write(DQConstants.FILE_REPORT_FILE, new Text(DQConstants.GOOD_RECORDS_FILE + " : " + goodRecords), NullWritable.get(), DQConstants.INTERMEDIATE_REPORT_OUTPUT_DIR);
            mos.write(DQConstants.FILE_REPORT_FILE, new Text(DQConstants.BAD_RECORDS_FILE + " : " + badRecords), NullWritable.get(), DQConstants.INTERMEDIATE_REPORT_OUTPUT_DIR);
        } catch (Exception e) {
            LOGGER.info("cleanup : " + e.toString());
        } finally {
            mos.close();
        }
    }

}
