package com.wipro.ats.bdre.wgen;

import java.io.FileWriter;
import java.io.IOException;

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
        try {
            FileWriter fw = new FileWriter("/home/cloudera/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                getName().replace('-', '_') +"=BashOperator(\n" +
                "    task_id='"+getName().replace('-','_')+"',\n" +
                "    bash_command='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieTermStep --sub-process-id "+getId().toString() +" ' ,\n" +
                "    dag=dag,\n" +
                "    trigger_rule='one_success')\n";
    }
}
