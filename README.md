# Bigdata Ready Enterprise Open Source Software

## Table of Contents

[License](#license)
***
[Objective](#objective)
***
[Features](#features)
***
[Architecture](#architecture)
***
[Installation](#installation)
***
[Data Ingestion](#data-ingestion)
***
[Workflow Builder](#workflow-builder)
***
[Bulk Data Manufacturing](#bulk-data-manufacturing)
***
[Web Crawler](#web-crawler)
***
[Operational Metadata Management](#operational-metadata-management)
***
[How To Contribute](#how-to-contribute)
***

# License
Released under Apache Public License 2.0. You can get a copy of the license at http://www.apache.org/licenses/LICENSE-2.0.
# Objective
Big Data Ready Enterprise (BDRE)makes big data technology adoption simpler by optimizing and integrating various big data solutions and providing them under one integrated package. BDRE provides a uniﬁed framework for a Hadoop implementation that can drastically minimize development time and fast track the Hadoop implementation. It comprises a reusable framework that can be customized as per the enterprise ecosystem. The components are loosely integrated and can be de-coupled or replaced easily with alternatives.
 
The primary goal of BDRE is to accelerate Bigdata implementations by supplying the essential frameworks that are most likely to be written from scratch. It can drastically reduce effort by eliminating hundreds of man hours in operational framework development. Big Data implementations however, require specialized skills, signiﬁcant development effort on data loading, semantic processing, DQ, code deployment across environments etc.

# Features

- Operational Metadata Management
 - Registry of all workflow processes/templates
 - Parameters/configuration(key/value) for processes
 - Dependency information (upstream/downstream)
 - Batch management/tracking. Batch concept in BDRE is for tracking the data flow between workflow processes.
 - Run control (for delta processing/dependency check)
 - Execution status for jobs(dynamic metadata - with step level granularity)
 - File registry - can be used to register e.g. ingested files or a raw file as an output of an upstream.
 - Execution statistics logging (key/value)
 - Executed hive queries and data lineage information.
 - Java APIs that integrates with Big Data with non-Big Data applications alike.
 - Job monitoring and proactive/reactive alerting
- Data ingestion framework
 - Tabular data from RDBMS
 - Streaming data from 16 types of sources (including logs, message queues and Twitter)
 - Arbitrary file ingestion by directory monitoring
- Web Crawler
- Distributed Data Manufacturing framework
 - Generate billons of records based on patterns and ranges
- Semantic Layer Building Framework
 - Build the sematic layer using visual workflow creator using the data you ingested.
 - Supports Hive, Pig, MapReduce, Spark, R etc.
 - Generates Oozie workflows
- Data Quality Framework
 - Validates your data using your rules in a distributed way
 - Integrated with Drools rule engine
- HTML5 User Interface
 - Create ingestion, data generation, Crawler jobs or create Oozie workflows graphically without writing any code
 - One click deploy and execute jobs without SSH into the edge node.

# Architecture

![image](http://wiproopensourcepractice.github.io/openbdre/bdreimages/architecture.PNG)

# Installation

## Overview

This section will help you build BDRE from source. Audience for this document are developers and architects who want be part of BDRE framework development or may just want to evaluate it.

### General Prerequisite

For testing/development purpose and to save time, use the fully loaded Hadoop VMs from Cloudera or Hortonworks because all the required software are typically installed and configured.

- A Hadoop Cluster
 - In this section we are using *Hortonworks Sandbox 2.2.0*
- Git 1.9 and up
- Maven 3 and up
- Oracle JDK 7(and up)
- BDRE is shipped with an embedded database which is okay for running the UI and evaluating and testing jobs in a single node cluster.
For production use BDRE currently supports following production scale databases.)
  - MySQL Server 5.1 and up
  - Oracle 11g Server or better
  - PostgreSQL
- Google Chrome browser

You should be able to do the same in Mac or Windows but note that setting up a Hadoop cluster might be tricky in Windows and might more involvement. However to deploy and run the jobs we recommend a Linux system. BDRE is typically installed in Hadoop edge node in a multi-node cluster.

## Preparation

* Download and install VirtualBox from https://www.virtualbox.org/
* Download and install Hortonworks Sandbox 2.2 Virtual Box image from http://hortonworks.com/products/releases/hdp-2-2/#install
* Setup a 'Host-Only Adapter' for network to enable communication between Host and Guest OS.
* Now ssh into the sandbox using *root@VM_IP* (password hadoop)
    - The VM_IP is usually something between 192.168.56.101 - 192.168.56.109

* Now create *openbdre* user account.

    ```shell
    [root@sandbox ~]# adduser -m -s /bin/bash openbdre
    [root@sandbox ~]# passwd openbdre
    Changing password for user openbdre.
    New password:
    Retype new password:
    passwd: all authentication tokens updated successfully.
    ```
* As root edit /etc/sudoers and allow openbdre to perform `sudo`. Below will do it

    ```shell
    echo "openbdre ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers
    ```

* Login to the HDP Sandbox with the newly created openbdre user. You can perform a **su openbdre** to switch to this account. Please make sure you are not root user beyond this point.

    ```shell
    [root@sandbox ~]# su openbdre
    [openbdre@sandbox root]$ cd ~
    [openbdre@sandbox ~]$
    ```

* Download Maven from a mirror, unpack and add to the PATH.

    ```shell
    [openbdre@sandbox ~]# wget http://www.us.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
    [openbdre@sandbox ~]# unzip apache-maven-3.3.9-bin.zip
    [openbdre@sandbox ~]# export PATH=$PATH:/home/openbdre/apache-maven-3.3.9/bin
    ```

## Building BDRE from source

1. Obtain the source code
 * cd to the home directory of openbdre.

    ```shell
    [openbdre@sandbox ~]# cd ~
    ```

 * Pull BDRE source from this git repository. To find out your repository link navigate to the repository in this website and copy the https repo URL.

    ```shell
    [openbdre@sandbox ~]# git clone https://github.com/WiproOpenSourcePractice/openbdre.git
    ```

 * cd to the cloned source dir (so you can be in /home/openbdre/openbdre)

    ```shell
    [openbdre@sandbox ~]# cd openbdre
    ```

2. Database Setup
    * Execute the dbsetup.sh script without any parameters as shown below. In this example, we are going to use MySQL as BDRE backend as it's already available in the HDP Sandbox. If you would like to use another database please select it accordingly.

    ```shell
    [openbdre@sandbox ~]# sh dbsetup.sh
    ```

    ```shell
    [openbdre@sandbox openbdre]$ sh dbsetup.sh⏎
    Supported DB
    1) Embedded (Default - Good for running BDRE user interface only. )
    2) Oracle
    3) MySQL
    4) PostgreSQL

    Select Database Type(Enter 1, 2, 3 , 4 or leave empty and press empty to select the default DB):3⏎

    Enter DB username (Type username or leave it blank for default 'root'):⏎
    Enter DB password (Type password or leave it blank for default '<blank>'):⏎
    Enter DB hostname (Type db hostname or leave it blank for default 'localhost'):⏎
    Enter DB port (Type db port or leave it blank for default '3306'):⏎
    Enter DB name (Type db name or leave it blank for default 'bdre'):⏎
    Enter DB schema (Type schema or leave it blank for default 'bdre'):⏎
    Please confirm:

    Database Type: mysql
    JDBC Driver Class: com.mysql.jdbc.Driver
    JDBC Connection URL: jdbc:mysql://localhost:3306/bdre
    Database Username: root
    Database Password:
    Hibernate Dialect: org.hibernate.dialect.MySQLDialect
    Database Schema: bdre
    Are those correct? (type y or n - default y):y⏎
    Database configuration written to ./md-dao/src/main/resources/db.properties
    Will create DB and tables
    Tables created successfully in MySQL bdre DB
    ```

3. Building
 * Now build BDRE using (note BDRE may not compile if the **settings.xml** is not passed from the command line so be sure to use the *-s* option. When building for the first time, it might take a while as maven resolves and downloads the jar libraries from different repositories.

    ```shell
    mvn -s settings.xml clean install -P hdp22
    ```
 * *Note:* Selecting hdp22 will compile BDRE with HDP 2.2 libraries and automatically configure BDRE with Hortonworks Sandbox 2.2.0. Similarly one should be able to build this using -P cdh52 which will configure BDRE for CDH 5.2 QuickStart VM. During building it'll pick up the environment specific configurations from <source root>/databases/setup/profile.*hdp22*.properties.

    Content of databases/setup/profile.hdp22.properties
 ```properties
    bdre_user_name=openbdre
    name_node_hostname=sandbox.hortonworks.com
    name_node_port=8020
    job_tracker_port=8050
    flume_path=/usr/hdp/current/flume-server
    oozie_host=sandbox.hortonworks.com
    oozie_port=11000
    thrift_hostname=sandbox.hortonworks.com
    hive_server_hostname=sandbox.hortonworks.com
    drools_hostname=sandbox.hortonworks.com
    hive_jdbc_user=openbdre
    hive_jdbc_password=openbdre
 ```

    ```shell
    $ mvn -s settings.xml clean install -P hdp22
    [INFO] Scanning for projects...
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Build Order:
        .......blah blah.........
        .......blah blah.........
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 3:39.479s
    [INFO] Finished at: Wed Dec 30 01:50:02 PST 2015
    [INFO] Final Memory: 127M/2296M
    [INFO] ------------------------------------------------------------------------
    ```

4. Installing BDRE
 * After building BDRE successfully run

    ```shell
    sh install-scripts.sh local
    ```
 * It'll install the BDRE scripts and artifacts in /home/openbdre/bdre

### Using BDRE

* After a successful build, start the BDRE UI service

```shell
 sudo service bdre start
```
* Start Oozie as the Oozie user incase Oozie isn't already started. ```ps -ef | grep -i oozie``` will help determine status of Oozie.

    ```shell
    su - oozie -c "/usr/hdp/current/oozie-server/bin/oozie-start.sh"
    ps -ef | grep -i oozie
    ```
* Use *Google Chrome browser* from the host machine and open *http://VM_IP:28850/mdui/pages/content.page*
* Login using admin/zaq1xsw2

### Creating, Deploying and Running a Test Job

* Create a RDBMS data import job from *Job Setup From Template > Import from RDBMS*
* Change the JDBC URL/credentials to your local MySQL DB that contains some data.
* Click *Test Connection*
* Expand and select 1 table (be sure to expand the tables before selecting).
* Create the jobs and see the pipeline.
* Click *XML*, *Diagram* etc. and check the generated Oozie workflow XML and diagram.
* Search for 'Process' in the search window and open the 'Process' page
* Click deploy button on process page corresponding to the process you want to deploy. (Deploy button will show status regarding deployment of process, when you hover over the button.)
* Wait for 2 minutes and the deployment will be completed by then.
* After the deployment is complete and in UI the status for the process is deployed (turns green).
* Click the execution button to execute the *Import job*.
* Check the process in Oozie console *http://VM_IP:11000/oozie*
* When the import job is complete start the *data load job*.

# Data Ingestion

## RDBMS Data Ingestion

<a href="http://www.youtube.com/watch?v=JcbYU7oEmxc" target="_blank"><img src="http://img.youtube.com/vi/JcbYU7oEmxc/1.jpg" 
alt="BDRE RDBMS data ingestion demo video" width="240" height="180" border="10" /></a>


## Streaming Data Ingestion

<a href="http://www.youtube.com/watch?v=1yqoAVENrjo" target="_blank"><img src="http://img.youtube.com/vi/1yqoAVENrjo/1.jpg" 
alt="BDRE Twitter Ingestion demo video" width="240" height="180" border="10" /></a>


## Directory Monitoring and File Ingestion

<a href="http://www.youtube.com/watch?v=IhDMYase1fU" target="_blank"><img src="http://img.youtube.com/vi/IhDMYase1fU/0.jpg" 
alt="BDRE File ingestion demo video" width="240" height="180" border="10" /></a>

# Workflow Builder
Demo video TBD
# Bulk Data Manufacturing
Demo video TBD
# Web Crawler

<a href="http://www.youtube.com/watch?v=0b6dWGxin4Y" target="_blank"><img src="http://img.youtube.com/vi/0b6dWGxin4Y/0.jpg" 
alt="BDRE Web Crawling" width="240" height="180" border="10" /></a>


# Operational Metadata Management System

### Operational Metadata Management

BDRE provides complete job/operational metadata management solution for Hadoop. At its core acts as a registry and tracker for different types of jobs running in different Hadoop clusters or as a standalone. It provides APIs to integrate with virtually any jobs.


![image](http://wiproopensourcepractice.github.io/openbdre/bdreimages/mdgraph.png)


BDRE uses RDBMS database to store all job related metadata. A set of stored procedures are there to interface will the tables which are exposed via Java APIs to manage/create/update the static and run time metadata information. Below is the data model for BDRE metadata operational database.

![eer](http://wiproopensourcepractice.github.io/openbdre/bdreimages/eer.png)

# How to Contribute

Contribution for the enhancements in BDRE are welcome and humbly requested by us. To contribute, please navigate to our GitHub project page and click the 'Fork' button fork the repository under your own account. You can make changes to your own forked repository and then open a pull request to merge your change.

<a class="buttons github" href="https://github.com/WiproOpenSourcePractice/openbdre">Goto BDRE@GitHub</a>

