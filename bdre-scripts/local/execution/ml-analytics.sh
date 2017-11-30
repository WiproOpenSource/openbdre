#!/bin/sh

BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_app
if [ -z "$1" ] || [ -z "$2" ] ; then
        echo Insufficient parameters !
        exit 1
fi
processId=$3
userName=$4
echo $0

uploadedJarsDir=/home/cloudera/openbdre/target/lib/ml-analytics

JARS=`find $uploadedJarsDir -name '*.jar'`
OTHER_JARS=""
   for eachjarinlib in $JARS ; do
       OTHER_JARS=$eachjarinlib,$OTHER_JARS
   done

spark-submit --class com.wipro.ats.bdre.ml.driver.MLMain --master local[*]  --jars $OTHER_JARS /home/cloudera/openbdre/target/lib/ml-analytics/ml-analytics-1.1-SNAPSHOT.jar $processId $userName > ~/log.txt