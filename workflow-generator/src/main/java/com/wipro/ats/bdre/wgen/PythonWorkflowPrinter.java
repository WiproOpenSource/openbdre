package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by SU324335 on 6/30/16.
 */
public class PythonWorkflowPrinter {
    private static final Logger LOGGER = Logger.getLogger(PythonWorkflowPrinter.class);
    private static final String EMPTYERROR = "Empty Workflow name";
    //Add this map for future step chain(to determine the next step of a subprocess)
    private Map<Integer, PythonNodeCollection> uniqNodeCollectionTreeMap = new LinkedHashMap<Integer, PythonNodeCollection>();

    /**
     * pThis method uses process information and generates a workflow as a String.
     *
     * @param processInfos This variable contains list of ProcessInfo containing information regarding process.
     * @param workflowName name of workflow
     * @return This method returns an instance of Class Workflow containing whole workflow as a String and dot String to print workflow
     * as a schematic diagram.
     */
    public Workflow execute(List<ProcessInfo> processInfos, String workflowName) {
        CommonNodeMaintainer nodeMaintainer = new CommonNodeMaintainer();
        //storing the nodes that are already printed
        //this will prevent same node to be printed multiple times
        PythonNodeCollection nc = nodeMaintainer.getPnc();
        Set<String> printedNodeNames = new HashSet<String>();
        if (workflowName == null || workflowName.trim().isEmpty()) {
            LOGGER.error(EMPTYERROR);
            throw new MetadataException(EMPTYERROR);
        }
        final String prefixXml = "\nfrom airflow.operators import BashOperator,BranchPythonOperator,DummyOperator\n"+
                "from datetime import datetime, timedelta\n"+
                "from airflow import DAG\n"+
                "import os\n" +
                "args = {'owner': 'airflow','start_date': datetime(2015, 10, 1, 5, 40, 0), 'depends_on_past': False}\n" +
                "\n" +
                "dag = DAG(dag_id='sparkeg1',  default_args=args)";
        String pid = processInfos.get(0).getProcessId().toString();
        StringBuilder credentials = new StringBuilder();
        credentials.append(isSecurityEnabled(pid, "security"));
        //final String postfixXml = "\n</workflow-app>";

        StringBuilder workflowXML = new StringBuilder();
        StringBuilder stepXML = new StringBuilder();
        LOGGER.info("Starting workflow generation for " + workflowName);
        //Populate the map
        Map<Integer, ProcessInfo> tempProcessInfos = new HashMap<Integer, ProcessInfo>();
        for (ProcessInfo processInfo : processInfos) {
            tempProcessInfos.put(processInfo.getProcessId(), processInfo);
        }
        //Populate the tree
        for (ProcessInfo processInfo : processInfos) {
            LOGGER.info("processing " + processInfo);
            PythonNodeCollection nodeCollection = uniqNodeCollectionTreeMap.get(processInfo.getProcessId());
            if (nodeCollection == null) {
                nodeCollection = new PythonNodeCollection(processInfo);
            }
            String[] children = processInfo.getNextProcessIds().split(",");
            for (String child : children) {
                LOGGER.debug("Analyzing child: " + child);
                Integer childKey = new Integer(child);
                PythonNodeCollection childNodeCollection = uniqNodeCollectionTreeMap.get(childKey);
                if (childNodeCollection == null) {
                    childNodeCollection = new PythonNodeCollection(tempProcessInfos.get(childKey));
                }
                childNodeCollection.addParent(nodeCollection, nodeMaintainer);
                nodeCollection.addChild(childNodeCollection, nodeMaintainer);
                uniqNodeCollectionTreeMap.put(childKey, childNodeCollection);
            }
            //Check for the last sub-process
            if (nodeCollection.getHaltStepNode().getToNode().getId().equals(tempProcessInfos.get(nodeCollection.getId()).getParentProcessId())) {
                nodeCollection.getHaltStepNode().setToNode(nc.getHaltJobNode());
            }
            if (tempProcessInfos.get(nodeCollection.getId()).getParentProcessId() == 0) {
                OozieNode firstNode = null;
                if (nodeCollection.getPythonForkNode() != null) {
                    LOGGER.info("First node in the workflow is a fork");
                    firstNode = nodeCollection.getPythonForkNode();
                } else {

                    firstNode = nodeCollection.getChildren().get(0).getInitStepNode();
                }
                LOGGER.info("firstNode=" + firstNode.getName());
                nc.getInitJobNode().setToNode(firstNode);
            }

            uniqNodeCollectionTreeMap.put(processInfo.getProcessId(), nodeCollection);
        }
        LOGGER.info("uniqNodeCollectionTreeMap=" + uniqNodeCollectionTreeMap);
        Map<String, OozieNode> oozieNodeMap = new HashMap<String, OozieNode>();
        //oozieNodeMap.put(nc.getStart().getName(), nc.getStart());
        oozieNodeMap.put(nc.getInitJobNode().getName(), nc.getInitJobNode());
        LOGGER.info("nodeMaintainer.restartNodes=" + nodeMaintainer.getRestartNodes());
        //add case to nodes
        //nc.getRecoveryDecisionNode().setPreviousHaltNodes(nodeMaintainer.getRestartNodes());
        //oozieNodeMap.put(nc.getRecoveryDecisionNode().getName(), nc.getRecoveryDecisionNode());

        for (Integer id : uniqNodeCollectionTreeMap.keySet()) {
            if (tempProcessInfos.get(id).getParentProcessId() == 0) {
                uniqNodeCollectionTreeMap.get(id).setActionNode(null);
                uniqNodeCollectionTreeMap.get(id).setTermStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setHaltStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setInitStepNode(null);
                //Add ids to the main process to all process nodes
                nc.setIdForProcessNodes(id);
            }
            LOGGER.info("Adding collection for: " + id);
            oozieNodeMap.putAll(uniqNodeCollectionTreeMap.get(id).getOozieNodes());
            stepXML.append(uniqNodeCollectionTreeMap.get(id).toXML(printedNodeNames));
        }

        oozieNodeMap.put(nc.getHaltJobNode().getName(), nc.getHaltJobNode());
        oozieNodeMap.put(nc.getTermJobNode().getName(), nc.getTermJobNode());
        //oozieNodeMap.put(nc.getHalt().getName(), nc.getHalt());
        //oozieNodeMap.put(nc.getKill().getName(), nc.getKill());
        LOGGER.info("oozieNodeMap size=" + oozieNodeMap.size());
        workflowXML.append(prefixXml);
        workflowXML.append(credentials.toString());
        //workflowXML.append(nc.getStart().getXML());
        workflowXML.append(nc.getInitJobNode().getXML());
        //workflowXML.append(nc.getRecoveryDecisionNode().getXML());
        workflowXML.append(stepXML);
        workflowXML.append(nc.getHaltJobNode().getXML());
        workflowXML.append(nc.getTermJobNode().getXML());
        //workflowXML.append(nc.getKill().getXML());
        //workflowXML.append(nc.getHalt().getXML());
        String postfixXml ="";
        try {
            InputStream fis = new FileInputStream("/home/cloudera/defFile.txt");
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            Boolean flag = true;
            line = br.readLine();
            while ( (line = br.readLine()) != null) {
                if(flag && line.contains("python_halt_step_"))
                    continue;
                else
                    flag = false;
                postfixXml = postfixXml + line+"\n";
            }
            File file = new File("/home/cloudera/defFile.txt");
            file.delete();
        }catch (IOException e){
            System.out.println("e = " + e);
        }

        workflowXML.append(postfixXml);
        LOGGER.info("Complete !");
        Workflow workflow = new Workflow();
        workflow.setXml(workflowXML);
//        workflow.setDot(DotUtil.getDot(oozieNodeMap.values()));
        return workflow;
    }

    public String isSecurityEnabled(String pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties isEnabled = getProperties.getProperties(pid, configGroup);
        Enumeration e = isEnabled.propertyNames();
        StringBuilder addCredentials = new StringBuilder();
        if (!isEnabled.isEmpty()) {
            String key = (String) e.nextElement();

            if ("true".equalsIgnoreCase(isEnabled.getProperty(key))) {
                addCredentials.append("            <credentials>\n" +
                        "                <credential name='hive_credentials' type='hcat'>\n");

                addCredentials.append(getCredProperties(pid, "credential"));

                addCredentials.append("                </credential>\n" +
                        "            </credentials>");
            }
        }
        return addCredentials.toString();
    }

    public String getCredProperties(String pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties properties = getProperties.getProperties(pid, configGroup);
        Enumeration e = properties.propertyNames();
        StringBuilder addProperties = new StringBuilder();
        if (!properties.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                addProperties.append("                        <property>\n" +
                        "                                <name>" + key + "</name>\n" +
                        "                                <value>" + properties.getProperty(key) + "</value>\n" +
                        "                        </property>\n");
            }
        }
        return addProperties.toString();
    }

    public Workflow execInfo(List<ProcessInfo> processInfos, String workflowName) {
        CommonNodeMaintainer nodeMaintainer = new CommonNodeMaintainer();
        //storing the nodes that are already printed
        //this will prevent same node to be printed multiple times
        PythonNodeCollection nc = nodeMaintainer.getPnc();
        Set<String> printedNodeNames = new HashSet<String>();
        if (workflowName == null || workflowName.trim().isEmpty()) {
            LOGGER.error(EMPTYERROR);
            throw new MetadataException(EMPTYERROR);
        }
        final String prefixXml = "\nfrom airflow.operators import BashOperator,BranchPythonOperator,DummyOperator\n"+
                "from datetime import datetime, timedelta\n"+
                "import os\n" +
                "args = {'owner': 'airflow','start_date': datetime(2015, 10, 1, 5, 40, 0), 'depends_on_past': False}\n" +
                "\n" +
                "dag = airflow.DAG(dag_id='sparkeg1',  default_args=args)";
        String pid = processInfos.get(0).getProcessId().toString();
        StringBuilder credentials = new StringBuilder();
        credentials.append(isSecurityEnabled(pid, "security"));

        StringBuilder workflowXML = new StringBuilder();
        StringBuilder stepXML = new StringBuilder();
        LOGGER.info("Starting workflow generation for " + workflowName);
        //Populate the map
        Map<Integer, ProcessInfo> tempProcessInfos = new HashMap<Integer, ProcessInfo>();
        for (ProcessInfo processInfo : processInfos) {
            tempProcessInfos.put(processInfo.getProcessId(), processInfo);
        }
        //Populate the tree
        for (ProcessInfo processInfo : processInfos) {
            LOGGER.info("processing " + processInfo);
            PythonNodeCollection nodeCollection = uniqNodeCollectionTreeMap.get(processInfo.getProcessId());
            if (nodeCollection == null) {
                nodeCollection = new PythonNodeCollection(processInfo);
            }
            String[] children = processInfo.getNextProcessIds().split(",");
            for (String child : children) {
                LOGGER.info("Analyzing child: " + child);
                Integer childKey = new Integer(child);
                PythonNodeCollection childNodeCollection = uniqNodeCollectionTreeMap.get(childKey);
                if (childNodeCollection == null) {
                    childNodeCollection = new PythonNodeCollection(tempProcessInfos.get(childKey));
                }
                childNodeCollection.addParent(nodeCollection, nodeMaintainer);
                nodeCollection.addChild(childNodeCollection, nodeMaintainer);
                uniqNodeCollectionTreeMap.put(childKey, childNodeCollection);
            }
            //Check for the last sub-process
            if (nodeCollection.getHaltStepNode().getToNode().getId().equals(tempProcessInfos.get(nodeCollection.getId()).getParentProcessId())) {
                nodeCollection.getHaltStepNode().setToNode(nc.getHaltJobNode());
            }
            if (tempProcessInfos.get(nodeCollection.getId()).getParentProcessId() == 0) {
                OozieNode firstNode = null;
                if (nodeCollection.getPythonForkNode() != null) {
                    LOGGER.info("First node in the workflow is a fork");
                    firstNode = nodeCollection.getPythonForkNode();
                } else {

                    firstNode = nodeCollection.getChildren().get(0).getInitStepNode();
                }
                LOGGER.info("firstNode=" + firstNode.getName());
                nc.getInitJobNode().setToNode(firstNode);
            }

            uniqNodeCollectionTreeMap.put(processInfo.getProcessId(), nodeCollection);
        }
        LOGGER.info("uniqNodeCollectionTreeMap=" + uniqNodeCollectionTreeMap);
        Map<String, OozieNode> oozieNodeMap = new HashMap<String, OozieNode>();
        //oozieNodeMap.put(nc.getStart().getName(), nc.getStart());
        oozieNodeMap.put(nc.getInitJobNode().getName(), nc.getInitJobNode());
        LOGGER.info("nodeMaintainer.restartNodes=" + nodeMaintainer.getRestartNodes());
        //add case to nodes
        //nc.getRecoveryDecisionNode().setPreviousHaltNodes(nodeMaintainer.getRestartNodes());
        //oozieNodeMap.put(nc.getRecoveryDecisionNode().getName(), nc.getRecoveryDecisionNode());

        for (Integer id : uniqNodeCollectionTreeMap.keySet()) {
            if (tempProcessInfos.get(id).getParentProcessId() == 0) {
                uniqNodeCollectionTreeMap.get(id).setActionNode(null);
                uniqNodeCollectionTreeMap.get(id).setTermStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setHaltStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setInitStepNode(null);
                //Add ids to the main process to all process nodes
                nc.setIdForProcessNodes(id);
            }
            LOGGER.info("Adding collection for: " + id);
            oozieNodeMap.putAll(uniqNodeCollectionTreeMap.get(id).getOozieNodes());
            stepXML.append(uniqNodeCollectionTreeMap.get(id).toXML(printedNodeNames));
        }

        oozieNodeMap.put(nc.getHaltJobNode().getName(), nc.getHaltJobNode());
        oozieNodeMap.put(nc.getTermJobNode().getName(), nc.getTermJobNode());
        //oozieNodeMap.put(nc.getHalt().getName(), nc.getHalt());
        //oozieNodeMap.put(nc.getKill().getName(), nc.getKill());
        LOGGER.info("oozieNodeMap size=" + oozieNodeMap.size());
        workflowXML.append(prefixXml);
        workflowXML.append(credentials.toString());
        //workflowXML.append(nc.getStart().getXML());
        workflowXML.append(nc.getInitJobNode().getXML());
        //workflowXML.append(nc.getRecoveryDecisionNode().getXML());
        workflowXML.append(stepXML);
        workflowXML.append(nc.getHaltJobNode().getXML());
        workflowXML.append(nc.getTermJobNode().getXML());
        //workflowXML.append(nc.getKill().getXML());
        //workflowXML.append(nc.getHalt().getXML());
        String postfixXml ="";
        try {
            InputStream fis = new FileInputStream("/home/cloudera/defFile.txt");
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( (line = br.readLine()) != null) {
                postfixXml = postfixXml + line+"\n";
            }
        }catch (IOException e){
            System.out.println("e = " + e);
        }

        workflowXML.append(postfixXml);
        LOGGER.info("Complete !");
        Workflow workflow = new Workflow();
        workflow.setXml(workflowXML);
        workflow.setDot(DotUtil.getDashboardDot(oozieNodeMap.values(), processInfos));
        return workflow;
    }
}

