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

package com.wipro.ats.bdre.daggen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arijit on 1/3/15.
 */
public class WorkflowUtil {

    List<OozieNode> oozieNodeList = new ArrayList<OozieNode>();
    Map<String, OozieNode> oozieNodeMap = new HashMap<String, OozieNode>();

    /**
     * This method rearranges oozie nodes.
     *
     * @param oozieNodeMap a map containg all oozie nodes
     * @return
     */
    public List<OozieNode> rearrangeOozieNodes(Map<String, OozieNode> oozieNodeMap) {
        this.oozieNodeMap = oozieNodeMap;
        addToNode(oozieNodeMap.get("fork-205"));
        addToNode(oozieNodeMap.get("term-step-206"));
        return oozieNodeList;
    }

    private void addToNode(OozieNode node) {
        oozieNodeList.add(node);
        if (node instanceof ForkNode) {
            ForkNode forkNode = (ForkNode) node;
            for (OozieNode forkChild : forkNode.getToNodes()) {
                addToNode(forkChild);
            }
        } else if (!("halt-job".equalsIgnoreCase(node.getToNode().getName()) || "term-job".equalsIgnoreCase(node.getToNode().getName()))) {
            OozieNode nextNode = oozieNodeMap.get(node.getToNode().getName());
            addToNode(nextNode);
        }
        return;
    }

    private void addTermNode(OozieNode node) {
        oozieNodeList.add(node);
        if (node instanceof ForkNode) {
            ForkNode forkNode = (ForkNode) node;
            for (OozieNode forkChild : forkNode.getToNodes()) {
                addTermNode(forkChild);
            }
        } else if (!("halt-job".equalsIgnoreCase(node.getToNode().getName()) || "term-job".equalsIgnoreCase(node.getToNode().getName()))) {
            OozieNode nextNode = oozieNodeMap.get(node.getToNode().getName());
            addTermNode(nextNode);
        }
        return;
    }


}
