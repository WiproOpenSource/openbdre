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

import org.apache.log4j.Logger;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.util.Enumeration;

/**
 * Created by MI294210 on 22/01/2015
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML. 
*/

public class HadoopStreamingActionNode extends GenericActionNode {

    private static final Logger LOGGER = Logger.getLogger(HadoopStreamingActionNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public HadoopStreamingActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "hadoopStream-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        LOGGER.info("Inside HadoopStreaming");
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <map-reduce>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n");
        ret.append("            <streaming>\n");
        ret.append("            <mapper>"+getScriptPath(getId(), "mapper").replace("hadoopstream/","")+"</mapper>\n");
        ret.append("            <reducer>"+getScriptPath(getId(), "reducer").replace("hadoopstream/","")+"</reducer>\n");
        ret.append("            </streaming>\n");
        ret.append("         <configuration>\n");
        ret.append(getParams(getId(), "param"));
        ret.append("         </configuration>\n");
        ret.append("            <file>"+getScriptPath(getId(), "mapper")+"</file>\n");
        ret.append("            <file>"+getScriptPath(getId(), "reducer")+"</file>\n");
        ret.append(getSupplementaryFiles(getId(),"extraFiles"));
        ret.append("        </map-reduce>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }
    /**
     * This method gets path for Streaming files
     *
     * @param pid         process-id of Streaming files
     * @param configGroup config_group entry in properties table mapper and reducer for query path
     * @return String containing script path to be appended to workflow string
     */
    public String getScriptPath(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties scriptPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = scriptPath.propertyNames();
        StringBuilder addScript = new StringBuilder();

        if (!scriptPath.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addScript.append(scriptPath.getProperty(key));
            }
        }
        return addScript.toString();
    }

    /**
     * This method gets all the properties required for Hadoop Streaming
     *
     * @param pid         process-id of Hadoop streaming
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getParams(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();
        StringBuilder addProperty = new StringBuilder();
        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addProperty.append("               <property>\n"+
                                   "                   <name>"+key+"</name>\n" +
                                   "                   <value>"+listForParams.getProperty(key) + "</value>\n"+
                                   "               </property>\n");

            }
        }
        return addProperty.toString();
    }

    /**
     * This method gets details about additional files to be uploaded
     *
     * @param pid         process-id of Hadoop streaming
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
