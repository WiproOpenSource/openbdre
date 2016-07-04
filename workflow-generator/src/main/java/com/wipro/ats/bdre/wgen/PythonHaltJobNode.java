package com.wipro.ats.bdre.wgen;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonHaltJobNode extends OozieNode {
    public PythonHaltJobNode() {

    }

    public String getName() {
        return "python-halt-job";
    }

    @Override
    public String getXML() {
        return getName().replace('-', '_') +"= BashOperator(\n"+
                "    task_id='"+getName().replace('-','_')+"',\n"+
                "    bash_command='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieHaltJob --process-id "+ getId().toString()+" -bmax 1' ,\n"+
                "    dag=dag)\n";

    }

}
