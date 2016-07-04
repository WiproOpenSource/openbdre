package com.wipro.ats.bdre.wgen;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonInitStepNode extends OozieNode {
    /**
     * This constructor is used to set id for Init Step Node
     *
     * @param id id to be set for Init Step Node
     */
    public PythonInitStepNode(Integer id) {
        setId(id);
    }

    public String getName() {
        String initStepNodeName = "python-init-step-" + getId();
        return initStepNodeName.substring(0, Math.min(initStepNodeName.length(), 45));
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

        return "\ndef "+getName().replace('-', '_')+"_pc():\n" +
                "\tcommand='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieInitStep -p "+ getId().toString()+"'\n" +
                "\tbash_output = os.system(command)\n" +
                "\tif(bash_output == 0):\n" +
                "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getTermNode().getName().replace('-', '_') +"'\n" +
                "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getTermNode().getName().replace('-', '_')+")\n" +
                getName().replace('-','_')+" = BranchPythonOperator(task_id='"+getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n";

    }

}
