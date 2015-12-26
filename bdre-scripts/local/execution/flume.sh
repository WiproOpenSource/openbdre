#!/bin/sh
pathForFlumeng=/usr/lib/flume-ng/bin
pathForFlumeconf=/usr/lib/flume-ng
pathForFlumeconfFile=../../..
if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3
echo $0
#creating flume command for
$pathForFlumeng/flume-ng agent --conf $pathForFlumeconf/conf/ -f $pathForFlumeconfFile/BDRE/$busDomainId/$processTypeId/$processId/flume-$processId.conf -Dflume.root.logger=DEBUG,console -n agent$processId
