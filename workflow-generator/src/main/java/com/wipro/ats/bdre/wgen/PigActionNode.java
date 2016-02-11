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
 * Created by arijit on 12/21/14.s
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML. 
*/

public class PigActionNode extends GenericActionNode {

    private static final Logger LOGGER = Logger.getLogger(PigActionNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public PigActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "pig-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <pig>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n");
        ret.append(getScriptPath(getId(), "script"));
        /*ret.append("            <param>exec-id=${wf:actionData(\"init-job\")[\"instance-exec-id\"]}</param>\n" +
                "            <param>target-batch-id=${wf:actionData(\"init-job\")[\"target-batch-id\"]}</param>\n" +
                "            <param>min-batch-id=${wf:actionData(\"init-job\")[\"min-batch-id-map." + getId() + "\"]}</param>\n" +
                "            <param>max-batch-id=${wf:actionData(\"init-job\")[\"max-batch-id-map." + getId() + "\"]}</param>\n" +
                "            <param>min-pri=${wf:actionData(\"init-job\")[\"min-source-instance-exec-map." + getId() + "\"]}</param>\n" +
                "            <param>max-pri=${wf:actionData(\"init-job\")[\"max-source-instance-exec-map." + getId() + "\"]}</param>\n" +
                "            <param>min-batch-marking=${wf:actionData(\"init-job\")[\"min-batch-marking-map." + getId() + "\"]}</param>\n" +
                "            <param>max-batch-marking=${wf:actionData(\"init-job\")[\"max-batch-marking-map." + getId() + "\"]}</param>\n" +
                "            <param>target-batch-marking=${wf:actionData(\"init-job\")[\"target-batch-marking\"]}</param>\n" +
                "            <param>last-recoverable-sp-id=${wf:actionData(\"init-job\")[\"last-recoverable-sp-id\"]}</param>\n");*/
        ret.append(getParams(getId(), "param"));
        ret.append("        </pig>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }

    /**
     * This method gets path for Pig Script
     *
     * @param pid         process-id of Pig Script
     * @param configGroup config_group entry in properties table "script" for query path
     * @return String containing script path to be appended to workflow string
     */
    public String getScriptPath(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties scriptPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = scriptPath.propertyNames();
        StringBuilder addScriptPath = new StringBuilder();
        if (scriptPath.size() != 0) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addScriptPath.append("            <script>" + scriptPath.getProperty(key) + "</script>\n");

            }
        } else {
            addScriptPath.append("            <script>pig/script" + getId() + ".pig</script>\n");
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
        if (listForParams.size() != 0) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if (key.equals("run_id")) {
                    addParams.append("<argument>-param</argument>");
                    addParams.append("<argument>" + key + "=" + "${wf:actionData(\"init-job\")[\"min-batch-id-map." + getId() + "\"]}" + "</argument>\n");
                } else {
                    addParams.append(" <param>" + key + "=" + listForParams.getProperty(key) + "</param>\n");
                }
            }
        }
        return addParams.toString();
    }
}