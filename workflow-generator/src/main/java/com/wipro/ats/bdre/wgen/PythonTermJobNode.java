package com.wipro.ats.bdre.wgen;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonTermJobNode extends OozieNode{

    public PythonTermJobNode(){

    }

    public String getName() {
        return "python-term-job";
    }

    @Override
    public String getXML(){
        return getName().replace('-', '_') +"= BashOperator(\n"+
                "    task_id='"+getName().replace('-','_')+"',\n"+
                "    bash_command='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieTermJob --process-id "+ getId().toString()+"',\n"+
                "    dag=dag)\n";
    }
}
