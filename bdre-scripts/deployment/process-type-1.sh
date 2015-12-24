rcmd#!/bin/sh
source ./deploy-env.properties

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3

edgeNodeBDRERoot=/home/$bdreLinuxUserName
hdfsPath=/user/$bdreLinuxUserName
nameNode=hdfs://$hostName:$nameNodePort
jobTracker=$hostName:$dataNodePort
hadoopConfDir=/etc/hive/$hiveConfDir

bdreVersion=1.1-SNAPSHOT
cd $edgeNodeBDRERoot


#Generating workflow

java -cp "$pathForJars/flume-conf-generator-$bdreVersion.jar:$pathForWorkflowJars/*" com.wipro.ats.bdre.fcgen.FlumeConfGeneratorMain --parent-process-id $processId
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

#move generated conf to edge node process dir
mv flume-$processId.conf BDRE/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi

#move generated jars in flume-ng lib
sudo cp $pathForFlumeHDFSSinkJar/flume-hdfs-sink-$bdreVersion.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $pathForFlumeHDFSSinkJar/flume-ng-auth-1.6.0.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $pathForFlumeHDFSSinkJar/md_api-$bdreVersion.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $pathForFlumeHDFSSinkJar/md-commons-$bdreVersion.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi


sudo cp $pathForFlumeHDFSSinkJar/mysql-connector-java-5.1.34.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $pathForFlumeHDFSSinkJar/slf4j-api-1.7.10.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi

sudo cp $pathForFlumeHDFSSinkJar/slf4j-log4j12-1.7.5.jar $flumeLibDir
if [ $? -eq 1 ]
then exit 1
fi