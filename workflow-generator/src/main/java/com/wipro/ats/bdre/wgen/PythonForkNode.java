package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.exception.BDREException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonForkNode extends OozieNode{
    private Set<Integer> idSet = new HashSet<Integer>();
    private List<OozieNode> toNodes = new ArrayList<OozieNode>();

    private PythonForkNode(Integer id) {
        setId(id);
    }



    public static PythonForkNode getForkNode(Integer id, List<PythonNodeCollection> children, CommonNodeMaintainer nodeMaintainer) {
        Map<Integer, PythonForkNode> pythonForkNodeMap = nodeMaintainer.getPythonForkNodeMap();
        PythonForkNode pythonForkNode = pythonForkNodeMap.get(id);
        if (pythonForkNode == null) {
            pythonForkNode = new PythonForkNode(id);
            pythonForkNodeMap.put(id, pythonForkNode);
        }
        Set<Integer> parentIdSet = new HashSet<Integer>();
        for (int i = 0; i < children.size(); i++) {
            PythonNodeCollection child = children.get(i);
            List<PythonNodeCollection> parents = child.getParents();
            for (PythonNodeCollection parent : parents) {
                parentIdSet.add(parent.getId());
            }
        }

        for (int i = 0; i < children.size(); i++) {
            PythonNodeCollection child = children.get(i);
            List<PythonNodeCollection> parents = child.getParents();
            for (PythonNodeCollection parent : parents) {
                PythonForkNode storedForkedNode = pythonForkNodeMap.get(parent.getId());
                if (storedForkedNode != null) {
                    storedForkedNode.getIdSet().addAll(parentIdSet);
                }
            }
        }
        return pythonForkNodeMap.get(id);
    }

    public Set<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(Set<Integer> idSet) {
        this.idSet = idSet;
    }

    /**
     * This method adds OozieNode node to toNodes ArrayList.
     *
     * @param node node to be added to toNodes.
     */
    public void addToNode(OozieNode node) {
        toNodes.add(node);
    }

    @Override
    public OozieNode getToNode() {
        throw new BDREException("Getting To node from fork is not supported");
    }

    @Override
    public void setToNode(OozieNode node) {
        throw new BDREException("Setting To node to fork is not supported");
    }

    @Override
    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Integer forkId : getIdSet()) {
            stringBuilder.append(forkId);
            if (++i < getIdSet().size()) {
                stringBuilder.append("-");
            }

        }
        String forkNodeName = "python-fork-" + stringBuilder;
        return forkNodeName.substring(0, Math.min(forkNodeName.length(), 45));
    }

    @Override
    public String getXML() {
        try {
            FileWriter fw = new FileWriter("/home/cloudera/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        StringBuilder ret = new StringBuilder("\n"+ getName().replace('-','_')+" = DummyOperator(task_id='"+getName().replace('-','_') +"', dag=dag)\ndef f_"+getName().replace('-','_')+"():");
        for (OozieNode toNode : toNodes) {
            ret.append("\n\t"+getName().replace('-','_')+".set_downstream("+toNode.getName().replace('-','_')+")");
        }
        return ret.toString();
    }

    public List<OozieNode> getToNodes() {
        return toNodes;
    }
}
