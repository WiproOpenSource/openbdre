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

package com.wipro.ats.bdre.util;

import com.wipro.ats.bdre.datagen.util.RandomValueGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;

public class RandomValueGeneratorTest {
    private static final Logger LOGGER=Logger.getLogger(RandomValueGeneratorTest.class);

    @Test
    public void testRandomString() throws Exception {

    }

    @Test
    public void testRandomNumber() throws Exception {

    }

    @Test
    public void testRandomDecimal() throws Exception {

    }

    @Test
    public void testRandomReverseRegex() throws Exception {
        String pattern="20[0-2][0-9]-((0[1-9])|10|11|12)-[0-3][0-9]";
        for(int i=0;i<20;i++) {
            String generatedString = RandomValueGenerator.randomRegexPattern(pattern);
            LOGGER.debug("generatedString=" + generatedString);
        }
        pattern="[1-9][0-9]*";
        for(int i=0;i<20;i++) {
            String generatedString = RandomValueGenerator.randomRegexPattern(pattern);
            LOGGER.debug("generatedString=" + generatedString);
        }
        pattern="user[1-2][0-9](\\@gmail\\.com|\\@hotmail\\.com)";
        for(int i=0;i<100;i++) {
            String generatedString = RandomValueGenerator.randomRegexPattern(pattern);
            LOGGER.debug("generatedString=" + generatedString);
        }

    }
}