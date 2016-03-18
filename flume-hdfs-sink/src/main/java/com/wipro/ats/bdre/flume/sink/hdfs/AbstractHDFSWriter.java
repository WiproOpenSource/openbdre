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

import com.google.common.base.Preconditions;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class AbstractHDFSWriter implements HDFSWriter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractHDFSWriter.class);

  private FSDataOutputStream outputStream;
  private FileSystem fs;
  private Path destPath;
  private Method refGetNumCurrentReplicas = null;
  private Method refGetDefaultReplication = null;
  private Method refHflushOrSync = null;
  private Integer configuredMinReplicas = null;
  private Integer numberOfCloseRetries = null;
  private long timeBetweenCloseRetries = Long.MAX_VALUE;

  private final static Object [] NO_ARGS = new Object []{};

  @Override
  public void configure(Context context) {
    configuredMinReplicas = context.getInteger("hdfs.minBlockReplicas");
    if (configuredMinReplicas != null) {
      Preconditions.checkArgument(configuredMinReplicas >= 0,
          "hdfs.minBlockReplicas must be greater than or equal to 0");
    }
    numberOfCloseRetries = context.getInteger("hdfs.closeTries", 1) - 1;

    if (numberOfCloseRetries > 1) {
      try {
        timeBetweenCloseRetries = context.getLong("hdfs.callTimeout", 10000l);
      } catch (NumberFormatException e) {
        LOGGER.warn("hdfs.callTimeout can not be parsed to a long: " + context.getLong("hdfs.callTimeout"));
      }
      timeBetweenCloseRetries = Math.max(timeBetweenCloseRetries/numberOfCloseRetries, 1000);
    }

  }

  /**
   * Contract for subclasses: Call registerCurrentStream() on open,
   * unregisterCurrentStream() on close, and the base class takes care of the
   * rest.
   * @return
   */
  @Override
  public boolean isUnderReplicated() {
    try {
      int numBlocks = getNumCurrentReplicas();
      if (numBlocks == -1) {
        return false;
      }
      int desiredBlocks;
      if (configuredMinReplicas != null) {
        desiredBlocks = configuredMinReplicas;
      } else {
        desiredBlocks = getFsDesiredReplication();
      }
      return numBlocks < desiredBlocks;
    } catch (IllegalAccessException e) {
      LOGGER.error("Unexpected error while checking replication factor", e);
    } catch (InvocationTargetException e) {
      LOGGER.error("Unexpected error while checking replication factor", e);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Unexpected error while checking replication factor", e);
    }
    return false;
  }

  protected void registerCurrentStream(FSDataOutputStream outputStream,
                                      FileSystem fs, Path destPath) {
    Preconditions.checkNotNull(outputStream, "outputStream must not be null");
    Preconditions.checkNotNull(fs, "fs must not be null");
    Preconditions.checkNotNull(destPath, "destPath must not be null");

    this.outputStream = outputStream;
    this.fs = fs;
    this.destPath = destPath;
    this.refGetNumCurrentReplicas = reflectGetNumCurrentReplicas(outputStream);
    this.refGetDefaultReplication = reflectGetDefaultReplication(fs);
    this.refHflushOrSync = reflectHflushOrSync(outputStream);

  }

  protected void unregisterCurrentStream() {
    this.outputStream = null;
    this.fs = null;
    this.destPath = null;
    this.refGetNumCurrentReplicas = null;
    this.refGetDefaultReplication = null;
  }

  public int getFsDesiredReplication() {
    short replication = 0;
    if (fs != null && destPath != null) {
      if (refGetDefaultReplication != null) {
        try {
          replication = (Short) refGetDefaultReplication.invoke(fs, destPath);
        } catch (IllegalAccessException e) {
          LOGGER.warn("Unexpected error calling getDefaultReplication(Path)", e);
        } catch (InvocationTargetException e) {
          LOGGER.warn("Unexpected error calling getDefaultReplication(Path)", e);
        }
      } else {
        // will not work on Federated HDFS (see HADOOP-8014)
        replication = fs.getDefaultReplication();
      }
    }
    return replication;
  }

  /**
   * This method gets the datanode replication count for the current open file.
   *
   * If the pipeline isn't started yet or is empty, you will get the default
   * replication factor.
   *
   * <p/>If this function returns -1, it means you
   * are not properly running with the HDFS-826 patch.
   * @throws java.lang.reflect.InvocationTargetException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   */
  public int getNumCurrentReplicas()
      throws IllegalArgumentException, IllegalAccessException,
          InvocationTargetException {
    if (refGetNumCurrentReplicas != null && outputStream != null) {
      OutputStream dfsOutputStream = outputStream.getWrappedStream();
      if (dfsOutputStream != null) {
        Object repl = refGetNumCurrentReplicas.invoke(dfsOutputStream, NO_ARGS);
        if (repl instanceof Integer) {
          return ((Integer)repl).intValue();
        }
      }
    }
    return -1;
  }

  /**
   * Find the 'getNumCurrentReplicas' on the passed <code>os</code> stream.
   * @return Method or null.
   */
  private Method reflectGetNumCurrentReplicas(FSDataOutputStream os) {
    Method m = null;
    if (os != null) {
      Class<? extends OutputStream> wrappedStreamClass = os.getWrappedStream()
          .getClass();
      try {
        m = wrappedStreamClass.getDeclaredMethod("getNumCurrentReplicas",
            new Class<?>[] {});
        m.setAccessible(true);
      } catch (NoSuchMethodException e) {
        LOGGER.info("FileSystem's output stream doesn't support"
            + " getNumCurrentReplicas; --HDFS-826 not available; fsOut="
            + wrappedStreamClass.getName() + "; err=" + e);
      } catch (SecurityException e) {
        LOGGER.info("Doesn't have access to getNumCurrentReplicas on "
            + "FileSystems's output stream --HDFS-826 not available; fsOut="
            + wrappedStreamClass.getName(), e);
        m = null; // could happen on setAccessible()
      }
    }
    if (m != null) {
      LOGGER.debug("Using getNumCurrentReplicas--HDFS-826");
    }
    return m;
  }

  /**
   * Find the 'getDefaultReplication' method on the passed <code>fs</code>
   * FileSystem that takes a Path argument.
   * @return Method or null.
   */
  private Method reflectGetDefaultReplication(FileSystem fileSystem) {
    Method m = null;
    if (fileSystem != null) {
      Class<?> fsClass = fileSystem.getClass();
      try {
        m = fsClass.getMethod("getDefaultReplication",
            new Class<?>[] { Path.class });
      } catch (NoSuchMethodException e) {
        LOGGER.debug("FileSystem implementation doesn't support"
            + " getDefaultReplication(Path); -- HADOOP-8014 not available; " +
            "className = " + fsClass.getName() + "; err = " + e);
      } catch (SecurityException e) {
        LOGGER.debug("No access to getDefaultReplication(Path) on "
            + "FileSystem implementation -- HADOOP-8014 not available; " +
            "className = " + fsClass.getName() + "; err = " + e);
      }
    }
    if (m != null) {
      LOGGER.debug("Using FileSystem.getDefaultReplication(Path) from " +
          "HADOOP-8014");
    }
    return m;
  }

  @SuppressWarnings("squid:S1166")
  private Method reflectHflushOrSync(FSDataOutputStream os) {
    Method m = null;
    if(os != null) {
      Class<?> fsDataOutputStreamClass = os.getClass();
      try {
        m = fsDataOutputStreamClass.getMethod("hflush");
      } catch (NoSuchMethodException ex) {
        LOGGER.debug("HFlush not found. Will use sync() instead");
        try {
          m = fsDataOutputStreamClass.getMethod("sync");
        } catch (Exception ex1) {
          String msg = "Neither hflush not sync were found. That seems to be " +
            "a problem!";
          LOGGER.error(msg);
          throw new FlumeException(ex1);
        }
      }
    }
    return m;
  }

  /**
   * If hflush is available in this version of HDFS, then this method calls
   * hflush, else it calls sync.
   * @param os - The stream to flush/sync
   * @throws java.io.IOException
   */
  protected void hflushOrSync(FSDataOutputStream os) throws IOException {
    try {
      // At this point the refHflushOrSync cannot be null,
      // since register method would have thrown if it was.
      this.refHflushOrSync.invoke(os);
    } catch (InvocationTargetException e) {
      String msg = "Error while trying to hflushOrSync!";
      LOGGER.error(msg);
      Throwable cause = e.getCause();
      if(cause != null && cause instanceof IOException) {
        throw (IOException)cause;
      }
      throw new FlumeException(msg, e);
    } catch (Exception e) {
      String msg = "Error while trying to hflushOrSync!";
      LOGGER.error(msg);
      throw new FlumeException(msg, e);
    }
  }
}
