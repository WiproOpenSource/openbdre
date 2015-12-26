#!/bin/sh
pwd
. ../env.properties
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

java -cp "$BDRE_HOME/lib/*" com.wipro.ats.bdre.fcgen.FlumeConfGeneratorMain --parent-process-id $processId
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

#move generated jars in flume-ng lib
sudo cp $BDRE_HOME/lib/flume-hdfs-sink-$bdreVersion.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $BDRE_HOME/lib/flume-ng-auth-1.6.0.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $BDRE_HOME/lib/md_api-$bdreVersion.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $BDRE_HOME/lib/md-commons-$bdreVersion.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi


sudo cp $BDRE_HOME/lib/mysql-connector-java-5.1.34.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $BDRE_HOME/lib/slf4j-api-1.7.10.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $BDRE_HOME/lib/slf4j-log4j12-1.7.5.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi