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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by arijit on 1/15/15.
 */
public class CommonNodeMaintainer {
    private NodeCollection nc = new NodeCollection();
    private Set<String> printedNodeNames = new HashSet<String>();
    private Map<Integer, ForkNode> forkNodeMap = new HashMap<Integer, ForkNode>();
    private Map<Integer, JoinNode> joinNodeMap = new HashMap<Integer, JoinNode>();
    private Set<OozieNode> restartNodes = new HashSet<OozieNode>();

    public Set<OozieNode> getRestartNodes() {
        return restartNodes;
    }

    public void setRestartNodes(Set<OozieNode> restartNodes) {
        this.restartNodes = restartNodes;
    }


    public NodeCollection getNc() {
        return nc;
    }

    public void setNc(NodeCollection nc) {
        this.nc = nc;
    }

    public Set<String> getPrintedNodeNames() {
        return printedNodeNames;
    }

    public void setPrintedNodeNames(Set<String> printedNodeNames) {
        this.printedNodeNames = printedNodeNames;
    }

    public Map<Integer, ForkNode> getForkNodeMap() {
        return forkNodeMap;
    }

    public void setForkNodeMap(Map<Integer, ForkNode> forkNodeMap) {
        this.forkNodeMap = forkNodeMap;
    }

    public Map<Integer, JoinNode> getJoinNodeMap() {
        return joinNodeMap;
    }

    public void setJoinNodeMap(Map<Integer, JoinNode> joinNodeMap) {
        this.joinNodeMap = joinNodeMap;
    }
}
