#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$nameNodeHostName:$nameNodePort
jobTracker=$jobTrackerHostName:$jobTrackerPort
hadoopConfDir=/etc/hive/$hiveConfDir
cd $BDRE_APPS_HOME

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ];  then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3
userName=$4


#Generating workflow

java -cp "$BDRE_HOME/lib/workflow-generator/*" com.wipro.ats.bdre.wgen.WorkflowGenerator --parent-process-id $processId --file-name workflow-$processId.xml --username $userName
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

#copy generated jar for hive-data-gen
cp -f $BDRE_HOME/lib/hive-data-gen/hive-data-gen-$bdreVersion-executable.jar $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/lib
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

#Create job.properties
echo nameNode=$nameNode > $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo jobTracker=$jobTracker >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.use.system.libpath=true >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo queueName=default >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.application.path=$hdfsPath/wf/$busDomainId/$processTypeId/$processId/workflow-$processId.xml >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.validate.ForkJoin=false >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
