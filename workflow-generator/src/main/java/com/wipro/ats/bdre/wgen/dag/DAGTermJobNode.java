package com.wipro.ats.bdre.wgen.dag;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGTermJobNode extends DAGNode {

    public DAGTermJobNode(){

    }

    public String getName() {
        return "dag_term_job";
    }

    @Override
    public String getDAG(){
        String homeDir = System.getProperty("user.home");
        StringBuilder ret = new StringBuilder();
        ret.append("\n"+getName() +"= BashOperator(\n"+
                "    task_id='"+getName()+"',\n"+
                "    bash_command='java -cp \""+homeDir+"/bdre/lib/airflow_lib/*\" com.wipro.ats.bdre.md.api.airflow.AirflowTermJob --process-id "+ getId().toString()+"',\n"+
                "    dag=dag,\n" +
                "    trigger_rule='one_success')\n");
        return ret.toString();
    }
}
