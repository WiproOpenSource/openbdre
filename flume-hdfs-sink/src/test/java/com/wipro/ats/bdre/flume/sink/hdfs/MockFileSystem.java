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

import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class MockFileSystem extends FileSystem {

  private static final Logger logger =
      LoggerFactory.getLogger(MockFileSystem.class);

  FileSystem fs;
  int numberOfRetriesRequired;
  MockFsDataOutputStream latestOutputStream;
  int currentRenameAttempts;
  boolean closeSucceed = true;

  public MockFileSystem(FileSystem fs,
    int numberOfRetriesRequired) {
    this.fs = fs;
    this.numberOfRetriesRequired = numberOfRetriesRequired;
  }

  public MockFileSystem(FileSystem fs,
                        int numberOfRetriesRequired, boolean closeSucceed) {
    this.fs = fs;
    this.numberOfRetriesRequired = numberOfRetriesRequired;
    this.closeSucceed = closeSucceed;
  }

  @Override
  public FSDataOutputStream append(Path arg0, int arg1, Progressable arg2)
      throws IOException {

    latestOutputStream = new MockFsDataOutputStream(
      fs.append(arg0, arg1, arg2), closeSucceed);

    return latestOutputStream;
  }

  @Override
  public FSDataOutputStream create(Path arg0) throws IOException {
    //throw new IOException ("HI there2");
    latestOutputStream = new MockFsDataOutputStream(
      fs.create(arg0), closeSucceed);

    return latestOutputStream;
  }

  @Override
  public FSDataOutputStream create(Path arg0, FsPermission arg1,
    boolean arg2, int arg3, short arg4, long arg5, Progressable arg6)
    throws IOException {
    throw new IOException("Not a real file system");
  }

  @Override
  @Deprecated
  public boolean delete(Path arg0) throws IOException {
    return fs.delete(arg0);
  }

  @Override
  public boolean delete(Path arg0, boolean arg1) throws IOException {
    return fs.delete(arg0, arg1);
  }

  @Override
  public FileStatus getFileStatus(Path arg0) throws IOException {
    return fs.getFileStatus(arg0);
  }

  @Override
  public URI getUri() {
    return fs.getUri();
  }

  @Override
  public Path getWorkingDirectory() {
    return fs.getWorkingDirectory();
  }

  @Override
  public FileStatus[] listStatus(Path arg0) throws IOException {
    return fs.listStatus(arg0);
  }

  @Override
  public boolean mkdirs(Path arg0, FsPermission arg1) throws IOException {
    // TODO Auto-generated method stub
    return fs.mkdirs(arg0, arg1);
  }

  @Override
  public FSDataInputStream open(Path arg0, int arg1) throws IOException {
    return fs.open(arg0, arg1);
  }

  @Override
  public boolean rename(Path arg0, Path arg1) throws IOException {
    currentRenameAttempts++;
    logger.info(
      "Attempting to Rename: '" + currentRenameAttempts + "' of '" +
      numberOfRetriesRequired + "'");
    if (currentRenameAttempts >= numberOfRetriesRequired ||
      numberOfRetriesRequired == 0) {
      logger.info("Renaming file");
      return fs.rename(arg0, arg1);
    } else {
      throw new IOException("MockIOException");
    }
  }

  @Override
  public void setWorkingDirectory(Path arg0) {
    fs.setWorkingDirectory(arg0);

  }
}
