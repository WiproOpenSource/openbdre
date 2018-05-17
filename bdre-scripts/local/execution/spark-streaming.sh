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
uploadedJarsDir=$BDRE_HOME/lib/stream-analytics/

JARS=`find $uploadedJarsDir -name '*.jar'`
OTHER_JARS=""
   for eachjarinlib in $JARS ; do
       OTHER_JARS=$eachjarinlib,$OTHER_JARS
   done
echo ---final list of jars are : $OTHER_JARS

echo spark-submit --class driver.StreamAnalyticsDriver --master yarn-client --conf spark.driver.cores=$5 --conf spark.driver.memory=$6M --conf spark.executor.cores=$7 --conf spark.executor.memory=$8M --conf spark.streaming.receiver.maxRate=$9 --conf spark.eventLog.enabled=${10} --conf spark.task.maxFailures=${11} --conf spark.dynamicAllocation.enabled=${12}



spark-submit --class driver.StreamAnalyticsDriver --master local[*]  --conf spark.driver.cores=$5 --conf spark.driver.memory=$6M \
                                                                        --conf spark.executor.cores=$7 --conf spark.executor.memory=$8M --conf spark.streaming.receiver.maxRate=$9  \
                                                                        --conf spark.task.maxFailures=${11} --conf spark.dynamicAllocation.enabled=${12} \
                                                                          $BDRE_HOME/lib/stream-analytics/spark-streaming-1.1-SNAPSHOT-jar-with-dependencies.jar $processId $userName > ~/log.txt
