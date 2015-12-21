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

package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.oozie.cli.OozieCLI;
import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WorkflowPrinterTest {
    private static List<ProcessInfo> getProcessBeans1() {
        //Unit test with test data
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
        ProcessInfo parent = new ProcessInfo(1, "sample parent", "Semantic Process", 1, 2, 0, false, 0, "2");
        ProcessInfo sub1 = new ProcessInfo(2, "sample sub", "Semantic Process", 1, 9, 1, false, 0, "3");
        ProcessInfo sub2 = new ProcessInfo(3, "sample sub", "Semantic Process", 1, 9, 1, false, 0, "4");
        ProcessInfo sub3 = new ProcessInfo(4, "sample sub", "Semantic Process", 1, 9, 1, false, 0, "5");
        ProcessInfo sub4 = new ProcessInfo(5, "sample sub", "Semantic Process", 1, 9, 1, false, 0, "6");
        ProcessInfo sub5 = new ProcessInfo(6, "sample sub", "Semantic Process", 1, 9, 1, false, 0, "7");
        ProcessInfo sub6 = new ProcessInfo(7, "sample sub", "Semantic Process", 1, 9, 1, false, 0, "1");
        processInfos.add(parent);
        processInfos.add(sub1);
        processInfos.add(sub2);
        processInfos.add(sub3);
        processInfos.add(sub4);
        processInfos.add(sub5);
        processInfos.add(sub6);
        return processInfos;
    }

    private static List<ProcessInfo> getProcessBeans2() {
        //Unit test with test data
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
        ProcessInfo parent = new ProcessInfo(1, "sample parent", "ETL Process", 1, 5, 0, false, 0, "2");
        ProcessInfo sub1 = new ProcessInfo(2, "sample sub", "RAW_LOAD_ACTION Process", 1, 6, 1, false, 0, "3,4");
        ProcessInfo sub2 = new ProcessInfo(3, "sample sub", "STAGE_LOAD_ACTION Process", 1, 7, 1, false, 0, "1");
        ProcessInfo sub3 = new ProcessInfo(4, "sample sub", "BASE_LOAD_ACTION Process", 1, 8, 1, false, 0, "1");
        //ProcessInfo sub4 = new ProcessInfo(5, "sample sub", "Semantic Process", 1, 1, 1, false, 0, "6");
        // ProcessInfo sub5 = new ProcessInfo(6, "sample sub", "Semantic Process", 1, 1, 1, false, 0, "7");
        //ProcessInfo sub6 = new ProcessInfo(7, "sample sub", "Semantic Process", 1, 1, 1, false, 0, "1");
        processInfos.add(parent);
        processInfos.add(sub1);
        processInfos.add(sub2);
        processInfos.add(sub3);
        //processInfos.add(sub4);
        //processInfos.add(sub5);
        //processInfos.add(sub6);
        return processInfos;
    }

    @Test
    @Ignore
    public void testExecute1() throws Exception {
        List<ProcessInfo> processInfos = getProcessBeans1();
        String workflowXML = new WorkflowPrinter().execute(processInfos, "test-workflow").getXml().toString();
        PrintWriter out = new PrintWriter("unitTest1.workflow.xml");
        out.println(workflowXML);
        out.close();
        OozieCLI oozieCLI = new OozieCLI();
        oozieCLI.run(new String[]{"validate", "unitTest1.workflow.xml"});
    }

    @Test
    @Ignore
    public void testExecute2() throws Exception {
        List<ProcessInfo> processInfos = getProcessBeans2();
        String workflowXML = new WorkflowPrinter().execute(processInfos, "test-workflow").getXml().toString();
        PrintWriter out = new PrintWriter("unitTest2.workflow.xml");
        out.println(workflowXML);
        out.close();
        OozieCLI oozieCLI = new OozieCLI();
        oozieCLI.run(new String[]{"validate", "unitTest2.workflow.xml"});
    }
}