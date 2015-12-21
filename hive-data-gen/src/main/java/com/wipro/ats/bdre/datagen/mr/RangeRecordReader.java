/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.datagen.mr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

/**
 * A record reader that will generate a range of numbers.
 */
 public class RangeRecordReader
        extends RecordReader<LongWritable, NullWritable> {
    long startRow;
    long finishedRows;
    long totalRows;
    LongWritable key = null;

    public RangeRecordReader() {
    }

    public void initialize(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        startRow = ((RangeInputSplit)split).firstRow;
        finishedRows = 0;
        totalRows = ((RangeInputSplit)split).rowCount;
    }

    public void close() throws IOException {
        // NOTHING
    }

    public LongWritable getCurrentKey() {
        return key;
    }

    public NullWritable getCurrentValue() {
        return NullWritable.get();
    }

    public float getProgress() throws IOException {
        return finishedRows / (float) totalRows;
    }

    public boolean nextKeyValue() {
        if (key == null) {
            key = new LongWritable();
        }
        if (finishedRows < totalRows) {
            key.set(startRow + finishedRows);
            finishedRows += 1;
            return true;
        } else {
            return false;
        }
    }

}
