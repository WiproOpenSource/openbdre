package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.wgen.dag.CommonNodeMaintainer;
import com.wipro.ats.bdre.wgen.dag.DAGNode;

import java.util.*;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGNodeCollection {
    /**
     * Creating objects of the node types InitJobNode, HaltJobNode, TermJobNode
     * and HaltNode. They are common to the entire workflow and are referred by all the nodes.
     * <p/>
     * Next, we are setting the ToNode(next node on success) and TermNode(next node on failure)for initJobNode,haltJobNode,
     * termJobNode and haltStepNode. They are in a default constructor because this definition is constant. For example, termJobNode
     * on success will necessarily go to the killNode.
     */


    private DAGInitJobNode initJobNode = new DAGInitJobNode();
    private DAGHaltJobNode haltJobNode = new DAGHaltJobNode();
    private DAGTermJobNode termJobNode = new DAGTermJobNode();


    /**
     * Objects of node types InitStepNode, TaskNode, HaltStepNode, TermStepNode, JoinNode, ForkNode are created.
     * There would be one initStepNode , one actionNode, one haltStepNode, one termStepNode associated for each step(sub-process) in the workflow.
     * <p/>
     * Join and Fork nodes are not created initially as they would be applicable only in relevant situations.
     * List containing NodeCollection object is created for parents and children.
     */
    private DAGInitStepNode initStepNode;
    private DAGTaskNode taskNode;
    private DAGHaltStepNode haltStepNode;
    private DAGTermStepNode termStepNode;
    //private JoinNode joinNode;
    private DAGHaltStepNode haltStepNode1;
    //private ForkNode forkNode;
    private DAGForkNode dagForkNode;
    private DAGJoinNode dagJoinNode;
    private Integer id;
    private List<DAGNodeCollection> parents = new ArrayList<DAGNodeCollection>();
    private List<DAGNodeCollection> children = new ArrayList<DAGNodeCollection>();


    /**
     * Instantiating object of class NodeCollection using ProcessInfo.
     * Setting the ToNode( next node on success ) and FailNode ( next node on failure ) for the nodes initStepNode, actionNode, termStepNode and haltStepNode
     * These are the default relations(flow) and are used to initialize the constructor of NodeCollection
     * initStepNode on success --> actionNode   ;  initStepNode on failure --> termJobNode
     * actionNode on success --> haltStepNode     ;  actionNode on failure --> termStepNode
     * termStepNode on success --> termJobNode;  termStepNode on failure --> termJobNode
     * haltStepNode on failure --> termStepNode
     *
     * @param processInfo This variable contains information regarding process like id, description, busDomain Id etc.
     */

    public DAGNodeCollection(ProcessInfo processInfo) {
        this.id = processInfo.getProcessId();
        initStepNode = new DAGInitStepNode(id);
        taskNode = new DAGTaskNode(id);
        taskNode.setProcessInfo(processInfo);
        termStepNode = new DAGTermStepNode(id);
        initStepNode.setToNode(taskNode);
        initStepNode.setTermNode(termStepNode);
        haltStepNode = new DAGHaltStepNode(id.toString());
        taskNode.setToNode(haltStepNode);
        haltStepNode.setToNode(haltJobNode);
        haltStepNode.setTermNode(termStepNode);
        taskNode.setTermNode(termStepNode);
        termStepNode.setToNode(termJobNode);
        termStepNode.setTermNode(termJobNode);
    }

    public DAGNodeCollection() {
        /**
         * haltJobNode on failure --> termJobNode
         * initJobNode on success---> initStepNode     
         * initJobNode on failure --> termJobNode
         */


//TODO        initJobNode.setToNode(recoveryDecisionNode);
        initJobNode.setTermNode(termJobNode);
    }

    /**
     * This method sets id for initJobNode, haltJobNode,termJobNode
     *
     * @param id process id
     */
    public void setIdForProcessNodes(Integer id) {
        initJobNode.setId(id);
        haltJobNode.setId(id);
        termJobNode.setId(id);
    }


    // Getter-setter methods for the node types

    public DAGJoinNode getDAGJoinNode() {
        return dagJoinNode;
    }

    public void setDAGJoinNode(DAGJoinNode dagJoinNode) {
        this.dagJoinNode = dagJoinNode;
    }

    public DAGForkNode getDAGForkNode() {
        return dagForkNode;
    }

    public void setDAGForkNode(DAGForkNode dagForkNode) {
        this.dagForkNode = dagForkNode;
    }

    public DAGInitJobNode getInitJobNode() {
        return initJobNode;
    }

    public DAGHaltJobNode getHaltJobNode() {
        return haltJobNode;
    }

    public DAGTermJobNode getTermJobNode() {
        return termJobNode;
    }


    /**
     * This method traverses through each process node and checks for parent(s) and populates the parent collection
     * <p/>
     * Populating parentCollection.
     * For processes with only parent, the parent process is added to parentCollection. The haltStepNode of
     * parent process is directed to initStepNode of the current process, thereby establishing a parent-child relationship
     * between them.
     * <p/>
     * For processes with multiple parents we use Join.
     * The success of action nodes of all the parent processes are directed to the joinNode.
     * Hence,
     * actionNode for parent 1 ---> joinNode, actionNode for parent 2 ---> joinNode and so on.
     *
     * @param parentCollection An instance of NodeCollection having values about parents.
     */

    public void addParent(DAGNodeCollection parentCollection, CommonNodeMaintainer nodeMaintainer) {
        if (parents.isEmpty()) {
            parents.add(parentCollection);
            parentCollection.getHaltStepNode().setToNode(initStepNode);
            nodeMaintainer.getRestartNodes().add(parentCollection.getHaltStepNode());
        } else {
            parents.add(parentCollection);
            if (dagJoinNode == null) {
                dagJoinNode = DAGJoinNode.getJoinNode(id, parents, nodeMaintainer);
            }
            StringBuilder sid = new StringBuilder("");
            for (int i = 0; i < parents.size(); i++) {
                if (i < parents.size() - 1) {
                    sid.append(parents.get(i).getId().toString() + ",");
                } else {
                    sid.append(parents.get(i).getId().toString());
                }
            }
            if (haltStepNode1 == null) {
                haltStepNode1 = new DAGHaltStepNode(sid.toString());
                haltStepNode1.setTermNode(parents.get(0).getTermStepNode());
            } else {
                haltStepNode1.setSid(sid.toString());
            }

            for (int i = 0; i < parents.size(); i++) {      /*Traversing parents of current node*/
                DAGNodeCollection parent = parents.get(i);     //Choosing parents
                parent.getDAGTaskNode().setToNode(dagJoinNode);


                if (i == 0) {

                    dagJoinNode.setToNode(haltStepNode1);
                    parent.setHaltStepNode(haltStepNode1);
                    nodeMaintainer.getRestartNodes().add(haltStepNode1);
                    nodeMaintainer.getRestartNodes().remove(parent.getHaltStepNode());
                    parent.getTermStepNode().setToNode(parents.get(i + 1).getTermStepNode());


                } else if (i == parents.size() - 1) {
                    /**
                     *   Condition ( i == parents.size() - 1 ) corresponds to the last parent process. The Nth parent of a process having
                     *   N parent processes.
                     *       1. The haltStepNode on success is directed to the initStepNode of next step.
                     *       2. TermNode is directed to termJobNode
                     *       3. ActionNode on failure is directed to TermNode of parent 1.
                     *       Hence,
                     *           TermNode of parent N --> TermJobNode
                     *           ActionNode of parent N on failure --> TermNode of parent 1.
                     *
                     */

                    parent.setHaltStepNode(haltStepNode1);
                    parent.getHaltStepNode().setToNode(initStepNode);
                    parent.getTermStepNode().setToNode(termJobNode);
                    parent.getDAGTaskNode().setTermNode(parents.get(0).getTermStepNode());
                } else {

                    /**
                     *Setting the next nodes for HaltNode and FailNode for the nodes in between the first and last parent node
                     *   For every parent excepting the first and the last, the HaltNode on success is directed to HaltNode of next
                     *   parent, TermNode on success is directed to TermNode of next parent, and ActionNode on failure is directed to
                     *   TermNode of parent 1
                     *   Hence,
                     *       1. HaltNode of parent 2 on success --> HaltNode of parent 3, HaltNode of parent 3 on success --> HaltNode of parent 4
                     *          and so on.
                     *       2. TermNode of parent 2 --> TermNode of parent 3, TermNode of parent 4 --> TermNode of parent 5
                     *          and so on.
                     *       3. ActionNode of parent 2 on failure --> TermNode of parent 1, ActionNode of parent 3 on failure --> TermNode of parent 1,
                     *          ActionNode of parent 4 on failure --> TermNode of parent 1 and so on.
                     *          The structure established as a result is
                     *
                     *          ActionNode 1 on failure --> TermNode 1 --> TermNode 2 --> ..... --> TermNode N
                     *          ActionNode 2 on failure --> TermNode 1 --> TermNode 2 --> ..... --> TermNode N
                     *          ActionNode 3 on failure --> TermNode 1 --> TermNode 2 --> ..... --> TermNode N
                     *          .
                     *          .
                     *          .
                     *          ActionNode N on failure --> TermNode 1 --> TermNode 2 --> ..... --> TermNode N
                     *
                     *          This common failure instance is shared by ActionNode of all the parents.
                     */


                    parent.getHaltStepNode().setToNode(haltStepNode1);
                    parent.getTermStepNode().setToNode(parents.get(i + 1).getTermStepNode());
                    parent.getDAGTaskNode().setTermNode(parents.get(0).getTermStepNode());

                }
            }
        }
    }

    /**
     * Populating childCollection
     * When a process has only one child process, the child is added to childCollection and haltStepNode of the current process
     * is directed to BeginNode of the childNode.
     * In case of multiple children, all the child processes are traversed one by one and are added to childCollection.
     * Multiple child processes are handled using something called the Fork Control Node.
     *
     * @param childCollection An instance of NodeCollection having values about parents.
     */


    public void addChild(DAGNodeCollection childCollection, CommonNodeMaintainer nodeMaintainer) {
        if (children.isEmpty()) {
            children.add(childCollection);
            haltStepNode.setToNode(childCollection.getInitStepNode());
        } else {
            children.add(childCollection);

            /**
             * Inserting a forkNode into the workflow. A fork node splits one path of execution into multiple concurrent paths
             * of execution. Fork Node is inserted between the current process and the multiple child processes.
             * Structurally,
             *      1. forkNode --> BeginNode of child 1, forkNode --> BeginNode of child 2,..... forkNode --> BeginNode of child N
             *      2. haltStepNode of current parent process --> forkNode
             */

            if (dagForkNode == null) {
                dagForkNode = DAGForkNode.getForkNode(id, children, nodeMaintainer);
                for (DAGNodeCollection child : children) {
                    dagForkNode.addToNode(child.getInitStepNode());
                }
            } else {
                dagForkNode.addToNode(childCollection.getInitStepNode());
            }
            haltStepNode.setToNode(dagForkNode);
        }

    }

    /**
     * Getter and setter methods for initStepNode, actionNode, haltStepNode,
     * termStepNode, joinNode, forkNode
     */
    public DAGInitStepNode getInitStepNode() {
        return initStepNode;
    }

    public void setInitStepNode(DAGInitStepNode initStepNode) {
        this.initStepNode = initStepNode;
    }

    public DAGTaskNode getDAGTaskNode() {
        return taskNode;
    }

    public void setDAGTaskNode(DAGTaskNode actionNode) {
        this.taskNode = actionNode;
    }

    public DAGHaltStepNode getHaltStepNode() {
        return haltStepNode;
    }

    public void setHaltStepNode(DAGHaltStepNode haltStepNode) {
        this.haltStepNode = haltStepNode;
    }

    public DAGTermStepNode getTermStepNode() {
        return termStepNode;
    }

    public void setTermStepNode(DAGTermStepNode termStepNode) {
        this.termStepNode = termStepNode;
    }

/*    public JoinNode getJoinNode() {
        return joinNode;
    }

    public void setJoinNode(JoinNode joinNode) {
        this.joinNode = joinNode;
    }

    public ForkNode getForkNode() {
        return forkNode;
    }

    public void setForkNode(ForkNode forkNode) {
        this.forkNode = forkNode;
    }*/


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    // Getter- Setter methods.

    public List<DAGNodeCollection> getParents() {
        return parents;
    }

    public void setParents(List<DAGNodeCollection> parents) {
        this.parents = parents;
    }

    public List<DAGNodeCollection> getChildren() {
        return children;
    }

    public void setChildren(List<DAGNodeCollection> children) {
        this.children = children;
    }

    /**
     * Method toString()
     * Used to format the string to be returned.
     * Ex.
     * Parents: [1] Children: [2,3] would be the string returned for a process with process id having 2 children
     * having IDs 2 and 3
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Parents: [");
        for (DAGNodeCollection parent : parents) {
            stringBuilder.append(parent.getId() + " ");
        }
        stringBuilder.append("];Children: [");
        for (DAGNodeCollection child : children) {
            stringBuilder.append(child.getId() + " ");

        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * This method creates map for nodes present in oozie workflow
     *
     * @return Returns hash-map containing nodes.
     */
    public Map<String, DAGNode> getDAGNodes() {
        Map<String, DAGNode> oozieNodeMap = new HashMap<String, DAGNode>();

        if (dagForkNode != null) {
            oozieNodeMap.put(dagForkNode.getName(), dagForkNode);
        }
        if (initStepNode != null) {
            oozieNodeMap.put(initStepNode.getName(), initStepNode);
        }
        if (taskNode != null) {
            for (DAGNode containingNode : taskNode.getContainingNodes()) {
                oozieNodeMap.put(containingNode.getName(), containingNode);
            }
        }
        if (dagJoinNode != null) {
            oozieNodeMap.put(dagJoinNode.getName(), dagJoinNode);
        }
        if (haltStepNode != null) {
            oozieNodeMap.put(haltStepNode.getName(), haltStepNode);
        }
        if (termStepNode != null) {
            oozieNodeMap.put(termStepNode.getName(), termStepNode);
        }
        if (termStepNode != null) {
            oozieNodeMap.put(termStepNode.getName(), termStepNode);
        }
        return oozieNodeMap;
    }

    /**
     * Method toXML()
     * Here , we append the various node types to the stringBuffer, which  calls the toString method
     * of the parent class OozieNode. That internally calls getXML() and it finally returns the string
     * in XML format
     *
     * @return
     */

    public String toXML(Set<String> printedNodeNames) {
        StringBuilder stringBuilder = new StringBuilder();
        if (dagForkNode != null && !printedNodeNames.contains(dagForkNode.getName())) {
            stringBuilder.append(dagForkNode);
            printedNodeNames.add(dagForkNode.getName());
        }
        if (initStepNode != null && !printedNodeNames.contains(initStepNode.getName())) {
            stringBuilder.append(initStepNode);
            printedNodeNames.add(initStepNode.getName());
        }
        if (taskNode != null) {

            for (DAGNode containingNode : taskNode.getContainingNodes()) {
                if (!printedNodeNames.contains(containingNode.getName())) {
                    stringBuilder.append(containingNode);
                    printedNodeNames.add(containingNode.getName());
                }

            }
        }
        if (dagJoinNode != null && !printedNodeNames.contains(dagJoinNode.getName())) {
            stringBuilder.append(dagJoinNode);
            printedNodeNames.add(dagJoinNode.getName());
        }
        if (haltStepNode != null && !printedNodeNames.contains(haltStepNode.getName())) {
            stringBuilder.append(haltStepNode);
            printedNodeNames.add(haltStepNode.getName());
        }
        if (termStepNode != null && !printedNodeNames.contains(termStepNode.getName())) {
            stringBuilder.append(termStepNode);
            printedNodeNames.add(termStepNode.getName());
        }
        return stringBuilder.toString();
    }
}
