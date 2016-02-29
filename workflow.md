What is a workflow ?

A workflow describes the sequential steps that comprise a work process in the business environment. In its most comprehensive form, workflow includes the procedures, people and tools involved in each step of a business process. Workflow may either be sequential, with each step contingent upon completion of the previous one, or parallel, with multiple steps occurring simultaneously.

Workflow for our metadata

We use Oozie scheduler for the workflow of our metadata. An Oozie workflow is a collection of actions (e.g. Hive queries) arranged in a control dependency DAG (Directed Acyclic Graph). A "control dependency" from one action to another means that the second action cannot run until the first action has completed. Jobs are represented by processes and sub-steps are represented by sub-processes in our metadata.

