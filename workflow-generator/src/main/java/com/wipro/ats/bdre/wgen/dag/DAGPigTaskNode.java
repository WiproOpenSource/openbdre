package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.GetParentProcessType;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by su324335 on 7/13/16.
 */
public class DAGPigTaskNode extends GenericActionNode{
    private static final Logger LOGGER = Logger.getLogger(DAGPigTaskNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode dagTaskNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param dagTaskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGPigTaskNode(DAGTaskNode dagTaskNode) {
        setId(dagTaskNode.getId());
        processInfo = dagTaskNode.getProcessInfo();
        this.dagTaskNode = dagTaskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "dag-pig-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }
    @Override
    public String getDAG() {
        LOGGER.info("Inside PigAction");
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        String homeDir = System.getProperty("user.home");
       // ProcessDAO processDAO = new ProcessDAO();
        String jobInfoFile = homeDir+"/jobInfo.txt";
        GetParentProcessType getParentProcessType = new GetParentProcessType();

        StringBuilder ret = new StringBuilder();
        ret.append(
                "with open('"+jobInfoFile+"','a+') as propeties_file:\n"+
                "\tfor line in propeties_file:\n"+
                "\t\tinfo = line.split('::',2)\n"+
                "\t\tdict[info[0]] = info[1].replace('\\n','')\n"+

                "\ndef "+ getName().replace('-','_')+"_pc():\n" +
                "\tcommand='java -cp "+ homeDir +"/bdre/lib/semantic-core/semantic-core-1.1-SNAPSHOT.jar:"+homeDir+"/bdre/lib/*/* com.wipro.ats.bdre.semcore.PigScriptRunner "+homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString() + "/" + getScriptPath(getId(), "script")+" "+getParams(getId(),"param") +"',\n" +
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
    }

    /**
     * This method gets path for Pig Script
     *
     * @param pid         process-id of Pig Script
     * @param configGroup config_group entry in properties table "script" for query path
     * @return String containing script path to be appended to workflow string
     */
    public String getScriptPath(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties scriptPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = scriptPath.propertyNames();
        StringBuilder addScriptPath = new StringBuilder();
        if (!scriptPath.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addScriptPath.append(" " + scriptPath.getProperty(key));

            }
        } else {
            addScriptPath.append(" pig/script" + getId() + ".pig ");
        }
        return addScriptPath.toString();
    }

    /**
     * This method gets all the extra arguments required for Pig Script
     *
     * @param pid         process-id of Pig Script
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getParams(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();
        StringBuilder addParams = new StringBuilder();
        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if ("run_id".equals(key)) {
                    addParams.append(" -param ");
                    addParams.append(" " + key + "=" + "dict[\"initJobInfo.getMinBatchIdMap()\"][" +getId()+ "] " );
                } else {
                    addParams.append(" " + key + "=" + listForParams.getProperty(key));
                }
            }
        }
        return addParams.toString();
    }
}
