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

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.util.Enumeration;

/**
 * Created by Sandeep T on 08/04/2015
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML. 
*/

public class ShellActionNode extends GenericActionNode {

    private ProcessInfo processInfo = new ProcessInfo();
    private static final String SCRIPT = "script";
    private ActionNode actionNode = null;
    private OozieNode toNode;
    public OozieNode getToNode() {
        return toNode;
    }
    public void setToNode(OozieNode toNode) {
        this.toNode = toNode;
    }
    private OozieNode termNode;
    public OozieNode getTermNode() {
        return termNode;
    }
    public void setTermNode(OozieNode termNode) {
        this.termNode = termNode;
    }
    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public ShellActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "shell-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
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
        ret.append("            <exec>"+getScriptPath(getId(), SCRIPT).replace("shell/","")+"</exec>\n");
        ret.append(getParams(getId(), "param"));
        ret.append("            <file>"+getScriptPath(getId(), SCRIPT)+"</file>\n");
        ret.append(getSupplementaryFiles(getId(),"extraFiles"));
        ret.append("        </shell>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }

    /**
     * This method gets path for Shell Script
     *
     * @param pid         process-id of Shell Script
     * @param configGroup config_group entry in properties table "script" for query path
     * @return String containing script path to be appended to workflow string
     */
    public String getScriptPath(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties scriptPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = scriptPath.propertyNames();
        StringBuilder addScriptPath = new StringBuilder();

        if (scriptPath.size() > 1) {
            throw new BDREException("Can Handle only 1 script in shell action processInfo.getProcessTypeId()=" + processInfo.getProcessTypeId());
        } else if (scriptPath.isEmpty()) {
            addScriptPath.append(SCRIPT + getId() + ".sh");
        } else {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addScriptPath.append(scriptPath.getProperty(key));
            }
        }
        return addScriptPath.toString();
    }

    /**
     * This method gets all the extra arguments required for Pig Script
     *
     * @param pid         process-id of Pig Script
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getParams(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();
        StringBuilder addParams = new StringBuilder();
        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addParams.append(" <argument>" + listForParams.getProperty(key) + "</argument>\n");

            }
        }
        return addParams.toString();
    }

    /**
     * This method gets details about additional files to be uploaded
     *
     * @param pid         process-id of Shell Script
     * @param configGroup config_group entry in properties table "more-scripts" for query path
     * @return String containing script path to be appended to workflow string
     */
    public String getSupplementaryFiles(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties addtionalScripts = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = addtionalScripts.propertyNames();
        StringBuilder addScriptPaths = new StringBuilder();

        if (!addtionalScripts.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addScriptPaths.append("            <file>"+addtionalScripts.getProperty(key)+"</file>\n");
            }
        }
        return addScriptPaths.toString();
    }

}