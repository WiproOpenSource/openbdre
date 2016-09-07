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

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Mi294210 on 22-07-2016.
 */
public class DAGFileRegistrationTaskNode extends GenericActionNode {
    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode taskNode = null;
    private static final Logger LOGGER = Logger.getLogger(DAGFileRegistrationTaskNode.class);
    /**
     * This constructor is used to set node id and process information.
     *
     * @param taskNode An instance of DAGTaskNode class.
     */
    public  DAGFileRegistrationTaskNode(DAGTaskNode taskNode) {
        setId(taskNode.getId());
        processInfo = taskNode.getProcessInfo();
        this.taskNode = taskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public String getName() {

        String nodeName = "fileRegistration_" + getId() + "_" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getDAG() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        DAGNode node = null;
        for (DAGNode dagNode : taskNode.getContainingNodes()) {
            if (dagNode.getToNode() instanceof DAGFileRegistrationTaskNode) {
                node = dagNode;
            }
        }
        String homeDir = System.getProperty("user.home");
        String jobInfoFile = homeDir+"/bdre/airflow/"+processInfo.getProcessId().toString()+"_fileInfo.txt";

        StringBuilder ret = new StringBuilder();
        ret.append(
                "\nwith open('"+jobInfoFile+"','a+') as propeties_register_file:\n"+
                "\tfor line in propeties_register_file:\n"+
                "\t\tfile_info = line.split('::',2)\n"+
                "\t\tdict[file_info[0]] = file_info[1].replace('\\n','')\n"+

                "\ndef "+ getName()+"_pc():\n" +
                        "\tcommand='java -cp "+homeDir+"/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.md.api.airflow.AirflowRegisterFile  --sub-process-id \'+dict[\"fileInfo.getSubProcessId()\"]+\' --path \'+dict[\"fileInfo.getPath()\"]+\' --file-size \'+dict[\"fileInfo.getFileSize()\"]+\' --file-hash \'+dict[\"fileInfo.getFileHash()\"]+\' --batch-id  \'+dict[\"initJobInfo.getTargetBatchId()\"]+\' --server-id 123461 --creation-timestamp \'+dict[\"fileInfo.getCreationTs()\"]  \n"+
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tprint(\"out is \",out)\n"+
                "\tprint(\"err is \",err)\n"+
                "\tif(bash_output.returncode != 0):\n" +
                "\t\treturn 'dummy_"+getName() +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getToNode().getName() +"'\n" +

                "\ndef f_"+ getName()+"():\n" +
                "\t"+ getName()+".set_downstream("+ getToNode().getName()+")\n" +
                "\t"+ getName()+".set_downstream(dummy_"+ getName()+")\n" +
                "\t"+ "dummy_"+ getName()+".set_downstream("+getTermNode().getName() +")\n"+
                getName()+" = BranchPythonOperator(task_id='"+getName()+"', python_callable="+getName()+"_pc, dag=dag)\n"+
                "dummy_"+ getName()+" = DummyOperator(task_id ='"+"dummy_"+ getName()+"',dag=dag)\n"
        );


        try {

            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName()+"()");
            fw.close();
        }
        catch (IOException e){
            LOGGER.debug("error occured:  " + e);
        }


        return  ret.toString();



    }

}
