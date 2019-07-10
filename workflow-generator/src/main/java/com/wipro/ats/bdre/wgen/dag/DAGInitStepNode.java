package com.wipro.ats.bdre.wgen.dag;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGInitStepNode extends DAGNode {
    /**
     * This constructor is used to set id for Init Step Node
     *
     * @param id id to be set for Init Step Node
     */
    public DAGInitStepNode(Integer id) {
        setId(id);
    }

    public String getName() {
        String initStepNodeName = "dag_init_step_" + getId();
        return initStepNodeName.substring(0, Math.min(initStepNodeName.length(), 45));
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

        return "\ndef "+getName()+"_pc():\n" +
                "\tcommand='java -cp \""+homeDir+"/bdre/lib/airflow_lib/*\"  com.wipro.ats.bdre.md.api.airflow.AirflowInitStep -p "+ getId().toString()+"'\n" +
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
                getName()+" = BranchPythonOperator(task_id='"+getName()+"', python_callable="+getName()+"_pc, dag=dag)\n"+
                "dummy_"+ getName()+" = DummyOperator(task_id ='"+"dummy_"+ getName()+"',dag=dag)\n";

    }

}
