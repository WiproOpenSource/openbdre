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

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.util.Enumeration;

/**
 * Created by arijit on 12/21/14.s
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML. 
*/

public class MRActionNode extends GenericActionNode {


    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public MRActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "mapreduce-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <java>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n");
        ret.append(getClassName(getId(), "program"));

        ret.append(getArgs(getId(), "param"));
        ret.append("<capture-output />");
        ret.append("        </java>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }

    /**
     * This method gets main class for MapReduce Job
     *
     * @param pid         process-id of MapReduce Job
     * @param configGroup config_group entry in properties table "program" for class name
     * @return String containing main class to be appended to workflow string
     */
    public String getClassName(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties className = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = className.propertyNames();
        StringBuilder addClassName = new StringBuilder();
        if (className.size() != 0) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addClassName.append("            <main-class>" + className.getProperty(key) + "</main-class>\n");

            }
        } else {
            addClassName.append("            <main-class>JavaMainClass" + getId() + "</main-class>\n");
        }
        return addClassName.toString();
    }

    /**
     * This method gets all the extra arguments required for MapReduce Job
     *
     * @param pid         process-id of MapReduce Job
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getArgs(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForArgs = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForArgs.propertyNames();
        StringBuilder addArgs = new StringBuilder();
        if (listForArgs.size() != 0) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addArgs.append("            <arg>--" + key + "</arg>\n<arg>" + listForArgs.getProperty(key) + "</arg>\n");
            }
        }
        return addArgs.toString();
    }
}

