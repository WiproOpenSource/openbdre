package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.wgen.dag.CommonNodeMaintainer;
import com.wipro.ats.bdre.wgen.dag.DAGNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGForkNode extends DAGNode {
    private Set<Integer> idSet = new HashSet<Integer>();
    private List<DAGNode> toNodes = new ArrayList<DAGNode>();

    private DAGForkNode(Integer id) {
        setId(id);
    }



    public static DAGForkNode getForkNode(Integer id, List<DAGNodeCollection> children, CommonNodeMaintainer nodeMaintainer) {
        Map<Integer, DAGForkNode> dagForkNodeMap = nodeMaintainer.getDAGForkNodeMap();
        DAGForkNode dagForkNode = dagForkNodeMap.get(id);
        if (dagForkNode == null) {
            dagForkNode = new DAGForkNode(id);
            dagForkNodeMap.put(id, dagForkNode);
        }
        Set<Integer> parentIdSet = new HashSet<Integer>();
        for (int i = 0; i < children.size(); i++) {
            DAGNodeCollection child = children.get(i);
            List<DAGNodeCollection> parents = child.getParents();
            for (DAGNodeCollection parent : parents) {
                parentIdSet.add(parent.getId());
            }
        }

        for (int i = 0; i < children.size(); i++) {
            DAGNodeCollection child = children.get(i);
            List<DAGNodeCollection> parents = child.getParents();
            for (DAGNodeCollection parent : parents) {
                DAGForkNode storedForkedNode = dagForkNodeMap.get(parent.getId());
                if (storedForkedNode != null) {
                    storedForkedNode.getIdSet().addAll(parentIdSet);
                }
            }
        }
        return dagForkNodeMap.get(id);
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
    public void addToNode(DAGNode node) {
        toNodes.add(node);
    }

    @Override
    public DAGNode getToNode() {
        throw new BDREException("Getting To node from fork is not supported");
    }

    @Override
    public void setToNode(DAGNode node) {
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
        String forkNodeName = "dag-fork-" + stringBuilder;
        return forkNodeName.substring(0, Math.min(forkNodeName.length(), 45));
    }

    @Override
    public String getDAG() {
        try {
            String homeDir = System.getProperty("user.home");
            FileWriter fw = new FileWriter(homeDir+"/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        StringBuilder ret = new StringBuilder("\n"+ getName().replace('-','_')+" = DummyOperator(task_id='"+getName().replace('-','_') +"', dag=dag)\ndef f_"+getName().replace('-','_')+"():");
        for (DAGNode toNode : toNodes) {
            ret.append("\n\t"+getName().replace('-','_')+".set_downstream("+toNode.getName().replace('-','_')+")");
        }
        return ret.toString();
    }

    public List<DAGNode> getToNodes() {
        return toNodes;
    }
}
