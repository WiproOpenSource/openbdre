package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;
import org.apache.oozie.cli.OozieCLI;
import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 7/1/16.
 */
public class PythonWorkflowPrinterTest {
    private static final Logger LOGGER = Logger.getLogger(PythonWorkflowPrinterTest.class);
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
        ProcessInfo parent = new ProcessInfo(9, "sample parent", "ETL Process", 1, 2, 0, false, 0, "10");
        ProcessInfo sub1 = new ProcessInfo(10, "sample sub", "RAW_LOAD_ACTION Process", 1, 25, 9, false, 0, "11");
        ProcessInfo sub2 = new ProcessInfo(11, "sample sub", "STAGE_LOAD_ACTION Process", 1, 25, 9, false, 0, "12,13");
        ProcessInfo sub3 = new ProcessInfo(12, "sample sub", "BASE_LOAD_ACTION Process", 1, 25, 9, false, 0, "9");
        ProcessInfo sub4 = new ProcessInfo(13, "sample sub", "BASE_LOAD_ACTION Process2", 1, 25, 9, false, 0, "9");

        //ProcessInfo sub4 = new ProcessInfo(5, "sample sub", "Semantic Process", 1, 1, 1, false, 0, "6");
        // ProcessInfo sub5 = new ProcessInfo(6, "sample sub", "Semantic Process", 1, 1, 1, false, 0, "7");
        //ProcessInfo sub6 = new ProcessInfo(7, "sample sub", "Semantic Process", 1, 1, 1, false, 0, "1");
        processInfos.add(parent);
        processInfos.add(sub1);
        processInfos.add(sub2);
        processInfos.add(sub3);
        processInfos.add(sub4);
        //processInfos.add(sub5);
        //processInfos.add(sub6);
        return processInfos;
    }

    private static List<ProcessInfo> getProcessBeans3() {
        //Unit test with test data
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
        ProcessInfo parent = new ProcessInfo(1, "sample parent", "Parent Process", 1, 2, 0, false, 0, "2");
        ProcessInfo sub1 = new ProcessInfo(2, "sample sub", "sub Process", 1, 25, 1, false, 0, "1");

        processInfos.add(parent);
        processInfos.add(sub1);

        return processInfos;
    }

    @Test
    public void testExecute1() throws Exception {
        List<ProcessInfo> processInfos = getProcessBeans2();
        String workflowXML = new PythonWorkflowPrinter().execute(processInfos, "test-workflow").getXml().toString();
        LOGGER.info("workflowxml is "+workflowXML);
        /*PrintWriter out = new PrintWriter("unitTest1.workflow.xml");
        out.println(workflowXML);
        out.close();
        OozieCLI oozieCLI = new OozieCLI();
        oozieCLI.run(new String[]{"validate", "unitTest1.workflow.xml"});*/
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
