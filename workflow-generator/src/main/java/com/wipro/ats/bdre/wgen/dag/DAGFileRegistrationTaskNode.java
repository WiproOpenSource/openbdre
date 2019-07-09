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

        StringBuilder ret = new StringBuilder();
        ret.append(
                "\ndef "+ getName()+"_pc(**kwargs):\n" +
                "\tjobInfoDict = kwargs['task_instance'].xcom_pull(task_ids='init_job',key='initjobInfo')\n"+
                "\tRegFileInfoDict = kwargs['task_instance'].xcom_pull(task_ids=None,key='RegFileInfoFile')\n"+
                "\tcommand='java -cp \""+homeDir+"/bdre/lib/airflow_lib/*\"  com.wipro.ats.bdre.md.api.airflow.AirflowRegisterFile  --sub-process-id \'+RegFileInfoDict[\"fileInfo.getSubProcessId()\"]+\' --path \'+RegFileInfoDict[\"fileInfo.getPath()\"]+\' --file-size \'+RegFileInfoDict[\"fileInfo.getFileSize()\"]+\' --file-hash \'+RegFileInfoDict[\"fileInfo.getFileHash()\"]+\' --batch-id  \'+jobInfoDict[\"initJobInfo.getTargetBatchId()\"]+\' --server-id 123461 --creation-timestamp \'+RegFileInfoDict[\"fileInfo.getCreationTs()\"]  \n"+
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tlogger.info(\"out is \"+str(out))\n"+
                "\tlogger.info(\"err is \"+str(err))\n"+
                "\tif(bash_output.returncode != 0):\n" +
                "\t\treturn 'dummy_"+getName() +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getToNode().getName() +"'\n" +

                "\ndef f_"+ getName()+"():\n" +
                "\t"+ getName()+".set_downstream("+ getToNode().getName()+")\n" +
                "\t"+ getName()+".set_downstream(dummy_"+ getName()+")\n" +
                "\t"+ "dummy_"+ getName()+".set_downstream("+getTermNode().getName() +")\n"+
                getName()+" = BranchPythonOperator(task_id='"+getName()+"', python_callable="+getName()+"_pc,provide_context=True, dag=dag)\n"+
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
