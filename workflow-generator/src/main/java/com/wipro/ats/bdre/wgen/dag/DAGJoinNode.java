package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.wgen.dag.CommonNodeMaintainer;
import com.wipro.ats.bdre.wgen.dag.DAGNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGJoinNode extends DAGNode {
    private Set<Integer> idSet = new HashSet<Integer>();

    private DAGJoinNode(Integer id) {
        setId(id);
    }

    /**
     * This static method is done to address situation where a join is followed by a fork.
     *
     * @param id      node id.
     * @param parents List of parents of node having node-id as id.
     * @return This method returns a JoinNode
     */
    public static DAGJoinNode getJoinNode(Integer id, List<DAGNodeCollection> parents, CommonNodeMaintainer nodeMaintainer) {
        Map<Integer, DAGJoinNode> dagJoinNodeMap = nodeMaintainer.getDAGJoinNodeMap();
        DAGJoinNode dagJoinNode = dagJoinNodeMap.get(id);
        if (dagJoinNode == null) {
            dagJoinNode = new DAGJoinNode(id);
           dagJoinNodeMap.put(id, dagJoinNode);
        }
        Set<Integer> childIdSet = new HashSet<Integer>();
        for (int i = 0; i < parents.size(); i++) {
            DAGNodeCollection parent = parents.get(i);
            List<DAGNodeCollection> children = parent.getChildren();
            for (DAGNodeCollection child : children) {
                childIdSet.add(child.getId());
            }
        }
        dagJoinNode.getIdSet().addAll(childIdSet);
        return dagJoinNodeMap.get(id);
    }

    public Set<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(Set<Integer> idSet) {
        this.idSet = idSet;
    }

    @Override
    public DAGNode getTermNode() {
        throw new BDREException("Setting getTermStepNode is not supported");
    }

    @Override
    public void setTermNode(DAGNode node) {
        throw new BDREException("Setting setTermStepNode is not supported");
    }

    @Override
    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Integer joinId : getIdSet()) {
            stringBuilder.append(joinId);
            if (++i < getIdSet().size()) {
                stringBuilder.append("-");
            }

        }
        String joinNodeName = "dag-join-" + stringBuilder;
        return joinNodeName.substring(0, Math.min(joinNodeName.length(), 45));
    }

    @Override
    public String getDAG() {
        try {
            FileWriter fw = new FileWriter("/home/cloudera/defFile.txt", true);
            fw.write("\nf_"+getName().replace('-', '_')+"()");
            fw.close();
        }
        catch (IOException e){
            System.out.println("e = " + e);
        }
        return "\ndef f_"+ getName().replace('-','_')+"():\n" +
                "\t"+ getName().replace('-', '_')+".set_downstream("+ getToNode().getName().replace('-', '_')+")\n" +
                "\n"+ getName().replace('-','_')+" = DummyOperator(task_id='"+getName().replace('-','_') +"', dag=dag)";
    }


}
