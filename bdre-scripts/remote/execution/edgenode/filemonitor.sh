#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_app
if [ -z "$1" ] ; then
        echo Insufficient parameters !
        exit 1
fi
processId=$1
echo $0
#creating flume command for
nohup java -cp "$BDRE_HOME/lib/file-mon/*" com.wipro.ats.bdre.filemon.FileMonRunnableMain -p $processId &
