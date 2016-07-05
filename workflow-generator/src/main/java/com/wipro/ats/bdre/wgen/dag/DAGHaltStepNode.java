package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.wgen.dag.DAGNode;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGHaltStepNode extends DAGNode {
    public DAGHaltStepNode(String sid) {
        setSid(sid);
    }

    public String getName() {
        String haltStepNodeName = "python-halt-step-" + getSid().replaceAll(",", "_");
        return haltStepNodeName.substring(0, Math.min(haltStepNodeName.length(), 45));
    }

    @Override
    public String getDAG() {
        try {
            FileWriter fw = new FileWriter("/home/cloudera/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return "\ndef "+getName().replace('-','_')+"_pc():\n" +
                "\tcommand='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieHaltStep --sub-process-id "+ getSid()+"'\n" +
                "\tbash_output = os.system(command)\n" +
                "\tif(bash_output == 0):\n" +
                "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getTermNode().getName().replace('-','_') +"'\n" +
                "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getTermNode().getName().replace('-', '_')+")\n" +
                getName().replace('-', '_')+" = BranchPythonOperator(task_id='"+getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n";

    }

}
