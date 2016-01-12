#!/bin/sh

BDRE_APPS_HOME=~/bdre_apps


if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3

curl -i -k -u biadmin:ddp@bluemix -H Content-Type:application/xml -T $BDRE_APPS_HOME/$busDomainId/$processTypeId/$processId/job-$processId.xml -X POST 'https://ehaasp-550-mastermanager.bi.services.bluemix.net:8443/gateway/default/oozie/v1/jobs?action=start'