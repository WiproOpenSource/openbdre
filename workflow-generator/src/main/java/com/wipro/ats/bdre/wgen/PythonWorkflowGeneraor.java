package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.apache.oozie.cli.OozieCLI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonWorkflowGeneraor extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(PythonWorkflowGeneraor.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "parent-process-id", "Process Id of the process to begin"},
            {"f", "file-name", "Output XML file name where the xml would be saved"},
            {"u", "username", "Username"}
    };
    @Autowired
    ProcessDAO processDAO;

    public PythonWorkflowGeneraor() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * This method generates workflow
     *
     * @param args String array contains process id, file-name and environment id with their commandline notation.
     * @throws java.io.FileNotFoundException xmlFile is not found then it will throw this exception
     */
    public static void main(String[] args) throws FileNotFoundException {
        PythonWorkflowGeneraor workflowGenerator = new PythonWorkflowGeneraor();
        workflowGenerator.workflowGenerator(args);
    }
    public void workflowGenerator(String [] args) throws FileNotFoundException{
        CommandLine commandLine = new WorkflowGenerator().getCommandLine(args, PARAMS_STRUCTURE);
        String pid = commandLine.getOptionValue("parent-process-id");
        LOGGER.debug("processId is " + pid);
        String outputFile = commandLine.getOptionValue("file-name");
        LOGGER.debug("Output file " + outputFile);
        String username = commandLine.getOptionValue("username");
        LOGGER.debug("username is " + username);
        processDAO.securityCheck(Integer.parseInt(pid),username, "execute");

        //Fetching process details from metadata using API calls
        List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{"--parent-process-id", pid , "--username",username});
        LOGGER.info("Workflow Type Id is " + processInfos.get(0).getWorkflowId() + " for pid=" + processInfos.get(0).getProcessId());
        Workflow workflow = new PythonWorkflowPrinter().execute(processInfos, "workflow-" + pid);
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

    @Override
    public String execute(String[] args){
        return null;
    }
}
