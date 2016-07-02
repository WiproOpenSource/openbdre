package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.util.Enumeration;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonSparkActionNode extends GenericActionNode {
    private static final Logger LOGGER = Logger.getLogger(PythonSparkActionNode.class);
    private ProcessInfo processInfo = new ProcessInfo();
    private PythonActionNode actionNode = null;

    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public PythonSparkActionNode(PythonActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "python-spark-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        LOGGER.info("Inside python Spark");
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        OozieNode fileListNode = null;
        for (OozieNode oozieNode : actionNode.getContainingNodes()) {
          if (oozieNode instanceof LOFActionNode) {
                fileListNode = oozieNode;   //TODO
            }
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName());
        if (isSecurityEnabled(this.getProcessInfo().getParentProcessId(), "security") != 0) {
            ret.append(" cred='spark_credentials'");
        }

        ret.append("\">\n" +
                "   <java>\n" +
                "        <job-tracker>${jobTracker}</job-tracker>\n" +
                "        <name-node>${nameNode}</name-node>\n" +
                "        <main-class>org.apache.spark.deploy.SparkSubmit</main-class>\n" +
                "        <arg>--class</arg>\n");
        ret.append(getAppMainClass(getId(), "spark-main"));
        ret.append(getConf(getId(), "spark-conf"));
        ret.append("        <arg>" + getJarName(getId(), "spark-jar") + "</arg>\n");
        ret.append(getAppArgs(getId(), "app-args"));
        ret.append("        <file>" + getJarName(getId(), "spark-jar") + "</file>\n");
        ret.append("     </java>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        //return ret.toString();

        return getName() +"= BashOperator(\n" +
                "    task_id=' "+getName()+" ',\n"+
                "    bash_command='java -cp /home/cloudera/bdre/lib/spark-core/spark-core-1.1-SNAPSHOT.jar:/home/cloudera/bdre/lib/*/* org.apache.spark.deploy.SparkSubmit --class "+ getAppMainClass(getId(), "spark-main") +" " +getConf(getId(), "spark-conf")+" "+getJarName(getId(), "spark-jar")+getAppArgs(getId(), "app-args")+"',\n"
                +"    dag=dag)\n";
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
