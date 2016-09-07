package com.wipro.ats.bdre.wgen.dag;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGInitJobNode extends DAGNode {
    public DAGInitJobNode() {

    }

    public String getName() {
        return "init_job";
    }

    @Override
    public String getDAG() {
        String homeDir = System.getProperty("user.home");
        String jobInfoFile = homeDir+"/bdre/airflow/"+getId().toString()+"_jobInfo.txt";

        try {
            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName()+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return "\ndef "+ getName()+"_pc(**kwargs):\n" +
                "\tcommand='java -cp "+homeDir+"/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.md.api.airflow.AirflowInitJob -p "+ getId().toString() +" -bmax 1'\n" +
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tlogger.info(\"out is \",out)\n"+
                "\tlogger.info(\"err is \",err)\n"+
                "\twith open('"+jobInfoFile+"','a+') as propeties_file:\n"+
                "\t\tfor line in propeties_file:\n"+
                "\t\t\tinfo = line.split('::',2)\n"+
                "\t\t\tdict[info[0]] = info[1].replace('\\n','')\n"+
                "\tkwargs['task_instance'].xcom_push(key='initjobInfo',value=dict)\n"+

                "\tif(bash_output.returncode != 0):\n" +
                "\t\treturn \'dag_kill_task\'\n" +
                "\telse:\n" +
                "\t\treturn '"+getToNode().getName() +"'\n" +

                "\ndef f_"+ getName()+"():\n" +
                "\t"+ getName()+".set_downstream("+ getToNode().getName()+")\n" +
                "\t"+ getName()+".set_downstream(dag_kill_task)\n" +
                
                getName()+" = BranchPythonOperator(task_id='"+getName()+"', python_callable="+getName()+"_pc,provide_context=True, dag=dag)\n" +
                "dag_kill_task = DummyOperator(task_id=\'dag_kill_task\',dag=dag)\n";
                
                


    }

}
