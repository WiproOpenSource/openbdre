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
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

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
    public static final int HADOOP_STREAMING_ACTION = 30;

    //Cluster to Cluster Hive Table Migration
    public static final int HIVE_MIGRATION_ACTION = 31;
    public static final int MIGRATION_PREPROCESSOR_ACTION = 32;
    public static final int SOURCE_STAGE_LOAD_ACTION = 33;
    public static final int SOURCE_TO_DEST_COPY_ACTION = 34;
    public static final int DEST_TABLE_LOAD_ACTION = 35;
    public static final int REGISTER_PARTITIONS_ACTION = 36;

    private static final Logger LOGGER = Logger.getLogger(ActionNode.class);


    public static final int SUPER_WF_ACTION = 37;
    public static final int SUB_WF_ACTION = 38;


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

    public Set processTypeSet = new HashSet<Integer>();

    public void setProcessInfo(ProcessInfo processInfo) {
        this.processInfo = processInfo;
        setPluginProcessInfo(this.processInfo);
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

        }  else if (processInfo.getProcessTypeId() == DATA_EXPORT_ACTION) {
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
            HadoopStreamingActionNode hadoopStreamingActionNode = new HadoopStreamingActionNode(this);
            containingNodes.add(hadoopStreamingActionNode);

        } else if (processInfo.getProcessTypeId() == MAPREDUCE_ACTION) {
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


        } else if (processInfo.getProcessTypeId() == DQ_ACTION) {
            LOFActionNode lofActionNode = new LOFActionNode(this);
            DataQualityActionNode dataQualityActionNode = new DataQualityActionNode(this);
            FileRegistrationNode fileRegistrationNode = new FileRegistrationNode(this);
            lofActionNode.setToNode(dataQualityActionNode);
            dataQualityActionNode.setToNode(fileRegistrationNode);
            containingNodes.add(lofActionNode);
            containingNodes.add(dataQualityActionNode);
            containingNodes.add(fileRegistrationNode);
        }  else if (processInfo.getProcessTypeId() == SEMANTIC_ACTION) {

        } else if (processInfo.getProcessTypeId() == INGESTION) {

        } else if (processInfo.getProcessTypeId() == EXPORT_ACTION) {

        } else if (processInfo.getProcessTypeId() == IMPORT_ACTION) {

        } else if (processInfo.getProcessTypeId() == DQ_PARENT_ACTION) {

        }  else if (processInfo.getProcessTypeId() == HIVE_GEN_PARENT_ACTION) {

        } else if (processInfo.getProcessTypeId() == HIVE_MIGRATION_ACTION) {

        } else if (processInfo.getProcessTypeId() == SUPER_WF_ACTION) {

        } else if (processInfo.getProcessTypeId() == SFTP) {

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
        } else if (!processTypeSet.contains(processInfo.getProcessTypeId())) {
            throw new BDREException("Don't know how to handle processInfo.getProcessTypeId()=" + processInfo.getProcessTypeId());
        }

    }


    public void setPluginProcessInfo(ProcessInfo processInfo) {

        Set<String> pluginIdList = new HashSet();
        //TODO: fill plugin id list by iteration through plugin config and populate with each row's plugin id. Set puts only unique entries
        pluginIdList.add("td-action-1.0.0");

        for (String pluginId : pluginIdList) {
            if (processInfo.getParentProcessId() == null) {
                processTypeSet.add(processInfo.getProcessTypeId());
                return;
            }

            //TODO: for each plugin id check for a config group of "wf-gen" for same structure of plugin config for a  action node refer to:
            // TODO: Under each such "wf-gen" config group, read the value with key as 'parent-process-id' for a parent process id

            //TODO:   Read corresponding list of sub processes through key as parent-processid.sub-process-id and add to the set
            /*Set<Integer> subProcessSet = new HashSet<>();
            subProcessSet.add(processInfo.getProcessTypeId());
            TODO: add logic to populate set with all sub processes and proceed any further only if processInfo.processId belongs to the set*/
            int subProcessId = processInfo.getProcessTypeId();
            LOGGER.debug("Sub process id = " + subProcessId);
            processTypeSet.add(subProcessId);
            //TODO: iterate through plugin config with '${subProcessId}.wf-gen' as config group,get corresponding values which are jar paths and  adding all jars to classpath
            //TODO: add logic to skip adding jars if classes are already loaded, classes can be taken from containing nodes
            List<String> jarsToLoad = new ArrayList<>();
            jarsToLoad.add("/home/cloudera/workspace/openbdre/workflow-generator/target/workflow-generator-1.1-SNAPSHOT.jar");
            URL[] urls = new URL[10];
            for (String jar : jarsToLoad) {
                int index = 0;
                LOGGER.info("adding " + jar + " in classpath");
                try {
                    File file = new File(jar);
                    URL url = file.toURL();
                    urls[index] = url;
                    index++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new BDREException(ex);
                }
            }
            URLClassLoader pluginClassLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

            //TODO: iterate through plugin config for config group as "wf-cont-nodes", form list of nodes in correct order
            List<String> listOfNodeClasses = new LinkedList<>();   //keys as a list
            /*listOfNodeClasses.add("com.wipro.ats.bdre.wgen.LOFActionNode");
            listOfNodeClasses.add("com.wipro.ats.bdre.wgen.DataQualityActionNode");
            listOfNodeClasses.add("com.wipro.ats.bdre.wgen.FileRegistrationNode");*/
            /*listOfNodeClasses.add("com.wipro.ats.bdre.wgen.RawLoadActionNode");
            listOfNodeClasses.add("com.wipro.ats.bdre.wgen.StageLoadActionNode");
            listOfNodeClasses.add("com.wipro.ats.bdre.wgen.BaseLoadActionNode");*/
            /*listOfNodeClasses.add("com.wipro.ats.bdre.wgen.ImportActionNode");
            listOfNodeClasses.add("com.wipro.ats.bdre.wgen.FileRegistrationNode");*/
            List<Class> listOfClassesToLoad = new LinkedList<>();
            List<GenericActionNode> listOfNodeObjects = new LinkedList<>();
            //iterate through all nodes and instantiate them
            for (String nodeClass : listOfNodeClasses) {
                try {
                    LOGGER.debug("current class = " + nodeClass);
                    Class classToLoad = Class.forName(nodeClass, true, pluginClassLoader);
                    listOfClassesToLoad.add(classToLoad);
                    Constructor[] constructors = classToLoad.getDeclaredConstructors();
                    LOGGER.debug("constructor being evoked =" + constructors[0]);
                    Object nodeInstance = constructors[0].newInstance(this);
                    listOfNodeObjects.add((GenericActionNode) nodeInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BDREException(e);
                }
            }

            for (int i = 0; i < listOfNodeObjects.size(); i++) {
                try {
                    LOGGER.debug("object class= " + listOfNodeObjects.get(i).getClass());
                    //super class of ActionNode is GenericActionNode and GenericActionNode's super class is OozieNode which contains the setToNode method. Hence invoking the same
                    Method setToNodeMethod = listOfClassesToLoad.get(i).getDeclaredMethod("setToNode", new Class[]{OozieNode.class});
                    if (i < listOfNodeObjects.size() - 1)
                        setToNodeMethod.invoke(listOfNodeObjects.get(i), new Object[]{listOfNodeObjects.get(i + 1)});
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BDREException(e);
                }

            }
            containingNodes.clear();
            containingNodes.addAll(listOfNodeObjects);
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
