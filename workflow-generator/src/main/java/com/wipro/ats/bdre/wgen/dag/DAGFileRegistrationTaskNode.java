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

        String nodeName = "fileRegistration-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
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
        String registerFileInfo = homeDir+"/registerFileInfo.txt";

        StringBuilder ret = new StringBuilder();
        ret.append(
                "with open('"+registerFileInfo+"','a+') as propeties_register_file:\n"+
                "\tfor line in propeties_register_file:\n"+
                "\t\tfile_info = line.split(':',2)\n"+
                "\t\tdict[file_info[0]] = file_info[1].replace('\\n','')\n"+

                "\ndef "+ getName().replace('-','_')+"_pc():\n" +
                "\tcommand='java -cp "+homeDir+"/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieRegisterFile  --sub-process-id dict[\"fileInfo.getSubProcessId()\"] --path dict[\"fileInfo.getPath()\"] --file-size dict[\"fileInfo.getFileSize()\"] --file-hash dict[\"fileInfo.getFileHash()\"] --batch-id  dict[\"initJobInfo.getTargetBatchId()\"] --server-id 123461 --creation-timestamp dict[\"fileInfo.getCreationTs()\"] ' \n"+
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tprint(\"out is \",out)\n"+
                "\tprint(\"err is \",err)\n"+
                "\tif(bash_output.returncode > 0):\n" +
                "\t\treturn 'dummy_"+getName().replace('-', '_') +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +

                "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream(dummy_"+ getName().replace('-', '_')+")\n" +
                "\t"+ "dummy_"+ getName().replace('-', '_')+".set_downstream("+getTermNode().getName().replace('-', '_') +")\n"+
                getName().replace('-','_')+" = BranchPythonOperator(task_id='"+getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n"+
                "dummy_"+ getName().replace('-', '_')+" = DummyOperator(task_id ='"+"dummy_"+ getName().replace('-', '_')+"',dag=dag)\n"
        );


        try {

            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            LOGGER.debug("error occured:  " + e);
        }


        return  ret.toString();



    }

}
