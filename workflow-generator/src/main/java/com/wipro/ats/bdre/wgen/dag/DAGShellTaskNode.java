package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.GetParentProcessType;
import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by cloudera on 7/3/16.
 */
public class DAGShellTaskNode extends com.wipro.ats.bdre.wgen.dag.GenericActionNode {


    private ProcessInfo processInfo = new ProcessInfo();
    private static final String SCRIPT = "script";
    private static final String UPLOADBASEDIRECTORY = "upload.base-directory";
    private DAGTaskNode taskNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param taskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGShellTaskNode(DAGTaskNode taskNode) {
        setId(taskNode.getId());
        processInfo = taskNode.getProcessInfo();
        this.taskNode = taskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "shell_" + getId() + "_" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getDAG() {
        String homeDir = System.getProperty("user.home");
        //ProcessDAO processDAO = new ProcessDAO();
        GetParentProcessType getParentProcessType = new GetParentProcessType();

        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\ndef "+ getName().replace('-','_')+"_pc():\n" +
                "\tcommand='sh "+ homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString()  + "/" + getScriptPath(getId(), SCRIPT) +" " + getParams(getId(), "param")  +"',\n" +

                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tprint(\"out is \",out)\n"+
                "\tprint(\"err is \",err)\n"+
                "\tif(bash_output.returncode > 0):\n" +
                "\t\treturn 'dummy_"+getName() +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getToNode().getName() +"'\n" +

                "\ndef f_"+ getName()+"():\n" +
                "\t"+ getName()+".set_downstream("+ getToNode().getName()+")\n" +
                "\t"+ getName()+".set_downstream(dummy_"+ getName()+")\n" +
                "\t"+ "dummy_"+ getName()+".set_downstream("+getTermNode().getName() +")\n"+
                getName()+" = BranchPythonOperator(task_id='"+getName()+"', python_callable="+getName()+"_pc, dag=dag)\n"+
                "dummy_"+ getName()+" = DummyOperator(task_id ='"+"dummy_"+ getName()+"',dag=dag)\n"
        );

        try {
            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName()+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }


        return  ret.toString();
    }

    /**
     * This method gets path for Shell Script
     *
     * @param pid         process-id of Shell Script
     * @param configGroup config_group entry in properties table "script" for query path
     * @return String containing script path to be appended to workflow string
     */
    public String getScriptPath(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties scriptPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = scriptPath.propertyNames();
        StringBuilder addScriptPath = new StringBuilder();

        if (scriptPath.size() > 1) {
            throw new BDREException("Can Handle only 1 script in shell action processInfo.getProcessTypeId()=" + processInfo.getProcessTypeId());
        } else if (scriptPath.isEmpty()) {
            addScriptPath.append(SCRIPT + getId() + ".sh");
        } else {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addScriptPath.append(scriptPath.getProperty(key));
            }
        }
        return addScriptPath.toString();
    }

    /**
     * This method gets all the extra arguments required for Pig Script
     *
     * @param pid         process-id of shell Script
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
                addParams.append(" " + listForParams.getProperty(key) );

            }
        }
        return addParams.toString();
    }

    /**
     * This method gets details about additional files to be uploaded
     *
     * @param pid         process-id of Shell Script
     * @param configGroup config_group entry in properties table "more-scripts" for query path
     * @return String containing script path to be appended to workflow string
     */
    public String getSupplementaryFiles(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties addtionalScripts = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = addtionalScripts.propertyNames();
        StringBuilder addScriptPaths = new StringBuilder();

        if (!addtionalScripts.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addScriptPaths.append("            <file>"+addtionalScripts.getProperty(key)+"</file>\n");
            }
        }
        return addScriptPaths.toString();
    }
}
