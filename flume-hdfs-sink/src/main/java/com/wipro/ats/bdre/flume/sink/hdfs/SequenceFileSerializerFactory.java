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

import com.google.common.base.Preconditions;
import org.apache.flume.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceFileSerializerFactory {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SequenceFileSerializerFactory.class);

  /**
   * {@link Context} prefix
   */
  static final String CTX_PREFIX = "writeFormat.";

  private SequenceFileSerializerFactory(){
  }

  @SuppressWarnings({"squid:MethodCyclomaticComplexity","squid:S1166"})
  static SequenceFileSerializer getSerializer(String formatType,
                                              Context context) {
    Preconditions.checkNotNull(formatType, "serialize type must not be null");

    // try to find builder class in enum of known formatters
    SequenceFileSerializerType type;
    try {
      type = SequenceFileSerializerType.valueOf(formatType);
    } catch (IllegalArgumentException e) {
      LOGGER.debug("Not in enum, loading builder class: {}" + formatType);
      type = SequenceFileSerializerType.OTHER;
    }
    Class<? extends SequenceFileSerializer.Builder> builderClass =
        type.getBuilderClass();

    // handle the case where they have specified their own builder in the config
    if (builderClass == null) {
      try {
        Class c = Class.forName(formatType);
        if (c != null && SequenceFileSerializer.Builder.class.isAssignableFrom(c)) {
          builderClass = (Class<? extends SequenceFileSerializer.Builder>) c;
        } else {
          LOGGER.error("Unable to instantiate AvroEventSerializerBuilder from {}", formatType);
          return null;
        }
      } catch (ClassNotFoundException ex) {
        LOGGER.error("Class not found: " + formatType, ex);
        return null;
      } catch (ClassCastException ex) {
        LOGGER.error("Class does not extend " +
            SequenceFileSerializer.Builder.class.getCanonicalName() + ": " +
            formatType, ex);
        return null;
      }
    }

    // build the builder
    SequenceFileSerializer.Builder builder;
    try {
      builder = builderClass.newInstance();
    } catch (InstantiationException ex) {
      LOGGER.error("Cannot instantiate builder: " + formatType, ex);
      return null;
    } catch (IllegalAccessException ex) {
      LOGGER.error("Cannot instantiate builder: " + formatType, ex);
      return null;
    }

    return builder.build(context);
  }

}
