package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.md.beans.InitJobInfo;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

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
        InitJobInfo initJobInfo = null;
        try
        {
            FileInputStream fileIn = new FileInputStream("/home/cloudera/bdre/initjobInfo.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            initJobInfo = (InitJobInfo) in.readObject();
            in.close();
            fileIn.close();
        }catch(Exception e)
        {
            e.printStackTrace();

        }
        return getName().replace('-', '_') +"= BashOperator(\n"+
                "    task_id='"+getName().replace('-','_')+"',\n"+
                "    bash_command='java -cp /home/cloudera/bdre/lib/md_api/md_api-1.1-SNAPSHOT-executable.jar:/home/cloudera/bdre/lib/*/*  com.wipro.ats.bdre.md.api.oozie.OozieHaltJob --process-id "+ getId().toString()+" -bmax "+ initJobInfo.getTargetBatchMarkingSet() +"' ,\n"+
                "    dag=dag)\n";

    }

}
