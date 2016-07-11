package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.md.beans.InitJobInfo;
import com.wipro.ats.bdre.wgen.dag.DAGNode;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGHaltJobNode extends  DAGNode {
    public DAGHaltJobNode() {

    }

    public String getName() {
        return "dag-halt-job";
    }

    @Override
    public String getDAG() {
        return "with open('/home/cloudera/jobinfo.txt','a+') as propeties_file:\n"+
                "\tfor line in propeties_file:\n"+
                "\t\tinfo = line.split(':',2)\n"+
                "\t\tdict[info[0]] = info[1].replace('\\n','')\n"+
                getName().replace('-', '_') +"= BashOperator(\n"+
                "    task_id='"+getName().replace('-','_')+"',\n"+
                "    bash_command='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieHaltJob --process-id "+ getId().toString()+" -batchmarking dict[\"initJobInfo.getTargetBatchMarkingSet()\"] ' ,\n"+
                "    dag=dag)\n";

}

}
