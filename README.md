# Bigdata Ready Enterpise Open Source Software 

Released under Apache Public License 2.0. You can get a copy for the license at http://www.apache.org/licenses/LICENSE-2.0.

## Overview

This document will help you build BDRE from source. Audience for this document are developers and architects who want be part of BDRE framework development or may just want to test it by running the UI. Install Git, Maven and Oracle JDK 7(and up) if you haven't already. In Windows be sure to add git and other bash tools in the commandline path during installation. In this example, we are going to use *Cloudera Quickstart VM* with *VirtualBox* software because all the required software are already installed and configured. BDRE is shipped with an embedded database which is okay for running the UI and evaluating and testing jobs in a single node cluster. For production use BDRE currently supports following production scale databases.

  - MySQL Server
  - Oracle 11g Server
  - PostgreSql

 In this guide we are going to show you how to build and install BDRE in a Cloudera QuickStart Hadoop VM which is Linux based with a MySQL database. You should be able to do the same in Mac or Windows but note that setting up a Hadoop cluster might be tricky in Windows and might more involvement. You should be able to launch the BDRE user interface in Windows and design various jobs. However to deploy and run the jobs we recommend a Linux system with Hadoop installed. BDRE is typically installed in Hadoop edge node in a multi-node cluster.


## Preparation

* Download VirtualBox from https://www.virtualbox.org/
* Download Cloudera Quickstart VM 5.2 

It's also quite easy to run BDRE for HortonWorks Sandbox. You can download HortonWorks Sandbox and build BDRE after enabling the HDP profile in settings.xml (included with BDRE source code). 

## Building BDRE from source

1. Obtain the source code
    * Login to the Cloudera QuickStart VM.
    * Navigate to folder where you want to download BDRE source code and build it.
    * Pull BDRE source from this git repository. To find out your repository link navigate to the repository in this website and copy the https repo URL.

    ```git clone https://gitlab.com/bdre/openbdre.git
       cd openbdre
    ```
   

2. Database Setup 
    * Execute the dbsetup.sh script without any parameters as shown below. In this example, we are going to use MySQL as BDRE backend as it's already available in the QuickStart VM. If you would like to use another database please select it accordingly.
    
    ```sh dbsetup.sh```
    
    ```shell
    $ sh dbsetup.sh
    Supported DB
    1) Embedded (Default - Good for running BDRE user interface only. )
    2) Oracle
    3) MySQL
    4) PostgreSQL
    
    Select Database Type(Enter 1, 2, 3 , 4 or leave empty and press empty to select the default DB):3
    
    Enter DB username (Type username or leave it blank for default 'root'):⏎
    Enter DB password (Type password or leave it blank for default 'cloudera'):⏎
    Enter DB hostname (Type db hostname or leave it blank for default 'localhost'):⏎
    Enter DB port (Type db port or leave it blank for default '3306'):⏎
    Enter DB name (Type db name or leave it blank for default 'bdre'):⏎
    Enter DB schema (Type schema or leave it blank for default 'bdre'):⏎
    Please confirm:
    
    Database Type: mysql
    JDBC Driver Class: com.mysql.jdbc.Driver
    JDBC Connection URL: jdbc:mysql://localhost:3306/bdre
    Database Username: root
    Database Password: cloudera
    Hibernate Dialect: org.hibernate.dialect.MySQLDialect
    Database Schema: bdre
    Are those correct? (type y or n - default y): y⏎
    Database configuration written to ./md-dao/src/main/resources/db.properties
    Will create DB and tables
    Tables successfully created in MySQL bdre database.
    ```
    
3. Building
    * Now build BDRE using (note BDRE may not compile if the **settings.xml** is not passed from the commandline so be sure to use the *-s* option. When building for the first time, it might take a while as maven resolves and downloads the jar libraries from diffrent repositories.
    
    ```mvn -s settings.xml clean install```
    
    ```shell
    $ mvn -s settings.xml clean install
    [INFO] Scanning for projects...
    [WARNING]
    [WARNING] Some problems were encountered while building the effective model for com.wipro.ats.bdre:md-ui:war:1.1-SNAPSHOT
    [WARNING] 'build.plugins.plugin.version' for org.apache.maven.plugins:maven-war-plugin is missing. @ line 275, column 21
    [WARNING]
    [WARNING] It is highly recommended to fix these problems because they threaten the stability of your build.
    [WARNING]
    [WARNING] For this reason, future Maven versions might no longer support building such malformed projects.
    [WARNING]
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
    
    ```sh install-scripts.sh local```
    
    * It'll install the BDRE scripts and artifacts in <user home>/bdre (/home/cloudera/bdre in this example)

### Using BDRE

* After a successful build, cd into md-rest-api folder and start the BDRE UI using 

```shell sh ./quick-run.sh```

* Use *Google Chrome browser* from the host machine and open *http://VM_IP:288503/mdui/pages/content.page*
* Login using admin/zaq1xsw2

### Creating, Deploying and Running a Test Job

* Create a RDBMS data import job from *Job Setup From Template > Import from RDBMS*
* Change the JDBC URL/credentials to your local MySQL retail_db DB that contains some sample retail data metadata.
* Click *Test Connection*
* Expand and select 1 table (be sure to expand the tables before selecting).
* Create the jobs and see the pipeline.
* Click *XML* , *Diagram* etc and check the generated Oozie workflow XML and diagram.
* Search for 'Process' in the search window and open the 'Process' page
* Click deploy button on process page corresponding to the process you want to deploy. ( Deploy button will show status regarding deployment of process, when you hover over the button.)
* Wait for 2 minutes and the deployment will be completed by then.
* After the deployment is complete and in UI the status for the process is deployed (turns green).
* Click the execution button to execute the workflow.
* Check the process in Oozie console *http://VM_IP:11000/oozie*


