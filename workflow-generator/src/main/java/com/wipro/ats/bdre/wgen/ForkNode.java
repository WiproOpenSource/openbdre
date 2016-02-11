/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.wgen;

import java.util.*;

/**
 * Created by arijit on 12/23/14.
 */
/*
Methods getName() and getXML() for ForkNode
getXML() is formatting the string to be returned as XML format
A fork node splits one path of execution into multiple concurrent paths of execution.

*/


public class ForkNode extends OozieNode {

    private Set<Integer> idSet = new HashSet<Integer>();
    private List<OozieNode> toNodes = new ArrayList<OozieNode>();

    private ForkNode(Integer id) {
        setId(id);
    }

    /**
     * This static method is done to address situation where a join is followed by a fork.
     *
     * @param id       This variable is referred as node id.
     * @param children List of children of the node having node-id as id.
     * @return This method return a fork node.
     */

    public static ForkNode getForkNode(Integer id, List<NodeCollection> children, CommonNodeMaintainer nodeMaintainer) {
        Map<Integer, ForkNode> forkNodeMap = nodeMaintainer.getForkNodeMap();
        ForkNode forkNode = forkNodeMap.get(id);
        if (forkNode == null) {
            forkNode = new ForkNode(id);
            forkNodeMap.put(id, forkNode);
        }
        Set<Integer> parentIdSet = new HashSet<Integer>();
        for (int i = 0; i < children.size(); i++) {
            NodeCollection child = children.get(i);
            List<NodeCollection> parents = child.getParents();
            for (NodeCollection parent : parents) {
                parentIdSet.add(parent.getId());
            }
        }
        //forkNode.getIdSet().addAll(parentIdSet);

        for (int i = 0; i < children.size(); i++) {
            NodeCollection child = children.get(i);
            List<NodeCollection> parents = child.getParents();
            for (NodeCollection parent : parents) {
                ForkNode storedForkedNode = forkNodeMap.get(parent.getId());
                if (storedForkedNode != null) {
                    storedForkedNode.getIdSet().addAll(parentIdSet);
                }
            }
        }
        return forkNodeMap.get(id);
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
        throw new RuntimeException("Getting To node from fork is not supported");
    }

    @Override
    public void setToNode(OozieNode node) {
        throw new RuntimeException("Setting To node to fork is not supported");
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
        String forkNodeName = "fork-" + stringBuilder;
        return forkNodeName.substring(0, Math.min(forkNodeName.length(), 45));
    }

    @Override
    public String getXML() {
        StringBuilder ret = new StringBuilder("\n<fork name='" + getName() + "'>");
        for (OozieNode toNode : toNodes) {
            ret.append("\n\t<path start='" + toNode.getName() + "' />");
        }
        ret.append("\n</fork>");
        return ret.toString();
    }

    public List<OozieNode> getToNodes() {
        return toNodes;
    }
}