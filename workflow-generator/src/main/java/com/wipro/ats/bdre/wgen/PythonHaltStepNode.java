package com.wipro.ats.bdre.wgen;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonHaltStepNode  extends OozieNode  {
    public PythonHaltStepNode(String sid) {
        setSid(sid);
    }

    public String getName() {
        String haltStepNodeName = "python-halt-step-" + getSid().replaceAll(",", "_");
        return haltStepNodeName.substring(0, Math.min(haltStepNodeName.length(), 45));
    }

    @Override
    public String getXML() {
        return "\ndef "+getName()+"_pc():\n" +
                "\tcommand='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieHaltStep --sub-process-id"+ getSid()+"\n" +
                "\tbash_output = os.system(command)\n" +
                "\tif(bash_output == 0):\n" +
                "\t\treturn "+getToNode().getName() +"\n" +
                "\telse:\n" +
                "\t\treturn "+getTermNode().getName() +"\n" +
                getName()+" = BranchPythonOperator(task_id="+getName()+", python_callable="+getName()+"_pc, dag=dag)\n";

    }

}
