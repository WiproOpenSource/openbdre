#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$nameNodeHostName:$nameNodePort
jobTracker=$jobTrackerHostName:$jobTrackerPort
hadoopConfDir=/etc/hive/$hiveConfDir
cd $BDRE_APPS_HOME

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3


#Generating flume conf

java -cp "$BDRE_HOME/lib/analytic-ui-generator/*" com.wipro.ats.bdre.augen.AnalyticUIGeneratorMain --parent-process-id $processId
if [ $? -eq 1 ]
then exit 1
fi
