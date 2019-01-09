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

package com.wipro.ats.bdre.md.api;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

public class GetPropertiesTest {
    private static final Logger LOGGER = Logger.getLogger(GetPropertiesTest.class);

    @Ignore
    @Test
    public void testExecute() throws Exception {
        GetProperties gp = new GetProperties();
        String[] args = {"--process-id", "10802", "--config-group", "credential"};
        gp.execute(args);
        LOGGER.debug("executed! ");
    }

    @Ignore
    @Test
    public void testGetProperties() throws Exception {
        GetProperties gp = new GetProperties();
        Properties properties = gp.getProperties("10802", "credential");
        LOGGER.debug("properties = " + properties.getProperty("rules.username"));
        LOGGER.debug("executed! ");
    }
}