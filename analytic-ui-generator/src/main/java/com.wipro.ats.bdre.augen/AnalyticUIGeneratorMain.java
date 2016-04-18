package com.wipro.ats.bdre.augen;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * Created by cloudera on 4/15/16.
 */
public class AnalyticUIGeneratorMain extends BaseStructure {

    private static final Logger LOGGER = Logger.getLogger(AnalyticUIGeneratorMain.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "parent-process-id", "Process Id of the process to begin"}
    };

    public static void main(String[] args)  {
        CommandLine commandLine = new AnalyticUIGeneratorMain().getCommandLine(args, PARAMS_STRUCTURE);
        String pid = commandLine.getOptionValue("parent-process-id");
        LOGGER.debug("processId is " + pid);
        String outputFile = "";


        //Getting sub-process for process-id
        List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{"--parent-process-id", pid});
        // Getting properties related with flume action for every process
        StringBuilder addFlumeProperties = new StringBuilder();
        for (ProcessInfo processInfo : processInfos) {
            if (processInfo.getParentProcessId() == 0) {
                continue;
            }
            GetProperties getProperties = new GetProperties();
            java.util.Properties flumeProperties = getProperties.getProperties(processInfo.getProcessId().toString(), "analytics-app");
            Enumeration e = flumeProperties.propertyNames();

            if (!flumeProperties.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();

                }
                addFlumeProperties.append("agent.sinks.sink.hdfs.processId="+pid+"\n");
                // writing flume conf properties in flum.conf file

            }
        }


    }
}
