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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.GetPropertiesInfo;
import com.wipro.ats.bdre.md.beans.InitJobInfo;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OozieUtilTest {
    private static final Logger LOGGER = Logger.getLogger(OozieUtilTest.class);

    @Test
    public void testPersistBeanData() throws Exception {
        InitJobInfo initJobInfo = new InitJobInfo();
        Map<String, String> stringStringMap = new HashMap<String, String>();
        stringStringMap.put("key1", "val1");
        stringStringMap.put("key2", "val2");
        //initJobInfo.setMinBatchId(stringStringMap);
        initJobInfo.setInstanceExecId(1000L);

        OozieUtil oozieUtil = new OozieUtil();
        try {
            oozieUtil.persistBeanData(initJobInfo, true);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }
    }

    @Test
    public void testPersistBeanList() throws Exception {
        List<GetPropertiesInfo> getPropertiesInfos = new ArrayList<GetPropertiesInfo>();
        for (int i = 0; i < 10; i++) {
            GetPropertiesInfo propertiesInfo = new GetPropertiesInfo();
            propertiesInfo.setKey("prop" + i);
            propertiesInfo.setValue("value" + i);
            propertiesInfo.setProcessId(9);
            getPropertiesInfos.add(propertiesInfo);
        }
        OozieUtil oozieUtil = new OozieUtil();
        try {
            oozieUtil.persistBeanList(getPropertiesInfos, true);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }

    }
}