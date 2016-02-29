Bigdata Ready Enterprise
This document contains information about BDRE's metadata management.

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