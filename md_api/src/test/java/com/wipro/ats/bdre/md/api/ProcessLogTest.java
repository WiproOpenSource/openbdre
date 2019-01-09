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

import com.wipro.ats.bdre.md.beans.ProcessLogInfo;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;


public class ProcessLogTest {
    private static final Logger LOGGER = Logger.getLogger(ProcessLogTest.class);


    @Ignore
    @Test
    public void testExecute() throws Exception {
        String[] args = {"--process-id", "1", "--add-ts", "2015-03-10 08:00:00", "--log-category", "Category 1", "--message", "Message 1", "--message-id", "Id 1", "--instance-ref", "1"};
        ProcessLog processLog = new ProcessLog();
        processLog.execute(args);
        LOGGER.debug("executed!");
    }


    @Ignore
    @Test
    public void testGetLastValue() throws Exception {
        ProcessLog processLog = new ProcessLog();
        ProcessLogInfo processLogInfo = processLog.getLastValue("9000", "last value", "ImpTable");
        LOGGER.debug("executed!");
    }


    @Ignore
    @Test
    public void testLog() throws Exception {
        ProcessLog processLog = new ProcessLog();
        ProcessLogInfo processLogInfo = new ProcessLogInfo();
        processLogInfo.setProcessId(new Integer("1"));
        processLogInfo.setAddTs(new Date());
        processLogInfo.setInstanceRef(1L);
        processLogInfo.setLogCategory("Category1");
        //Logging num good records
        processLogInfo.setMessageId("Id 1");
        processLogInfo.setMessage("Message 1");
        processLog.log(processLogInfo);

    }
}