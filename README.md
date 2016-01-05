# Bigdata Ready Enterprise Open Source Software 

Released under Apache Public License 2.0. You can get a copy for the license at http://www.apache.org/licenses/LICENSE-2.0.

## Overview

This document will help you build BDRE from source. Audience for this document are developers and architects who want be part of BDRE framework development or may just want to test it by running the UI. Install Git, Maven and Oracle JDK 7(and up) if you haven't already. In Windows be sure to add git and other bash tools in the commandline path during installation. In this example, we are going to use *HortonWorks Sandbox* with *VirtualBox* software because all the required software are mostly installed and configured. BDRE is shipped with an embedded database which is okay for running the UI and evaluating and testing jobs in a single node cluster. For production use BDRE currently supports following production scale databases.

  - MySQL Server
  - Oracle 11g Server
  - PostgreSql

 In this guide we are going to show you how to build and install BDRE in a CentOS VM with a MySQL database. You should be able to do the same in Mac or Windows but note that setting up a Hadoop cluster might be tricky in Windows and might more involvement. You should be able to launch the BDRE user interface in Windows and design various jobs. However to deploy and run the jobs we recommend a Linux system with Hadoop installed. BDRE is typically installed in Hadoop edge node in a multi-node cluster.


## Preparation

* Download and install VirtualBox from https://www.virtualbox.org/
* Download and install HortonWorks Sandbox 2.2 Virtual Box image from http://hortonworks.com/products/releases/hdp-2-2/#install 
* Setup a 'Host-Only Adapter' for network to enable communication between Host and Guest OS.
* Now ssh into the sandbox using *root@VM_IP* (password hadoop)
    - The VM_IP is usually something between 192.168.56.101 - 192.168.56.109 
* Start Oozie as the Oozie user and Oozie isn't already started. ```ps -ef | grep -i oozie``` will help determine status of Oozie.

    ```shell
    su - oozie -c "/usr/hdp/current/oozie-server/bin/oozie-start.sh"
    ps -ef | grep -i oozie
    ```
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
    
* Login to the HDP Sandbox with the newly created openbdre user. You can perform a **su openbdre** to switch to this account. 

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
 * Now build BDRE using (note BDRE may not compile if the **settings.xml** is not passed from the commandline so be sure to use the *-s* option. When building for the first time, it might take a while as maven resolves and downloads the jar libraries from diffrent repositories.
    
    ```shell
    mvn -s settings.xml clean install -P hdp22
    ```
 * *Note:* Selecting hdp22 will compile BDRE with HDP 2.2 libraries and automatically configure BDRE with Hortonworks Sandbox 2.2.0. Similarly one should be able to build this using -P cdh52 which will configure BDRE for CDH 5.2 Quickstart VM. During building it'll pickup the environment specific configurations from <source root>/databases/setup/profile.*hdp22*.properties.
 
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

* After a successful build, start the BDRE UI using 

```shell
 /home/openbdre/bdre/bdre-scripts/execution/run-ui.sh
```

* Use *Google Chrome browser* from the host machine and open *http://VM_IP:288503/mdui/pages/content.page*
* Login using admin/zaq1xsw2

### Creating, Deploying and Running a Test Job

* Create a RDBMS data import job from *Job Setup From Template > Import from RDBMS*
* Change the JDBC URL/credentials to your local MySQL DB that contains some data.
* Click *Test Connection*
* Expand and select 1 table (be sure to expand the tables before selecting).
* Create the jobs and see the pipeline.
* Click *XML* , *Diagram* etc and check the generated Oozie workflow XML and diagram.
* Search for 'Process' in the search window and open the 'Process' page
* Click deploy button on process page corresponding to the process you want to deploy. ( Deploy button will show status regarding deployment of process, when you hover over the button.)
* Wait for 2 minutes and the deployment will be completed by then.
* After the deployment is complete and in UI the status for the process is deployed (turns green).
* Click the execution button to execute the *Import job*.
* Check the process in Oozie console *http://VM_IP:11000/oozie*
* When the import job is complete start the *data load job*.



