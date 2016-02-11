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

import com.wipro.ats.bdre.md.beans.table.IntermediateInfo;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IntermediateTest {
    private static final Logger LOGGER = Logger.getLogger(IntermediateTest.class);

    @Ignore
    @Test
    public void testExecute() throws Exception {
        String[] args = {"--uuid", "1vghdtyere5", "--key", "jvsds34765fr", "--value", "jauxa78"};
        com.wipro.ats.bdre.md.api.Intermediate intermediate = new com.wipro.ats.bdre.md.api.Intermediate();
        intermediate.execute(args);
        LOGGER.debug("executed!");
    }

    @Ignore
    @Test
    public void testKeyValue() throws Exception {
        com.wipro.ats.bdre.md.api.Intermediate intermediate = new com.wipro.ats.bdre.md.api.Intermediate();
        List<IntermediateInfo> intermediateInfos = new ArrayList<IntermediateInfo>();
        for (int i = 0; i < 5; i++) {
            IntermediateInfo intermediateInfo = new IntermediateInfo();
            intermediateInfo.setUuid("testing the log " + i);
            intermediateInfo.setKey("key for " + i);
            intermediateInfo.setValue("value for" + i);

            intermediateInfos.add(i, intermediateInfo);
        }
        LOGGER.debug("0 " + intermediateInfos.get(0).getKey() + "1 is " + intermediateInfos.get(2).getKey() + "2 is " + intermediateInfos.get(4).getKey());
        intermediate.keyValue(intermediateInfos);

    }

}