package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.exception.BDREException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by SU324335 on 7/1/16.
 */
public class PythonJoinNode extends OozieNode {
    private Set<Integer> idSet = new HashSet<Integer>();

    private PythonJoinNode(Integer id) {
        setId(id);
    }

    /**
     * This static method is done to address situation where a join is followed by a fork.
     *
     * @param id      node id.
     * @param parents List of parents of node having node-id as id.
     * @return This method returns a JoinNode
     */
    public static PythonJoinNode getJoinNode(Integer id, List<PythonNodeCollection> parents, CommonNodeMaintainer nodeMaintainer) {
        Map<Integer, PythonJoinNode> pythonjoinNodeMap = nodeMaintainer.getPythonJoinNodeMap();
        PythonJoinNode pythonjoinNode = pythonjoinNodeMap.get(id);
        if (pythonjoinNode == null) {
            pythonjoinNode = new PythonJoinNode(id);
            pythonjoinNodeMap.put(id, pythonjoinNode);
        }
        Set<Integer> childIdSet = new HashSet<Integer>();
        for (int i = 0; i < parents.size(); i++) {
            PythonNodeCollection parent = parents.get(i);
            List<PythonNodeCollection> children = parent.getChildren();
            for (PythonNodeCollection child : children) {
                childIdSet.add(child.getId());
            }
        }
        pythonjoinNode.getIdSet().addAll(childIdSet);
        return pythonjoinNodeMap.get(id);
    }

    public Set<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(Set<Integer> idSet) {
        this.idSet = idSet;
    }

    @Override
    public OozieNode getTermNode() {
        throw new BDREException("Setting getTermStepNode is not supported");
    }

    @Override
    public void setTermNode(OozieNode node) {
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
        String joinNodeName = "python-join-" + stringBuilder;
        return joinNodeName.substring(0, Math.min(joinNodeName.length(), 45));
    }

    @Override
    public String getXML() {
        return "\n"+getName()+" = DummyOperator(task_id=' "+getName() +" ', dag=dag)";
    }


}
