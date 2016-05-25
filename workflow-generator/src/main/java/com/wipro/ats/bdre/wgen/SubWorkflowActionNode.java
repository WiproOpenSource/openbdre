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

package com.wipro.ats.bdre.wgen;

        import com.wipro.ats.bdre.md.api.GetProcess;
        import com.wipro.ats.bdre.md.api.GetProperties;
        import com.wipro.ats.bdre.md.beans.ProcessInfo;
        import org.apache.log4j.Logger;

/**
 * Created by MI294210 on 05/24/16.
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML.
*/

public class SubWorkflowActionNode extends GenericActionNode {

    private static final Logger LOGGER = Logger.getLogger(SubWorkflowActionNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;




    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */

    public SubWorkflowActionNode(ActionNode actionNode) {

        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "subworkflow-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));



    }

    @Override
    public String getXML() {
        LOGGER.info("Inside SubWorkflowAction");

        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }

        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <sub-workflow>\n" +
                "            <app-path>${baseAppPath}" +  getSubWorkflowPath(processInfo.getProcessId(), processInfo.getParentProcessId().toString())       +"</app-path>\n" +
                "            <propagate-configuration/>\n" +
                "            <configuration>\n" +
                "            </configuration>\n" +
                "            <capture-output />\n" +
                "       </sub-workflow>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();

    }


    /**
     * This method gets process type of the sub workflow type process
     *
     * @param pid         process-id of Sub Workflow
     * @param configGroup config_group entry in properties table "workflow_pid" for process type of the sub workflow type process
     * @return Integer containing query path to be appended to workflow string
     */
    public String getSubWorkflowPath(Integer pid, String configGroup) {

        GetProperties getProperties = new GetProperties();
        java.util.Properties fetchWorkflowId = getProperties.getProperties(pid.toString(), configGroup);
        String parentProcessId=fetchWorkflowId.getProperty("workflow_pid");
        LOGGER.info("Workflow parent process: "+ parentProcessId);
        GetProcess getProcess= new GetProcess();
        ProcessInfo workflowProcess = getProcess.getProcess(Integer.parseInt(parentProcessId));
        Integer busDomainId=workflowProcess.getBusDomainId();
        Integer processTypeId=workflowProcess.getProcessTypeId();
        StringBuilder workflowPath=new StringBuilder();
        workflowPath.append("/"+ busDomainId +"/"+ processTypeId +"/" +parentProcessId);

        return workflowPath.toString();
    }


}

