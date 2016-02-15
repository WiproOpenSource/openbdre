#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$nameNodeHostName:$nameNodePort
jobTracker=$jobTrackerHostName:$jobTrackerPort
hadoopConfDir=/etc/hive/$hiveConfDir
cd $BDRE_APPS_HOME

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ];  then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3


#Generating workflow

java -cp "$BDRE_HOME/lib/workflow-generator/*" com.wipro.ats.bdre.wgen.WorkflowGenerator --parent-process-id $processId --file-name workflow-$processId.xml
if [ $? -ne 0 ]
then exit 1
fi

#clean edgenode process directory, if exists
rm -r -f $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi

mkdir -p $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi

mkdir -p $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/lib
if [ $? -ne 0 ]
then exit 1
fi

#move generated workflow to edge node process dir
mv  workflow-$processId.xml $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi

mv  workflow-$processId.xml.dot $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/
if [ $? -ne 0 ]
then exit 1
fi


#copy generated jar for data-import
cp -f $BDRE_HOME/lib/etl-driver/* $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
then exit 1
fi

#create/clean hdfs process directory
hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi

hdfs dfs -rm -r -f $hdfsPath/wf/$busDomainId/$processTypeId/$processId/*
if [ $? -ne 0 ]
then exit 1
fi

#copying files to hdfs

hdfs dfs -put $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/* $hdfsPath/wf/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
    then exit 1
fi

#List HDFS process dir structure
hdfs dfs -ls -R $hdfsPath/wf/$busDomainId/$processTypeId/$processId/
if [ $? -eq 1 ]
then exit 1
fi

# Create job.xml
echo '<?xml version="1.0" encoding="UTF-8"?>' > $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<configuration>'  >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<property><name>user.name</name><value>'"$bdreLinuxUserName"'</value></property>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<property><name>nameNode</name><value>'"$nameNode"'</value></property>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<property><name>jobTracker</name><value>'"$jobTracker"'</value></property>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<property><name>oozie.use.system.libpath</name><value>true</value></property>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<property><name>queueName</name><value>default</value></property>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<property><name>oozie.wf.application.path</name><value>'"$hdfsPath/wf/$busDomainId/$processTypeId/$processId/workflow-$processId.xml"'</value></property>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '<property><name>oozie.wf.validate.ForkJoin</name><value>false</value></property>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml
echo '</configuration>' >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml