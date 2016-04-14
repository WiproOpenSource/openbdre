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

/**
 * Created by cloudera on 3/31/16.
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML.
*/

public class RegisterPartitionsActionNode extends GenericActionNode {

    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public RegisterPartitionsActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    @Override
    public String getName() {

        String nodeName = "reg-partitions" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        LOGGER.info("nodeName is: "+nodeName);
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
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
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n" );
        ret.append(getQueryPath());
        ret.append("            <param>dest-table=${wf:actionData(\"migration-preprocessor\")[\"dest-table\"]}</param>\n");
        ret.append("            <param>dest-db=${wf:actionData(\"migration-preprocessor\")[\"dest-db\"]}</param>\n");
        ret.append("        </hive>\n" +
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
        addQueryPath.append("            <script>hql/repair-table.hql</script>\n");
        return addQueryPath.toString();
    }


    public Integer isSecurityEnabled(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties properties = getProperties.getProperties(pid.toString(), configGroup);
        return properties.size();
    }
}

