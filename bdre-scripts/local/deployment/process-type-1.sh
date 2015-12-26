#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$hostName:$nameNodePort
jobTracker=$hostName:$dataNodePort
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

java -cp "$BDRE_HOME/lib/flume-conf-generator/*" com.wipro.ats.bdre.fcgen.FlumeConfGeneratorMain --parent-process-id $processId
if [ $? -eq 1 ]
then exit 1
fi

#clean edgenode process directory, if exists
rm -r -f $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi

mkdir -p $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi

#move generated conf to edge node process dir
mv flume-$processId.conf $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi

