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

        String nodeName = "dag-hive-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
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
                "with open('" + jobInfoFile + "','a+') as propeties_file:\n" +
                        "\tfor line in propeties_file:\n" +
                        "\t\tinfo = line.split('::',2)\n" +
                        "\t\tdict[info[0]] = info[1].replace('\\n','')\n" +

                 "\ndef getQuery():\n" +
                        "\tif os.path.exists('"+ jobInfoFile +"') and os.path.getsize('"+ jobInfoFile +"') > 0:"+
                        "\t\twith open('"+ homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString() + "/" + getQueryPath(getId(), "query") +"','r+') as queryFile:\n"+
                        "\t\t\tqueryString=str("+getParams(getId(), "param")+")+queryFile.read()\n" +
                        "\t\treturn queryString\n"+
                        "\telse:"+
                        "\t\treturn ' ' "+

                "\ndef success_" + getName().replace('-', '_') + "(body,**context):\n" +
                        "\t"+getName().replace('-','_')+".xcom_push(body,'key',body['task_instance'].state)" +

                "\ndef failure_" + getName().replace('-', '_') + "(body,**context):\n" +
                        "\t"+getName().replace('-','_')+".xcom_push(body,'key',body['task_instance'].state)" +

                 "\ndef branching_" + getName().replace('-', '_') + "_pc(**context):\n" +
                        "\tvalue = context['task_instance'].xcom_pull(task_ids='" + getName().replace('-', '_') + "',key=None)\n" +
                        "\tif(value == 'success'):\n" +
                        "\t\treturn '" + getToNode().getName().replace('-', '_') + "'\n" +
                        "\telse:\n" +
                        "\t\treturn 'dummy_" + getName().replace('-', '_') + "'\n" +

                  "\ndef f_" + getName().replace('-', '_') + "():\n" +
                        "\t" + getName().replace('-', '_') + ".set_downstream(branching_" + getName().replace('-', '_') + ")\n" +
                        "\t" + "branching_" + getName().replace('-', '_') + ".set_downstream(" + getToNode().getName().replace('-', '_') + ")\n" +
                        "\t" + "branching_" + getName().replace('-', '_') + ".set_downstream(dummy_" + getName().replace('-', '_') + ")\n" +
                        "\t" + "dummy_" + getName().replace('-', '_') + ".set_downstream(" + getTermNode().getName().replace('-', '_') + ")\n" +

                    getName().replace('-', '_') + " = HiveOperator(task_id='" + getName().replace('-', '_') + "',hql=str(getQuery()), on_success_callback=success_" + getName().replace('-', '_') + ", on_failure_callback=failure_" + getName().replace('-', '_') + " , provide_context=True, dag=dag)\n" +
                    "branching_" + getName().replace('-', '_') + " = BranchPythonOperator(task_id ='branching_" + getName().replace('-', '_') + "',python_callable=branching_" + getName().replace('-', '_') + "_pc, trigger_rule='all_done',provide_context=True, dag=dag)\n" +
                    "dummy_" + getName().replace('-', '_') + " = DummyOperator(task_id ='dummy_" + getName().replace('-', '_') + "',dag=dag)\n");

        try {

            FileWriter fw = new FileWriter(homeDir + "/defFile.txt", true);
            fw.write("\nf_" + getName().replace('-', '_') + "()");
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
        StringBuilder addParams = new StringBuilder();
        String url =
             //   +"set hive.exec.post.hooks=com.wipro.ats.bdre.hiveplugin.hook.LineageHook;"
             //   +"set bdre.lineage.processId="+getId()
            //    +";set bdre.lineage.instanceExecId=\"+dict['initJobInfo.getInstanceExecId()'];"
                 "\"set run_id="+"\"+str(ast.literal_eval(str(dict['initJobInfo.getMinBatchIdMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set exec-id=\"+str(dict['initJobInfo.getInstanceExecId()'])+\""
                +";set target-batch-id=\"+str(dict['initJobInfo.getTargetBatchId()'])+\""
                +";set target-batch-marking=\"+str(dict['initJobInfo.getTargetBatchMarkingSet()'])+\""
                +";set min-batch-id="+"\"+str(ast.literal_eval(str(dict['initJobInfo.getMinBatchIdMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set max-batch-id="+"\"+str(ast.literal_eval(str(dict['initJobInfo.getMaxBatchIdMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set min-pri="+"\"+str(ast.literal_eval(str(dict['initJobInfo.getMinSourceInstanceExecIdMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set max-pri="+"\"+str(ast.literal_eval(str(dict['initJobInfo.getMaxSourceInstanceExecIdMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set min-batch-marking="+"\"+str(ast.literal_eval(str(dict['initJobInfo.getMinBatchMarkingMap()']).replace('=',':'))["+getId()+ "])+\""
                +";set min-batch-marking="+"\"+str(ast.literal_eval(str(dict['initJobInfo.getMinBatchMarkingMap()']).replace('=',':'))["+getId()+ "])+\";\" ";

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
