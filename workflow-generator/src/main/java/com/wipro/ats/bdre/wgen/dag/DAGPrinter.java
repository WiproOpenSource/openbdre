package com.wipro.ats.bdre.wgen.dag;

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
public class DAGPrinter {
    private static final Logger LOGGER = Logger.getLogger(DAGPrinter.class);
    private static final String EMPTYERROR = "Empty DAG name";
    //Add this map for future step chain(to determine the next step of a subprocess)
    private Map<Integer, DAGNodeCollection> uniqNodeCollectionTreeMap = new LinkedHashMap<Integer, DAGNodeCollection>();

    /**
     * pThis method uses process information and generates a dag as a String.
     *
     * @param processInfos This variable contains list of ProcessInfo containing information regarding process.
     * @param dagName name of dag
     * @return This method returns an instance of Class Workflow containing whole dag as a String and dot String to print dag
     * as a schematic diagram.
     */
    public DAG execute(List<ProcessInfo> processInfos, String dagName) {
        CommonNodeMaintainer nodeMaintainer = new CommonNodeMaintainer();
        //storing the nodes that are already printed
        //this will prevent same node to be printed multiple times
        DAGNodeCollection nc = nodeMaintainer.getPnc();
        Set<String> printedNodeNames = new HashSet<String>();
        if (dagName == null || dagName.trim().isEmpty()) {
            LOGGER.error(EMPTYERROR);
            throw new MetadataException(EMPTYERROR);
        }
        String pid = processInfos.get(0).getProcessId().toString();

        final String prefixDAG = "\nfrom airflow.operators import BashOperator,BranchPythonOperator,DummyOperator,HiveOperator\n"+
                "import subprocess\n"+
                "from datetime import datetime, timedelta\n"+
                "from airflow import DAG\n"+
                "import os\n" +
                "import ast\n" +
                "args = {'owner': 'airflow','start_date': datetime(2015, 10, 1, 5, 40, 0), 'depends_on_past': False}\n" +
                "\n" +
                "dag = DAG(dag_id='dag_"+ processInfos.get(0).getBusDomainId().toString()+"_"+  processInfos.get(0).getProcessTypeId().toString() + "_" + pid+"',  default_args=args)"+"\n"+
                "dict = {}\n";

        StringBuilder credentials = new StringBuilder();
        credentials.append(isSecurityEnabled(pid, "security"));
        //final String postfixXml = "\n</dag-app>";

        StringBuilder airflowDAG = new StringBuilder();
        StringBuilder stepDAG = new StringBuilder();
        LOGGER.info("Starting dag generation for " + dagName);
        //Populate the map
        Map<Integer, ProcessInfo> tempProcessInfos = new HashMap<Integer, ProcessInfo>();
        for (ProcessInfo processInfo : processInfos) {
            tempProcessInfos.put(processInfo.getProcessId(), processInfo);
        }
        //Populate the tree
        for (ProcessInfo processInfo : processInfos) {
            LOGGER.info("processing " + processInfo);
            DAGNodeCollection nodeCollection = uniqNodeCollectionTreeMap.get(processInfo.getProcessId());
            if (nodeCollection == null) {
                nodeCollection = new DAGNodeCollection(processInfo);
            }
            String[] children = processInfo.getNextProcessIds().split(",");
            for (String child : children) {
                LOGGER.debug("Analyzing child: " + child);
                Integer childKey = new Integer(child);
                DAGNodeCollection childNodeCollection = uniqNodeCollectionTreeMap.get(childKey);
                if (childNodeCollection == null) {
                    childNodeCollection = new DAGNodeCollection(tempProcessInfos.get(childKey));
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
                DAGNode firstNode = null;
                if (nodeCollection.getDAGForkNode() != null) {
                    LOGGER.info("First node in the dag is a fork");
                    firstNode = nodeCollection.getDAGForkNode();
                } else {

                    firstNode = nodeCollection.getChildren().get(0).getInitStepNode();
                }
                LOGGER.info("firstNode=" + firstNode.getName());
                nc.getInitJobNode().setToNode(firstNode);
            }

            uniqNodeCollectionTreeMap.put(processInfo.getProcessId(), nodeCollection);
        }
        LOGGER.info("uniqNodeCollectionTreeMap=" + uniqNodeCollectionTreeMap);
        Map<String,DAGNode> dagNodeMap = new HashMap<String, DAGNode>();
        //oozieNodeMap.put(nc.getStart().getName(), nc.getStart());
        dagNodeMap.put(nc.getInitJobNode().getName(), nc.getInitJobNode());
        LOGGER.info("nodeMaintainer.restartNodes=" + nodeMaintainer.getRestartNodes());
        //add case to nodes
        //nc.getRecoveryDecisionNode().setPreviousHaltNodes(nodeMaintainer.getRestartNodes());
        //oozieNodeMap.put(nc.getRecoveryDecisionNode().getName(), nc.getRecoveryDecisionNode());

        for (Integer id : uniqNodeCollectionTreeMap.keySet()) {
            if (tempProcessInfos.get(id).getParentProcessId() == 0) {
                uniqNodeCollectionTreeMap.get(id).setDAGTaskNode(null);
                uniqNodeCollectionTreeMap.get(id).setTermStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setHaltStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setInitStepNode(null);
                //Add ids to the main process to all process nodes
                nc.setIdForProcessNodes(id);
            }
            LOGGER.info("Adding collection for: " + id);
            dagNodeMap.putAll(uniqNodeCollectionTreeMap.get(id).getDAGNodes());
            stepDAG.append(uniqNodeCollectionTreeMap.get(id).toXML(printedNodeNames));
        }

        dagNodeMap.put(nc.getHaltJobNode().getName(), nc.getHaltJobNode());
        dagNodeMap.put(nc.getTermJobNode().getName(), nc.getTermJobNode());

        LOGGER.info("oozieNodeMap size=" + dagNodeMap.size());
        airflowDAG.append(prefixDAG);
        airflowDAG.append(credentials.toString());
        airflowDAG.append(nc.getInitJobNode().getDAG());
        //airflowDAG.append(nc.getRecoveryDecisionNode().getDAG());
        airflowDAG.append(stepDAG);
        airflowDAG.append(nc.getHaltJobNode().getDAG());
        airflowDAG.append(nc.getTermJobNode().getDAG());
        //airflowDAG.append(nc.getKill().getDAG());
        //airflowDAG.append(nc.getHalt().getDAG());
        String postfixXml ="";
        String homeDir = System.getProperty("user.home");
        try {

            InputStream fis = new FileInputStream(homeDir+"/defFile.txt");
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            Boolean flag = true;
            line = br.readLine();
            while ( (line = br.readLine()) != null) {
                if(flag && line.contains("dag_halt_step_"))
                    continue;
                else
                    flag = false;
                postfixXml = postfixXml + line+"\n";
            }
            File file = new File(homeDir+"/defFile.txt");
            file.delete();
        }catch (IOException e){
            System.out.println("e = " + e);
        }

        airflowDAG.append(postfixXml);
        LOGGER.info("Complete !");
        DAG dag = new DAG();
        dag.setDAG(airflowDAG);
//        dag.setDot(DotUtil.getDot(oozieNodeMap.values()));
        return dag;
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

    public DAG execInfo(List<ProcessInfo> processInfos, String dagName) {
        CommonNodeMaintainer nodeMaintainer = new CommonNodeMaintainer();
        //storing the nodes that are already printed
        //this will prevent same node to be printed multiple times
        DAGNodeCollection nc = nodeMaintainer.getPnc();
        Set<String> printedNodeNames = new HashSet<String>();
        if (dagName == null || dagName.trim().isEmpty()) {
            LOGGER.error(EMPTYERROR);
            throw new MetadataException(EMPTYERROR);
        }
        String owner =System.getProperty("user.name");
        final String prefixDAG = "\nfrom airflow.operators import BashOperator,BranchPythonOperator,DummyOperator,PythonOperator,HiveOperator\n"+
                "from datetime import datetime, timedelta\n"+
                "import os\n" +
                "args = {'owner': '"+ owner+"','start_date': datetime(2015, 10, 1, 5, 40, 0), 'depends_on_past': False}\n" +
                "\n" +
                "dag = airflow.DAG(dag_id='BDRE',  default_args=args)";
        String pid = processInfos.get(0).getProcessId().toString();
        StringBuilder credentials = new StringBuilder();
        credentials.append(isSecurityEnabled(pid, "security"));

        StringBuilder airflowDAG = new StringBuilder();
        StringBuilder stepDAG = new StringBuilder();
        LOGGER.info("Starting dag generation for " + dagName);
        //Populate the map
        Map<Integer, ProcessInfo> tempProcessInfos = new HashMap<Integer, ProcessInfo>();
        for (ProcessInfo processInfo : processInfos) {
            tempProcessInfos.put(processInfo.getProcessId(), processInfo);
        }
        //Populate the tree
        for (ProcessInfo processInfo : processInfos) {
            LOGGER.info("processing " + processInfo);
            DAGNodeCollection nodeCollection = uniqNodeCollectionTreeMap.get(processInfo.getProcessId());
            if (nodeCollection == null) {
                nodeCollection = new DAGNodeCollection(processInfo);
            }
            String[] children = processInfo.getNextProcessIds().split(",");
            for (String child : children) {
                LOGGER.info("Analyzing child: " + child);
                Integer childKey = new Integer(child);
                DAGNodeCollection childNodeCollection = uniqNodeCollectionTreeMap.get(childKey);
                if (childNodeCollection == null) {
                    childNodeCollection = new DAGNodeCollection(tempProcessInfos.get(childKey));
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
                DAGNode firstNode = null;
                if (nodeCollection.getDAGForkNode() != null) {
                    LOGGER.info("First node in the dag is a fork");
                    firstNode = nodeCollection.getDAGForkNode();
                } else {

                    firstNode = nodeCollection.getChildren().get(0).getInitStepNode();
                }
                LOGGER.info("firstNode=" + firstNode.getName());
                nc.getInitJobNode().setToNode(firstNode);
            }

            uniqNodeCollectionTreeMap.put(processInfo.getProcessId(), nodeCollection);
        }
        LOGGER.info("uniqNodeCollectionTreeMap=" + uniqNodeCollectionTreeMap);
        Map<String,DAGNode> dagNodeMap = new HashMap<String, DAGNode>();
        dagNodeMap.put(nc.getInitJobNode().getName(), nc.getInitJobNode());
        LOGGER.info("nodeMaintainer.restartNodes=" + nodeMaintainer.getRestartNodes());
        //add case to nodes
        //nc.getRecoveryDecisionNode().setPreviousHaltNodes(nodeMaintainer.getRestartNodes());
        //oozieNodeMap.put(nc.getRecoveryDecisionNode().getName(), nc.getRecoveryDecisionNode());

        for (Integer id : uniqNodeCollectionTreeMap.keySet()) {
            if (tempProcessInfos.get(id).getParentProcessId() == 0) {
                uniqNodeCollectionTreeMap.get(id).setDAGTaskNode(null);
                uniqNodeCollectionTreeMap.get(id).setTermStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setHaltStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setInitStepNode(null);
                //Add ids to the main process to all process nodes
                nc.setIdForProcessNodes(id);
            }
            LOGGER.info("Adding collection for: " + id);
            dagNodeMap.putAll(uniqNodeCollectionTreeMap.get(id).getDAGNodes());
            stepDAG.append(uniqNodeCollectionTreeMap.get(id).toXML(printedNodeNames));
        }

        dagNodeMap.put(nc.getHaltJobNode().getName(), nc.getHaltJobNode());
        dagNodeMap.put(nc.getTermJobNode().getName(), nc.getTermJobNode());

        LOGGER.info("dagNodeMap size=" + dagNodeMap.size());
        airflowDAG.append(prefixDAG);
        airflowDAG.append(credentials.toString());
        //airflowDAG.append(nc.getStart().getDAG());
        airflowDAG.append(nc.getInitJobNode().getDAG());
        //airflowDAG.append(nc.getRecoveryDecisionNode().getDAG());
        airflowDAG.append(stepDAG);
        airflowDAG.append(nc.getHaltJobNode().getDAG());
        airflowDAG.append(nc.getTermJobNode().getDAG());
        //airflowDAG.append(nc.getKill().getDAG());
        //airflowDAG.append(nc.getHalt().getDAG());
        String postfixDAG ="";
        String homeDir = System.getProperty("user.home");
        try {
            InputStream fis = new FileInputStream(homeDir+"/defFile.txt");
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( (line = br.readLine()) != null) {
                postfixDAG = postfixDAG + line+"\n";
            }
        }catch (IOException e){
            System.out.println("e = " + e);
        }

        airflowDAG.append(postfixDAG);
        LOGGER.info("Complete !");
        DAG dag = new DAG();
        dag.setDAG(airflowDAG);
       // dag.setDot(DotUtil.getDashboardDot(dagNodeMap.values(), processInfos));
        return dag;
    }
}

