uploadedJarsDir=/home/cloudera/openbdre/target/lib/ml-analytics

JARS=`find $uploadedJarsDir -name '*.jar'`
OTHER_JARS=""
   for eachjarinlib in $JARS ; do
       OTHER_JARS=$eachjarinlib,$OTHER_JARS
   done

spark-submit --class com.wipro.ats.bdre.ml.driver.MLMain --master yarn-client  --jars $OTHER_JARS /home/cloudera/openbdre/target/lib/ml-analytics/ml-analytics-1.1-SNAPSHOT.jar 3 admin > ~/log.txt