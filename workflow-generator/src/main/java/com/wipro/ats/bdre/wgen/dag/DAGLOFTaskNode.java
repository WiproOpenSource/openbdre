package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by su324335 on 8/25/16.
 */
public class DAGLOFTaskNode extends GenericActionNode {
    private static final Logger LOGGER = Logger.getLogger(DAGLOFTaskNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode taskNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class.
     */
    public DAGLOFTaskNode(DAGTaskNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.taskNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "lof_" + getId() + "_" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getDAG() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        String homeDir = System.getProperty("user.home");
        String etlFileInfoFile = homeDir+"/bdre/airflow/etldriverInfo.txt";

        ret.append(      "\ndef "+ getName().replace('-','_')+"_pc(**kwargs):\n" +
                        "\tjobInfoDict = kwargs['task_instance'].xcom_pull(task_ids='init_job',key='initjobInfo')\n"+
                        "\tcommand='java -cp "+homeDir+"/bdre/lib/md_api/*:"+homeDir+"/bdre/lib/*/*  com.wipro.ats.bdre.md.api.airflow.AirflowGetETLDriver  --min-batch-id   \'+str(ast.literal_eval(str(jobInfoDict[\"initJobInfo.getMinBatchIdMap()\"]).replace('=',':'))["+getId()+ "]) +\'  --max-batch-id  '+str(ast.literal_eval(str(jobInfoDict[\"initJobInfo.getMaxBatchIdMap()\"]).replace('=',':'))["+getId()+ "]) \n"+
                        "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                        "\tout,err = bash_output.communicate()\n"+
                        "\tlogger.info(\"out is \"+str(out))\n"+
                        "\tlogger.info(\"err is \"+str(err))\n"+

                        "\twith open('"+etlFileInfoFile+"','a+') as etl_file_info_file:\n"+
                        "\t\tfor line in etl_file_info_file:\n"+
                        "\t\t\tinfo = line.split('::',2)\n"+
                        "\t\t\tdict[info[0]] = info[1].replace('\\n','')\n"+
                        "\tkwargs['task_instance'].xcom_push(key='etlFileInfo',value=dict)\n"+
                        "\tif(bash_output.returncode != 0):\n" +
                        "\t\treturn 'dummy_"+getName().replace('-', '_') +"'\n" +
                        "\telse:\n" +
                        "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +

                        "\ndef f_"+ getName().replace('-','_')+"():\n" +
                        "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                        "\t"+ getName().replace('-', '_')+".set_downstream(dummy_"+ getName().replace('-', '_')+")\n" +
                        "\t"+ "dummy_"+ getName().replace('-', '_')+".set_downstream("+getTermNode().getName().replace('-', '_') +")\n"+
                        getName().replace('-','_')+" = BranchPythonOperator(task_id='"+getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, provide_context=True,dag=dag)\n"+
                        "dummy_"+ getName().replace('-', '_')+" = DummyOperator(task_id ='"+"dummy_"+ getName().replace('-', '_')+"',dag=dag)\n"
        );


        try {

            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            LOGGER.debug("error occured:  " + e);
        }


        return ret.toString();


    }
}
