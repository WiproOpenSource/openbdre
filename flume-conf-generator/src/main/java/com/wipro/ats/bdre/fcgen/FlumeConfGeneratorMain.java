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

import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by KA294215 on 08-09-2015.
 */

public class FlumeConfGeneratorMain extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(FlumeConfGeneratorMain.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "parent-process-id", "Process Id of the process to begin"},
            {"u", "username", "Username"}
    };
    @Autowired
    ProcessDAO processDAO;

    public FlumeConfGeneratorMain() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public static void main(String[] args) throws FileNotFoundException {

        FlumeConfGeneratorMain flumeConfGeneratorMain = new FlumeConfGeneratorMain();
        flumeConfGeneratorMain.execute(args);
    }

    public void flumeConfGenerator(String[] params) throws SecurityException, FileNotFoundException {
        CommandLine commandLine = new FlumeConfGeneratorMain().getCommandLine(params, PARAMS_STRUCTURE);
        String pid = commandLine.getOptionValue("parent-process-id");
        LOGGER.debug("processId is " + pid);
        String username = commandLine.getOptionValue("username");
        LOGGER.debug("username is " + username);
        processDAO.securityCheck(Integer.parseInt(pid),username, "execute");
        //Getting sub-process for process-id
        List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{"--parent-process-id", pid});
        // Getting properties related with flume action for every process
        StringBuilder addFlumeProperties = new StringBuilder();
        for (ProcessInfo processInfo : processInfos) {
            if (processInfo.getParentProcessId() == 0) {
                continue;
            }
            GetProperties getProperties = new GetProperties();
            java.util.Properties flumeProperties = getProperties.getProperties(processInfo.getProcessId().toString(), "flume");
            Enumeration e = flumeProperties.propertyNames();

            if (!flumeProperties.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    addFlumeProperties.append(key + "=" + flumeProperties.get(key) + "\n");
                }
                addFlumeProperties.append("agent.sinks.sink.hdfs.processId="+pid+"\n");
                // writing flume conf properties in flum.conf file

            }
        }
        String outputFile = "flume-" + processInfos.get(0).getProcessId() + ".conf";
        PrintWriter confOut = new PrintWriter(outputFile);
        confOut.println(outputFile);
        confOut.close();
        LOGGER.info("XML is written to " + outputFile);

    }
    @Override
    public String execute(String[] args){
       return null;
    }
}
