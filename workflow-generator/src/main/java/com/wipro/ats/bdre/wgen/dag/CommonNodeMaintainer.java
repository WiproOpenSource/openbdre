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

package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.wgen.dag.DAGNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by mi294210 on 07/05/16.
 */

public class CommonNodeMaintainer {
    private DAGNodeCollection pnc = new DAGNodeCollection();
    private Set<String> printedNodeNames = new HashSet<String>();
    private Map<Integer, DAGForkNode> dagForkNodeMap = new HashMap<Integer, DAGForkNode>();
    private Map<Integer, DAGJoinNode> dagJoinNodeMap = new HashMap<Integer, DAGJoinNode>();
    private Set<DAGNode> restartNodes = new HashSet<DAGNode>();
    public DAGNodeCollection getPnc() {
        return pnc;
    }

    public void setPnc(DAGNodeCollection pnc) {
        this.pnc = pnc;
    }

    public Map<Integer, DAGForkNode> getDAGForkNodeMap() {
        return dagForkNodeMap;
    }

    public void setDAGForkNodeMap(Map<Integer, DAGForkNode> dagForkNodeMap) {
        this.dagForkNodeMap = dagForkNodeMap;
    }

    public Map<Integer, DAGJoinNode> getDAGJoinNodeMap() {
        return dagJoinNodeMap;
    }


    public void setDAGJoinNodeMap(Map<Integer, DAGJoinNode> dagJoinNodeMap) {
        this.dagJoinNodeMap = dagJoinNodeMap;
    }

    public Set<DAGNode> getRestartNodes() {
        return restartNodes;
    }

    public void setRestartNodes(Set<DAGNode> restartNodes) {
        this.restartNodes = restartNodes;
    }
    public Set<String> getPrintedNodeNames() {
        return printedNodeNames;
    }

    public void setPrintedNodeNames(Set<String> printedNodeNames) {
        this.printedNodeNames = printedNodeNames;
    }
}
