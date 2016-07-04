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

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * Created by arijit on 1/4/15.
 */
public class DotUtil {
    private static final Logger LOGGER = Logger.getLogger(DotUtil.class);
    private static final String SHAPE=  " [shape=rectangle,style=\"rounded\",penwidth=3];";
    private static final String FORK = "fork_";

    private DotUtil(){
    }
    /**
     * This method creates schematic diagram of oozie workflow using dot language.
     *
     * @param oozieNodes This is a collection containing all oozie nodes to be used in the workflow.
     * @return Method returns String buffer of schematic diagram of oozie workflow.
     */
    public static StringBuilder getDot(Collection<OozieNode> oozieNodes) {
        StringBuilder dot = new StringBuilder("digraph{\n" +

                "ratio=auto;" +
                "ranksep=.25;" +
                "nodesep=.25;" +
                "node[margin=\".30,.055\",fontsize=7,fontname=\"verdana\"];" +
                "labelloc=\"t\";" +
                "label=<<FONT\n" +
                "  COLOR=\"gray\"\n" +
                "  FACE=\"verdana\"\n" +
                "  POINT-SIZE=\"20\"\n" +
                ">Workflow</FONT>>;" +
                "\n");
        String dotLine = "";
        for (OozieNode oozieNode : oozieNodes) {
            if (oozieNode instanceof ForkNode) {
                ForkNode forkNode = (ForkNode) oozieNode;
                dot.append(forkNode.getName().replace('-', '_') + " [shape=triangle];");
                for (OozieNode child : forkNode.getToNodes()) {
                    dotLine = oozieNode.getName().replace('-', '_') + "->" + child.getName().replace('-', '_');
                    LOGGER.debug(dotLine);
                    dot.append(dotLine + ";\n");
                }
            } else if (oozieNode instanceof JoinNode) {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=invtriangle];");
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                dot.append(dotLine + ";\n");
                LOGGER.debug(dotLine);
            } else if (oozieNode instanceof KillNode) {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=doublecircle,style=filled,color=red,fontcolor=white];");
                LOGGER.debug("Hit " + oozieNode.getClass().getName());
            } else if (oozieNode instanceof HaltNode) {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=doubleoctagon,style=filled,color=darkgreen,fontcolor=white];");
                LOGGER.debug("Hit " + oozieNode.getClass().getName());
            } else if (oozieNode instanceof StartNode) {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=oval,style=filled,color=darkolivegreen3];");
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            } else if (oozieNode.getName().startsWith("init-step")) {
                dot.append(oozieNode.getName().replace('-', '_') +SHAPE);
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            } else if (oozieNode.getName().startsWith("halt-step")) {
                dot.append(oozieNode.getName().replace('-', '_') +SHAPE);
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            } else if (oozieNode.getName().startsWith("term-step")) {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=rectangle,style=\"rounded\",penwidth=3,color=red];");
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            } else if (oozieNode.getName().startsWith("term-job")) {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=rectangle,style=\"rounded\",penwidth=3,color=red];");
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            } else if (oozieNode.getName().startsWith("init-job")) {
                dot.append(oozieNode.getName().replace('-', '_') +SHAPE);
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            } else if (oozieNode.getName().startsWith("halt-job")) {
                dot.append(oozieNode.getName().replace('-', '_') +SHAPE);
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            } else if (oozieNode.getName().startsWith("recovery-test")) {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=diamond,penwidth=1];");
                RecoveryDecisionNode recoveryDecisionNode = (RecoveryDecisionNode) oozieNode;
                for (OozieNode previousHaltNode : recoveryDecisionNode.getPreviousHaltNodes()) {
                    dotLine = recoveryDecisionNode.getName().replace('-', '_') + "->" + previousHaltNode.getToNode().getName().replace('-', '_') + "[style=dashed]";
                    dot.append(dotLine + ";\n");
                }
                LOGGER.debug(dotLine);
            } else {
                dot.append(oozieNode.getName().replace('-', '_') + " [shape=rectangle];");
                dotLine = oozieNode.getName().replace('-', '_') + "->" + oozieNode.getToNode().getName().replace('-', '_');
                LOGGER.debug(dotLine);
                dot.append(dotLine + ";\n");
            }

        }
        dot.append("\n}");
        return dot;
    }

    public static StringBuilder getDashboardDot(Collection<OozieNode> oozieNodes, List<ProcessInfo> processInfoList) throws NullPointerException {

        StringBuilder dot = new StringBuilder("digraph{\n" +
                "edge[penwidth=.25,color=gray86,arrowsize=.50,arrowType=vee];" +
                "ratio=auto;" +
                "ranksep=.25;" +
                "nodesep=.25;" +
                "splines=ortho;" +
                "concentrate=true;" +
                "node[fontsize=7,fontname=\"verdana\"];" +
                "labelloc=\"t\";" +
                "label=<<FONT\n" +
                "  COLOR=\"black\"\n" +
                "  FACE=\"verdana\"\n" +
                "  POINT-SIZE=\"7\"\n" +
                ">Workflow</FONT>>;" +
                "\n");


        for (ProcessInfo processInfo : processInfoList) {
            StringBuilder label = new StringBuilder();
            label.append("<<TABLE ALIGN=\"LEFT\" CELLSPACING=\"4\" CELLPADDING=\"0\" BORDER=\"0\" WIDTH=\"100%\">" +
                    "<TR><TD>" + processInfo.getProcessName() + "</TD></TR>" +
                    "<TR><TD>");
            label.append(DotUtil.getExecStatus(processInfo).split("#")[1]);
            label.append("</TD></TR>" +
                    "</TABLE>>");
            String tooltip = "\"Process ID : " + processInfo.getProcessId() + " Description : " + processInfo.getDescription() + " Start Time : " + processInfo.getStartTs() + " End Time : " + processInfo.getEndTs() + "\"";
            String color = DotUtil.getExecStatus(processInfo).split("#")[0];
            if (processInfo.getParentProcessId() != 0) {
                dot.append(processInfo.getProcessId() + " [label =" + label + " ,tooltip=" + tooltip + " ,shape=rectangle,style=filled,style = rounded,color=" + color + "];\n");
            }

            if (processInfo.getParentProcessId() == 0) {
                String[] nextProcess = processInfo.getNextProcessIds().split(",");
                if (nextProcess.length > 1) {
                    dot.append(FORK + processInfo.getNextProcessIds().replace(',', '_') + " [shape=rnastab,label =\" \",style=\"rounded\",penwidth=.25,height = 0.1,width =0.1,color=gray86];\n");
                    dot.append("start" + "->" + FORK + processInfo.getNextProcessIds().replace(',', '_') + " ;\n");
                    for (String next : nextProcess) {

                        if (dot.toString().contains(FORK + processInfo.getNextProcessIds().replace(',', '_') + " ->" + next + " ;\n")) {
                            continue;
                        } else {
                            dot.append(FORK + processInfo.getNextProcessIds().replace(',', '_') + " ->" + next + " ;\n");
                        }
                    }
                } else {
                    dot.append("start" + "->" + processInfo.getNextProcessIds().toString() + " ;\n");
                }

            } else if (processInfo.getNextProcessIds().equals(processInfo.getParentProcessId().toString())) {
                dot.append(processInfo.getProcessId().toString() + "-> end ;\n");
            } else {
                String[] nextProcess = processInfo.getNextProcessIds().split(",");
                if (nextProcess.length > 1) {
                    dot.append(FORK + processInfo.getNextProcessIds().replace(',', '_') + " [shape=rnastab,label = \" \",style=\"rounded\",penwidth=.25,height = 0.1,width =0.1,color=gray86];\n");
                    dot.append(processInfo.getProcessId().toString() + "->" + FORK + processInfo.getNextProcessIds().replace(',', '_') + ";\n");
                    for (String next : nextProcess) {

                        if (dot.toString().contains(FORK + processInfo.getNextProcessIds().replace(',', '_') + " ->" + next + " ;\n")) {
                            continue;
                        } else {
                            dot.append(FORK + processInfo.getNextProcessIds().replace(',', '_') + " ->" + next + " ;\n");
                        }
                    }
                } else {
                    dot.append(processInfo.getProcessId().toString() + "->" + processInfo.getNextProcessIds().toString() + " ;\n");
                }
            }

        }
        dot.append("start" + " [shape=rectangle,style=\"rounded\"];\n");
        dot.append("end" + " [shape=rectangle,style=\"rounded\"];\n");
        dot.append("}");
        LOGGER.debug(dot);
        return dot;

    }

    private static String getExecStatus(ProcessInfo processInfo) {
        String color = "";
        String label = "";
        try {
            if (processInfo.getExecState() == null) {
                color = "black";
                label = "(Not Started)";
            } else if (processInfo.getExecState() == 2) {
                color = "gold";
                label = "(Running)";
            } else if (processInfo.getExecState() == 6) {
                color = "red";
                label = "(Failed)";
            } else if (processInfo.getExecState() == 3) {
                color = "green";
                label = "(Completed)";
            } else {
                color = "white";
            }
        } catch (NullPointerException e) {
            LOGGER.info(e);
            color = "black";
            label = "(Not Started)";
        }
        return color + "#" + label;
    }
}
