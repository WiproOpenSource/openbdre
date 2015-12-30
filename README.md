### Overview

This document will help you build BDRE from source. Audience for this document are developers and architects who want be part of BDRE framework development or may just want to test it by running the UI. 

* Install Git, MVN and Oracle JDK 7(and up) if you haven't already. In Windows be sure to add git and other bash tools in the commandline path during installation. To get started download and install following software.
 - Oracle JDK 7 
 - Git Command line Client [Download](https://git-scm.com/download)
 - Maven [Download](http://apache.mirrors.pair.com/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.zip)
 - IntelliJ Idea [Download](https://www.jetbrains.com/idea/download/)
 - VirtualBox [Download](https://www.virtualbox.org/wiki/Downloads) and Cloudera QuickStart VM 5.2 for VirtualBox
 - Google Chrome Browser
 
BDRE is by default configured to run with H2 embedded database which is okay for evaluating and testing jobs in a single node cluster. For production use BDRE currently supports following production scale databases.

  - MySQL Server
  - Oracle 11g Server
  - PostgreSql

 In this guide we are going to show you how to build and install BDRE in a Cloudera QuickStart Hadoop VM which is Linux based with a MySQL database. You should be able to do the same in Mac or Windows but note that setting up a Hadoop cluster might be tricky in Windows and might more involvement. You should be able to launch the BDRE user interface in Windows and design various jobs. However to deploy and run the jobs we recommend a Linux system with Hadoop installed. BDRE is typically installed in Hadoop edge node in a multi-node cluster.

### Preparation

This section is applicable for those who need with Git or Maven setup. You may skip the **Preparation** section if you already have Java/Git/Maven etc set up and properly working.

#### Git


`Note` while installing git in `Windows` (Gitbash) please select following options

- Checkout Windows-style commit unix-style line endings
- Run git and included Unix tools from the Windows command prompt

After installing Git first set your full name (like John Doe) and email id in git config using following command.
```shell
git config --global user.name "Your Name"
git config --global user.email "your_email@company.com"
```
Replace *Your Name* and *your_email* with your real name and your real email.

Linux and Windows differ in how they handle line endings. The `git config core.autocrlf` command is used to change how Git handles line endings. It takes a single argument. So it is better to set the `autocrlf` in git accordingly to avoid further complications.

```shell
Windows:
git config --global core.autocrlf true
Linux:
git config --global core.autocrlf input
```

For further changes to convert CRLF to LF when you are using Linux systems refer to this link (https://help.github.com/articles/dealing-with-line-endings/)

Proxy setup while working behind a proxy network

- If you are behind proxy then you need to setup proxy for command line operations. The easiest way to do that would be adding 3 environment variables before performing any git operations.

```shell
export http_proxy=http://<proxy username>:<proxy password>@<your proxy server>:<proxy port>
export https_proxy=http://<proxy username>:<proxy password>@<your proxy server>:<proxy port>
export no_proxy=localhost
```
Replace `export` with `set` if you are working in Windows.

Git sometimes needs separate proxy configuration to connect to repositories.

For e.g. to set proxy:

```shell
git config --global https.proxy https://<proxy username>:<proxy password>@<your proxy server>:<proxy port>
git config --global http.proxy http://<proxy username>:<proxy password>@<your proxy server>:<proxy port>
```

Note: If your proxy uses Active Directory authentication then you may have to add `DOMAIN\` before the username.

To reset configured proxy use this:

```shell
git config --global --unset https.proxy
git config --global --unset http.proxy
```

#### Environment setup

* Add `jdk` bin directory to the PATH env variable.
* Add `JAVA_HOME` env variable with your installed jdk location.
* Add `mvn` bin directory to the PATH env variable.
* Add `M2_HOME` env variable with your installed maven location.

#### Maven

BDRE is built using Maven build tool. BDRE specific maven settings.xml is part of BDRE source code. Make your environment specific changes in $MAVEN_HOME/conf/settings.xml.
For example if you are behind a proxy or you want a mirror repo, you can use following in $MAVEN_HOME/conf/settings.xml. 

`Cloudera VM already has Maven installed and the settings is located at /usr/local/apache-maven/apache-maven-3.0.4/conf/settings.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>repo.maven.apache.org</id>
            <name>repo.maven.apache.org</name>
            <url>https://repo.maven.apache.org/maven2</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
    <proxies>
        <proxy>
            <id>optional</id>
            <active>true</active>
            <protocol>http</protocol>
            <username>yourUserName</username>
            <password>yourPassword</password>
            <host>yourProxyHost</host>
            <port>yourProxyPort</port>
            <nonProxyHosts>localhost</nonProxyHosts>
        </proxy>
    </proxies>
</settings>

```

## Building BDRE from source

1. Obtain the source code
    * Login to the Cloudera QuickStart VM.
    * Navigate to folder where you want to download BDRE source code and build it.
    * Pull BDRE source from this git repository. To find out your repository link navigate to the repository in this website and copy the https repo URL.

    ```git clone <BDRE git URL>```
    * Now ```cd``` to the bdre source code folder that git created.

2. Database Setup 
    
    * Execute the `dbsetup.sh` script without any parameters as shown below. In this example, we are going to use MySQL as BDRE backend as it's already available in the QuickStart VM. If you would like to use another database please select it accordingly.

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
     
* Now build BDRE using (note BDRE may not compile if the `settings.xml` is not passed from the commandline so be sure to use the `-s` option

    ```mvn -s settings.xml clean install```
    When building for the first time, it might take a while as maven resolves and downloads the jar libraries from diffrent repositories.

4. Installing BDRE
    * After building BDRE successfully run `sh install-scripts.sh local`
    * It'll install the BDRE scripts and artifacts in <user home>/bdre (/home/cloudera/bdre in this example)
    

### Using BDRE

* After a successful build, cd into md-rest-api folder and start the BDRE UI using 
```shell sh ./quick-run.sh```
* Use Google Chrome browser and open http://localhost:9999/mdui/pages/content.page
* Login using admin/zaq1xsw2

### Creating a Test Job

* Create a RDBMS data import job from *Job Setup From Template > New RDBMS Import Job*
* Change the JDBC URL/credentials to your localhost MySQL platmd DB that contains BDRE metadata.
* Click *Test Connection*
* Expand and select 2 - 3 tables (be sure to expand the tables before selecting).
* Create the jobs and see the pipeline.
* Click *XML* , *Diagram* etc and check the generated Oozie workflow XML and diagram.
* Click deploy button on process page corresponding to the process you want to deploy. ( Deploy button will show status regarding deployment of process, when you hover over the button.)
* You have to provide executable permissions to every shell script present in home folder.
* You have to edit deploy-env.properties. Set all properties based on your environment.
* Process Deploy main class calls deploy script based on type of container process. For Example for process type 1 it will call process-type-1.sh script.
* You have to setup a crontab in your environment. Crontab will run process-deploy.sh script present in home folder.
    * for example `*/5 * * * * /home/cloudera/process-deploy.sh`.
* After the deployment is complete and in UI the status for the process is deployed (turns green), you have to execute it.
* Update Workflow.py and flume.sh according to your environment present in home folder.
    * Update hostname variable in Workflow.py.
    * Update `pathForFlumeng`, `pathForFlumeconf`, `pathForFlumeconfFile` variables in flume.sh.
* To store logs for execution, you have to create log directory as mentioned in administration -> settings -> mdconfig -> execute.log-path. create directory and give permission for every user to write into it. `chmod -R 777 <folderName>`
* Also update path in administration -> settings -> mdconfig -> execute.oozie-script-path tags for Workflow.py. And under administration -> settings -> mdconfig -> execute.standalone-script-path for flume.sh.

### Additional Setup

* Two additional jars are required for *flume-source* and JSON *SerDe*. For this to be done, download the zip from [here](https://github.com/cloudera/cdh-twitter-example.git) as these jars are still not available in the maven repository, they need to be custom built. 
    - Goto *flume-sources* inside the folder and do `mvn clean install`.
    - Goto *hive-serdes* inside the folder and do `mvn clean install`.
    - From inside the flume-sources/target folder copy the `flume-sources-1.0-SNAPSHOT` jar to bdre-app/target/lib.
    - From inside the hive-serdes/target folder copy the `hive-serdes-1.0-SNAPSHOT` jar to bdre-app/target/lib.

### Open Project in IntelliJ Idea

* We strongly recommend using IntelliJ Idea to develop BDRE. However BDRE should work fine with other Java IDEs
* Start Idea and go to **File > Open ...** and then browse to the repo folder and open it.
* Idea will automatically detect the maven projects and prompt to *Import Changes*. Import changes.
* You *must* now create your own branch in the project when you are ready to contribute to BDRE.
* For completed, committed and pushed changes, open *Merge Request*.
* Good luck!

