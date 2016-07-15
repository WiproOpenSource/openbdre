package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by cloudera on 7/8/16.
 */
public class DAGRTaskNode extends GenericActionNode {

    private static final Logger LOGGER = Logger.getLogger(DAGRTaskNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode dagTaskNode = null;
    private static final String UPLOADBASEDIRECTORY = "upload.base-directory";

    /**
     * This constructor is used to set node id and process information.
     *
     * @param dagTaskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGRTaskNode(DAGTaskNode dagTaskNode) {
        setId(dagTaskNode.getId());
        processInfo = dagTaskNode.getProcessInfo();
        this.dagTaskNode = dagTaskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "R-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getDAG() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <shell xmlns=\"uri:oozie:shell-action:0.1\">\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n");
        ret.append("            <exec>Rhadoop.sh</exec>\n");
        ret.append("            <argument>"+getRFile(getId(), "r-file").replace("r/","")+"</argument>\n");
        ret.append(getArguments("param"));
        ret.append("            <file>Rhadoop.sh</file>\n");
        ret.append("            <file>" +getRFile(getId(), "r-file")+"</file>\n");
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
                "\tcommand='sh "+ MDConfig.getProperty(UPLOADBASEDIRECTORY).replace("/bdre-wfd","")+"/bdre/bdre-scripts/deployment/Rhadoop.sh "+MDConfig.getProperty(UPLOADBASEDIRECTORY) + "/" + processInfo.getParentProcessId().toString() + "/" + getRFile(getId(), "r-file")+"',\n" +
                "\tbash_output = subprocess.Popen(command,shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE )\n" +
                "\tout,err = bash_output.communicate()\n"+
                "\tprint(\"out is \",out)\n"+
                "\tprint(\"err is \",err)\n"+
                "\tif(bash_output.returncode > 0):\n" +
                "\t\treturn '"+getTermNode().getName().replace('-', '_') +"'\n" +
                "\telse:\n" +
                "\t\treturn '"+getToNode().getName().replace('-', '_') +"'\n" +

                "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                "\t"+ getName().replace('-','_')+".set_downstream("+ getTermNode().getName().replace('-', '_')+")\n" +

                getName().replace('-', '_')+" = BranchPythonOperator(task_id='" + getName().replace('-', '_')+"', python_callable="+getName().replace('-','_')+"_pc, dag=dag)\n";

    }

    /**
     * This method gets the required arguments for running the R Script
     *
     * @param configGroup config_group entry in properties table for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getArguments(String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties argumentProperty = getProperties.getProperties(getId().toString(), configGroup);

        String arguments="";
        if(!argumentProperty.isEmpty()) {

            arguments = "            <argument>" + argumentProperty.values().toString().substring(1, argumentProperty.values().toString().length() - 1) + "</argument>\n";
        }
        return arguments;
    }

    /**
     * This method gets the required R Script file for running the R Script
     *
     * @param pid         process-id of R Script
     * @param configGroup config_group entry in properties table "rScript" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getRFile(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties rScript = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = rScript.propertyNames();
        LOGGER.info("rScript = " + rScript.size());
        StringBuilder addRScript = new StringBuilder();
        if (rScript.size() > 1) {
            throw new BDREException("Can Handle only 1 input file in R action, process type=" + processInfo.getProcessTypeId());
        } else if (rScript.isEmpty()) {
            addRScript.append("r/" + getId() + ".R");
        } else {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addRScript.append(rScript.getProperty(key));
            }
        }

        return addRScript.toString();
    }

}
