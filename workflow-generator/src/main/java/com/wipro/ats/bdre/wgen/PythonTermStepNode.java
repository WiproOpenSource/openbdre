package com.wipro.ats.bdre.wgen;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonTermStepNode extends OozieNode{
    public PythonTermStepNode(Integer id) {
        setId(id);
    }

    public String getName() {
        String termStepNodeName = "python-term-step-" + getId();
        return termStepNodeName.substring(0, Math.min(termStepNodeName.length(), 45));

    }

    @Override
    public String getXML() {
        return getName() +"=BashOperator(\n" +
                "    task_id=' "+getName()+" ',\n" +
                "    bash_command='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieTermStep --sub-process-id "+getId().toString() +" ' ,\n" +
                "    dag=dag,\n" +
                "    trigger_rule='one_success)\n";
    }
}
