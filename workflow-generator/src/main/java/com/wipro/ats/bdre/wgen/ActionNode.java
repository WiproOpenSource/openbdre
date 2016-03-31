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

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 12/21/14
 */


/**
 * Action nodes are the mechanism by which a workflow triggers the execution of a task
 * Here, we set the id and return name of the action node.
 * The method getXML() returns a string which contains name, Id, next success node(ToNode) and next failure node(TermNode)
 * for the current action node, appropriately formatted as XML.
 */

public class ActionNode extends OozieNode {
    public static final int INGESTION = 1;
    public static final int SEMANTIC_ACTION = 2;
    public static final int EXPORT_ACTION = 3;
    public static final int IMPORT_ACTION = 4;
    public static final int ETL_ACTION = 5;
    public static final int RAW_LOAD_ACTION = 6;
    public static final int STAGE_LOAD_ACTION = 7;
    public static final int BASE_LOAD_ACTION = 8;
    public static final int HIVE_ACTION = 9;
    public static final int PIG_ACTION = 10;
    public static final int MAPREDUCE_ACTION = 11;
    public static final int SFTP = 12;
    public static final int DATA_IMPORT_ACTION = 13;
    public static final int HIVE_GEN_ACTION = 14;
    public static final int FILE_REG_ACTION = 15;
    public static final int DQ_ACTION = 16;
    public static final int DATA_EXPORT_ACTION = 17;
    public static final int HIVE_GEN_PARENT_ACTION = 18;
    public static final int DQ_PARENT_ACTION = 19;

    //Adding Shell Action
    public static final int SHELL_ACTION = 22;

    public static final int R_ACTION = 24;
    public static final int SPARK_ACTION = 25;

    //Adding crawler Action
    public static final int CRAWLER_PARENT_ACTION = 28;
    public static final int CRAWLER_CHILD_ACTION = 29;

    //Hadoop streaming action
    public static final int HADOOP_STREAMING_ACTION=30;

    private ProcessInfo processInfo = new ProcessInfo();
    private List<GenericActionNode> containingNodes = new ArrayList<GenericActionNode>();

    /**
     * This constructor sets id for Action Node
     *
     * @param id node-id to set for Action Node.
     */
    public ActionNode(Integer id) {
        setId(id);

    }

    public List<GenericActionNode> getContainingNodes() {
        return containingNodes;
    }

    public void setContainingNodes(List<GenericActionNode> containingNodes) {
        this.containingNodes = containingNodes;
    }

    public ProcessInfo getProcessInfo() {
        return processInfo;
    }

    public void setProcessInfo(ProcessInfo processInfo) {
        this.processInfo = processInfo;
        if (processInfo.getProcessTypeId() == RAW_LOAD_ACTION) {
            RawLoadActionNode rawLoadActionNode = new RawLoadActionNode(this);
            containingNodes.add(rawLoadActionNode);

        } else if (processInfo.getProcessTypeId() == HIVE_ACTION) {
            HiveActionNode hiveActionNode = new HiveActionNode(this);
            containingNodes.add(hiveActionNode);

        } else if (processInfo.getProcessTypeId() == DATA_IMPORT_ACTION) {
            ImportActionNode importActionNode = new ImportActionNode(this);
            FileRegistrationNode fileRegistrationNode = new FileRegistrationNode(this);
            importActionNode.setToNode(fileRegistrationNode);
            containingNodes.add(importActionNode);
            containingNodes.add(fileRegistrationNode);

        } else if (processInfo.getProcessTypeId() == DATA_EXPORT_ACTION) {
            ExportActionNode exportActionNode = new ExportActionNode(this);
            containingNodes.add(exportActionNode);

        } else if (processInfo.getProcessTypeId() == STAGE_LOAD_ACTION) {
            StageLoadActionNode stageLoadActionNode = new StageLoadActionNode(this);
            containingNodes.add(stageLoadActionNode);

        } else if (processInfo.getProcessTypeId() == BASE_LOAD_ACTION) {
            BaseLoadActionNode baseLoadActionNode = new BaseLoadActionNode(this);
            containingNodes.add(baseLoadActionNode);
        } else if (processInfo.getProcessTypeId() == ETL_ACTION) {

        } else if (processInfo.getProcessTypeId() == PIG_ACTION) {
            PigActionNode pigActionNode = new PigActionNode(this);
            containingNodes.add(pigActionNode);

        } else if (processInfo.getProcessTypeId() == HADOOP_STREAMING_ACTION) {
            HadoopStreamingActionNode hadoopStreamingActionNode= new HadoopStreamingActionNode(this);
            containingNodes.add( hadoopStreamingActionNode);

        }else if (processInfo.getProcessTypeId() == MAPREDUCE_ACTION) {
            MRActionNode mrActionNode = new MRActionNode(this);
            containingNodes.add(mrActionNode);
        } else if (processInfo.getProcessTypeId() == FILE_REG_ACTION) {
            FileRegistrationNode frActionNode = new FileRegistrationNode(this);
            containingNodes.add(frActionNode);

        } else if (processInfo.getProcessTypeId() == HIVE_GEN_ACTION) {
            DataGenerationNode dataGenerationNode = new DataGenerationNode(this);
            FileRegistrationNode fileRegistrationNode = new FileRegistrationNode(this);
            dataGenerationNode.setToNode(fileRegistrationNode);
            containingNodes.add(dataGenerationNode);
            containingNodes.add(fileRegistrationNode);


        } else if (processInfo.getProcessTypeId() == DQ_ACTION) {
            LOFActionNode lofActionNode = new LOFActionNode(this);
            DataQualityActionNode dataQualityActionNode = new DataQualityActionNode(this);
            FileRegistrationNode fileRegistrationNode = new FileRegistrationNode(this);
            lofActionNode.setToNode(dataQualityActionNode);
            dataQualityActionNode.setToNode(fileRegistrationNode);
            containingNodes.add(lofActionNode);
            containingNodes.add(dataQualityActionNode);
            containingNodes.add(fileRegistrationNode);
        } else if (processInfo.getProcessTypeId() == SEMANTIC_ACTION) {

        } else if (processInfo.getProcessTypeId() == INGESTION) {

        } else if (processInfo.getProcessTypeId() == EXPORT_ACTION) {

        } else if (processInfo.getProcessTypeId() == IMPORT_ACTION) {

        } else if (processInfo.getProcessTypeId() == DQ_PARENT_ACTION) {

        } else if (processInfo.getProcessTypeId() == HIVE_GEN_PARENT_ACTION) {

        }  else if (processInfo.getProcessTypeId() == SFTP) {

            SFTPNonOozieActionNode sftpNonOozieActionNode = new SFTPNonOozieActionNode(this);
            containingNodes.add(sftpNonOozieActionNode);

        } else if (processInfo.getProcessTypeId() == SHELL_ACTION) {
            ShellActionNode shellActionNode = new ShellActionNode(this);
            containingNodes.add(shellActionNode);
        } else if (processInfo.getProcessTypeId() == R_ACTION) {
            RActionNode rActionNode = new RActionNode(this);
            containingNodes.add(rActionNode);
        } else if (processInfo.getProcessTypeId() == SPARK_ACTION) {
            SparkActionNode sparkActionNode = new SparkActionNode(this);
            containingNodes.add(sparkActionNode);
        } else if (processInfo.getProcessTypeId() == CRAWLER_PARENT_ACTION) {

        } else if (processInfo.getProcessTypeId() == CRAWLER_CHILD_ACTION) {
            CrawlerActionNode crawlerActionNode = new CrawlerActionNode(this);
            containingNodes.add(crawlerActionNode);
        } else {
            throw new BDREException("Don't know how to handle processInfo.getProcessTypeId()=" + processInfo.getProcessTypeId());
        }

    }

    @Override
    public void setTermNode(OozieNode termNode) {
        if (this.getProcessInfo().getParentProcessId() != 0) {
            /**all containing node failures should go to same termNode*/
            for (OozieNode containingNode : containingNodes) {
                containingNode.setTermNode(termNode);
            }
        }
        super.setTermNode(termNode);
    }

    @Override
    public void setToNode(OozieNode toNode) {
        if (this.getProcessInfo().getParentProcessId() != 0) {
            containingNodes.get(containingNodes.size() - 1).setToNode(toNode);
        }
        super.setToNode(toNode);
    }

    public String getName() {
        if (this.getProcessInfo().getParentProcessId() != 0) {
            return containingNodes.get(0).getName();
        } else {
            return "action-" + getId();
        }
    }

    @Override
    public String getXML() {
        StringBuilder ret = new StringBuilder();
        ret.append("\n");
        ret.append("<!-- ");
        ret.append("Process type: ");
        ret.append(processInfo.getProcessTypeId());
        ret.append("\n");
        ret.append("Process name: ");
        ret.append(processInfo.getProcessName());
        ret.append("\n");
        ret.append("This sub-process is enqueued by Process id: ");
        ret.append(processInfo.getEnqProcessId());
        ret.append("\n");
        ret.append("Can recover after restart? : ");
        ret.append(processInfo.isCanRecover());
        ret.append("\n");
        ret.append(processInfo.getDescription());
        ret.append(" -->");


        return ret.toString();
    }
}
