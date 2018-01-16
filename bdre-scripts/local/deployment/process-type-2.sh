#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$nameNodeHostName:$nameNodePort
jobTracker=$jobTrackerHostName:$jobTrackerPort
hadoopConfDir=/etc/hive/$hiveConfDir
AIRFLOW_DAG_PATH=$airflowDagPath
mkdir -p $AIRFLOW_DAG_PATH
cd $BDRE_APPS_HOME

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ] || [ -z "$5" ]; then
        echo Insufficient parameters !
        exit 1
fi


busDomainId=$1
processTypeId=$2
processId=$3
userName=$4
workflowTypeId=$5

filename="dag_"${busDomainId}_${processTypeId}_${processId}
if [ "$workflowTypeId" == "1" ]; then
echo 'Generating workflow'
java -cp "$BDRE_HOME/lib/workflow-generator/*" com.wipro.ats.bdre.wgen.WorkflowGenerator --parent-process-id $processId --file-name workflow-$processId.xml --username $userName
if [ $? -ne 0 ]
then exit 1
fi
fi

if [ "$workflowTypeId" == "3" ]; then
echo 'Generating airflow dag'
java -cp "$BDRE_HOME/lib/workflow-generator/*" com.wipro.ats.bdre.wgen.dag.DAGGenerator --parent-process-id $processId --file-name $filename.py --username $userName
if [ $? -ne 0 ]
then exit 1
fi
cp $filename.py $AIRFLOW_DAG_PATH
if [ $? -ne 0 ]
then exit 1
fi
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

mkdir -p $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/hql
if [ $? -ne 0 ]
then exit 1
fi

mkdir -p $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/spark
if [ $? -ne 0 ]
then exit 1
fi

if [ "$workflowTypeId" == "1" ]; then
echo 'generated workflow xml to edge node process dir'
#move generated workflow to edge node process dir
mv  workflow-$processId.xml $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi

mv  workflow-$processId.xml.dot $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/
if [ $? -ne 0 ]
then exit 1
fi
fi

if [ "$workflowTypeId" == "3" ]; then
echo 'generated workflow dag to edge node process dir'
#move generated workflow to edge node process dir
mv  $filename.py $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi
fi


#copying data-lineage jar
cp $BDRE_HOME/lib/hive-plugin/hive-plugin-$bdreVersion-executable.jar $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/lib
if [ $? -ne 0 ]
    then exit 1
fi


#copying hive-plugin jar
cp $BDRE_HOME/lib/data-lineage/data-lineage-$bdreVersion.jar $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/lib
if [ $? -ne 0 ]
    then exit 1
fi

#copying spark-core jar
cp $BDRE_HOME/lib/spark-core/spark-core-$bdreVersion.jar $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/lib
if [ $? -ne 0 ]
    then exit 1
fi

# copying metadata jars
cp $BDRE_HOME/lib/md_api/md_api-$bdreVersion-executable.jar $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/lib
if [ $? -ne 0 ]
    then exit 1
fi


#copy all developer checked in files

cp -r $uploadBaseDir/$processId/* $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId


#copy hive-site.xml

cp $hadoopConfDir/hive-site.xml $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi

#copy R shell script
cp $BDRE_HOME/bdre-scripts/deployment/Rhadoop.sh $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -ne 0 ]
then exit 1
fi

dos2unix $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/Rhadoop.sh

dos2unix $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/shell/*
dos2unix $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/additional/*

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

#These lines don't make sense as the R shell script is in the application dir
## deleting and copying R shell script in /tmp folder
#hdfs dfs -rm -r -f /tmp/run_r_hadoop.sh
#if [ $? -ne 0 ]
#then exit 1
#fi
#
#hdfs dfs -put $BDRE_HOME/bdre-scripts/deployment/run_r_hadoop.sh /tmp
#if [ $? -ne 0 ]
#then exit 1
#fi

#List HDFS process dir structure

hdfs dfs -ls -R $hdfsPath/wf/$busDomainId/$processTypeId/$processId/
if [ $? -ne 0 ]
then exit 1
fi

#Create job.properties
echo nameNode=$nameNode > $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo jobTracker=$jobTracker >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.use.system.libpath=true >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo queueName=default >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.application.path=$hdfsPath/wf/$busDomainId/$processTypeId/$processId/workflow-$processId.xml >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.validate.ForkJoin=false >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.properties