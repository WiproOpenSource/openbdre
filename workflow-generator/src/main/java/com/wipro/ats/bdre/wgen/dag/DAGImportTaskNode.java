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
 * Created by IshitaParekh on 04-03-2015.
 */
public class DAGImportTaskNode extends GenericActionNode {
    private static final Logger LOGGER = Logger.getLogger(DAGImportTaskNode.class);

    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode taskNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param taskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGImportTaskNode(DAGTaskNode taskNode) {
        setId(taskNode.getId());
        processInfo = taskNode.getProcessInfo();
        this.taskNode = taskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "import_" + getId() + "_" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }


    @Override
    public String getDAG() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        String homeDir = System.getProperty("user.home");
        String RegFileInfoFile = homeDir+"/bdre/airflow/"+processInfo.getParentProcessId().toString()+"_jobInfo.txt";

        StringBuilder ret = new StringBuilder();

        ret.append(
                "\ndef "+ getName()+"_pc(**kwargs):\n" +
                "\tjobInfoDict = kwargs['task_instance'].xcom_pull(task_ids='init_job',key='initjobInfo')\n"+
                "\tcommand='java -cp "+homeDir+"/bdre/lib/data-import/*:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.dataimport.DataImportMain --process-id "+ getId().toString()+"  --batch-id  \'+jobInfoDict[\"initJobInfo.getTargetBatchId()\"]+\'  --config-group imp-common  --instance-exec-id \'+jobInfoDict[\"initJobInfo.getInstanceExecId()\"]  \n"+
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tlogger.info(\"out is \"+str(out))\n"+
                "\tlogger.info(\"err is \"+str(err))\n"+
                "\twith open('"+RegFileInfoFile+"','a+') as reg_file_info_file:\n"+
                "\t\tfor line in reg_file_info_file:\n"+
                "\t\t\tinfo = line.split('::',2)\n"+
                "\t\t\tdict[info[0]] = info[1].replace('\\n','')\n"+
                "\tkwargs['task_instance'].xcom_push(key='RegFileInfoFile',value=dict)\n"+

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
