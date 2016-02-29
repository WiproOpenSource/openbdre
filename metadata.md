Bigdata Ready Enterprise
This document contains information about BDRE's metadata management.

Metadata framework salient features:

DATA INTEGRITY:

Data integrity refers to maintaining and assuring the accuracy and consistency of data over its entire life-cycle, and is a critical to any enterprise environment or data warehouse.
Data integrity in our Metadata Framework is imposed at its design stage through the use of standard rules and is maintained through the use of error checking and validation routines at the necessary steps, listed below :
Attempting to simultaneously run a job more than once by accident could compromise with the Data Integrity. So, before running a job, our metadata framework makes sure that the job is not already running. Else, error is thrown. This ensures that there are no multiple running instances of a particular job at any point of time.
A job is composed of several steps which contribute to its successful completion. These steps come into picture only after the main job starts running. The metadata framework validates that the main job is successfully running before running any of the sub- steps. This ensures that no step can run unless the main job which comprises those steps is already running.



BDRE Metadata Tables:

PROCESS

This table will act as a registry for all processes (jobs) that will run on the cluster. The processes could be a file to table process, semantic process or an export process. Each process will be registered using designated procedure API and given a unique process identifier.

INSTANCE_EXEC

Every time a process runs it’s assigned a new auto-generated process id, which gets inserted in a row with other information in INSTANCE_EXEC table. This run id is used as an ETL partition in Hive tables where the data is being loaded.

BATCH_CONSUMP_QUEUE

Every time a process runs it consumes batches through its sub-processes. Those sub-processes consume batches enqueued by its enqueuing process(es). A sub-process can have one, two or more enqueuing processes but majority of them will generally have only one enqueuing process. This means that a sub process can consume delta batches from different tables, which are independently populated with different batches (usually by different file to table interfaces). A row gets added in this table for an eligible sub-process by its enqueuing process(a parent process always) when the enqueuing process completes its execution successfully. If multiple processes (sub) have same enqueuing process then for each process a row gets added with same batch id but with different process ids. The target batch id initially remains empty and gets populated with an auto generated batch id when the process given by the process_id accesses the corresponding batch. When all batches for the parent process have been processed successfully then the enqueued batches are moved to the ARCHIVE_CONSUMP_QUEUE table.

ARCHIVE_CONSUMP_QUEUE

A replica of BATCH_CONSUMP_QUEUE. When batches are successfully consumed and parent process exits successfully, the records get moved to this table.

BATCH

This table is responsible for generating a batch id when a new batch has to be created.Each process when executes creates a batch after its completion.This batch id is used to link between the files for upstream and downstream processes by registering the batch Id in batch consump queue table.

FILE

If a process is a file to table interface then the file table would be queried for file location.The file path and server Id is registered in this table with other file details once the upstream process is successfully completed so the downstream process can access the file by querying the file table using the batch Id.

HIVE TABLES

This table registers the DDLs required to create the tables and views in the Hive for the Data Load operations. An auto increment table Id is generated which can be referred to link these DDLs to a Data Load process.

PROPERTIES

This table registers the properties as key-value pair with a configuration group against a sub-process required for the execution.The processes like data-generation,semantic utilizes this table to define the parameters required for their execution.

ETLDRIVER

This table is used by Data Load Process to link the DDLs with the process by registering the table Ids generated in the HIVE_TABLES.

SERVERS

The servers table registers the server details like name,login user,password,ssh private key, IP address,etc, against an auto incremented server Id which can be referred by the process to make an entry in file table.

BUS_DOMAIN

Multiple process in a given subject area are grouped into a given business domain. Example – Security application can contain different security related processes.

PROCESS_LOG

Specific logs are registered in the process log table with respective process id and instance exec Id as instance_ref after its execution. For example, logs of Data Quality process and Import process. This log can be referenced later for further analysis.

PROCESS_TEMPLATE

A table which contains a standard template of the form process, from which more such processes can be created or existing such processes can be edited.

PROPERTIES_TEMPLATE

A table which contains properties for any associated process template.

LINEAGE_QUERY

This table contains information about the hive/pig query for which column level lineage is to be shown.

LINEAGE_NODE

This table contains information about different nodes involved in a query(table, column, function etc).

LINEAGE_RELATION

This table contains information about relations between different columns involved in a query.

BATCH_STATUS

This table is a master table which defines the status of the batches generated by the processes. Depending on the state of the batch the batch can be moved from batch_consump_queue table to archive_consump_queue table.

EXEC_STATUS

This is another master table which defines the execution status of the processes.The execution state : 1, 2, 3, 4, 5 or 6 represents not running, running, success,paused, suspended or failed. Depending on these status the Init job,Init step,Halt job,Halt step,Term job and Term step can be initiated.

PROCESS_TYPE

This is also a master table which defines the process type. It can be semantic,hive data generation,etc.Each type is assigned a process type Id. These ids are referred in workflow generation module to generate the workflow xml file for oozie and the dot file for workflow visualization.

WORKFLOW_TYPE

Another master table which defines the workflow type and assigns a workflow type Id to each type. This has 3 entries : steps,stand alone and oozie.

LINEAGE_QUERY_TYPE

This table registers the lineage query type which would referenced by different tables of the Lineage module.The query type can be pig,hive etc.

LINEAGE_NODE_TYPE

This table is used to define the lineage node type which can be a table,column,function etc. This would be used by lineage module to generate the lineage graphic.

USERS

This table registers the users with their passwords for authentication and performing actions.

USER_ROLES

This table defines the user role for the different users registered in the USERS table.


Metadata DB APIs

There are quite a lot of APIs that are already developed and working in BDRE. For above use case following important metadata APIs are being explained in more detail as they would be important, as they will be called by the workflows to update job status. The Oozie workflows generated by BDRE(explained below with more details)

InitJob API

Mark start of a workflow/process
The InitJob API returns a selection of rows, which have the data from the PROCESS_BATCH_QUEUE table and other data like last_recoverable_sp_id, instance_exec_id. - This selection of rows is parsed in the corresponding Java API to obtain the following outputs.
Minimum and maximum batch ids enqueued by upstreams for each sub process.
Minimum and maximum batch dates for each sub process.
Optional: Target batch marking, which will be used as an input parameter for HaltJob procedure to set as the batch marking of all the downstream processes having this parent process as an enqueuing process. A target batch marking is a collection of dates of all the batches involved in the present process.
Process execution id corresponding to the present execution of the process.
Target batch id. This is the batch resulting from the successful execution of the present process to be enqueued to its downstreams.
Last recoverable sub process id. In case of a previously failed instance of this parent process (due to failure of one of the sub processes), the InitJob starts its execution from the last recoverable sub process id, instead of running the successfully completed sub processes again.

HaltJob API

Marks completion of a workflow/process .
Mark batch_state to PROCESSED in PROCESS_BATCH_QUEUE.
Mark run_state as complete in INSTANCE_EXEC and also populate end timestamp.
Enqueue one row for all processes that have this process as enqueueing process. Also mark them per IN_BATCH_MARKING. Use the target_batch_id from PROCESS_BATCH_QUEUE as the source_batch_id and target batch marking from the InitJob procedure as the batch marking for these new enqueued batches.
Move all processed records from BATCH_CONSUMP_QUEUE to ARCHIVE_CONSUMP_QUEUE queue.

TermJob API

Records failure of a parent process
Mark the row in the INSTANCE_EXEC for this sub-process as FAILED
Update the BATCH_CONSUMP_QUEUE table for this process id and set batch status to a value corresponding to FAILURE. These batches are NOT moved to the ARCHIVE_CONSUMP_QUEUE.

InitStep API

Marks start of a sub-process/steps/routine
Add a row in the INSTANCE_EXEC for this sub-process
Update the BATCH_CONSUMP_QUEUE table for this process id and set start_ts with current system TS.

HaltStep API

Marks end of a sub-process/steps/routine
Mark the row in the INSTANCE_EXEC for this sub-process as COMPLETED.
Mark with end_ts (as system TS) in BATCH_CONSUMP_QUEUE.

TermStep API

Records failure of a sub-process/steps/routine
Mark the row in the INSTANCE_EXEC for this sub-process as FAILED.

GetFiles API

Fetches the records of files from join of file table and servers table between the min batch and max batch id passed through parameters.

GetProperties API

Fetches the records from properties table related to the process Id and config group passed as parameters.

GetETLDriver API

Fetches the records of files from file table between the min batch and max batch id passed through parameters.
Returns a string of file details separated by semicolon.

RegisterFile

Records registration of a new file and the associated file details.
Add a row in the File table with the file details as provided through the parameters.

AddProcessLog

Enables adding a new process log to the process log table.
On calling the procedure, the inputs get added as a new row entry in the table along with an auto incremented log_id.

BatchEnqueuer

Enables adding batch in batch table and then a file entry in file table and finally, an entry in batch consump queue.
Add a row in the Batch table,File table and BatchConsumpQueue table, with the details as provided through the parameters.

BatchCheck

Checks the presence of the required batches for initiation of the Process which is passed as parameter.This proc is used in the Init Job.