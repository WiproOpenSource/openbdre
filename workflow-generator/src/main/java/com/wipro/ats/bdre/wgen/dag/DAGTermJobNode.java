package com.wipro.ats.bdre.wgen.dag;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGTermJobNode extends DAGNode {

    public DAGTermJobNode(){

    }

    public String getName() {
        return "dag-term-job";
    }

    @Override
    public String getDAG(){
        return getName().replace('-', '_') +"= BashOperator(\n"+
                "    task_id='"+getName().replace('-','_')+"',\n"+
                "    bash_command='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieTermJob --process-id "+ getId().toString()+"',\n"+
                "    dag=dag,\n" +
                "    trigger_rule='one_success')\n";
    }
}
