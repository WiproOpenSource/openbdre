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

import com.google.common.annotations.VisibleForTesting;
import com.wipro.ats.bdre.md.api.BatchEnqueuer;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.serialization.EventSerializer;
import org.apache.flume.serialization.EventSerializerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class HDFSDataStream extends AbstractHDFSWriter {

  private static final Logger logger =
      LoggerFactory.getLogger(HDFSDataStream.class);

  private FSDataOutputStream outStream;
  private String serializerType;
  private Context serializerContext;
  private EventSerializer serializer;
  private boolean useRawLocalFileSystem;

  private Configuration conf;
  private Path dstPath;
  private String inUseSuffix;
  private String processId;
  protected HDFSDataStream(){
    logger.info("Init HDFSDataStream");
    ApplicationContext appCtx = new ClassPathXmlApplicationContext("spring-dao.xml");
    AutowireCapableBeanFactory acbFactory = appCtx.getAutowireCapableBeanFactory();
    acbFactory.autowireBean(this);
  }
  private static HDFSDataStream hdfsDataStream;
  public static HDFSDataStream getHDFSDataStream(){
    if(hdfsDataStream==null)hdfsDataStream=new HDFSDataStream();
    return hdfsDataStream;
  }
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
    serializerContext =
        new Context(context.getSubProperties(EventSerializer.CTX_PREFIX));
    logger.info("Serializer = " + serializerType + ", UseRawLocalFileSystem = "
        + useRawLocalFileSystem);
  }

  @VisibleForTesting
  protected FileSystem getDfs(Configuration conf,
    Path dstPath) throws IOException{
    return  dstPath.getFileSystem(conf);
  }

  protected void doOpen(Configuration conf,
    Path dstPath, FileSystem hdfs) throws
    IOException {
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
      outStream = hdfs.append(dstPath);
      appending = true;
    } else {
      outStream = hdfs.create(dstPath);
    }

    serializer = EventSerializerFactory.getInstance(
        serializerType, serializerContext, outStream);
    if (appending && !serializer.supportsReopen()) {
      outStream.close();
      serializer = null;
      throw new IOException("serializer (" + serializerType +
          ") does not support append");
    }

    // must call superclass to check for replication issues
    registerCurrentStream(outStream, hdfs, dstPath);

    if (appending) {
      serializer.afterReopen();
    } else {
      serializer.afterCreate();
    }
  }

  @Override
  public void open(String filePath) throws IOException {
    conf = new Configuration();
    dstPath = new Path(filePath);
    FileSystem hdfs = getDfs(conf, dstPath);
    doOpen(conf, dstPath, hdfs);
  }

  @Override
  public void open(String filePath, CompressionCodec codec,
                   CompressionType cType) throws IOException {
    open(filePath);
  }

  @Override
  public void append(Event e) throws IOException {
    serializer.write(e);
  }

  @Override
  public void sync() throws IOException {
    serializer.flush();
    outStream.flush();
    hflushOrSync(outStream);
  }

  @Override
  public void close() throws IOException {
    serializer.flush();
    serializer.beforeClose();
    outStream.flush();
    hflushOrSync(outStream);
    outStream.close();

    unregisterCurrentStream();

    // calling the method to save file details in tables
    saveFileToHDFS(conf, dstPath, inUseSuffix, processId);
  }
  @Autowired
  private BatchEnqueuer batchEnqueuer;
  public void saveFileToHDFS(Configuration conf, Path dstPath, String inUseSuffix, String processId) throws IOException {
    FileSystem hdfs = dstPath.getFileSystem(conf);
    FileStatus fileStatus=hdfs.getFileStatus(dstPath);

    logger.info("Process Id :" + processId);

    String fSize = String.valueOf(fileStatus.getLen());
    logger.info("File Size in Byte :" + fSize);

    String filePath;
    if (dstPath.toString().contains(inUseSuffix)){
      filePath = dstPath.toString().replace(inUseSuffix,"");
    }else{
      filePath = dstPath.toString();
    }

    logger.info("File path :" + filePath);

    // creating the command to save
    String[] beargs={"-p",processId,"-sId","123461","-path",filePath,"-fs",fSize,"-fh","","-cTS",new Timestamp(new Date().getTime()).toString(),"-bid","null","-bm",""};
    batchEnqueuer.execute(beargs);
  }

}
