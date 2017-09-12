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
uploadedJarsDir=~/streamgrids/$processId/

JARS=`find $uploadedJarsDir -name '*.jar'`
OTHER_JARS=""
   for eachjarinlib in $JARS ; do
       OTHER_JARS=$eachjarinlib,$OTHER_JARS
   done
echo ---final list of jars are : $OTHER_JARS

echo spark-submit --class driver.StreamAnalyticsDriver --master yarn-client --conf spark.driver.cores=$5 --conf spark.driver.memory=$6M --conf spark.executor.cores=$7 --conf spark.executor.memory=$8M --conf spark.streaming.receiver.maxRate=$9 --conf spark.eventLog.enabled=${10} --conf spark.task.maxFailures=${11} --conf spark.dynamicAllocation.enabled=${12}

cd $BDRE_HOME/lib/md_api

spark-submit --class driver.StreamAnalyticsDriver --master yarn-client  --conf spark.driver.cores=$5 --conf spark.driver.memory=$6M \
                                                                        --conf spark.executorEnv.JAVA_HOME=/usr/java/jdk1.8.0_131 \
                                                                        --conf spark.executor.cores=$7 --conf spark.executor.memory=$8M --conf spark.streaming.receiver.maxRate=$9  \
                                                                        --conf spark.task.maxFailures=${11} --conf spark.dynamicAllocation.enabled=${12} \
                                                                        --jars activation-1.1.jar,activemq-client-5.11.1.jar,antlr-2.7.7.jar,aopalliance-1.0.jar,commons-beanutils-1.6.jar,commons-beanutils-core-1.8.0.jar,commons-cli-1.2.jar,commons-collections-3.2.1.jar,commons-configuration-1.6.jar,commons-dbcp-1.4.jar,commons-digester-1.8.jar,commons-io-2.1.jar,commons-jxpath-1.3.jar,commons-lang-2.5.jar,commons-logging-1.1.1.jar,commons-pool-1.5.4.jar,derby-10.11.1.1.jar,dom4j-1.6.1.jar,geronimo-j2ee-management_1.1_spec-1.0.1.jar,geronimo-jms_1.1_spec-1.1.1.jar,h2-1.4.190.jar,hawtbuf-1.11.jar,hibernate-commons-annotations-4.0.1.Final.jar,hibernate-core-4.2.0.Final.jar,hibernate-entitymanager-4.2.0.Final.jar,hibernate-jpa-2.0-api-1.0.1.Final.jar,hibernate-jpa-2.1-api-1.0.0.Final.jar,hibernate-validator-4.2.0.Final.jar,jackson-core-asl-1.9.13.jar,jackson-mapper-asl-1.9.13.jar,javassist-3.15.0-GA.jar,jboss-logging-3.1.0.GA.jar,jboss-transaction-api_1.1_spec-1.0.0.Final.jar,jms-1.1.jar,log4j-1.2.17.jar,mail-1.4.5.jar,md_api-1.1-SNAPSHOT.jar,md-commons-1.1-SNAPSHOT.jar,md-dao-1.1-SNAPSHOT.jar,mysql-connector-java-5.1.34.jar,ojdbc6-11.2.0.3.jar,postgresql-9.1-901.jdbc4.jar,slf4j-api-1.7.10.jar,spring-aop-3.1.0.RELEASE.jar,spring-asm-3.1.0.RELEASE.jar,spring-beans-3.1.0.RELEASE.jar,spring-context-3.1.0.RELEASE.jar,spring-core-3.1.0.RELEASE.jar,spring-expression-3.1.0.RELEASE.jar,spring-jdbc-3.1.0.RELEASE.jar,spring-orm-3.1.0.RELEASE.jar,spring-tx-3.1.0.RELEASE.jar,spring-web-3.1.0.RELEASE.jar,validation-api-1.1.0.Final.jar,../stream-analytics/hbase-spark-1.2.0-cdh5.8.0.jar,$OTHER_JARS  ../stream-analytics/stream-analytics-1.1-SNAPSHOT.jar $processId $userName > ~/log.txt
