/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.beans.table.BatchConsumpQueue;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * Created by SR294224 on 5/29/2015.
 */
public class BatchEnqueuerTest {
    private static final Logger LOGGER = Logger.getLogger(BatchEnqueuerTest.class);

    @Ignore
    @Test
    public void testExecute() throws Exception {
        String[] args = {"-cTS", "2014-12-11 11:56:17", "-fh", "1", "-sId", "123461", "-fs", "1", "-p", "1", "--path", "1", "--batch-id", "1", "-bm", "1"};
        BatchEnqueuer be = new BatchEnqueuer();
        List<BatchConsumpQueue> info = be.execute(args);
        LOGGER.info(info.size());
    }
}
