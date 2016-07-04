package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by cloudera on 7/3/16.
 */
public class PythonShellActionNode extends GenericActionNode {


    private ProcessInfo processInfo = new ProcessInfo();
    private static final String SCRIPT = "script";
    private PythonActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public PythonShellActionNode(PythonActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "shell-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <shell xmlns=\"uri:oozie:shell-action:0.1\">\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n");
        ret.append("            <exec>"+getScriptPath(getId(), SCRIPT).replace("shell/","")+"</exec>\n");
        ret.append(getParams(getId(), "param"));
        ret.append("            <file>"+getScriptPath(getId(), SCRIPT)+"</file>\n");
        ret.append(getSupplementaryFiles(getId(),"extraFiles"));
        ret.append("        </shell>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        try {
            FileWriter fw = new FileWriter("/home/cloudera/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return "\ndef "+ getName().replace('-','_')+"_pc():\n" +
                "\tcommand='sh  "+ getScriptPath(getId(), SCRIPT).replace("shell/","") +" " + getParams(getId(), "param")  +",\n" +
                "\tbash_output = os.system(command)\n" +
                "\tif(bash_output == 0):\n" +
                "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getTermNode().getName().replace('-', '_') +"'\n" +

                "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                "\t"+ getName().replace('-','_')+".set_downstream("+ getTermNode().getName().replace('-', '_')+")\n" +

                getName().replace('-', '_')+" = BranchPythonOperator(task_id='" + getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n";

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
                addParams.append(" <argument>" + listForParams.getProperty(key) + "</argument>\n");

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
