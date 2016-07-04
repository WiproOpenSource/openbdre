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

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.util.Enumeration;

/**
 * Created by SR294224 on 12/01/2015
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML. 
*/

public class RActionNode extends GenericActionNode {

    private static final Logger LOGGER = Logger.getLogger(RActionNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public RActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "R-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <shell xmlns=\"uri:oozie:shell-action:0.1\">\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n");
        ret.append("            <exec>Rhadoop.sh</exec>\n");
        ret.append("            <argument>"+getRFile(getId(), "r-file").replace("r/","")+"</argument>\n");
        ret.append(getArguments("param"));
        ret.append("            <file>Rhadoop.sh</file>\n");
        ret.append("            <file>" +getRFile(getId(), "r-file")+"</file>\n");
        ret.append("        </shell>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }

    /**
     * This method gets the required arguments for running the R Script
     *
     * @param configGroup config_group entry in properties table for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getArguments(String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties argumentProperty = getProperties.getProperties(getId().toString(), configGroup);

        String arguments="";
        if(!argumentProperty.isEmpty()) {

            arguments = "            <argument>" + argumentProperty.values().toString().substring(1, argumentProperty.values().toString().length() - 1) + "</argument>\n";
        }
        return arguments;
    }

    /**
     * This method gets the required R Script file for running the R Script
     *
     * @param pid         process-id of R Script
     * @param configGroup config_group entry in properties table "rScript" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getRFile(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties rScript = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = rScript.propertyNames();
        LOGGER.info("rScript = " + rScript.size());
        StringBuilder addRScript = new StringBuilder();
        if (rScript.size() > 1) {
            throw new BDREException("Can Handle only 1 input file in R action, process type=" + processInfo.getProcessTypeId());
        } else if (rScript.isEmpty()) {
            addRScript.append("r/" + getId() + ".R");
        } else {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addRScript.append(rScript.getProperty(key));
            }
        }

        return addRScript.toString();
    }
}