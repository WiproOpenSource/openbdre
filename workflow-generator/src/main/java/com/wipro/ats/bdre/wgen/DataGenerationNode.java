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
 * Created by IshitaParekh on 04-03-2015.
 */
public class DataGenerationNode extends GenericActionNode {
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DataGenerationNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {
        String nodeName = "hive-generation-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }


    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }

        OozieNode fileRegNode = null;
        for (OozieNode oozieNode : actionNode.getContainingNodes()) {
            if (oozieNode instanceof FileRegistrationNode) {
                fileRegNode = oozieNode;
            }
        }


        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <java>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n" +
                "            <main-class>com.wipro.ats.bdre.datagen.GeneratorMain</main-class>\n" +


                "            <arg>--sub-process-id</arg>\n" +
                "            <arg>" + getId() + "</arg>\n" +
                "            <capture-output />\n" +
                "        </java>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");
        return ret.toString();
    }


}
