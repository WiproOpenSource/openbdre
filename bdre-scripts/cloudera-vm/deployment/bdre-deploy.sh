#!/bin/sh
source ./deploy-env.properties
cd /home/cloudera
rm -r -f BDRE
mkdir -p BDRE/target/lib
mkdir -p BDRE/workflow-generator/target
mkdir -p BDRE/dq/target
mkdir -p BDRE/data-extraction/data-import/target
mkdir -p BDRE/data-extraction/mq-import/target
mkdir -p BDRE/hive-plugin/target
mkdir -p BDRE/im-crawler/target
mkdir -p BDRE/spark-core/target
mkdir -p BDRE/data-export/target

cd $pathToBdreAppRepo
#bdre-app deploy
echo "copying jars inside BDRE local directory"
\cp -r bdre-app/target/lib/* /home/cloudera/BDRE/target/lib/
\cp -r bdre-app/workflow-generator/target/* /home/cloudera/BDRE/workflow-generator/target/

\cp -r bdre-app/dq/target/dq-*-jar-with-dependencies.jar  /home/cloudera/BDRE/dq/target/
\cp -r bdre-app/data-extraction/data-import/target/data-import-1.1-SNAPSHOT-jar-with-dependencies.jar /home/cloudera/BDRE/data-extraction/data-import/target/
\cp -r bdre-app/data-export/target/data-export-*-jar-with-dependencies.jar /home/cloudera/BDRE/data-export/target/
\cp -r bdre-app/data-extraction/mq-import/target/mq-import-*-jar-with-dependencies.jar /home/cloudera/BDRE/data-extraction/mq-import/target/
\cp -r bdre-app/hive-plugin/target/hive-plugin-1.1-SNAPSHOT-jar-with-dependencies.jar /home/cloudera/BDRE/hive-plugin/target/
\cp -r bdre-app/im-crawler/target/original-im-crawler-1.1-SNAPSHOT-shaded.jar /home/cloudera/BDRE/im-crawler/target/
\cp -r bdre-app/spark-core/target/spark-core-*.jar /home/cloudera/BDRE/spark-core/target

echo "copying BDRE jars into HDFS"
cd /home/cloudera
hadoop fs -mkdir -p /user/oozie/bdre/lib
hadoop fs -rm /user/oozie/bdre/lib/*
hadoop fs -put BDRE/target/lib/* /user/oozie/bdre/lib/
hdfs dfs -rm /user/oozie/bdre/lib/dq-*.jar 
hdfs dfs -rm /user/oozie/bdre/lib/hive-exec*.jar 
hadoop fs -mkdir -p /user/oozie/bdre/lib2
hadoop fs -rm /user/oozie/bdre/lib2/*
hadoop fs -put BDRE/target/lib/* /user/oozie/bdre/lib2/
hadoop fs -rm /user/oozie/bdre/lib2/hive-*
