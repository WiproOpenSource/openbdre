#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$nameNodeHostName:$nameNodePort
jobTracker=$jobTrackerHostName:$jobTrackerPort
hadoopConfDir=/etc/hive/$hiveConfDir

cd $BDRE_APPS_HOME
set +f
GLOBIGNORE=*
busDomainId=$1
processTypeId=$2
processId=$3
freq=$4
frequency=${freq//\"/}
startTime=$5
endTime=$6
timezone=$7
jobId=$8


#Create coordinatorjob.properties
echo nameNode=$nameNode > $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo jobTracker=$jobTracker >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo oozie.use.system.libpath=true >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo queueName=default >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo oozie.coord.application.path=$hdfsPath/wf/$busDomainId/$processTypeId/$processId/coordinator-workflow-$processId.xml >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
#echo oozie.wf.application.path=$hdfsPath/wf/$busDomainId/$processTypeId/$processId/workflow-$processId.xml >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
#echo oozie.wf.validate.ForkJoin=false >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties

echo frequency=$frequency >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo startTime=$startTime >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo endTime=$endTime >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo timezone=$timezone >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties
echo workflowPath=$hdfsPath/wf/$busDomainId/$processTypeId/$processId/workflow-$processId.xml >> $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties

#if [ "$jobId" != "null" ]
#then
#    oozie job -oozie http://localhost:11000/oozie -kill $jobId
#fi

oozie job -oozie http://localhost:11000/oozie -config $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/coordinatorjob-$processId.properties -run