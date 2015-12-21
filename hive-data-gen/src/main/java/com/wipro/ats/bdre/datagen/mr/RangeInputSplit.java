/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.datagen.mr;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapreduce.InputSplit;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * An input split consisting of a range on numbers.
 */
public class RangeInputSplit extends InputSplit implements Writable {
    long firstRow;
    long rowCount;

    public RangeInputSplit() { }

    public RangeInputSplit(long offset, long length) {
        firstRow = offset;
        rowCount = length;
    }

    public long getLength() throws IOException {
        return 0;
    }

    public String[] getLocations() throws IOException {
        return new String[]{};
    }

    public void readFields(DataInput in) throws IOException {
        firstRow = WritableUtils.readVLong(in);
        rowCount = WritableUtils.readVLong(in);
    }

    public void write(DataOutput out) throws IOException {
        WritableUtils.writeVLong(out, firstRow);
        WritableUtils.writeVLong(out, rowCount);
    }
}
