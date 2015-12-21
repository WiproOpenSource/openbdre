/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.dq;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class DQFileReportMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable();

    @Override
    protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        super.setup(context);
    }

    @Override
    protected void map(LongWritable key, Text value,
                       org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
        String[] tokens = value.toString().split(":");
        outputKey.set(tokens[0].trim());
        outputValue.set(Integer.parseInt(tokens[1].trim()));
        context.write(outputKey, outputValue);
    }
}
