/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
public class DataQualityActionNode extends GenericActionNode {
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DataQualityActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "data-quality" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }


    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }

        OozieNode fileListNode = null;
        for (OozieNode oozieNode : actionNode.getContainingNodes()) {
            if (oozieNode instanceof LOFActionNode) {
                fileListNode = oozieNode;
            }
        }


        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <java xmlns=\"uri:oozie:workflow:0.4\">\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n" +
                "<main-class>com.wipro.ats.bdre.dq.DQMain</main-class>" +
                "            <arg>--process-id</arg>\n" +
                "            <arg>" + getId() + "</arg>\n" +
                "            <arg>--source-file-path</arg>\n" +
                "            <arg>${wf:actionData(\"" + fileListNode.getName() + "\")[\"file-list\"]}</arg>\n" +
                "            <arg>--destination-directory</arg>\n" +
                "            <arg>/raw/${wf:actionData(\"init-job\")[\"instance-exec-id\"]}</arg>\n" +


                "            <capture-output/>\n" +
                "        </java >\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");
        return ret.toString();
    }


}
