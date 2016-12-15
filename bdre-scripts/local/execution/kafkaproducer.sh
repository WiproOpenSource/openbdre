#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_app
if [ -z "$1" ] || [ -z "$2" ] ; then
        echo Insufficient parameters !
        exit 1
fi
processId=$1
userName=$2
echo $0
#creating flume command for
java -cp "$BDRE_HOME/lib/kafka2/*" com.wipro.ats.bdre.kafka.FileProducer2 -p $processId -u userName
