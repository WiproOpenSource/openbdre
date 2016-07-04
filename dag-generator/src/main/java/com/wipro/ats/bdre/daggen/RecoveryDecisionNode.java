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

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by arijit on 12/21/14.s
 */


/*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML. 
*/

public class RecoveryDecisionNode extends OozieNode {
    private static final Logger LOGGER = Logger.getLogger(RecoveryDecisionNode.class);


    private Set<OozieNode> previousHaltNodes = new HashSet<OozieNode>();

    public RecoveryDecisionNode() {

    }

    public Set<OozieNode> getPreviousHaltNodes() {
        return previousHaltNodes;
    }

    public void setPreviousHaltNodes(Set<OozieNode> previousHaltNodes) {
        this.previousHaltNodes = previousHaltNodes;
    }

    public String getName() {
        return "recovery-test";
    }

    @Override
    public String getXML() {

        StringBuilder ret = new StringBuilder();
        ret.append("\n<decision name=\"" + getName() + "\">\n" +
                "    <switch>");

        String phPhn = null;
        String phn = null;
        LOGGER.debug("number of previousHaltNodes is " + previousHaltNodes.size());

        for (OozieNode previousHaltNode : previousHaltNodes) {
            phn = previousHaltNode.getToNode().getName();
            LOGGER.debug("\n\n\n");
            LOGGER.debug("node entering in loop is " + previousHaltNode.getToNode().getName());
            LOGGER.debug("ph_phn is " + phPhn);

            if (phPhn == null) {
                phPhn = previousHaltNode.getToNode().getName();
                LOGGER.debug("ph_phn is " + previousHaltNode.getToNode().getName());
            } else {
                phPhn = phn;
                LOGGER.debug("ph_phn is " + phn);
            }

            LOGGER.debug("phn is " + phn);
            LOGGER.debug("compare is" + getToNode().getName());


            LOGGER.debug("jsut before comparing  " + phPhn);
            if (phPhn.startsWith("init-step-")) {
                LOGGER.debug("init-step- " + phPhn);
                String resumeId = phPhn.substring(10);
                ret.append("\n      <case to=\"" + phn + "\">" +
                        "${wf:actionData('init-job')['last-recoverable-sp-id'] eq '" + resumeId + "'}" +
                        "</case>");

            } else if (phPhn.startsWith("fork-")) {
                LOGGER.debug("fork- " + phPhn);
                String[] forkIds = phPhn.substring(5).split("-");
                for (String resumeId : forkIds) {
                    ret.append("\n      <case to=\"" + phn + "\">" +
                            "${wf:actionData('init-job')['last-recoverable-sp-id'] eq '" + resumeId + "'}" +
                            "</case>");
                }
                if (getToNode().getName().equals(phn))      // skip first node
                    continue;
            }
        }

        ret.append("\n      <default to=\"" + getToNode().getName() + "\"/>\n" +
                "    </switch>\n" +
                "</decision>");

        return ret.toString();
    }
}