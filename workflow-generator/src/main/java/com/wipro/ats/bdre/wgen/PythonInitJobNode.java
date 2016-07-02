package com.wipro.ats.bdre.wgen;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonInitJobNode extends OozieNode {
    public PythonInitJobNode() {

    }

    public String getName() {
        return "python-init-job";
    }

    @Override
    public String getXML() {

        return "\ndef init_job_pc():\n" +
                "\tcommand='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieInitJob -p"+ getId().toString() +"-bmax 1'\n" +
                "\tbash_output = os.system(command)\n" +
                "\tif(bash_output == 0):\n" +
                "\t\treturn "+getToNode().getName() +"\n" +
                "\telse:\n" +
                "\t\treturn "+getTermNode().getName() +"\n" +
                getName()+" = BranchPythonOperator(task_id="+getName()+", python_callable=init_job_pc, dag=dag)\n";


    }

}
