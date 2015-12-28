#!/bin/sh
. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi


busDomainId=$1
processTypeId=$2
processId=$3
echo $0
#creating flume command for
$flumeLibDir/bin/flume-ng agent --conf $flumeLibDir/conf/ -f $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/flume-$processId.conf -Dflume.root.logger=DEBUG,console -n agent$processId
