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
