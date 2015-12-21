/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by arijit on 12/20/14.
 */
public class WorkflowPrinter {
    private static final Logger LOGGER = Logger.getLogger(WorkflowPrinter.class);
    //Add this map for future step chain(to determine the next step of a subprocess)
    private Map<Integer, NodeCollection> uniqNodeCollectionTreeMap = new LinkedHashMap<Integer, NodeCollection>();

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
        NodeCollection nc = nodeMaintainer.getNc();
        Set<String> printedNodeNames = new HashSet<String>();
        if (workflowName == null || workflowName.trim().isEmpty()) {
            LOGGER.error("Empty Workflow name");
            throw new MetadataException("Empty Workflow name");
        }
        final String prefixXml = "<workflow-app name=\"" + workflowName + "\" xmlns=\"uri:oozie:workflow:0.4\">\n";
        String pid = processInfos.get(0).getProcessId().toString();
        StringBuilder credentials = new StringBuilder();
        credentials.append(isSecurityEnabled(pid, "security"));
        final String postfixXml = "\n</workflow-app>";

        StringBuffer workflowXML = new StringBuffer();
        StringBuilder stepXML = new StringBuilder();
        LOGGER.info("Starting workflow generation for " + workflowName);
        //Populate the map
        Map<Integer, ProcessInfo> tempProcessInfos = new HashMap<Integer, ProcessInfo>();
        for (ProcessInfo processInfo : processInfos) {
            tempProcessInfos.put(processInfo.getProcessId(), processInfo);
        }
        //Populate the tree
        for (ProcessInfo processInfo : processInfos) {
            LOGGER.debug("processing " + processInfo);
            NodeCollection nodeCollection = uniqNodeCollectionTreeMap.get(processInfo.getProcessId());
            if (nodeCollection == null) {
                nodeCollection = new NodeCollection(processInfo);
            }
            String[] children = processInfo.getNextProcessIds().split(",");
            for (String child : children) {
                LOGGER.debug("Analyzing child: " + child);
                Integer childKey = new Integer(child);
                NodeCollection childNodeCollection = uniqNodeCollectionTreeMap.get(childKey);
                if (childNodeCollection == null) {
                    childNodeCollection = new NodeCollection(tempProcessInfos.get(childKey));
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
                if (nodeCollection.getForkNode() != null) {
                    LOGGER.info("First node in the workflow is a fork");
                    firstNode = nodeCollection.getForkNode();
                } else {

                    firstNode = nodeCollection.getChildren().get(0).getInitStepNode();
                }
                LOGGER.debug("firstNode=" + firstNode.getName());
                nc.getRecoveryDecisionNode().setToNode(firstNode);
            }

            uniqNodeCollectionTreeMap.put(processInfo.getProcessId(), nodeCollection);
        }
        LOGGER.debug("uniqNodeCollectionTreeMap=" + uniqNodeCollectionTreeMap);
        Map<String, OozieNode> oozieNodeMap = new HashMap<String, OozieNode>();
        oozieNodeMap.put(nc.getStart().getName(), nc.getStart());
        oozieNodeMap.put(nc.getInitJobNode().getName(), nc.getInitJobNode());
        LOGGER.debug("nodeMaintainer.restartNodes=" + nodeMaintainer.getRestartNodes());
        //add case to nodes
        nc.getRecoveryDecisionNode().setPreviousHaltNodes(nodeMaintainer.getRestartNodes());
        oozieNodeMap.put(nc.getRecoveryDecisionNode().getName(), nc.getRecoveryDecisionNode());

        for (Integer id : uniqNodeCollectionTreeMap.keySet()) {
            if (tempProcessInfos.get(id).getParentProcessId() == 0) {
                uniqNodeCollectionTreeMap.get(id).setActionNode(null);
                uniqNodeCollectionTreeMap.get(id).setTermStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setHaltStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setInitStepNode(null);
                //Add ids to the main process to all process nodes
                nc.setIdForProcessNodes(id);
            }
            LOGGER.debug("Adding collection for: " + id);
            oozieNodeMap.putAll(uniqNodeCollectionTreeMap.get(id).getOozieNodes());
            stepXML.append(uniqNodeCollectionTreeMap.get(id).toXML(printedNodeNames));
        }

        oozieNodeMap.put(nc.getHaltJobNode().getName(), nc.getHaltJobNode());
        oozieNodeMap.put(nc.getTermJobNode().getName(), nc.getTermJobNode());
        oozieNodeMap.put(nc.getHalt().getName(), nc.getHalt());
        oozieNodeMap.put(nc.getKill().getName(), nc.getKill());
        LOGGER.debug("oozieNodeMap size=" + oozieNodeMap.size());
        workflowXML.append(prefixXml);
        workflowXML.append(credentials.toString());
        workflowXML.append(nc.getStart().getXML());
        workflowXML.append(nc.getInitJobNode().getXML());
        workflowXML.append(nc.getRecoveryDecisionNode().getXML());
        workflowXML.append(stepXML);
        workflowXML.append(nc.getHaltJobNode().getXML());
        workflowXML.append(nc.getTermJobNode().getXML());
        workflowXML.append(nc.getKill().getXML());
        workflowXML.append(nc.getHalt().getXML());
        workflowXML.append(postfixXml);
        LOGGER.debug("Complete !");
        Workflow workflow = new Workflow();
        workflow.setXml(workflowXML);
        workflow.setDot(DotUtil.getDot(oozieNodeMap.values()));
        return workflow;
    }

    public String isSecurityEnabled(String pid, String configGroup) {
        GetProperties getProperties = new GetProperties();
        java.util.Properties isEnabled = getProperties.getProperties(pid, configGroup);
        Enumeration e = isEnabled.propertyNames();
        StringBuilder addCredentials = new StringBuilder();
        if (isEnabled.size() != 0) {
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
        if (properties.size() != 0) {
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
        NodeCollection nc = nodeMaintainer.getNc();
        Set<String> printedNodeNames = new HashSet<String>();
        if (workflowName == null || workflowName.trim().isEmpty()) {
            LOGGER.error("Empty Workflow name");
            throw new MetadataException("Empty Workflow name");
        }
        final String prefixXml = "<workflow-app name=\"" + workflowName + "\" xmlns=\"uri:oozie:workflow:0.4\">\n";
        String pid = processInfos.get(0).getProcessId().toString();
        StringBuilder credentials = new StringBuilder();
        credentials.append(isSecurityEnabled(pid, "security"));
        final String postfixXml = "\n</workflow-app>";

        StringBuffer workflowXML = new StringBuffer();
        StringBuilder stepXML = new StringBuilder();
        LOGGER.info("Starting workflow generation for " + workflowName);
        //Populate the map
        Map<Integer, ProcessInfo> tempProcessInfos = new HashMap<Integer, ProcessInfo>();
        for (ProcessInfo processInfo : processInfos) {
            tempProcessInfos.put(processInfo.getProcessId(), processInfo);
        }
        //Populate the tree
        for (ProcessInfo processInfo : processInfos) {
            LOGGER.debug("processing " + processInfo);
            NodeCollection nodeCollection = uniqNodeCollectionTreeMap.get(processInfo.getProcessId());
            if (nodeCollection == null) {
                nodeCollection = new NodeCollection(processInfo);
            }
            String[] children = processInfo.getNextProcessIds().split(",");
            for (String child : children) {
                LOGGER.debug("Analyzing child: " + child);
                Integer childKey = new Integer(child);
                NodeCollection childNodeCollection = uniqNodeCollectionTreeMap.get(childKey);
                if (childNodeCollection == null) {
                    childNodeCollection = new NodeCollection(tempProcessInfos.get(childKey));
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
                if (nodeCollection.getForkNode() != null) {
                    LOGGER.info("First node in the workflow is a fork");
                    firstNode = nodeCollection.getForkNode();
                } else {

                    firstNode = nodeCollection.getChildren().get(0).getInitStepNode();
                }
                LOGGER.debug("firstNode=" + firstNode.getName());
                nc.getRecoveryDecisionNode().setToNode(firstNode);
            }

            uniqNodeCollectionTreeMap.put(processInfo.getProcessId(), nodeCollection);
        }
        LOGGER.debug("uniqNodeCollectionTreeMap=" + uniqNodeCollectionTreeMap);
        Map<String, OozieNode> oozieNodeMap = new HashMap<String, OozieNode>();
        oozieNodeMap.put(nc.getStart().getName(), nc.getStart());
        oozieNodeMap.put(nc.getInitJobNode().getName(), nc.getInitJobNode());
        LOGGER.debug("nodeMaintainer.restartNodes=" + nodeMaintainer.getRestartNodes());
        //add case to nodes
        nc.getRecoveryDecisionNode().setPreviousHaltNodes(nodeMaintainer.getRestartNodes());
        oozieNodeMap.put(nc.getRecoveryDecisionNode().getName(), nc.getRecoveryDecisionNode());

        for (Integer id : uniqNodeCollectionTreeMap.keySet()) {
            if (tempProcessInfos.get(id).getParentProcessId() == 0) {
                uniqNodeCollectionTreeMap.get(id).setActionNode(null);
                uniqNodeCollectionTreeMap.get(id).setTermStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setHaltStepNode(null);
                uniqNodeCollectionTreeMap.get(id).setInitStepNode(null);
                //Add ids to the main process to all process nodes
                nc.setIdForProcessNodes(id);
            }
            LOGGER.debug("Adding collection for: " + id);
            oozieNodeMap.putAll(uniqNodeCollectionTreeMap.get(id).getOozieNodes());
            stepXML.append(uniqNodeCollectionTreeMap.get(id).toXML(printedNodeNames));
        }

        oozieNodeMap.put(nc.getHaltJobNode().getName(), nc.getHaltJobNode());
        oozieNodeMap.put(nc.getTermJobNode().getName(), nc.getTermJobNode());
        oozieNodeMap.put(nc.getHalt().getName(), nc.getHalt());
        oozieNodeMap.put(nc.getKill().getName(), nc.getKill());
        LOGGER.debug("oozieNodeMap size=" + oozieNodeMap.size());
        workflowXML.append(prefixXml);
        workflowXML.append(credentials.toString());
        workflowXML.append(nc.getStart().getXML());
        workflowXML.append(nc.getInitJobNode().getXML());
        workflowXML.append(nc.getRecoveryDecisionNode().getXML());
        workflowXML.append(stepXML);
        workflowXML.append(nc.getHaltJobNode().getXML());
        workflowXML.append(nc.getTermJobNode().getXML());
        workflowXML.append(nc.getKill().getXML());
        workflowXML.append(nc.getHalt().getXML());
        workflowXML.append(postfixXml);
        LOGGER.debug("Complete !");
        Workflow workflow = new Workflow();
        workflow.setXml(workflowXML);
        workflow.setDot(DotUtil.getDashboardDot(oozieNodeMap.values(), processInfos));
        return workflow;
    }

}