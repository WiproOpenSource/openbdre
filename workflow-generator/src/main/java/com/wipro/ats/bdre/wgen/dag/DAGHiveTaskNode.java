package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.GetParentProcessType;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by cloudera on 7/28/16.
 */
public class DAGHiveTaskNode extends GenericActionNode {
    private static final Logger LOGGER = Logger.getLogger(DAGHiveTaskNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode dagTaskNode = null;


    /**
     * This constructor is used to set node id and process information.
     *
     * @param dagTaskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGHiveTaskNode(DAGTaskNode dagTaskNode) {
        setId(dagTaskNode.getId());
        processInfo = dagTaskNode.getProcessInfo();
        this.dagTaskNode = dagTaskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "dag_hive_" + getId() + "_" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }
    @Override
    public String getDAG() {
        LOGGER.info("Inside HiveAction");

        LOGGER.info("processInfo " + this.getProcessInfo().getProcessId());

        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        if (isSecurityEnabled(this.getProcessInfo().getParentProcessId(), "security") != 0) {
            ret.append(" cred='hive_credentials'");
        }
        String homeDir = System.getProperty("user.home");
        //ProcessDAO processDAO = new ProcessDAO();
        GetParentProcessType getParentProcessType = new GetParentProcessType();
        String jobInfoFile = homeDir+"/bdre/airflow/"+processInfo.getParentProcessId().toString()+"_jobInfo.txt";

        LOGGER.info("processInfo " + processInfo.getProcessId());
        ret.append(

                "\ndef " + getName() + "_pc(**kwargs):\n" +
                        "\tif os.path.exists('"+ jobInfoFile +"') and os.path.getsize('"+ jobInfoFile +"') > 0:\n"+
                        "\t\tjobInfoDict = kwargs['task_instance'].xcom_pull(task_ids='init_job',key='initjobInfo')\n"+
                        "\t\twith open('"+ homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString() + "/" + getQueryPath(getId(), "query") +"','r+') as queryFile:\n"+
                        "\t\t\tqueryString=str("+getParams(getId(), "param")+")+queryFile.read()\n" +
                        "\t\tkwargs['task_instance'].xcom_push(key='query',value=queryString)\n"+
                        "\t\treturn 'query_runner_"+getName() +"'\n" +
                        "\telse:\n"+
                        "\t\treturn 'dummy2_"+getName() +"'\n" +

                "\ndef success_" + getName() + "(body,**kwargs):\n" +
                        "\t"+getName()+".xcom_push(body,'key',body['task_instance'].state)" +

                "\ndef failure_" + getName() + "(body,**kwargs):\n" +
                        "\t"+getName()+".xcom_push(body,'key',body['task_instance'].state)" +

                 "\ndef branching_" + getName() + "_pc(**kwargs):\n" +
                        "\tvalue = kwargs['task_instance'].xcom_pull(task_ids='query_runner_" + getName() + "',key=None)\n" +
                        "\tif(value == 'success'):\n" +
                        "\t\treturn '" + getToNode().getName() + "'\n" +
                        "\telif(value == 'failed'):\n" +
                        "\t\treturn 'dummy_" + getName() + "'\n" +
                        "\telse:\n" +
                        "\t\treturn 'branching_" + getName() + "'\n" +

                  "\ndef f_" + getName() + "():\n" +
                        "\t" + getName() + ".set_downstream(query_runner_" + getName() + ")\n" +
                        "\t" + getName() + ".set_downstream(dummy2_" + getName()+ ")\n" +
                        "\t" +"query_runner_" + getName() + ".set_downstream(branching_" + getName() + ")\n" +
                        "\t" + "branching_" + getName() + ".set_downstream(" + getToNode().getName() + ")\n" +
                        "\t" + "branching_" + getName() + ".set_downstream(dummy_" + getName() + ")\n" +
                        "\t" + "dummy_" + getName() + ".set_downstream(" + getTermNode().getName() + ")\n" +
                        "\t" + "dummy2_" + getName() + ".set_downstream(" + getTermNode().getName() + ")\n" +

                    getName() + " = BranchPythonOperator(task_id ='" + getName() + "',python_callable=" + getName() + "_pc ,provide_context=True, dag=dag)\n" +
                    "query_runner_" + getName() + " = HiveOperator(task_id='query_runner_" + getName() + "',hql=\"{{ task_instance.xcom_pull(task_ids ='" + getName() + "',key='query') }}\", on_success_callback=success_" + getName() + ", on_failure_callback=failure_" + getName() + " , provide_context=True, dag=dag)\n" +
                    "branching_" + getName() + " = BranchPythonOperator(task_id ='branching_" + getName() + "',python_callable=branching_" + getName() + "_pc, trigger_rule='all_done',provide_context=True, dag=dag)\n" +
                    "dummy2_" + getName() + " = DummyOperator(task_id ='dummy2_" + getName() + "',dag=dag)\n"+
                    "dummy_" + getName() + " = DummyOperator(task_id ='dummy_" + getName() + "',dag=dag)\n");


        try {

            FileWriter fw = new FileWriter(homeDir + "/defFile.txt", true);
            fw.write("\nf_" + getName() + "()");
            fw.close();
        } catch (IOException e) {
            System.out.println("e = " + e);
        }


        return ret.toString();


    }
    public String getQueryPath(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties queryPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = queryPath.propertyNames();
        StringBuilder addQueryPath = new StringBuilder();
        if (!queryPath.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addQueryPath.append( queryPath.getProperty(key) );

            }
        } else {
            addQueryPath.append("hql/query" + getId() + ".hql ");
        }
        return addQueryPath.toString();
    }

    /**
     * This method gets all the extra arguments required for Hive Query
     *
     * @param pid         process-id of Hive Query
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getParams(Integer pid, String configGroup) {
        String homeDir = System.getProperty("user.home");
        GetParentProcessType getParentProcessType = new GetParentProcessType();
        StringBuilder addParams = new StringBuilder();
        String url =

                "\"set run_id="+"\"+str(ast.literal_eval(str(jobInfoDict['initJobInfo.getMinBatchIdMap()']).replace('=',':'))["+getId()+ "])+\""

                //TODO; make this class available to hive
                +";add jar "+homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString() +"/lib/hive-plugin-1.1-SNAPSHOT-executable.jar"
                   +";set hive.exec.post.hooks=com.wipro.ats.bdre.hiveplugin.hook.LineageHook"
                   +";set bdre.lineage.processId="+getId()
                  +";set bdre.lineage.instanceExecId=\"+str(jobInfoDict['initJobInfo.getInstanceExecId()'])+\""

             //   +";set exec-id={{task_instance.xcom_pull(task_ids='init_job',key='initjobInfo').get('initJobInfo.getInstanceExecId()')}}"

                +";set exec-id=\"+str(jobInfoDict['initJobInfo.getInstanceExecId()'])+\""

                +";set target-batch-id=\"+str(jobInfoDict['initJobInfo.getTargetBatchId()'])+\""
                +";set target-batch-marking=\"+str(jobInfoDict['initJobInfo.getTargetBatchMarkingSet()'])+\""
                +";set min-batch-id="+"\"+str(ast.literal_eval(str(jobInfoDict['initJobInfo.getMinBatchIdMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set max-batch-id="+"\"+str(ast.literal_eval(str(jobInfoDict['initJobInfo.getMaxBatchIdMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set min-pri="+"\"+str(ast.literal_eval(str(jobInfoDict['initJobInfo.getMinSourceInstanceExecIdMap()']).replace('=',':')).get("+getId()+ "))+\""
                +";set max-pri="+"\"+str(ast.literal_eval(str(jobInfoDict['initJobInfo.getMaxSourceInstanceExecIdMap()']).replace('=',':')).get("+getId()+ "))+\""
                +";set min-batch-marking="+"\"+str(ast.literal_eval(str(jobInfoDict['initJobInfo.getMinBatchMarkingMap()']).replace('=',':0')).get("+getId()+ "))+\""
                +";set max-batch-marking="+"\"+str(ast.literal_eval(str(jobInfoDict['initJobInfo.getMaxBatchMarkingMap()']).replace('=',':0')).get("+getId()+ "))+\";\" ";

        addParams.append(url);
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();

        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addParams.append( "set "+key + "=" + listForParams.getProperty(key) +";");
            }
        }
        return addParams.toString();
    }

    public Integer isSecurityEnabled(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties properties = getProperties.getProperties(pid.toString(), configGroup);
        return properties.size();
    }


}
