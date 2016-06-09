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
#28, 'Crawler Parent', null
#37, 'Analytic UI', null
#39, 'Super Worklfow', null


BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ] || [ -z "$5" ]; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3
userName=$4
scriptName=$5
echo "busDomainId=$1 , processTypeId=$2 , processId=$3 userName=$4 scriptName=$5"
if [ $scriptName -eq Workflow.py ]; then
    python $(dirname $0)/Workflow.py $busDomainId $processTypeId $processId
else
    sh $(dirname $0)/$scriptName $busDomainId $processTypeId $processId $userName
fi

