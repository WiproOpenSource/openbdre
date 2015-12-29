#!/bin/sh

#1,'ingestion',null
#2, 'Semantic', null
#3, 'Export',null
#4, 'Data Extraction', null
#5, 'Hive Data Load', null
#15, 'File Registration', null
#18, 'Hive Generation_Parent', null
#19, 'DQ_Parent', null
#26,'Filemon Parent',null

BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3

sh $(dirname $0)/process-type-$processTypeId.sh $busDomainId $processTypeId $processId