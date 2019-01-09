#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_app
if [ -z "$1" ] || [ -z "$2" ]; then
        echo Insufficient parameters !
        exit 1
fi
processId=$1
userName=$2
echo $0
#creating flume command for
nohup java -cp "$BDRE_HOME/lib/file-mon/*" com.wipro.ats.bdre.filemon.FileMonRunnableMain -p $processId -u $userName > $logDir/file-mon-$processId.log 2> $logDir/file-mon-error-$processId.log &
