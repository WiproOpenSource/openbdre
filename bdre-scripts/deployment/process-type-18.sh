#!/bin/sh

source ./deploy-env.properties

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ];  then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3

edgeNodeBDRERoot=/home/$bdreLinuxUserName
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$hostName:$nameNodePort
jobTracker=$hostName:$jobTrackerPort
hadoopConfDir=/etc/hive/$hiveConfDir
bdreVersion=1.1-SNAPSHOT
cd $edgeNodeBDRERoot

#Generating workflow

java -cp "$pathForWorkflowMainJar/workflow-generator-$bdreVersion.jar:$pathForWorkflowJars/*" com.wipro.ats.bdre.wgen.WorkflowGenerator --parent-process-id $processId --file-name workflow-$processId.xml

if [ $? -eq 1 ] 
then exit 1
fi
#clean edgenode process directory, if exists
 rm -r -f BDRE/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ] 
then exit 1
fi
mkdir -p BDRE/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi
#move generated workflow to edge node process dir
mv  workflow-$processId.xml BDRE/$busDomainId/$processTypeId/$processId/
if [ $? -eq 1 ]
then exit 1
fi
mv  workflow-$processId.xml.dot BDRE/$busDomainId/$processTypeId/$processId/
if [ $? -eq 1 ]
then exit 1
fi
#create/clean hdfs process directory
hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi
hdfs dfs -rm -r -f $hdfsPath/wf/$busDomainId/$processTypeId/$processId/*
if [ $? -eq 1 ]
then exit 1
fi
hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
then exit 1
fi
#copy generated workflow to hdfs process dir
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/workflow-$processId.xml $hdfsPath/wf/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi
#copy generated jar for hive-data-gen
hdfs dfs -put $pathToHiveDataGen/hive-data-gen-$bdreVersion-executable.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib/
if [ $? -eq 1 ]
then exit 1
fi
#List HDFS process dir structure
hdfs dfs -ls -R $hdfsPath/wf/$busDomainId/$processTypeId/$processId/
if [ $? -eq 1 ]
then exit 1
fi
#Create job.properties
echo nameNode=$nameNode > BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo jobTracker=$jobTracker >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.use.system.libpath=true >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.libpath=/user/oozie/bdre/lib/ >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo queueName=default >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo examplesRoot=example >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.application.path=$nameNode$hdfsPath/wf/$busDomainId/$processTypeId/$processId/workflow-$processId.xml >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.validate.ForkJoin=false >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties 

