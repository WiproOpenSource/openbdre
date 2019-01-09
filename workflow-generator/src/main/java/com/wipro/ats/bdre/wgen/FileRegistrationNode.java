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

import com.wipro.ats.bdre.md.beans.ProcessInfo;

/**
 * Created by IshitaParekh on 04-03-2015.
 */
public class FileRegistrationNode extends GenericActionNode {
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class.
     */
    public FileRegistrationNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public String getName() {

        String nodeName = "fileRegistration-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        OozieNode node = null;
        for (OozieNode oozieNode : actionNode.getContainingNodes()) {
            if (oozieNode.getToNode() instanceof FileRegistrationNode) {
                node = oozieNode;
            }
        }
        return "\n<action name=\"" + getName() + "\">\n" +
                "        <java>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n" +
                "            <main-class>com.wipro.ats.bdre.md.api.oozie.OozieRegisterFile</main-class>\n" +


                "<arg>--sub-process-id</arg>" +
                "<arg>${wf:actionData(\"" + node.getName() + "\")[\"sub-process-id\"]}</arg>" +
                "            <arg>--path</arg>\n" +

                "            <arg>${wf:actionData(\"" + node.getName() + "\")[\"path\"]}</arg>\n" +
                "            <arg>--file-size</arg>\n" +
                "            <arg>${wf:actionData(\"" + node.getName() + "\")[\"file-size\"]}</arg>\n" +
                "            <arg>--file-hash</arg>\n" +
                "            <arg>${wf:actionData(\"" + node.getName() + "\")[\"file-hash\"]}</arg>\n" +
                "            <arg>--batch-id</arg>\n" +
                "            <arg>${wf:actionData(\"init-job\")[\"target-batch-id\"]}</arg>\n" +
                "<arg>--server-id</arg>" +
                "<arg>123461</arg>" +
                "<arg>--creation-timestamp</arg>" +
                "<arg>${wf:actionData(\"" + node.getName() + "\")[\"creation-ts\"]}</arg>" +
                "            <capture-output />\n" +
                "        </java>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>";

    }

}
