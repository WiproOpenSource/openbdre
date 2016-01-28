#!/bin/sh

. $(dirname $0)/../env.properties

BDRE_APPS_HOME=~/bdre_apps
BDRE_REMOTE_HOME=~$edgeNodeUserName/bdre
EDGE_NODE_USER_NAME=$edgeNodeUserName
EDGE_NODE_PASSWORD=$edgeNodePassword

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3

curl -i -k -u $EDGE_NODE_USER_NAME:$EDGE_NODE_PASSWORD -H Content-Type:application/xml -T $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml -X POST 'https://$oozieHost:$ooziePort$oozieUrl/v1/jobs?action=start' > $logDir/oozie-$processId.log 2> $logDir/oozie-error-$processId.log