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

package com.wipro.ats.bdre.fcgen;


import com.wipro.ats.bdre.exception.MetadataException;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;

public class FlumeConfGeneratorMainTest {
    private static final Logger LOGGER = Logger.getLogger(FlumeConfGeneratorMainTest.class);
    @Ignore
    @Test
    public void testMain() throws MetadataException,FileNotFoundException{

            FlumeConfGeneratorMain flumeConfGeneratorMain = new FlumeConfGeneratorMain();
            String[] args = {"-p", "125"};
            flumeConfGeneratorMain.main(args);

    }
}