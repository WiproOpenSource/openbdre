package com.wipro.ats.bdre.wgen.dag;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGHaltJobNode extends  DAGNode {
    public DAGHaltJobNode() {

    }

    public String getName() {
        return "dag_halt_job";
    }

    @Override
    public String getDAG() {
        String homeDir = System.getProperty("user.home");
        String jobInfoFile = homeDir+"/bdre/airflow/"+getId().toString()+"_jobInfo.txt";
        try {

            FileWriter fw = new FileWriter(homeDir + "/defFile.txt", true);
            fw.write("\nf_" + getName() + "()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return  "\ndef "+getName()+"_pc(**kwargs):\n" +
                "\tcommand='java -cp "+homeDir+"/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.md.api.airflow.AirflowHaltJob --process-id "+ getId().toString()+" -batchmarking \'+kwargs['task_instance'].xcom_pull(task_ids='init_job',key='initjobInfo').get(\"initJobInfo.getTargetBatchMarkingSet()\") \n"+
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tlogger.info(\"out is \",out)\n"+
                "\tlogger.info(\"err is \",err)\n"+
                "\tif(bash_output.returncode != 0):\n" +
                "\t\treturn '"+getTermNode().getName() +"'\n" +
                "\telse:\n" +
                "\t\treturn 'success'\n" +
                "\ndef f_"+ getName()+"():\n" +
                "\t"+ getName()+".set_downstream("+ getTermNode().getName()+")\n" +
                getName()+" = BranchPythonOperator(task_id='"+getName()+"', python_callable="+getName()+"_pc,provide_context=True, dag=dag)\n";


}




}
