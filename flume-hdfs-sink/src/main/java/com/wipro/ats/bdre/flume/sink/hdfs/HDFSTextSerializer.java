/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.wipro.ats.bdre.flume.sink.hdfs;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.util.Collections;

public class HDFSTextSerializer implements SequenceFileSerializer {

  private Text makeText(Event e) {
    Text textObject = new Text();
    textObject.set(e.getBody(), 0, e.getBody().length);
    return textObject;
  }

  @Override
  public Class<LongWritable> getKeyClass() {
    return LongWritable.class;
  }

  @Override
  public Class<Text> getValueClass() {
    return Text.class;
  }

  @Override
  public Iterable<Record> serialize(Event event) {
    Object key = getKey(event);
    Object value = getValue(event);
    return Collections.singletonList(new Record(key, value));
  }

  private Object getKey(Event event) {
    // Write the data to HDFS
    String timestamp = event.getHeaders().get("timestamp");
    long eventStamp;

    if (timestamp == null) {
      eventStamp = System.currentTimeMillis();
    } else {
      eventStamp = Long.valueOf(timestamp);
    }
    return new LongWritable(eventStamp);
  }

  private Object getValue(Event event) {
    return makeText(event);
  }

  public static class HDFSTextSerializerBuilder implements SequenceFileSerializer.Builder {

    @Override
    public SequenceFileSerializer build(Context context) {
      return new HDFSTextSerializer();
    }

  }

}
