/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.datagen.mr;

import com.wipro.ats.bdre.datagen.util.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An input format that assigns ranges of longs to each mapper.
 */
public class RangeInputFormat
        extends InputFormat<LongWritable, NullWritable> {
    private static final Log LOG = LogFactory.getLog(RangeInputFormat.class);

    public RecordReader<LongWritable, NullWritable>
    createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException {
        return new RangeRecordReader();
    }

    /**
     * Create the desired number of splits, dividing the number of rows
     * between the mappers.
     */
    public List<InputSplit> getSplits(JobContext job) {
        long totalRows = job.getConfiguration().getLong(Config.NUM_ROWS_KEY,1);
        int numSplits = job.getConfiguration().getInt(Config.NUM_SPLITS_KEY, 1);
        LOG.info("Generating " + totalRows + " using " + numSplits);
        List<InputSplit> splits = new ArrayList<InputSplit>();
        long currentRow = 0;
        for(int split = 0; split < numSplits; ++split) {
            long goal =
                    (long) Math.ceil(totalRows * (double)(split + 1) / numSplits);
            splits.add(new RangeInputSplit(currentRow, goal - currentRow));
            currentRow = goal;
        }
        return splits;
    }

}
