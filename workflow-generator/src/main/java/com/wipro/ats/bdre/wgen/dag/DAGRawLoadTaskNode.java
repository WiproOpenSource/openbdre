package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by su324335 on 8/22/16.
 */
public class DAGRawLoadTaskNode extends GenericActionNode {
    private static final Logger LOGGER = Logger.getLogger(DAGRawLoadTaskNode.class);

    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode taskNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param taskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGRawLoadTaskNode(DAGTaskNode taskNode) {
        setId(taskNode.getId());
        processInfo = taskNode.getProcessInfo();
        this.taskNode = taskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "rawLoad-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }
    @Override
    public String getDAG() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        /*OozieNode fileListNode = null;
        for (OozieNode oozieNode : actionNode.getContainingNodes()) {
            if (oozieNode instanceof com.wipro.ats.bdre.wgen.LOFActionNode) {
                fileListNode = oozieNode;
            }
        }*/
        StringBuilder ret = new StringBuilder();
        String homeDir = System.getProperty("user.home");
        String jobInfoFile = homeDir+"/bdre/airflow/"+processInfo.getParentProcessId().toString()+"_jobInfo.txt";

        ret.append( "with open('"+jobInfoFile+"','a+') as propeties_register_file:\n"+
                        "\tfor line in propeties_register_file:\n"+
                        "\t\tfile_info = line.split(':',2)\n"+
                        "\t\tdict[file_info[0]] = file_info[1].replace('\\n','')\n"+

                        "\ndef "+ getName().replace('-','_')+"_pc():\n" +
                        "\tcommand='java -cp "+homeDir+"/bdre/lib/etl-driver/*:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.im.etl.api.oozie.OozieRawLoad --process-id "+ getId().toString()+"  --instance-exec-id \'+dict[\"initJobInfo.getInstanceExecId()\"] +\' --list-of-files  \'+str(ast.literal_eval(str(dict[\"initJobInfo.getFileListMap()\"]).replace('=',':\\'').replace(',','\\',').replace('}','\\'}').replace('FileList.',''))["+getId()+ "]) +\'   --list-of-file-batchIds  \'+str(ast.literal_eval(str(dict[\"initJobInfo.getBatchListMap()\"]).replace('=',':').replace('FileBatchList.',''))["+getId()+ "])  \n"+
                        "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                        "\tout,err = bash_output.communicate()\n"+
                        "\tprint(\"out is \",out)\n"+
                        "\tprint(\"err is \",err)\n"+
                        "\tif(bash_output.returncode != 0):\n" +
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


        return ret.toString();
    }
}
