/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.dq;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;

public class DQFileReportReducer extends Reducer<Text, IntWritable, Text, Text> {
    private static Logger LOGGER = Logger.getLogger(DQFileReportReducer.class);
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private int goodRecords;
    private int badRecords;

    @Override
    protected void setup(org.apache.hadoop.mapreduce.Reducer.Context context)
            throws IOException, InterruptedException {
        goodRecords = 0;
        badRecords = 0;
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable value : values) {
            sum += value.get();
        }
        if (key.toString().equals(DQConstants.GOOD_RECORDS_FILE)) {
            goodRecords = sum;
        } else if (key.toString().equals(DQConstants.BAD_RECORDS_FILE)) {
            badRecords = sum;
        }

    }

    @Override
    protected void cleanup(org.apache.hadoop.mapreduce.Reducer.Context context)
            throws IOException, InterruptedException {

        try {
            outputKey.set(DQConstants.GOOD_RECORDS_FILE);
            outputValue.set("" + goodRecords);
            context.write(outputKey, outputValue);
            outputKey.set(DQConstants.BAD_RECORDS_FILE);
            outputValue.set("" + badRecords);
            context.write(outputKey, outputValue);

        } catch (Exception e) {
            LOGGER.error("Error cleaning up", e);
        }
    }
}
