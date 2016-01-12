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
BIADMIN_DIRNAME=/home/biadmin/bdre/bdre-scripts/execution

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3
echo "busDomainId=$1 , processTypeId=$2 , processId=$3"
if [ $processTypeId -eq 1 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/flume.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 2 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 4 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 5 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 18 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 19 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 28 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 26 ]; then
    ssh biadmin@169.55.78.217 sh $BIADMIN_DIRNAME/filemonitor.sh $processId
else
    echo "Don't know how to execute busDomainId=$1 , processTypeId=$2 , processId=$3"
fi

