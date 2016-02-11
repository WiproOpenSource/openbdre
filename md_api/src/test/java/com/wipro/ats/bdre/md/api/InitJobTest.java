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

import com.wipro.ats.bdre.md.beans.InitJobInfo;
import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class InitJobTest {
    private static final Logger LOGGER = Logger.getLogger(InitJobTest.class);

    @Test
    @Ignore
    public void testExecute() throws Exception {

        String[] args = {"--max-batch", "1", "--process-id", "10802"};
        InitJob bs = new InitJob();
        List<InitJobRowInfo> initJobRowInfos = bs.execute(args);
        for (InitJobRowInfo initJobRowInfo : initJobRowInfos) {
            LOGGER.debug("process id is " + initJobRowInfo.getProcessId() + " and file associated is"
                    + initJobRowInfo.getFileList() + "batch is" + initJobRowInfo.getSourceBatchId());
        }
        InitJobInfo initJob = bs.parseBean(initJobRowInfos);
        LOGGER.debug("file list is" + initJob.getFileListMap().get("10805"));
        LOGGER.debug("executed!");
    }
}
