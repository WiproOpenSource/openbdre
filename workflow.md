What is a workflow ?

A workflow describes the sequential steps that comprise a work process in the business environment. In its most comprehensive form, workflow includes the procedures, people and tools involved in each step of a business process. Workflow may either be sequential, with each step contingent upon completion of the previous one, or parallel, with multiple steps occurring simultaneously.

Workflow for our metadata

We use Oozie scheduler for the workflow of our metadata. An Oozie workflow is a collection of actions (e.g. Hive queries) arranged in a control dependency DAG (Directed Acyclic Graph). A "control dependency" from one action to another means that the second action cannot run until the first action has completed. Jobs are represented by processes and sub-steps are represented by sub-processes in our metadata.

image
https://gitlab.com/bdre/documentation/uploads/7a091b62b67849fe0fb30b84b7261ded/image.png

The workflow starts with process 1. Process 1 is the parent process with its corresponding entry in the process table. It comprises of the sub processes 2,3,4,5 and 6 which are also present in the process table.
The entire workflow goes like : Parent process begins, Sub-processes begin, Sub-processes end, Parent process ends. It is managed by what are known as workflow nodes.
To build the above workflow, these processes have to be valid entries in the process table. For example, following are queries to insert the parent process 1 and sub process 2 into the process table:
    insert into process values (1, 'Parent process', current_timestamp, 'Semantic', 1,2, null ,1, 0,null, '2',0,2);

    insert into process values (2, 'Sub process-1', current_timestamp, 'Semantic', 1,9, 1, 1, 8,null, '3,4,5',0,0);

Similarly, the other sub-processes and their respective enqueuing processes have to be valid entries in the process table.
This marks the population of the process table and we have valid process entries which constitutes the workflow.
In Oozie , processes function by different types of nodes.
Parent process or the main job begins with a InitJobNode and ends with an HaltJobNode. On failure, the job is failed by the TermJobNode.
Sub processes or the sub steps begin with a InitStepNode, perform some activities as part of the ActionNode and finally end with the HaltStepNode. In case of failure, the step is failed by the TermStepNode.

In general, workflow nodes are classified as control flow nodes and action nodes:
Control flow nodes:

nodes that control the start and end of the workflow and workflow job execution path.
Action nodes:

nodes that trigger the execution of a computation/processing task.

Also,
each node is linked to one or more nodes by:

(i) ToNode : Which points to the next node on success of the current node
(ii) FailNode :Which points to the next node on failure of the current node
The nodes are described as:
ActionNode : Action nodes are the mechanism by which a workflow triggers the execution of a computation/processing task.
StartNode : The start node is the entry point for a workflow job, it indicates the first workflow node the workflow job must transition to.When a workflow starts, it automatically transitions to the node specified in the start.
EndNode : The end node is the end for a workflow job, it indicates that the workflow job has completed successfully. When a workflow job reaches the end it finishes successfully (SUCCEEDED).
KillNode : The kill node allows a workflow job to terminate. When a workflow job reaches the kill it finishes in error (KILLED). If one or more actions started by the workflow job are executing when the kill node is reached, the actions will be killed.
InitJobNode : The actual execution of job starts from this node. This is the node from which the process begins and the start node points to this node. The InitJobNode must run successfully before the beginning of any sub-step.
HaltJobNode : The HaltJobNode on success notifies the metadata to mark the completion of a job in the process table and takes the workflow.
TermJobNode : The TermJobNode on success notifies the metadata to mark the failure of a process in the process table.
TermStepNode : The TermStepNode notifies the metadata to mark the failure of a sub-process in the process table.
InitStepNode : The InitStepNode notifies the metadata to mark the beginning of a sub process in the process table.
EndStepNode : The HaltStepNode notifies the metadata to mark the completion of a sub process in the process table.

The default settings for a node are:
image
https://gitlab.com/bdre/documentation/uploads/46811c76fb53634c0654edacb9706c48/image.png

image
https://gitlab.com/bdre/documentation/uploads/deab4781a3a1a763d1e808a0a3db7e3a/image.png

where IS = InitStepNode, AN = ActionNode, TS = TermStepNode, TJ = TermJobNode, HS = HaltStepNode

Hence by default,
StartNode goes to the InitJobNode and InitJobNode on failure goes to the KillNode
HaltJobNode on success goes to the EndNode and on failure goes to the TermJobNode
For a particular sub-process
    InitStepNode on success goes to the ActionNode
    ActionNode on success goes to the HaltStepNode
    InitStepNode on failure goes to the TermJobNode
    ActionNode on failure goes to the TermStepNode
    HaltStepNode on failure goes to the TermStepNode
    TermStepNode on success/ failure goes to TermJobNode
    TermJobNode on success/ failure goes to KillNode

While building the workflow , the individual nodes with the above default settings have to be linked to establish a relationship among the nodes.
image
https://gitlab.com/bdre/documentation/uploads/52423bf9744fc3199fe7f18987511a4d/image.png

Hence, HaltStepNode for a sub-process goes to the InitStepNode for the next sub-process. This works in a sequential workflow where each step of scheduled work is dependent on the preceding step. But in a parallel workflow, where a process could have multiple Parent nodes/ Children nodes, the default node relationships have to be modified.
It is taken care of by the FORK node and the JOIN node.

Fork Control Node

A fork node is used to handle multiple children processes for a particular process. A fork node splits one path of execution into multiple concurrent paths of execution.
image
https://gitlab.com/bdre/documentation/uploads/4f504c41d9b8bbbe46763fd4e7c331d7/image.png

After a fork node is inserted,
    HaltStepNode of the parent node goes to the ForkNode,
    ForkNode goes to InitStepNode of Child1
    ForkNode goes to InitStepNode of Child2

A join node waits until every concurrent execution path of a previous fork node arrives to it.
image
https://gitlab.com/bdre/documentation/uploads/3fae60dc89af819f4a07aeea7eb83a03/image.png

When there are multiple parent nodes, either none or both the steps would be ended.
So, JoinNode on success goes to HaltStepNode for parent 1, and also to HaltStepNode for parent 2. When both steps end successfully, they go to the InitStepNode of the next sub-process.

Node Collection

This class creates objects of all the node types. The constructor of this class is used to set the default node settings as shown in the graphics above. ParentCollection and ChildCollection lists are populated by traversing every node. Multiple parents/ children are taken care of by Fork / Join nodes. Getter/setter methods are used to fetch individual nodes and set their ToNode and FailNode. This completes forming relationships among the various nodes as per the workflow.

Workflow Generator

The parent process is which is the process at which the workflow has to begin is passed as a command line argument. The filename in which workflow is to be written to is also passed as argument. The process id is used to fetch all the sub-processes from the metadata and form the processInfos list which gets used by the NodeCollection class to initialize nodes for various sub-processes. It also is used by WorkflowPrinter to print the workflow.
Ultimately, two files are generated, an XML file that contains the entire workflow in XML format and a dot file which is a graphical representation of the workflow.

WORKFLOW GENERATION SETUP

Use md.sql to load process data into the process table
Run WorkflowGenerator.java with the following arguments:

-p 401 –f workflow –env env1
env1 : Environment variable that has details to set up connection with the database.
    Use the corresponding environment variable in mybatis-config.xml. workflow.xml is the filename containing the workflow
    hadoop fs –put metadata_mgmt\workflow-generator\workflow.xml
    Edit job.properties file with the above path-name of workflow in hdfs
    Along with workflow.xml, a dot file is also generated which is converted to pdf format by using Graphviz
    Graphviz installation link:
    http://www.graphviz.org/Download..php