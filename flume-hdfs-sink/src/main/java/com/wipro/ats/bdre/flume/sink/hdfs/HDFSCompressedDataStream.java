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

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.serialization.EventSerializer;
import org.apache.flume.serialization.EventSerializerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class HDFSCompressedDataStream extends AbstractHDFSWriter {

  private static final Logger logger =
      LoggerFactory.getLogger(HDFSCompressedDataStream.class);

  private FSDataOutputStream fsOut;
  private CompressionOutputStream cmpOut;
  private boolean isFinished = false;

  private String serializerType;
  private Context serializerContext;
  private EventSerializer serializer;
  private boolean useRawLocalFileSystem;
  private Compressor compressor;

  private Configuration conf;
  private Path dstPath;
  private String inUseSuffix;
  private String processId;

  @Override
  public void configure(Context context) {
    super.configure(context);
    // extracting in use suffix
    inUseSuffix = context.getString("hdfs.inUseSuffix","");

    // extracting process id
    processId = context.getString("hdfs.processId");

    serializerType = context.getString("serializer", "TEXT");
    useRawLocalFileSystem = context.getBoolean("hdfs.useRawLocalFileSystem",
        false);
    serializerContext = new Context(
        context.getSubProperties(EventSerializer.CTX_PREFIX));
    logger.info("Serializer = " + serializerType + ", UseRawLocalFileSystem = "
        + useRawLocalFileSystem);
  }

  @Override
  public void open(String filePath) throws IOException {
    DefaultCodec defCodec = new DefaultCodec();
    CompressionType cType = CompressionType.BLOCK;
    open(filePath, defCodec, cType);
  }

  @Override
  public void open(String filePath, CompressionCodec codec,
      CompressionType cType) throws IOException {
    conf = new Configuration();
    dstPath = new Path(filePath);
    FileSystem hdfs = dstPath.getFileSystem(conf);
    if(useRawLocalFileSystem) {
      if(hdfs instanceof LocalFileSystem) {
        hdfs = ((LocalFileSystem)hdfs).getRaw();
      } else {
        logger.warn("useRawLocalFileSystem is set to true but file system " +
            "is not of type LocalFileSystem: " + hdfs.getClass().getName());
      }
    }
    boolean appending = false;
    if (conf.getBoolean("hdfs.append.support", false) == true && hdfs.isFile
    (dstPath)) {
      fsOut = hdfs.append(dstPath);
      appending = true;
    } else {
      fsOut = hdfs.create(dstPath);
    }
    if(compressor == null) {
      compressor = CodecPool.getCompressor(codec, conf);
    }
    cmpOut = codec.createOutputStream(fsOut, compressor);
    serializer = EventSerializerFactory.getInstance(serializerType,
        serializerContext, cmpOut);
    if (appending && !serializer.supportsReopen()) {
      cmpOut.close();
      serializer = null;
      throw new IOException("serializer (" + serializerType
          + ") does not support append");
    }

    registerCurrentStream(fsOut, hdfs, dstPath);

    if (appending) {
      serializer.afterReopen();
    } else {
      serializer.afterCreate();
    }
    isFinished = false;
  }

  @Override
  public void append(Event e) throws IOException {
    if (isFinished) {
      cmpOut.resetState();
      isFinished = false;
    }
    serializer.write(e);
  }

  @Override
  public void sync() throws IOException {
    // We must use finish() and resetState() here -- flush() is apparently not
    // supported by the compressed output streams (it's a no-op).
    // Also, since resetState() writes headers, avoid calling it without an
    // additional write/append operation.
    // Note: There are bugs in Hadoop & JDK w/ pure-java gzip; see HADOOP-8522.
    serializer.flush();
    if (!isFinished) {
      cmpOut.finish();
      isFinished = true;
    }
    fsOut.flush();
    hflushOrSync(this.fsOut);
  }

  @Override
  public void close() throws IOException {
    serializer.flush();
    serializer.beforeClose();
    if (!isFinished) {
      cmpOut.finish();
      isFinished = true;
    }
    fsOut.flush();
    hflushOrSync(fsOut);
    cmpOut.close();
    if (compressor != null) {
      CodecPool.returnCompressor(compressor);
      compressor = null;
    }
    unregisterCurrentStream();

    // calling the method to save file details in tables
    HDFSDataStream.getHDFSDataStream().saveFileToHDFS(conf, dstPath, inUseSuffix, processId);

  }

}
