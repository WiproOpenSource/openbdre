/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre;

import org.apache.log4j.Logger;
import org.junit.Test;

public class IMConfigTest {
    private Logger LOGGER=Logger.getLogger(IMConfigTest.class);
    @Test
    public void testGetProperty() throws Exception {
        String key = "data-import.hadoop-home";

        LOGGER.info(key+"="+IMConfig.getProperty(key));
    }

   @Test
    public void testGetProperty1() throws Exception {
        String key = "etl.hive-connection";

        LOGGER.info(key+"="+IMConfig.getProperty(key));

    }
}