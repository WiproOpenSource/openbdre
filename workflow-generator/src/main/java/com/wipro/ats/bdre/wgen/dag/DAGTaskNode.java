package com.wipro.ats.bdre.wgen.dag;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.beans.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SU324335 on 7/1/16.
 */
public class DAGTaskNode extends DAGNode {
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

    //Cluster to Cluster Hive Table Migration
    public static final int HIVE_MIGRATION_ACTION=31;
    public static final int MIGRATION_PREPROCESSOR_ACTION=32;
    public static final int SOURCE_STAGE_LOAD_ACTION=33;
    public static final int SOURCE_TO_DEST_COPY_ACTION=34;
    public static final int DEST_TABLE_LOAD_ACTION=35;
    public static final int REGISTER_PARTITIONS_ACTION=36;


    public static final int SUPER_WF_ACTION=39;
    public static final int SUB_WF_ACTION=40;

    private ProcessInfo processInfo = new ProcessInfo();
    private List<GenericActionNode> containingNodes = new ArrayList<GenericActionNode>();

    /**
     * This constructor sets id for Action Node
     *
     * @param id node-id to set for Action Node.
     */
    public DAGTaskNode(Integer id) {
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

    //TODO
    public void setProcessInfo(ProcessInfo processInfo) {
        this.processInfo = processInfo;
        if (processInfo.getProcessTypeId() == SPARK_ACTION) {
            DAGSparkTaskNode sparkActionNode = new DAGSparkTaskNode(this);
            containingNodes.add(sparkActionNode);
        }
        else if (processInfo.getProcessTypeId() == SHELL_ACTION) {
            DAGShellTaskNode shellActionNode = new DAGShellTaskNode (this);
            containingNodes.add(shellActionNode);
        }
        else if (processInfo.getProcessTypeId() == HADOOP_STREAMING_ACTION) {
            DAGHadoopStreamingTaskNode hadoopStreamingTaskNode = new DAGHadoopStreamingTaskNode(this);
            containingNodes.add(hadoopStreamingTaskNode);
        }
        else if (processInfo.getProcessTypeId() == HIVE_ACTION) {
            DAGHiveTaskNode hiveActionNode = new DAGHiveTaskNode(this);
            containingNodes.add(hiveActionNode);
        }
        else if (processInfo.getProcessTypeId() == R_ACTION) {
            DAGRTaskNode rActionNode = new DAGRTaskNode(this);
            containingNodes.add(rActionNode);
        }
        else if (processInfo.getProcessTypeId() == PIG_ACTION) {
            DAGPigTaskNode pigActionNode = new DAGPigTaskNode(this);
            containingNodes.add(pigActionNode);

        }
        else if (processInfo.getProcessTypeId() == DATA_IMPORT_ACTION) {
            DAGImportTaskNode importActionNode = new DAGImportTaskNode(this);
            DAGFileRegistrationTaskNode fileRegistrationNode = new DAGFileRegistrationTaskNode(this);
            importActionNode.setToNode(fileRegistrationNode);
            containingNodes.add(importActionNode);
            containingNodes.add(fileRegistrationNode);
        }
        else if (processInfo.getProcessTypeId() == IMPORT_ACTION) {
        }
        else if (processInfo.getProcessTypeId() == HIVE_GEN_PARENT_ACTION) {
        }
        else if (processInfo.getProcessTypeId() == HIVE_GEN_ACTION) {
            DAGDataGenerationTaskNode dataGenerationNode = new DAGDataGenerationTaskNode(this);
            DAGFileRegistrationTaskNode fileRegistrationNode = new DAGFileRegistrationTaskNode(this);
            dataGenerationNode.setToNode(fileRegistrationNode);
            containingNodes.add(dataGenerationNode);
            containingNodes.add(fileRegistrationNode);
        }
        else if (processInfo.getProcessTypeId() == ETL_ACTION) {
        }
        else if (processInfo.getProcessTypeId() == BASE_LOAD_ACTION) {
            DAGBaseLoadTaskNode baseLoadActionNode = new DAGBaseLoadTaskNode(this);
            containingNodes.add(baseLoadActionNode);
        }
        else if (processInfo.getProcessTypeId() == RAW_LOAD_ACTION) {
            DAGRawLoadTaskNode rawLoadActionNode = new DAGRawLoadTaskNode(this);
            containingNodes.add(rawLoadActionNode);
        }
        else if (processInfo.getProcessTypeId() == STAGE_LOAD_ACTION) {
            DAGStageLoadTaskNode stageLoadActionNode = new DAGStageLoadTaskNode(this);
            containingNodes.add(stageLoadActionNode);
        }
        else if (processInfo.getProcessTypeId() == DQ_PARENT_ACTION) {
        }
        else if (processInfo.getProcessTypeId() == DQ_ACTION) {
            DAGLOFTaskNode lofActionNode = new DAGLOFTaskNode(this);
            DAGDataQualityTaskNode dataQualityActionNode = new DAGDataQualityTaskNode(this);
            DAGFileRegistrationTaskNode fileRegistrationNode = new DAGFileRegistrationTaskNode(this);
            lofActionNode.setToNode(dataQualityActionNode);
            dataQualityActionNode.setToNode(fileRegistrationNode);
            containingNodes.add(lofActionNode);
            containingNodes.add(dataQualityActionNode);
            containingNodes.add(fileRegistrationNode);
        }

       /*else if (processInfo.getProcessTypeId() == HIVE_ACTION) {
            HiveActionNode hiveActionNode = new HiveActionNode(this);
            containingNodes.add(hiveActionNode);
        } else if (processInfo.getProcessTypeId() == DATA_EXPORT_ACTION) {
            ExportActionNode exportActionNode = new ExportActionNode(this);
            containingNodes.add(exportActionNode);
        }  else if (processInfo.getProcessTypeId() == HADOOP_STREAMING_ACTION) {
            HadoopStreamingActionNode hadoopStreamingActionNode= new HadoopStreamingActionNode(this);
            containingNodes.add( hadoopStreamingActionNode);
        }else if (processInfo.getProcessTypeId() == MAPREDUCE_ACTION) {
            MRActionNode mrActionNode = new MRActionNode(this);
            containingNodes.add(mrActionNode);
        } else if (processInfo.getProcessTypeId() == FILE_REG_ACTION) {
            FileRegistrationNode frActionNode = new FileRegistrationNode(this);
            containingNodes.add(frActionNode);
        }else if (processInfo.getProcessTypeId() == SUB_WF_ACTION) {
            SubWorkflowActionNode subWorkflowActionNode = new SubWorkflowActionNode(this);
            containingNodes.add(subWorkflowActionNode);
        }  else if (processInfo.getProcessTypeId() == HIVE_GEN_ACTION) {
            DataGenerationNode dataGenerationNode = new DataGenerationNode(this);
            FileRegistrationNode fileRegistrationNode = new FileRegistrationNode(this);
            dataGenerationNode.setToNode(fileRegistrationNode);
            containingNodes.add(dataGenerationNode);
            containingNodes.add(fileRegistrationNode);
        }
            */
        else if (processInfo.getProcessTypeId() == SEMANTIC_ACTION) {
        }
        /*else if (processInfo.getProcessTypeId() == INGESTION) {
        } else if (processInfo.getProcessTypeId() == EXPORT_ACTION) {
        }  else if (processInfo.getProcessTypeId() == DQ_PARENT_ACTION) {
        }else if (processInfo.getProcessTypeId() == HIVE_MIGRATION_ACTION) {
        } else if (processInfo.getProcessTypeId() == SUPER_WF_ACTION) {
        }else if (processInfo.getProcessTypeId() == SFTP) {
            SFTPNonOozieActionNode sftpNonOozieActionNode = new SFTPNonOozieActionNode(this);
            containingNodes.add(sftpNonOozieActionNode);
        }  else if (processInfo.getProcessTypeId() == SPARK_ACTION) {
            SparkActionNode sparkActionNode = new SparkActionNode(this);
            containingNodes.add(sparkActionNode);
        } else if (processInfo.getProcessTypeId() == CRAWLER_PARENT_ACTION) {
        } else if (processInfo.getProcessTypeId() == CRAWLER_CHILD_ACTION) {
            CrawlerActionNode crawlerActionNode = new CrawlerActionNode(this);
            containingNodes.add(crawlerActionNode);
        } else if (processInfo.getProcessTypeId() == MIGRATION_PREPROCESSOR_ACTION) {
            MigrationPreprocessorActionNode migrationPreprocessorActionNodeNode = new MigrationPreprocessorActionNode(this);
            containingNodes.add(migrationPreprocessorActionNodeNode);
        } else if (processInfo.getProcessTypeId() == SOURCE_STAGE_LOAD_ACTION) {
            SourceStageLoadActionNode sourceStageLoadActionNode = new SourceStageLoadActionNode(this);
            containingNodes.add(sourceStageLoadActionNode);
        } else if (processInfo.getProcessTypeId() == SOURCE_TO_DEST_COPY_ACTION) {
            SourceToDestCopyActionNode sourceToDestCopyActionNode = new SourceToDestCopyActionNode(this);
            containingNodes.add(sourceToDestCopyActionNode);
        } else if (processInfo.getProcessTypeId() == DEST_TABLE_LOAD_ACTION) {
            DestTableLoadActionNode destTableLoadActionNode = new DestTableLoadActionNode(this);
            containingNodes.add(destTableLoadActionNode);
        } else if (processInfo.getProcessTypeId() == REGISTER_PARTITIONS_ACTION) {
            RegisterPartitionsActionNode registerPartitionsActionNode = new RegisterPartitionsActionNode(this);
            containingNodes.add(registerPartitionsActionNode);
        }
         */
        else {
            throw new BDREException("Don't know how to handle processInfo.getProcessTypeId()=" + processInfo.getProcessTypeId());
        }


    }

    @Override
    public void setTermNode(DAGNode termNode) {
        if (this.getProcessInfo().getParentProcessId() != 0) {
            /**all containing node failures should go to same termNode*/
            for (DAGNode containingNode : containingNodes) {
                containingNode.setTermNode(termNode);
            }
        }
        super.setTermNode(termNode);
    }

    @Override
    public void setToNode(DAGNode toNode) {
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
    public String getDAG() {
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
