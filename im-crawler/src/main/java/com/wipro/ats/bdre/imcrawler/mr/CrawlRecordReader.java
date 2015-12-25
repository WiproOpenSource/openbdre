package com.wipro.ats.bdre.imcrawler.mr;/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A record reader that will generate a range of numbers.
 */
public class CrawlRecordReader
        extends RecordReader<IntWritable, Text> {
    protected static final Logger logger = LoggerFactory.getLogger(CrawlRecordReader.class);
    long startRow;
    long finishedRows;
    long totalRows;
    int i = 1;

    //list to store the values being set by visit function
    //public static List<Text> valueList = new ArrayList<Text>();
    public static List<CrawlOutput> fullList = new ArrayList<CrawlOutput>();
    //thread running status; default false
    public static boolean shuttingDown = false;

    //value taken from list and that value deleted from the list
    IntWritable key = null;
    Text value = null;

    public CrawlRecordReader() {
    }

    /**
     * From Design Pattern, O'Reilly...
     * This method takes as arguments the map task’s assigned InputSplit and
     * TaskAttemptContext, and prepares the record reader. For file-based input
     * formats, this is a good place to seek to the byte position in the file to
     * begin reading.
     */
    public void initialize(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        //generated row(offset) and count(length) for each inputSplit
        //called for each inputsplit
        //call basicCrawlcontroller here

        int processId = context.getConfiguration().getInt("sub.process.id", 1);
        long instanceExecId = context.getConfiguration().getLong("instance.exec.id", 1);
        //int numThread = context.getConfiguration().getInt("number.of.concurrent.threads", 1);
        logger.info("Arguments passed to mapper:- sub.process.id"+processId+"instance.exec.id"+instanceExecId);
        /*try {
            Class.forName("org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }*/
        BasicCrawlController basicCrawlController = new BasicCrawlController(processId, instanceExecId, 1);
        Thread crawlThread = new Thread(basicCrawlController);
        crawlThread.start();
    }

    /**
     * From Design Pattern, O'Reilly...
     * This method is used by the framework for cleanup after there are no more
     * key/value pairs to process.
     */
    public void close() throws IOException {
        // NOTHING
    }

    /**
     * From Design Pattern, O'Reilly...
     * This methods are used by the framework to give generated key/value pairs
     * to an implementation of Mapper. Be sure to reuse the objects returned by
     * these methods if at all possible!
     */
    public IntWritable getCurrentKey() {
        return key;
    }

    /**
     * From Design Pattern, O'Reilly...
     * This methods are used by the framework to give generated key/value pairs
     * to an implementation of Mapper. Be sure to reuse the objects returned by
     * these methods if at all possible!
     */
    public Text getCurrentValue() {
        return value;
    }

    /**
     * From Design Pattern, O'Reilly...
     * Like the corresponding method of the InputFormat class, this is an
     * optional method used by the framework for metrics gathering.
     */
    public float getProgress() throws IOException {
        return finishedRows / (float) totalRows;
    }

    /**
     * From Design Pattern, O'Reilly...
     * Like the corresponding method of the InputFormat class, this reads a
     * single key/ value pair and returns true until the data is consumed.
     */
    public boolean nextKeyValue() {
        //iterates over all the K,V pairs for that inputsplit
        /*if (key == null) {
            key = new IntWritable();
            value = new Text();
        }*/

        if (shuttingDown == true && fullList.isEmpty() == true) {
            return false;
        } else if (shuttingDown == true && fullList.isEmpty() == false) {
            //thread stopped but list not empty
            key = new IntWritable(i++);
            value = new Text(fullList.get(0).getAnchor() + "--__--" + fullList.get(0).getDomain() + "--__--" +
                    fullList.get(0).getDocid() + "--__--" + fullList.get(0).getHtml() + "--__--" +
                    fullList.get(0).getParentUrl() + "--__--" + fullList.get(0).getPath() + "--__--" +
                    fullList.get(0).getSubDomain() + "--__--" + fullList.get(0).getUrl() + "--__--" +
                    fullList.get(0).getBinary() + "--__--");//add to value
            fullList.remove(0);//remove that
            return true;
        } else if (shuttingDown == false && fullList.isEmpty() == false) {
            key = new IntWritable(i++);
            value = new Text(fullList.get(0).getAnchor() + "--__--" + fullList.get(0).getDomain() + "--__--" +
                    fullList.get(0).getDocid() + "--__--" + fullList.get(0).getHtml() + "--__--" +
                    fullList.get(0).getParentUrl() + "--__--" + fullList.get(0).getPath() + "--__--" +
                    fullList.get(0).getSubDomain() + "--__--" + fullList.get(0).getUrl() + "--__--" +
                    fullList.get(0).getBinary() + "--__--");//add to value
            fullList.remove(0);//remove that
            return true;
        } else {//list empty but threads still running
            int COUNTER_TO_CHECK_INFINITE_LOOP = 0;
            while (fullList.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.info("Thread Interrupted in CrawlRecordReader");
                    throw new RuntimeException("Thread Interrupted"+e);
                }
                COUNTER_TO_CHECK_INFINITE_LOOP++;
                if (COUNTER_TO_CHECK_INFINITE_LOOP > 600) {
                    return false;
                }
            }
            key = new IntWritable(i++);
            value = new Text(fullList.get(0).getAnchor() + "--__--" + fullList.get(0).getDomain() + "--__--" +
                    fullList.get(0).getDocid() + "--__--" + fullList.get(0).getHtml() + "--__--" +
                    fullList.get(0).getParentUrl() + "--__--" + fullList.get(0).getPath() + "--__--" +
                    fullList.get(0).getSubDomain() + "--__--" + fullList.get(0).getUrl() + "--__--" +
                    fullList.get(0).getBinary() + "--__--");//add to value
            fullList.remove(0);//remove that
            return true;
        }
    }
}
