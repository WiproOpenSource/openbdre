Bigdata Ready Enterprise
Goal of BDRE is to give Bigdata implementation a significant acceleration by supplying the essential frameworks which are most likely to be written anyway. It'll drastically eliminate hundreds of man hours of effort in operational framework development.

BDRE currently implemented:
Operational Metadata Management
Registry of all workflow processes/templates
Parameters/configuration(key/val) for processes
Dependency information (upstream/downstream)
Batch management/tracking. Batch concept in BDRE is for tracking the data flow between workflow processes.
Run control (for delta processing/dependency check)
Execution status for jobs(dynamic metadata - with step level granularity)
File registry - can be used to register e.g. ingested files or a raw file as an output of an upstream.
Execution statistics logging (key/value)
Executed hive queries and data lineage information.
Java APIs that ingrates with Bigdata with non-bigdata applications alike.
Job monitoring and proactive/reactive alerting