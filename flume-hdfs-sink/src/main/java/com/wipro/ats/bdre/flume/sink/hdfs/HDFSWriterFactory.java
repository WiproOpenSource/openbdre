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

package com.wipro.ats.bdre.flume.sink.hdfs;

import java.io.IOException;

public class HDFSWriterFactory {
  static final String SEQUENCE_FILE_TYPE = "SequenceFile";
  static final String DATA_STREAM_TYPE = "DataStream";
  static final String COMP_STREAM_TYPE = "CompressedStream";

  public HDFSWriterFactory() {

  }

  public HDFSWriter getWriter(String fileType) throws IOException {
    if (fileType.equalsIgnoreCase(SEQUENCE_FILE_TYPE)) {
      return new HDFSSequenceFile();
    } else if (fileType.equalsIgnoreCase(DATA_STREAM_TYPE)) {
      return HDFSDataStream.getHDFSDataStream();
    } else if (fileType.equalsIgnoreCase(COMP_STREAM_TYPE)) {
      return new HDFSCompressedDataStream();
    } else {
      throw new IOException("File type " + fileType + " not supported");
    }
  }
}
