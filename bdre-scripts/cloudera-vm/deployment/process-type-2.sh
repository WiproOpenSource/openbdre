#!/bin/sh
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

mkdir -p BDRE/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
then exit 1
fi

mkdir -p BDRE/$busDomainId/$processTypeId/$processId/hql
if [ $? -eq 1 ]
then exit 1
fi

mkdir -p BDRE/$busDomainId/$processTypeId/$processId/pig
if [ $? -eq 1 ]
then exit 1
fi

mkdir -p BDRE/$busDomainId/$processTypeId/$processId/shell
if [ $? -eq 1 ]
then exit 1
fi

mkdir -p BDRE/$busDomainId/$processTypeId/$processId/r
if [ $? -eq 1 ]
then exit 1
fi

mkdir -p BDRE/$busDomainId/$processTypeId/$processId/spark
if [ $? -eq 1 ]
then exit 1
fi

#move generated workflow to edge node process dir
mv  workflow-$processId.xml BDRE/$busDomainId/$processTypeId/$processId
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

hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId/hql
if [ $? -eq 1 ]
then exit 1
fi

hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId/pig
if [ $? -eq 1 ]
then exit 1
fi

hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId/shell
if [ $? -eq 1 ]
then exit 1
fi

hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId/r
if [ $? -eq 1 ]
then exit 1
fi

hdfs dfs -mkdir -p $hdfsPath/wf/$busDomainId/$processTypeId/$processId/spark
if [ $? -eq 1 ]
then exit 1
fi

#copy generated workflow to hdfs process dir
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/workflow-$processId.xml $hdfsPath/wf/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi
#copy generated jar for data-lineage
if [ -d "$localPathForJars/$processId/jar" ]; then
  # Control will enter here if $DIRECTORY exists.

jars=($localPathForJars/$processId/jar/*)
if [ ${#jars[@]} -gt 0 ];
    then
        cp $localPathForJars/$processId/jar/* BDRE/$busDomainId/$processTypeId/$processId/lib
        if [ $? -eq 1 ]
            then exit 1
        fi
        "file(s) copied"
fi
fi
#copying hive-plugin jar
cp $pathForHivePlugin/hive-plugin-$bdreVersion-jar-with-dependencies.jar BDRE/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi

#copying spark-core jar
cp $pathForSparkCoreJar/spark-core-$bdreVersion.jar BDRE/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi


# copying metadata jars
cp $pathForJars/* BDRE/$busDomainId/$processTypeId/$processId/lib

#copying jars to hdfs
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/data-lineage-$bdreVersion.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/mysql-connector-java-5.1.34.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi
#hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/mybatis-3.2.8.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
#if [ $? -eq 1 ]
#    then exit 1
#fi
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/md-commons-$bdreVersion.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/md_api-$bdreVersion.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/login-module-$bdreVersion.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/log4j-1.2.17.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/hive-plugin-$bdreVersion-jar-with-dependencies.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi
hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/lib/spark-core-$bdreVersion.jar $hdfsPath/wf/$busDomainId/$processTypeId/$processId/lib
if [ $? -eq 1 ]
    then exit 1
fi


#copy hive-site.xml to hdfs process dir
hdfs dfs -put $hadoopConfDir/hive-site.xml $hdfsPath/wf/$busDomainId/$processTypeId/$processId
if [ $? -eq 1 ]
then exit 1
fi
#copy all developer checked in hql files to hdfs process dir
if [ -d "$localPathForDevFiles/$processId/hql" ]; then
hql=`ls -l $localPathForDevFiles/$processId/hql | grep -v total | wc -l`
if [ $hql -gt 0 ];   
 then
        cp $localPathForDevFiles/$processId/hql/* BDRE/$busDomainId/$processTypeId/$processId/hql
        if [ $? -eq 1 ]
            then exit 1
        fi
        echo "hql file(s) copied"
fi
else echo "no hql files to copy"
fi

#copy all developer checked in pig files to hdfs process dir
if [ -d "$localPathForDevFiles/$processId/pig" ]; then
hql=`ls -l $localPathForDevFiles/$processId/pig | grep -v total | wc -l`
if [ $hql -gt 0 ];   
 then
        cp $localPathForDevFiles/$processId/pig/* BDRE/$busDomainId/$processTypeId/$processId/pig
        if [ $? -eq 1 ]
            then exit 1
        fi
        echo "pig script(s) copied"
fi
else echo "no pig scripts to copy"
fi

#copy all developer checked in shell scripts to hdfs process dir
if [ -d "$localPathForDevFiles/$processId/shell" ]; then
hql=`ls -l $localPathForDevFiles/$processId/shell | grep -v total | wc -l`
if [ $hql -gt 0 ];   
 then
        cp $localPathForDevFiles/$processId/shell/* BDRE/$busDomainId/$processTypeId/$processId/shell
        if [ $? -eq 1 ]
            then exit 1
        fi
        echo "shell script(s) copied"
fi
else echo "no shell scripts to copy"
fi

#copy all developer checked in r scripts to hdfs process dir
if [ -d "$localPathForDevFiles/$processId/r" ]; then
hql=`ls -l $localPathForDevFiles/$processId/r | grep -v total | wc -l`
if [ $hql -gt 0 ];   
 then
        cp $localPathForDevFiles/$processId/r/* BDRE/$busDomainId/$processTypeId/$processId/r
        if [ $? -eq 1 ]
            then exit 1
        fi
        echo "r script(s) copied"
fi
else echo "no r scripts to copy"
fi

#copy all developer checked in spark files to hdfs process dir
if [ -d "$localPathForDevFiles/$processId/spark" ]; then
spark=`ls -l $localPathForDevFiles/$processId/spark | grep -v total | wc -l`
if [ $spark -gt 0 ];
 then
        cp $localPathForDevFiles/$processId/spark/* BDRE/$busDomainId/$processTypeId/$processId/spark
        if [ $? -eq 1 ]
            then exit 1
        fi
        echo "spark file(s) copied"
fi
else echo "no spark files to copy"
fi

hqls=`ls -l BDRE/$busDomainId/$processTypeId/$processId/hql | grep -v total | wc -l`
if [ $hqls -gt 0 ];
then
    echo "uploading $hqls hql files into hdfs"
    hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/hql/* $hdfsPath/wf/$busDomainId/$processTypeId/$processId/hql
    if [ $? -eq 1 ]
    then exit 1
    fi
fi

pigscripts=`ls -l BDRE/$busDomainId/$processTypeId/$processId/pig | grep -v total | wc -l`
if [ $pigscripts -gt 0 ];
then
    echo "uploading $pigscripts pig scripts into hdfs"
    hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/pig/* $hdfsPath/wf/$busDomainId/$processTypeId/$processId/pig
    if [ $? -eq 1 ]
    then exit 1
    fi
fi

shellscripts=`ls -l BDRE/$busDomainId/$processTypeId/$processId/shell | grep -v total | wc -l`
if [ $shellscripts -gt 0 ];
then
    echo "uploading $shellscripts shell scripts into hdfs"
    hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/shell/* $hdfsPath/wf/$busDomainId/$processTypeId/$processId/shell
    if [ $? -eq 1 ]
    then exit 1
    fi
fi

rscripts=`ls -l BDRE/$busDomainId/$processTypeId/$processId/r | grep -v total | wc -l`
if [ $rscripts -gt 0 ];
then
    echo "uploading $rscripts r scripts into hdfs"
    hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/r/* $hdfsPath/wf/$busDomainId/$processTypeId/$processId/r
    if [ $? -eq 1 ]
    then exit 1
    fi
fi

sparkfiles=`ls -l BDRE/$busDomainId/$processTypeId/$processId/spark | grep -v total | wc -l`
if [ $sparkfiles -gt 0 ];
then
    echo "uploading $sparkfiles spark application jars into hdfs"
    hdfs dfs -put BDRE/$busDomainId/$processTypeId/$processId/spark/* $hdfsPath/wf/$busDomainId/$processTypeId/$processId/spark
    if [ $? -eq 1 ]
    then exit 1
    fi
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
echo queueName=default >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo examplesRoot=example >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo env=$environment >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.application.path=$hdfsPath/wf/$busDomainId/$processTypeId/$processId/workflow-$processId.xml >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties
echo oozie.wf.validate.ForkJoin=false >> BDRE/$busDomainId/$processTypeId/$processId/job-$processId.properties