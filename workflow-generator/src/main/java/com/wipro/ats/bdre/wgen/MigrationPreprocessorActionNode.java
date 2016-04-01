package com.wipro.ats.bdre.wgen;

import com.wipro.ats.bdre.md.beans.ProcessInfo;

/**
 * Created by cloudera on 3/31/16.
 */
    /*
Action nodes are the mechanism by which a workflow triggers the execution of a task
Here, we set the id and return name of the action node.
The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
for the current action node, appropriately formatted as XML.
*/

    public class MigrationPreprocessorActionNode extends GenericActionNode {


        private ProcessInfo processInfo = new ProcessInfo();
        private ActionNode actionNode = null;

        /**
         * This constructor is used to set node id and process information.
         *
         * @param actionNode An instance of ActionNode class.
         */
        public MigrationPreprocessorActionNode(ActionNode actionNode) {
            setId(actionNode.getId());
            processInfo = actionNode.getProcessInfo();
            this.actionNode = actionNode;
        }

        public ProcessInfo getProcessInfo() {
            return processInfo;
        }


        public String getName() {

            String nodeName = "prepare-migrate-" + getId() + "-" + processInfo.getProcessName().replace(' ', '_');
            return nodeName.substring(0, Math.min(nodeName.length(), 45));

        }

        @Override
        public String getXML() {
            if (this.getProcessInfo().getParentProcessId() == 0) {
                return "";
            }
            StringBuilder ret = new StringBuilder();
            ret.append("\n<action name=\"" + getName() + "\">\n" +
                    "<java>\n" +
                    "            <job-tracker>${jobTracker}</job-tracker>\n" +
                    "            <name-node>${nameNode}</name-node>\n" +
                    "            <main-class>com.wipro.ats.bdre.clustermigration.oozie.OozieMigrationPreprocessor</main-class>\n" +
                    "            <arg>--process-id</arg>\n" +
                    "            <arg>" + getId() + "</arg>\n" +
                    "            <arg>--instance-exec-id</arg>\n" +
                    "            <arg>${wf:actionData(\"init-job\")[\"instance-exec-id\"]}</arg>\n" +
                    "            <capture-output/>\n" +
                    "        </java>\n" +
                    "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                    "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                    "    </action>");

            return ret.toString();
        }
}
