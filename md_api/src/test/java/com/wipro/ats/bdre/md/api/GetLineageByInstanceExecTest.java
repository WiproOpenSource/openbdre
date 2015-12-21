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

import com.wipro.ats.bdre.md.beans.GetLineageByInstanceExecInfo;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * Created by SU324335 on 24-Nov-15.
 */
public class GetLineageByInstanceExecTest {
    @Ignore
    @Test
    public void testExecute() throws Exception {
        GetLineageByInstanceExec getLineageByInstanceExec = new GetLineageByInstanceExec();
        String[] args = {"-eid", "1", "-env", null};
        List<GetLineageByInstanceExecInfo> lineageByInstanceExecInfoList = getLineageByInstanceExec.execute(args);
        System.out.println("lineageByInstanceExecInfoList = " + lineageByInstanceExecInfoList);
    }
}
