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


/**
 * Created by cloudera on 3/31/16.
 */


public class DestTableLoadActionNode extends GenericActionNode {

    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DestTableLoadActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    @Override
    public String getName() {

        String nodeName = "dest-table-load" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

  @Override
   public String getXML() {

       return "\n<action name=\"" + getName() + "\">\n" +
               "        <java>\n" +
               "            <job-tracker>${jobTracker}</job-tracker>\n" +
               "            <name-node>${nameNode}</name-node>\n" +
               "            <main-class>com.wipro.ats.bdre.clustermigration.oozie.OozieDestTableLoad</main-class>\n" +
               "            <arg>--source-path</arg>\n" +
               "            <arg>${wf:actionData(\'migration-preprocessor\')[\'dest-stg-folder-content-path\']}</arg>\n" +
               "            <arg>--dest-path</arg>\n" +
               "            <arg>${wf:actionData(\'migration-preprocessor\')[\'dest-table-path\']}</arg>\n" +
               "            <arg>--dest-fs</arg>\n" +
               "            <arg>${wf:actionData(\'migration-preprocessor\')[\'dest-fs\']}</arg>\n" +
               "            <capture-output />\n" +
               "        </java>\n" +
               "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
               "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
               "</action>";
   }

    public Integer isSecurityEnabled(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties properties = getProperties.getProperties(pid.toString(), configGroup);
        return properties.size();
    }

}
