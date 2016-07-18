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

package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.MDConfig;
import org.apache.log4j.Logger;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by MI294210 on 12/07/2016
 */


/*
DAGTaskNodes nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getDAG() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current task node, appropriately formatted as DAG.
*/

public class DAGHadoopStreamingTaskNode extends  GenericActionNode {

    private static final Logger LOGGER = Logger.getLogger(DAGHadoopStreamingTaskNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private static final String SCRIPT = "script";
    private static final String UPLOADBASEDIRECTORY = "upload.base-directory";
    private DAGTaskNode taskNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param taskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGHadoopStreamingTaskNode(DAGTaskNode taskNode) {
        setId(taskNode.getId());
        processInfo = taskNode.getProcessInfo();
        this.taskNode = taskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "hadoopStream-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getDAG() {
        LOGGER.info("Inside HadoopStreaming");
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();

        try {
            FileWriter fw = new FileWriter("/home/cloudera/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }

        ret.append("\ndummy_"+ getName().replace('-','_')+" = DummyOperator(task_id='dummy_"+getName().replace('-','_')+"', dag=dag)\n"+
                   "\ndef "+ getName().replace('-','_')+"_pc():\n" );

        ret.append( "\tcommand='hadoop jar "+getParamValue(getId(),"param","hadoop.streaming.jar") +
                "\t-input "+getParamValue(getId(),"param","mapred.input.dir")+
                "\t-output "+getParamValue(getId(),"param","mapred.output.dir"));

        if(!getParamValue(getId(),"param","mapred.reduce.tasks").isEmpty())
            ret.append("\t-numReduceTasks "+getParamValue(getId(),"param","mapred.reduce.tasks"));

        ret.append("\t-mapper "+MDConfig.getProperty(UPLOADBASEDIRECTORY) + "/" + processInfo.getParentProcessId().toString() + "/" + getScriptPath(getId(), "mapper")+
                "\t-reducer "+MDConfig.getProperty(UPLOADBASEDIRECTORY) + "/" + processInfo.getParentProcessId().toString() + "/" + getScriptPath(getId(), "reducer")+
                "\t-file "+MDConfig.getProperty(UPLOADBASEDIRECTORY) + "/" + processInfo.getParentProcessId().toString() + "/" + getScriptPath(getId(), "mapper")+
                "\t-file "+MDConfig.getProperty(UPLOADBASEDIRECTORY) + "/" + processInfo.getParentProcessId().toString() + "/" + getScriptPath(getId(), "reducer")+
                getSupplementaryFiles(getId(),"extraFiles"));
        ret.append( "\n\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tprint(\"out is \",out)\n"+
                "\tprint(\"err is \",err)\n"+
                "\tif(bash_output.returncode == 0):\n" +
                "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +
                "\telse:\n" +
                "\t\treturn 'dummy_"+getName().replace('-', '_') +"'\n" );

        ret.append("\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                "\t"+ getName().replace('-','_')+".set_downstream(dummy_"+getName().replace('-', '_')+")\n" +
                "\tdummy_"+ getName().replace('-','_')+".set_downstream("+getTermNode().getName().replace('-', '_')+")\n"+
                getName().replace('-', '_')+" = BranchPythonOperator(task_id='" + getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n");


        return  ret.toString();

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
     * This method gets the properties required for Hadoop Streaming
     *
     * @param pid         process-id of Hadoop streaming
     * @param configGroup config_group entry in properties table "param" for arguments
     * @param passedKey   Key to retrieve the corresponding value
     * @return String containing the value wrt passedKey.
     */
    public String getParamValue(Integer pid, String configGroup,String passedKey) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();
        StringBuilder propertyValue= new StringBuilder();
        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if(passedKey.equals(key))
                    propertyValue.append(listForParams.getProperty(key));
            }
        }
        return propertyValue.toString();
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
                addScriptPaths.append("\t-file  "+MDConfig.getProperty(UPLOADBASEDIRECTORY) + "/" + processInfo.getParentProcessId().toString() + "/" +addtionalScripts.getProperty(key)+"\\\n");
            }
        }
        String extraFiles=addScriptPaths.toString().substring(0,addScriptPaths.length() - 2)+"'";

        return extraFiles;
    }

}
