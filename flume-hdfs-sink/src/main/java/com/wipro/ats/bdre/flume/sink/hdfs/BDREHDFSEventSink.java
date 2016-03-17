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
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.flume.*;
import org.apache.flume.auth.FlumeAuthenticationUtil;
import org.apache.flume.auth.PrivilegedExecutor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.formatter.output.BucketPath;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
*   Custom HDFS Event Sink class
* */

public class BDREHDFSEventSink extends AbstractSink implements Configurable {
  public interface WriterCallback {
    public void run(String filePath);
  }

  private static final Logger LOGGER = LoggerFactory
      .getLogger(BDREHDFSEventSink.class);

  private static String DIRECTORY_DELIMITER = System.getProperty("file.separator");

  private static final long defaultRollInterval = 30;
  private static final long defaultRollSize = 1024;
  private static final long defaultRollCount = 10;
  private static final String defaultFileName = "FlumeData";
  private static final String defaultSuffix = "";
  private static final String defaultInUsePrefix = "";
  private static final String defaultInUseSuffix = ".tmp";
  private static final long defaultBatchSize = 100;
  private static final String defaultFileType = HDFSWriterFactory.SEQUENCE_FILE_TYPE;
  private static final int defaultMaxOpenFiles = 5000;
  // Time between close retries, in seconds
  private static final long defaultRetryInterval = 180;
  // Retry forever.
  private static final int defaultTryCount = Integer.MAX_VALUE;

  /**
   * Default length of time we wait for blocking BucketWriter calls
   * before timing out the operation. Intended to prevent server hangs.
   */
  private static final long defaultCallTimeout = 10000;
  /**
   * Default number of threads available for tasks
   * such as append/open/close/flush with hdfs.
   * These tasks are done in a separate thread in
   * the case that they take too long. In which
   * case we create a new file and move on.
   */
  private static final int defaultThreadPoolSize = 10;
  private static final int defaultRollTimerPoolSize = 1;

  private final HDFSWriterFactory writerFactory;
  private WriterLinkedHashMap sfWriters;

  private long rollInterval;
  private long rollSize;
  private long rollCount;
  private long batchSize;
  private int threadsPoolSize;
  private int rollTimerPoolSize;
  private CompressionCodec codeC;
  private CompressionType compType;
  private String fileType;
  private String filePath;
  private String fileName;
  private String suffix;
  private String inUsePrefix;
  private String inUseSuffix;
  private TimeZone timeZone;
  private int maxOpenFiles;
  private ExecutorService callTimeoutPool;
  private ScheduledExecutorService timedRollerPool;
  private String processId;

  private boolean needRounding = false;
  private int roundUnit = Calendar.SECOND;
  private int roundValue = 1;
  private boolean useLocalTime = false;

  private long callTimeout;
  private Context context;
  private SinkCounter sinkCounter;

  private volatile int idleTimeout;
  @SuppressWarnings("squid:S1068")
  private Clock clock;
  private FileSystem mockFs;
  private HDFSWriter mockWriter;
  private final Object sfWritersLock = new Object();
  private long retryInterval;
  private int tryCount;
  private PrivilegedExecutor privExecutor;



  /*
   * Extended Java LinkedHashMap for open file handle LRU queue.
   * We want to clear the oldest file handle if there are too many open ones.
   */
  @SuppressWarnings("squid:S2160")
  private static class WriterLinkedHashMap
      extends LinkedHashMap<String, BucketWriter> {

    private final int maxOpenFiles;

    public WriterLinkedHashMap(int maxOpenFiles) {
      super(16, 0.75f, true); // stock initial capacity/load, access ordering
      this.maxOpenFiles = maxOpenFiles;
    }

    @Override
    protected boolean removeEldestEntry(Entry<String, BucketWriter> eldest) {
      if (size() > maxOpenFiles) {
        // If we have more that max open files, then close the last one and
        // return true
        try {
          eldest.getValue().close();
        } catch (IOException e) {
          LOGGER.warn(eldest.getKey().toString(), e);
        } catch (InterruptedException e) {
          LOGGER.warn(eldest.getKey().toString(), e);
          Thread.currentThread().interrupt();
        }
        return true;
      } else {
        return false;
      }
    }
  }

  public BDREHDFSEventSink() {
    this(new HDFSWriterFactory());
  }

  public BDREHDFSEventSink(HDFSWriterFactory writerFactory) {
    this.writerFactory = writerFactory;
  }

  @VisibleForTesting
  Map<String, BucketWriter> getSfWriters() {
    return sfWriters;
  }

  // read configuration and setup thresholds
  @Override
  public void configure(Context context) {
    this.context = context;

    filePath = Preconditions.checkNotNull(
        context.getString("hdfs.path"), "hdfs.path is required");
    fileName = context.getString("hdfs.filePrefix", defaultFileName);
    this.suffix = context.getString("hdfs.fileSuffix", defaultSuffix);
    inUsePrefix = context.getString("hdfs.inUsePrefix", defaultInUsePrefix);
    inUseSuffix = context.getString("hdfs.inUseSuffix", defaultInUseSuffix);
    String tzName = context.getString("hdfs.timeZone");
    timeZone = tzName == null ? null : TimeZone.getTimeZone(tzName);
    rollInterval = context.getLong("hdfs.rollInterval", defaultRollInterval);
    rollSize = context.getLong("hdfs.rollSize", defaultRollSize);
    rollCount = context.getLong("hdfs.rollCount", defaultRollCount);
    batchSize = context.getLong("hdfs.batchSize", defaultBatchSize);
    idleTimeout = context.getInteger("hdfs.idleTimeout", 0);
    String codecName = context.getString("hdfs.codeC");
    fileType = context.getString("hdfs.fileType", defaultFileType);
    maxOpenFiles = context.getInteger("hdfs.maxOpenFiles", defaultMaxOpenFiles);
    callTimeout = context.getLong("hdfs.callTimeout", defaultCallTimeout);
    threadsPoolSize = context.getInteger("hdfs.threadsPoolSize",
        defaultThreadPoolSize);
    rollTimerPoolSize = context.getInteger("hdfs.rollTimerPoolSize",
        defaultRollTimerPoolSize);
    String kerbConfPrincipal = context.getString("hdfs.kerberosPrincipal");
    String kerbKeytab = context.getString("hdfs.kerberosKeytab");
    String proxyUser = context.getString("hdfs.proxyUser");
    tryCount = context.getInteger("hdfs.closeTries", defaultTryCount);
    processId = context.getString("hdfs.processId");
    if(tryCount <= 0) {
      LOGGER.warn("Retry count value : " + tryCount + " is not " +
        "valid. The sink will try to close the file until the file " +
        "is eventually closed.");
      tryCount = defaultTryCount;
    }
    retryInterval = context.getLong("hdfs.retryInterval",
      defaultRetryInterval);
    if(retryInterval <= 0) {
      LOGGER.warn("Retry Interval value: " + retryInterval + " is not " +
        "valid. If the first close of a file fails, " +
        "it may remain open and will not be renamed.");
      tryCount = 1;
    }

    Preconditions.checkArgument(batchSize > 0,
        "batchSize must be greater than 0");
    if (codecName == null) {
      codeC = null;
      compType = CompressionType.NONE;
    } else {
      codeC = getCodec(codecName);
      // TODO : set proper compression type
      compType = CompressionType.BLOCK;
    }

    // Do not allow user to set fileType DataStream with codeC together
    // To prevent output file with compress extension (like .snappy)
    if(fileType.equalsIgnoreCase(HDFSWriterFactory.DATA_STREAM_TYPE)
        && codecName != null) {
      throw new IllegalArgumentException("fileType: " + fileType +
          " which does NOT support compressed output. Please don't set codeC" +
          " or change the fileType if compressed output is desired.");
    }

    if(fileType.equalsIgnoreCase(HDFSWriterFactory.COMP_STREAM_TYPE)) {
      Preconditions.checkNotNull(codeC, "It's essential to set compress codec"
          + " when fileType is: " + fileType);
    }

    // get the appropriate executor
    this.privExecutor = FlumeAuthenticationUtil.getAuthenticator(
            kerbConfPrincipal, kerbKeytab).proxyAs(proxyUser);




    needRounding = context.getBoolean("hdfs.round", false);

    if(needRounding) {
      String unit = context.getString("hdfs.roundUnit", "second");
      if ("hour".equalsIgnoreCase(unit)) {
        this.roundUnit = Calendar.HOUR_OF_DAY;
      } else if ("minute".equalsIgnoreCase(unit)) {
        this.roundUnit = Calendar.MINUTE;
      } else if ("second".equalsIgnoreCase(unit)){
        this.roundUnit = Calendar.SECOND;
      } else {
        LOGGER.warn("Rounding unit is not valid, please set one of" +
            "minute, hour, or second. Rounding will be disabled");
        needRounding = false;
      }
      this.roundValue = context.getInteger("hdfs.roundValue", 1);
      if(roundUnit == Calendar.SECOND || roundUnit == Calendar.MINUTE){
        Preconditions.checkArgument(roundValue > 0 && roundValue <= 60,
            "Round value" +
            "must be > 0 and <= 60");
      } else if (roundUnit == Calendar.HOUR_OF_DAY){
        Preconditions.checkArgument(roundValue > 0 && roundValue <= 24,
            "Round value" +
            "must be > 0 and <= 24");
      }
    }

    this.useLocalTime = context.getBoolean("hdfs.useLocalTimeStamp", false);
    if(useLocalTime) {
      clock = new SystemClock();
    }

    if (sinkCounter == null) {
      sinkCounter = new SinkCounter(getName());
    }
  }

  @SuppressWarnings("squid:S1872")
  private static boolean codecMatches(Class<? extends CompressionCodec> cls,
      String codecName) {
    String simpleName = cls.getSimpleName();
    if (cls.getName().equals(codecName)
        || simpleName.equalsIgnoreCase(codecName)) {
      return true;
    }
    if (simpleName.endsWith("Codec")) {
      String prefix = simpleName.substring(0,
          simpleName.length() - "Codec".length());
      if (prefix.equalsIgnoreCase(codecName)) {
        return true;
      }
    }
    return false;
  }

  @VisibleForTesting
  @SuppressWarnings("squid:S1166")
  static CompressionCodec getCodec(String codecName) {
    Configuration conf = new Configuration();
    List<Class<? extends CompressionCodec>> codecs = CompressionCodecFactory
        .getCodecClasses(conf);
    // Wish we could base this on DefaultCodec but appears not all codec's
    // extend DefaultCodec(Lzo)
    CompressionCodec codec = null;
    List<String> codecStrs = new ArrayList<String>();
    codecStrs.add("None");
    for (Class<? extends CompressionCodec> cls : codecs) {
      codecStrs.add(cls.getSimpleName());
      if (codecMatches(cls, codecName)) {
        try {
          codec = cls.newInstance();
        } catch (InstantiationException e) {
          LOGGER.error("Unable to instantiate " + cls + " class");
        } catch (IllegalAccessException e) {
          LOGGER.error("Unable to access " + cls + " class");
        }
      }
    }

    if (codec == null) {
      if (!"None".equalsIgnoreCase(codecName)) {
        throw new IllegalArgumentException("Unsupported compression codec "
            + codecName + ".  Please choose from: " + codecStrs);
      }
    } else if (codec instanceof org.apache.hadoop.conf.Configurable) {
      // Must check instanceof codec as BZip2Codec doesn't inherit Configurable
      // Must set the configuration for Configurable objects that may or do use
      // native libs
      ((org.apache.hadoop.conf.Configurable) codec).setConf(conf);
    }
    return codec;
  }


  /**
   * Pull events out of channel and send it to HDFS. Take at most batchSize
   * events per Transaction. Find the corresponding bucket for the event.
   * Ensure the file is open. Serialize the data and write it to the file on
   * HDFS. <br/>
   * This method is not thread safe.
   */
  @Override
  public Status process() throws EventDeliveryException {
    Channel channel = getChannel();
    Transaction transaction = channel.getTransaction();
    List<BucketWriter> writers = Lists.newArrayList();
    transaction.begin();
    try {
      int txnEventCount = 0;
      for (txnEventCount = 0; txnEventCount < batchSize; txnEventCount++) {
        Event event = channel.take();
        if (event == null) {
          break;
        }

        // reconstruct the path name by substituting place holders
        String realPath = BucketPath.escapeString(filePath, event.getHeaders(),
            timeZone, needRounding, roundUnit, roundValue, useLocalTime);
        String realName = BucketPath.escapeString(fileName, event.getHeaders(),
          timeZone, needRounding, roundUnit, roundValue, useLocalTime);

        String lookupPath = realPath + DIRECTORY_DELIMITER + realName;
        BucketWriter bucketWriter;
        HDFSWriter hdfsWriter = null;
        // Callback to remove the reference to the bucket writer from the
        // sfWriters map so that all buffers used by the HDFS file
        // handles are garbage collected.
        WriterCallback closeCallback = new WriterCallback() {
          @Override
          public void run(String bucketPath) {
            LOGGER.info("Writer callback called.");
            synchronized (sfWritersLock) {
              sfWriters.remove(bucketPath);
            }
          }
        };
        synchronized (sfWritersLock) {
          bucketWriter = sfWriters.get(lookupPath);
          // we haven't seen this file yet, so open it and cache the handle
          if (bucketWriter == null) {
            hdfsWriter = writerFactory.getWriter(fileType);
            bucketWriter = initializeBucketWriter(realPath, realName,
              lookupPath, hdfsWriter, closeCallback);
            sfWriters.put(lookupPath, bucketWriter);
          }
        }

        // track the buckets getting written in this transaction
        if (!writers.contains(bucketWriter)) {
          writers.add(bucketWriter);
        }

        // Write the data to HDFS
        writeTheDataToHDFS(event, realPath, realName, lookupPath, bucketWriter, closeCallback);
      }

      if (txnEventCount == 0) {
        sinkCounter.incrementBatchEmptyCount();
      } else if (txnEventCount == batchSize) {
        sinkCounter.incrementBatchCompleteCount();
      } else {
        sinkCounter.incrementBatchUnderflowCount();
      }

      // flush all pending buckets before committing the transaction
      for (BucketWriter bucketWriter : writers) {
        bucketWriter.flush();
      }

      transaction.commit();

      if (txnEventCount < 1) {
        return Status.BACKOFF;
      } else {
        sinkCounter.addToEventDrainSuccessCount(txnEventCount);
        return Status.READY;
      }
    } catch (IOException eIO) {
      transaction.rollback();
      LOGGER.warn("HDFS IO error", eIO);
      return Status.BACKOFF;
    } catch (Exception ex) {
      transaction.rollback();
      LOGGER.error("process failed", ex);
      throw new EventDeliveryException(ex);
    } finally {
      transaction.close();
    }
  }

  private void writeTheDataToHDFS(Event event, String realPath, String realName, String lookupPath, BucketWriter bucketWriter, WriterCallback closeCallback) throws IOException, InterruptedException {
    HDFSWriter hdfsWriter;
    try {
      bucketWriter.append(event);
    } catch (BucketClosedException ex) {
      LOGGER.info("Bucket was closed while trying to append, " +
        "reinitializing bucket and writing event.");
      hdfsWriter = writerFactory.getWriter(fileType);
      bucketWriter = initializeBucketWriter(realPath, realName,
        lookupPath, hdfsWriter, closeCallback);
      synchronized (sfWritersLock) {
        sfWriters.put(lookupPath, bucketWriter);
      }
      bucketWriter.append(event);
    }
  }

  private BucketWriter initializeBucketWriter(String realPath,
    String realName, String lookupPath, HDFSWriter hdfsWriter,
    WriterCallback closeCallback) {
    BucketWriter bucketWriter = new BucketWriter(rollInterval,
      rollSize, rollCount,
      batchSize, context, realPath, realName, inUsePrefix, inUseSuffix,
      suffix, codeC, compType, hdfsWriter, timedRollerPool,
      privExecutor, sinkCounter, idleTimeout, closeCallback,
      lookupPath, callTimeout, callTimeoutPool, retryInterval,
      tryCount, processId);
    if(mockFs != null) {
      bucketWriter.setFileSystem(mockFs);
      bucketWriter.setMockStream(mockWriter);
    }
    return bucketWriter;
  }

  @Override
  public void stop() {
    // do not constrain close() calls with a timeout
    synchronized (sfWritersLock) {
      for (Entry<String, BucketWriter> entry : sfWriters.entrySet()) {
        LOGGER.info("Closing {}", entry.getKey());

        try {
          entry.getValue().close();
        } catch (InterruptedException ie) {
          LOGGER.warn("Exception while closing " + entry.getKey() + ". " +
                  "Exception follows.", ie);
          Thread.currentThread().interrupt();
        } catch (Exception ex) {
          LOGGER.warn("Exception while closing " + entry.getKey() + ". " +
                  "Exception follows.", ex);
        }
      }
    }

    // shut down all our thread pools
    ExecutorService toShutdown[] = {callTimeoutPool, timedRollerPool};
    for (ExecutorService execService : toShutdown) {
      execService.shutdown();
      try {
        while (execService.isTerminated() == false) {
          execService.awaitTermination(
                  Math.max(defaultCallTimeout, callTimeout), TimeUnit.MILLISECONDS);
        }
      } catch (InterruptedException ex) {
        LOGGER.warn("shutdown interrupted on " + execService, ex);
      }
    }

    callTimeoutPool = null;
    timedRollerPool = null;

    synchronized (sfWritersLock) {
      sfWriters.clear();
      sfWriters = null;
    }
    sinkCounter.stop();
    super.stop();
  }

  @Override
  public void start() {
    String timeoutName = "hdfs-" + getName() + "-call-runner-%d";
    callTimeoutPool = Executors.newFixedThreadPool(threadsPoolSize,
            new ThreadFactoryBuilder().setNameFormat(timeoutName).build());

    String rollerName = "hdfs-" + getName() + "-roll-timer-%d";
    timedRollerPool = Executors.newScheduledThreadPool(rollTimerPoolSize,
            new ThreadFactoryBuilder().setNameFormat(rollerName).build());

    this.sfWriters = new WriterLinkedHashMap(maxOpenFiles);
    sinkCounter.start();
    super.start();
  }

  @Override
  public String toString() {
    return "{ Sink type:" + getClass().getSimpleName() + ", name:" + getName() +
            " }";
  }

  @VisibleForTesting
  void setBucketClock(Clock clock) {
    BucketPath.setClock(clock);
  }

  @VisibleForTesting
  void setMockFs(FileSystem mockFs) {
    this.mockFs = mockFs;
  }

  @VisibleForTesting
  void setMockWriter(HDFSWriter writer) {
    this.mockWriter = writer;
  }

  @VisibleForTesting
  int getTryCount() {
    return tryCount;
  }
}
