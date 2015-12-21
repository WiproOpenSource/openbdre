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

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class TermStepTest {
    private static final Logger LOGGER = Logger.getLogger(TermStepTest.class);

    @Ignore
    @Test
    public void testExecute() throws Exception {
        String[] args = {"--sub-process-id", "96"};
        TermStep bs = new TermStep();
        bs.execute(args);
        LOGGER.debug("executed!");
    }
}
