# Bigdata Ready Enterpise Open Source Software 

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
* Now ssh into the sandbox using root@VM_IP (password hadoop)
    - The VM_IP is usually something between 192.168.56.101 - 192.168.56.109 
    
    ```shell
    [root@sandbox ~]# adduser -m -s /bin/bash openbdre
    [root@sandbox ~]# passwd openbdre
    Changing password for user openbdre.
    New password:
    Retype new password:
    passwd: all authentication tokens updated successfully.
    [root@sandbox ~]#
    [root@sandbox ~]# su openbdre
    [openbdre@sandbox root]$ cd
    [openbdre@sandbox ~]$
    ```
* Edit /etc/sudoers and append following line at the bottom. It's a readonly file so you need to save it with `wq!` in vi. This will allow openbdre to perform `sudo`.

    ```openbdre ALL=(ALL) NOPASSWD:ALL```

    It's also quite easy to run BDRE for CDH VM. You can download Cloudera Quickstart VM and build BDRE after enabling the CDH profile in settings.xml (included with BDRE source code). 
* Edit /etc/hosts file and add a mapping for openbdre.org and <VM_PRIVATE_IP>. The IP should be the same one sandbox.hortonworks.com is mapped to.
    - It should look like below
    
    ```shell
    # File is generated from /usr/lib/hue/tools/start_scripts/gen_hosts.sh
    # Do not remove the following line, or various programs
    # that require network functionality will fail.
    127.0.0.1               localhost.localdomain localhost
    10.0.2.15       sandbox.hortonworks.com sandbox ambari.hortonworks.com
    10.0.2.15       openbdre.org
    ```
* Download Maven from a mirror and unpack
    ```
    wget http://www.us.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
    unzip apache-maven-3.3.9-bin.zip
    export PATH=$PATH:/home/openbdre/apache-maven-3.3.9/bin
    ```
    
## Building BDRE from source

1. Obtain the source code
    * Login to the HDP Sandbox with the newly created openbdre user. You can perform a `su openbdre` to switch to this account.
    * cd to the home directory of openbdre ```cd ~```
    * Pull BDRE source from this git repository. To find out your repository link navigate to the repository in this website and copy the https repo URL.

    ```git clone https://gitlab.com/bdre/openbdre.git```
    If you want to be a non-annonymous user then change the URL format to https://GIT_USER:GIT_PASSWORD@gitlab.com/bdre/openbdre.git
    * cd to the cloned source dir
    
    ```cd openbdre```
    

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
    Are those correct? (type y or n - default y): y⏎
    Database configuration written to ./md-dao/src/main/resources/db.properties
    Will create DB and tables
    Tables successfully created in MySQL bdre database.
    ```
    
3. Building
    * Now build BDRE using (note BDRE may not compile if the **settings.xml** is not passed from the commandline so be sure to use the *-s* option. When building for the first time, it might take a while as maven resolves and downloads the jar libraries from diffrent repositories.
    
    ```mvn -s settings.xml clean install -P hdp```
    
    ```shell
    $ mvn -s settings.xml clean install -P hdp
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
    
    ```sh install-scripts.sh local```
    
    * It'll install the BDRE scripts and artifacts in /home/openbdre/bdre

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


