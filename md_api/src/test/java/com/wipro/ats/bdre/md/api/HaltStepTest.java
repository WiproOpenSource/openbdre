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

public class HaltStepTest {
    private static final Logger LOGGER = Logger.getLogger(HaltStepTest.class);

    @Ignore
    @Test
    public void testExecute() throws Exception {
        String[] args = {"--sub-process-id", "96"};
        HaltStep bs = new HaltStep();
        bs.execute(args);
        System.out.println("executed!");
    }
}