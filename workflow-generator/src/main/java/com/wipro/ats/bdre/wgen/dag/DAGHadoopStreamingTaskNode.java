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

import com.wipro.ats.bdre.GetParentProcessType;
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

        String nodeName = "hadoopStream_" + getId() + "_" + processInfo.getProcessName().replace(' ', '_');
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
            String homeDir = System.getProperty("user.home");
            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName()+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }

        ret.append("\ndummy_"+ getName()+" = DummyOperator(task_id='dummy_"+getName()+"', dag=dag)\n"+
                   "\ndef "+ getName()+"_pc():\n" );

        ret.append( "\tcommand='hadoop jar "+getParamValue(getId(),"param","hadoop.streaming.jar") +
                "\t-input "+getParamValue(getId(),"param","mapred.input.dir")+
                "\t-output "+getParamValue(getId(),"param","mapred.output.dir"));

        if(!getParamValue(getId(),"param","mapred.reduce.tasks").isEmpty())
            ret.append("\t-numReduceTasks "+getParamValue(getId(),"param","mapred.reduce.tasks"));

        String homeDir = System.getProperty("user.home");
        GetParentProcessType getParentProcessType = new GetParentProcessType();

        ret.append("\t-mapper "+homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString()  + "/" +  getScriptPath(getId(), "mapper")+
                "\t-reducer "+homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString()  + "/" +  getScriptPath(getId(), "reducer")+
                "\t-file "+homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString()  + "/" +  getScriptPath(getId(), "mapper")+
                "\t-file "+homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString()  + "/" +  getScriptPath(getId(), "reducer")+
                getSupplementaryFiles(getId(),"extraFiles"));
        ret.append( "\n\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tlogger.info(\"out is \"+str(out))\n"+
                "\tlogger.info(\"err is \"+str(err))\n"+
                "\tif(bash_output.returncode == 0):\n" +
                "\t\treturn '"+getToNode().getName() +"'\n" +
                "\telse:\n" +
                "\t\treturn 'dummy_"+getName() +"'\n" );

        ret.append("\ndef f_"+ getName()+"():\n" +
                "\t"+ getName()+".set_downstream("+ getToNode().getName()+")\n" +
                "\t"+ getName()+".set_downstream(dummy_"+getName()+")\n" +
                "\tdummy_"+ getName()+".set_downstream("+getTermNode().getName()+")\n"+
                getName()+" = BranchPythonOperator(task_id='" + getName()+"', python_callable="+getName()+"_pc, dag=dag)\n");


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
        String extraFiles="";
        if (addScriptPaths.length()>2)
         extraFiles=addScriptPaths.toString().substring(0,addScriptPaths.length() - 2)+"'";
        return extraFiles;
    }

}
