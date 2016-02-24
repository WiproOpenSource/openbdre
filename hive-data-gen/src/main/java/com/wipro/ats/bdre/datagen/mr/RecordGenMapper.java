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

package com.wipro.ats.bdre.datagen.mr;

import com.wipro.ats.bdre.datagen.Table;
import com.wipro.ats.bdre.datagen.util.Config;
import com.wipro.ats.bdre.datagen.util.TableUtil;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * The Mapper class that given a row number, will generate the appropriate
 * output line.
 */
public class RecordGenMapper
        extends Mapper<LongWritable, NullWritable, Text, Text> {
    private Table table;
    private TableUtil tableUtil;
    private String pid;
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        tableUtil=new TableUtil();
        pid=context.getConfiguration().get(Config.PID_KEY);
        table = tableUtil.formTableFromConfig(pid);
        super.setup(context);

    }
    @Override
    public void map(LongWritable row, NullWritable ignored,Context context) throws IOException, InterruptedException {
        String strRow = tableUtil.getDelimitedTextRow(table,pid);
        context.write(new Text(strRow),new Text(""));
    }

    @Override
    public void cleanup(Context context) {
        /*
        cleanup
         */
    }
}
