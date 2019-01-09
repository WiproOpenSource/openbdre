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

/**
 * Created by arijit on 12/23/14.
 */
/*
toNode is the next node on success
termNode is the next node on failure
*/

public abstract class DAGNode {

    private DAGNode toNode;
    private String name;
    private DAGNode termNode;
    private Integer id;
    private String sid;


    public DAGNode getTermNode() {
        return termNode;
    }

    public void setTermNode(DAGNode termNode) {
        this.termNode = termNode;
    }

    public DAGNode getToNode() {
        return toNode;
    }

    public void setToNode(DAGNode toNode) {
        this.toNode = toNode;
    }

    public String getName() {
        return name;
    }


    public abstract String getDAG();

    public Integer getId() {
        return id;
    }

    public String getSid() {
        return sid;
    }

    protected void setSid(String sid) {
        this.sid = sid;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getDAG();
    }
}