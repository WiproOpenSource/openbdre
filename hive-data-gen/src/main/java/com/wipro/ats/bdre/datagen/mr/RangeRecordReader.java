/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        startRow = ((RangeInputSplit)split).firstRow;
        finishedRows = 0;
        totalRows = ((RangeInputSplit)split).rowCount;
    }

    @Override
    public void close() throws IOException {
        // NOTHING
    }

    @Override
    public LongWritable getCurrentKey() {
        return key;
    }

    @Override
    public NullWritable getCurrentValue() {
        return NullWritable.get();
    }

    @Override
    public float getProgress() throws IOException {
        return finishedRows / (float) totalRows;
    }

    @Override
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