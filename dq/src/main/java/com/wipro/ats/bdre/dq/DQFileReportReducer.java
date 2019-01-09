/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.dq;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import java.io.IOException;

public class DQFileReportReducer extends Reducer<Text, IntWritable, Text, Text> {
    private static final Logger LOGGER = Logger.getLogger(DQFileReportReducer.class);
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
