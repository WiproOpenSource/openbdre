package com.wipro.ats.bdre.wgen.dag;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGHaltStepNode extends DAGNode {
    public DAGHaltStepNode(String sid) {
        setSid(sid);
    }

    public String getName() {
        String haltStepNodeName = "dag_halt_step_" + getSid().replaceAll(",", "_");
        return haltStepNodeName.substring(0, Math.min(haltStepNodeName.length(), 45));
    }

    @Override
    public String getDAG() {
        String homeDir = System.getProperty("user.home");
            try {

            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName()+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return "\ndef "+getName().replace('-','_')+"_pc():\n" +
                "\tcommand='java -cp "+homeDir+"/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.md.api.airflow.AirflowHaltStep --sub-process-id "+ getSid()+"'\n" +
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tprint(\"out is \",out)\n"+
                "\tprint(\"err is \",err)\n"+
                "\tif(bash_output.returncode > 0):\n" +
                "\t\treturn '"+getTermNode().getName() +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getToNode().getName() +"'\n" +

                "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName()+".set_downstream("+ getToNode().getName()+")\n" +
                "\t"+ getName()+".set_downstream("+ getTermNode().getName()+")\n" +
                getName()+" = BranchPythonOperator(task_id='"+getName()+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n";

    }

}
