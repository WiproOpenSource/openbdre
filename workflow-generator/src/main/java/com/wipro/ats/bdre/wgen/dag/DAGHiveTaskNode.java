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

        LOGGER.info("processInfo "+this.getProcessInfo().getProcessId());

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
        String jobInfoFile = homeDir+"/jobInfo.txt";

        LOGGER.info("processInfo "+processInfo.getProcessId());
        ret.append(
                "with open('"+jobInfoFile+"','a+') as propeties_file:\n"+
                        "\tfor line in propeties_file:\n"+
                        "\t\tinfo = line.split(':',2)\n"+
                        "\t\tdict[info[0]] = info[1].replace('\\n','')\n"+
//TODO: send jdbc url as arg.. use ? for conf and # for var
                        "\ndef "+ getName().replace('-','_')+"_pc():\n" +
                        "\tcommand='java -cp "+ homeDir +"/bdre/lib/semantic-core/semantic-core-1.1-SNAPSHOT.jar:"+homeDir+"/bdre/lib/*/* com.wipro.ats.bdre.semcore.HiveTask "+homeDir + "/bdre_apps/" + this.getProcessInfo().getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ this.getProcessInfo().getParentProcessId().toString() + "/" + getQueryPath(getId(), "query")+" "+getJdbcUrl(getId(),"param") +"',\n" +
                        "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                        "\tout,err = bash_output.communicate()\n"+
                        "\tprint(\"out is \",out)\n"+
                        "\tprint(\"err is \",err)\n"+
                        "\tif(bash_output.returncode > 0):\n" +
                        "\t\treturn 'dummy_"+getName().replace('-', '_') +"'\n" +
                        "\telse:\n" +
                        "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +

                        "\ndef f_"+ getName().replace('-','_')+"():\n" +
                        "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                        "\t"+ getName().replace('-', '_')+".set_downstream(dummy_"+ getName().replace('-', '_')+")\n" +
                        "\t"+ "dummy_"+ getName().replace('-', '_')+".set_downstream("+getTermNode().getName().replace('-', '_') +")\n"+
                        getName().replace('-','_')+" = BranchPythonOperator(task_id='"+getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n"+
                        "dummy_"+ getName().replace('-', '_')+" = DummyOperator(task_id ='"+"dummy_"+ getName().replace('-', '_')+"',dag=dag)\n");


        try {

            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }


        return  ret.toString();

        /*ret.append("\">\n" +
                "        <hive xmlns=\"uri:oozie:hive-action:0.2\">\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n" +

                "            <job-xml>hive-site.xml</job-xml>\n" +
                "            <configuration>\n" +
                "            <property>\n" +
                "                <name>run_id</name>\n" +
                "                <value>${wf:actionData(\"init-job\")[\"min-batch-id-map." + getId() + "\"]}</value>\n" +
                "            </property>\n" +

                "            <property>\n" +
                "                <name>hive.exec.post.hooks</name>\n" +
                "                <value>com.wipro.ats.bdre.hiveplugin.hook.LineageHook</value>\n" +
                "                </property>" +
                "                <property>\n" +
                "                <name>bdre.lineage.processId</name>\n" +
                "                <value>" + getId() + "</value>\n" +
                "                </property>\n" +
                "                <property>\n" +
                "                <name>bdre.lineage.instanceExecId</name>\n" +
                "                <value>${wf:actionData(\"init-job\")[\"instance-exec-id\"]}</value>\n" +
                "                </property>\n" +

                "                </configuration>");
        ret.append(getQueryPath(getId(), "query"));


        ret.append("            <param>exec-id=${wf:actionData(\"init-job\")[\"instance-exec-id\"]}</param>\n" +
                "            <param>target-batch-id=${wf:actionData(\"init-job\")[\"target-batch-id\"]}</param>\n" +
                "            <param>min-batch-id=${wf:actionData(\"init-job\")[\"min-batch-id-map." + getId() + "\"]}</param>\n" +
                "            <param>max-batch-id=${wf:actionData(\"init-job\")[\"max-batch-id-map." + getId() + "\"]}</param>\n" +
                "            <param>min-pri=${wf:actionData(\"init-job\")[\"min-source-instance-exec-id-map." + getId() + "\"]}</param>\n" +
                "            <param>max-pri=${wf:actionData(\"init-job\")[\"max-source-instance-exec-id-map." + getId() + "\"]}</param>\n" +
                "            <param>min-batch-marking=${wf:actionData(\"init-job\")[\"min-batch-marking-map." + getId() + "\"]}</param>\n" +
                "            <param>max-batch-marking=${wf:actionData(\"init-job\")[\"max-batch-marking-map." + getId() + "\"]}</param>\n" +
                "            <param>target-batch-marking=${wf:actionData(\"init-job\")[\"target-batch-marking\"]}</param>\n" +
                "            <param>last-recoverable-sp-id=${wf:actionData(\"init-job\")[\"last-recoverable-sp-id\"]}</param>\n");


        ret.append(getParams(getId(), "param"));

        ret.append("        </hive>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();*/
    }

    /**
     * This method gets path for Hive Query
     *
     * @param pid         process-id of Hive Query
     * @param configGroup config_group entry in properties table "query" for query path
     * @return String containing query path to be appended to workflow string
     */
    public String getQueryPath(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties queryPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = queryPath.propertyNames();
        StringBuilder addQueryPath = new StringBuilder();
        if (!queryPath.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addQueryPath.append( queryPath.getProperty(key) + " ");

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
    public String getJdbcUrl(Integer pid, String configGroup) {
        StringBuilder addParams = new StringBuilder();
        String url = "jdbc:hive://localhost:10000/default2?run_id="+"dict[\"initJobInfo.getMinBatchIdMap()\"][" +getId()+ "];"
                +"hive.exec.post.hooks=com.wipro.ats.bdre.hiveplugin.hook.LineageHook;"
                +"bdre.lineage.processId="+getId()
                +";bdre.lineage.instanceExecId=dict[\"initJobInfo.getInstanceExecId()\"]#"

                +"exec-id=dict[\"initJobInfo.getInstanceExecId()\"];"
                +"target-batch-id=dict[\"initJobInfo.getTargetBatchId()\"];"
                +"target-batch-marking=dict[\"initJobInfo.getTargetBatchMarkingSet()\"];"
                +"min-batch-id=dict[\"initJobInfo.getMinBatchIdMap()\"][" +getId()+ "];"
                +"max-batch-id=dict[\"initJobInfo.getMaxBatchIdMap()\"][" +getId()+ "];"
                +"min-pri=dict[\"initJobInfo.getMinSourceInstanceExecIdMap()\"][" +getId()+ "];"
                +"max-pri=dict[\"initJobInfo.getMaxSourceInstanceExecIdMap()\"][" +getId()+ "];"
                +"min-batch-marking=dict[\"initJobInfo.getMinBatchMarkingMap()\"][" +getId()+ "];"
                +"max-batch-marking=dict[\"initJobInfo.getMaxBatchMarkingMap()\"][" +getId()+ "]" ;


        addParams.append(url);
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();

        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addParams.append( ";"+key + "=" + listForParams.getProperty(key) );
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
