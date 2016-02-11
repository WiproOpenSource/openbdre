package com.wipro.ats.bdre.imcrawler.mr;/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An input format that assigns ranges of longs to each mapper.
 */
public class CrawlInputFormat
        extends InputFormat<IntWritable, Text> {
    private static final Log LOG = LogFactory.getLog(CrawlInputFormat.class);

    public RecordReader<IntWritable, Text>
    createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException {
        return new CrawlRecordReader();
    }

    /**
     * Create the desired number of splits, dividing the number of rows
     * between the mappers.
     */
    public List<InputSplit> getSplits(JobContext job) {
        long totalRows = 1000L;
        int numSplits = job.getConfiguration().getInt("number.of.mappers", 1);                 //determines no. of mappers
        LOG.info("Generating " + totalRows + " using " + numSplits);
        List<InputSplit> splits = new ArrayList<InputSplit>();
        long currentRow = 0;
        for (int split = 0; split < numSplits; ++split) {
            long goal =
                    (long) Math.ceil(totalRows * (double) (split + 1) / numSplits);
            splits.add(new CrawlInputSplit(currentRow, goal - currentRow));
            currentRow = goal;
        }
        return splits;
    }

}
