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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by arijit on 12/23/14.
 */

/**
 * Methods setId, getName() and getXML() for JoinNode
 * getXML() is formatting the string to be returned as XML format
 */

public class JoinNode extends OozieNode {

    private Set<Integer> idSet = new HashSet<Integer>();

    private JoinNode(Integer id) {
        setId(id);
    }

    /**
     * This static method is done to address situation where a join is followed by a fork.
     *
     * @param id      node id.
     * @param parents List of parents of node having node-id as id.
     * @return This method returns a JoinNode
     */
    public static JoinNode getJoinNode(Integer id, List<NodeCollection> parents, CommonNodeMaintainer nodeMaintainer) {
        Map<Integer, JoinNode> joinNodeMap = nodeMaintainer.getJoinNodeMap();
        JoinNode joinNode = joinNodeMap.get(id);
        if (joinNode == null) {
            joinNode = new JoinNode(id);
            joinNodeMap.put(id, joinNode);
        }
        Set<Integer> childIdSet = new HashSet<Integer>();
        for (int i = 0; i < parents.size(); i++) {
            NodeCollection parent = parents.get(i);
            List<NodeCollection> children = parent.getChildren();
            for (NodeCollection child : children) {
                childIdSet.add(child.getId());
            }
        }
        joinNode.getIdSet().addAll(childIdSet);
        return joinNodeMap.get(id);
    }

    public Set<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(Set<Integer> idSet) {
        this.idSet = idSet;
    }

    @Override
    public OozieNode getTermNode() {
        throw new RuntimeException("Setting getTermStepNode is not supported");
    }

    @Override
    public void setTermNode(OozieNode node) {
        throw new RuntimeException("Setting setTermStepNode is not supported");
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
        String joinNodeName = "join-" + stringBuilder;
        return joinNodeName.substring(0, Math.min(joinNodeName.length(), 45));
    }

    @Override
    public String getXML() {
        return "\n<join name='" + getName() + "' to='" + getToNode().getName() + "'></join>";
    }


}