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

package com.wipro.ats.bdre.daggen;


import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.apache.oozie.cli.OozieCLI;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by arijit on 12/24/14.
 */
public class WorkflowGenerator extends BaseStructure {
    private static final Logger LOGGER = Logger.getLogger(WorkflowGenerator.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "parent-process-id", "Process Id of the process to begin"},
            {"f", "file-name", "Output XML file name where the xml would be saved"},
    };

    /**
     * This method generates workflow
     *
     * @param args String array contains process id, file-name and environment id with their commandline notation.
     * @throws FileNotFoundException xmlFile is not found then it will throw this exception
     */
    public static void main(String[] args) throws FileNotFoundException {
        CommandLine commandLine = new WorkflowGenerator().getCommandLine(args, PARAMS_STRUCTURE);
        String pid = commandLine.getOptionValue("parent-process-id");
        LOGGER.debug("processId is " + pid);
        String outputFile = commandLine.getOptionValue("file-name");
        LOGGER.debug("Output file " + outputFile);

        //Fetching process details from metadata using API calls
        List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{"--parent-process-id", pid});
        LOGGER.info("Workflow Type Id is " + processInfos.get(0).getWorkflowId() + " for pid=" + processInfos.get(0).getProcessId());
        Workflow workflow = new WorkflowPrinter().execute(processInfos, "workflow-" + pid);
        if (processInfos.get(0).getWorkflowId() == 1) {

            String workflowXML = workflow.getXml().toString();
            String workflowdot = workflow.getDot().toString();

            PrintWriter xmlOut = new PrintWriter(outputFile);
            PrintWriter dotOut = new PrintWriter(outputFile + ".dot");
            xmlOut.println(workflowXML);
            dotOut.println(workflowdot);
            xmlOut.close();
            dotOut.close();
            OozieCLI oozieCLI = new OozieCLI();
            oozieCLI.run(new String[]{"validate", outputFile});
            LOGGER.info("XML is written to " + outputFile);
            LOGGER.info("DOT is written to " + outputFile + ".dot");
        } else {
            LOGGER.debug("This is not a Oozie process, hence no xml representation needed");
            String workflowdot = workflow.getDot().toString();
            PrintWriter dotOut = new PrintWriter(outputFile + ".dot");
            dotOut.println(workflowdot);
            dotOut.close();
            LOGGER.info("DOT is written to " + outputFile + ".dot");
        }
    }
}