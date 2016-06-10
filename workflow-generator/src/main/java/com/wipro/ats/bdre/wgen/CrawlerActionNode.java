package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.md.beans.ProcessInfo;

/**
 * Created by AS294216 on 12/28/2015.
 */

/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML.
*/

public class CrawlerActionNode extends GenericActionNode {


    private ProcessInfo processInfo = new ProcessInfo();
    private ActionNode actionNode = null;
    private OozieNode toNode;
    public OozieNode getToNode() {
        return toNode;
    }
    public void setToNode(OozieNode toNode) {
        this.toNode = toNode;
    }
    private OozieNode termNode;
    public OozieNode getTermNode() {
        return termNode;
    }
    public void setTermNode(OozieNode termNode) {
        this.termNode = termNode;
    }
    /**
     * This constructor is used to set node id and process information.
     *
     * @param actionNode An instance of ActionNode class which a workflow triggers the execution of a task.
     */
    public CrawlerActionNode(ActionNode actionNode) {
        setId(actionNode.getId());
        processInfo = actionNode.getProcessInfo();
        this.actionNode = actionNode;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }


    public String getName() {

        String nodeName = "crawler-mapreduce-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
        return nodeName.substring(0, Math.min(nodeName.length(), 45));

    }

    @Override
    public String getXML() {
        if (this.getProcessInfo().getParentProcessId() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("\n<action name=\"" + getName() + "\">\n" +
                "        <java>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n");
        ret.append(getClassName(getId(), "program"));

        ret.append(getArgs(getId(), "param"));
        ret.append("<capture-output />");
        ret.append("        </java>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "    </action>");

        return ret.toString();
    }

    /**
     * This method gets main class for Crawler Job
     *
     * @param pid         process-id of Crawler Job
     * @param configGroup config_group entry in properties table "program" for class name
     * @return String containing main class to be appended to workflow string
     */
    public String getClassName(Integer pid, String configGroup) {

        StringBuilder addClassName = new StringBuilder();

        addClassName.append("            <main-class>com.wipro.ats.bdre.imcrawler.mr.MRMain</main-class>\n");


        return addClassName.toString();
    }

    /**
     * This method gets all the extra arguments required for Crawler Job
     *
     * @param pid         process-id of MapReduce Job
     * @param configGroup config_group entry in properties table "param" for arguments
     * @return String containing arguments to be appended to workflow string.
     */
    public String getArgs(Integer pid, String configGroup) {

        StringBuilder addArgs = new StringBuilder();

        addArgs.append("            <arg>--" + "sub-process-id" + "</arg>\n<arg>" + pid.toString() + "</arg>\n");
        addArgs.append("            <arg>--" + "instance-exec-id" + "</arg>\n<arg>" + "${wf:actionData(\"init-job\")[\"instance-exec-id\"]}" + "</arg>\n");

        return addArgs.toString();
    }
}

