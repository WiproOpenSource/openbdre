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
import org.apache.log4j.Logger;

import java.util.Enumeration;

/**
 * Created by cloudera on 3/31/16.
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML.
*/

public class SourceStageLoadActionNode extends GenericActionNode {

    private static final Logger LOGGER = Logger.getLogger(SourceStageLoadActionNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public SourceStageLoadActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "source-stg-load-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

 /*   @Override
    public String getXML() {

        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName());
        if (isSecurityEnabled(this.getProcessInfo().getParentProcessId(), "security") != 0) {
            ret.append(" cred='hive_credentials'");
        }
        ret.append("\">\n" +
                "        <hive xmlns=\"uri:oozie:hive-action:0.2\">\n" +
                "            <job-tracker>${wf:actionData(\"migration-preprocessor\")[\"job-tracker-address\"]}</job-tracker>\n" +
                "            <name-node>${wf:actionData(\"migration-preprocessor\")[\"name-node-address\"]}</name-node>\n");
        ret.append(getQueryPath());
        ret.append("            <param>exec-id=${wf:actionData(\"init-job\")[\"instance-exec-id\"]}</param>\n");
        ret.append("            <param>source-stg-db=${wf:actionData(\"migration-preprocessor\")[\"source-stg-db\"]}</param>\n");
        ret.append("            <param>source-stg-table=${wf:actionData(\"migration-preprocessor\")[\"source-stg-table\"]}</param>\n");
        ret.append("            <param>stg-all-part-cols=${wf:actionData(\"migration-preprocessor\")[\"stg-all-part-cols\"]}</param>\n");
        ret.append("            <param>source-reg-cols=${wf:actionData(\"migration-preprocessor\")[\"source-reg-cols\"]}</param>\n");
        ret.append("            <param>source-bp-cols=${wf:actionData(\"migration-preprocessor\")[\"source-bp-cols\"]}</param>\n");
        ret.append("            <param>source-db=${wf:actionData(\"migration-preprocessor\")[\"source-db\"]}</param>\n");
        ret.append("            <param>source-table=${wf:actionData(\"migration-preprocessor\")[\"source-table\"]}</param>\n");
        ret.append("            <param>filter-condition=${wf:actionData(\"migration-preprocessor\")[\"filter-condition\"]}</param>\n");
        ret.append("        </hive>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }
*/

    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "<java>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n" +
                "            <main-class>com.wipro.ats.bdre.clustermigration.oozie.OozieSourceStageLoad</main-class>\n" +
                "            <arg>--src-stage-db</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"source-stg-db\"]}</arg>\n" +
                "            <arg>--src-stg-table</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"source-stg-table\"]}</arg>\n" +
                "            <arg>--stg-part-cols</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"stg-all-part-cols\"]}</arg>\n" +
                "            <arg>--src-reg-cols</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"source-reg-cols\"]}</arg>\n" +
                "            <arg>--stg-bp-cols</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"source-bp-cols\"]}</arg>\n" +
                "            <arg>--instance-exec-id</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"instance-exec-id\"]}</arg>\n" +
                "            <arg>--source-db</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"source-db\"]}</arg>\n" +
                "            <arg>--source-table</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"source-table\"]}</arg>\n" +
                "            <arg>--filter-condition</arg>\n" +
                "            <arg>${wf:actionData(\"migration-preprocessor\")[\"filter-condition\"]}</arg>\n" +
                "            <arg>--parent-process-id</arg>\n" +
                "            <arg>"+getProcessInfo().getParentProcessId()+"</arg>\n" +
                "            <capture-output/>\n" +
                "        </java>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }

    /**
     * This method gets path for Hive Query
     * @return String containing query path to be appended to workflow string
     */
    public String getQueryPath() {

        StringBuilder addQueryPath = new StringBuilder();
        addQueryPath.append("            <script>hql/source-stage-load.hql</script>\n");
        return addQueryPath.toString();
    }

    public Integer isSecurityEnabled(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties properties = getProperties.getProperties(pid.toString(), configGroup);
        return properties.size();
    }

}

