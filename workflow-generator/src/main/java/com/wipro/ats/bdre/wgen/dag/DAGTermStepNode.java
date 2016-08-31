package com.wipro.ats.bdre.wgen.dag;


import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGTermStepNode extends DAGNode {
    public DAGTermStepNode(Integer id) {
        setId(id);
    }

    public String getName() {
        String termStepNodeName = "dag_term_step_" + getId();
        return termStepNodeName.substring(0, Math.min(termStepNodeName.length(), 45));

    }

    @Override
    public String getDAG() {
        String homeDir = System.getProperty("user.home");
        StringBuilder ret = new StringBuilder();
        ret.append("\ndef f_"+ getName()+"():\n" +
                "\t"+ getName()+".set_downstream("+ getToNode().getName()+")\n" +
                getName() +"=BashOperator(\n" +
                "    task_id='"+getName()+"',\n" +
                "    bash_command='java -cp "+homeDir+"/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.md.api.airflow.AirflowTermStep --sub-process-id "+getId().toString() +" ' ,\n" +
                "    dag=dag,\n" +
                "    trigger_rule='one_success')\n");
        try {
            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName()+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return ret.toString();
    }
}
