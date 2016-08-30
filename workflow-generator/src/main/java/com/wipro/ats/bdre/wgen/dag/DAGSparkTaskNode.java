package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.GetParentProcessType;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGSparkTaskNode extends GenericActionNode {
    private static final Logger LOGGER = Logger.getLogger(DAGSparkTaskNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private DAGTaskNode dagTaskNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param dagTaskNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public DAGSparkTaskNode(DAGTaskNode dagTaskNode) {
        setId(dagTaskNode.getId());
        processInfo = dagTaskNode.getProcessInfo();
        this.dagTaskNode = dagTaskNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "dag_spark_" + getId() + "_" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getDAG() {
        String homeDir = System.getProperty("user.home");
        LOGGER.info("Inside dag Spark");
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }

        StringBuilder ret = new StringBuilder();

        //ProcessDAO processDAO = new ProcessDAO();
        GetParentProcessType getParentProcessType = new GetParentProcessType();
        //--class "+getAppMainClass(getId(), "spark-main")+"
        ret.append("\ndef "+ getName()+"_pc():\n" +
                "\tcommand='java -cp "+ homeDir +"/bdre/lib/semantic-core/semantic-core-1.1-SNAPSHOT.jar:"+homeDir+"/bdre/lib/*/* org.apache.spark.deploy.SparkSubmit  "+ getConf(getId(),"spark-conf")+" "+homeDir + "/bdre_apps/" + processInfo.getBusDomainId().toString()+"/" + getParentProcessType.getParentProcessTypeId(processInfo.getParentProcessId())+"/"+ processInfo.getParentProcessId().toString() + "/" +getJarName(getId(), "spark-jar")+" "+getAppArgs(getId(), "app-args")+"',\n" +
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
                "dummy_"+ getName()+" = DummyOperator(task_id ='"+"dummy_"+ getName()+"',dag=dag)\n");


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
     * This method gets path for spark jar file
     *
     * @param pid         process-id of Spark Job.
     * @param configGroup config_group entry in properties table "jar" for spark jar file path
     * @return String name of the jar
     */
    public String getJarName(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties jarPath = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = jarPath.propertyNames();
        StringBuilder addJarPath = new StringBuilder();
        if (!jarPath.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addJarPath.append(jarPath.getProperty(key));
            }
        } else {
            addJarPath.append("spark-" + getId() + ".jar");
        }

        return addJarPath.toString();
    }

    /**
     * This method gets all the extra configuration required for spark job
     *
     * @param pid         process-id of spark job
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getConf(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();
        StringBuilder addParams = new StringBuilder();
        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addParams.append(" --" + key);
                addParams.append(" " + listForParams.getProperty(key) + " ");
            }
        }
        return addParams.toString();
    }

    /**
     * This method gets all the extra configuration required for spark job
     *
     * @param pid         process-id of spark job
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getAppArgs(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = listForParams.propertyNames();
        StringBuilder addParams = new StringBuilder();
        if (!listForParams.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addParams.append(" "+listForParams.getProperty(key)+" ");
            }
        }
        return addParams.toString();
    }

    public Integer isSecurityEnabled(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties properties = getProperties.getProperties(pid.toString(), configGroup);
        return properties.size();
    }

    /**
     * This method gets main class for MapReduce Job
     *
     * @param pid         process-id of Spark Job
     * @param configGroup config_group entry in properties table "program" for class name
     * @return String containing main class to be appended to workflow string
     */
    public String getAppMainClass(Integer pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties className = getProperties.getProperties(getId().toString(), configGroup);
        Enumeration e = className.propertyNames();
        StringBuilder addClassName = new StringBuilder();
        if (!className.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addClassName.append(" "+className.getProperty(key)+" ");

            }
        } else {
            addClassName.append(" "+getId()+" ");
        }
        return addClassName.toString();
    }
}
