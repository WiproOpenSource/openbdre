#!/bin/sh
. $(dirname $0)/../env.properties
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
AIRFLOW_INSTALL_DIR=$airflowInstallDir

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3
userName=$4
dag="dag_"${busDomainId}_${processTypeId}_${processId}
echo "busDomainId=$1 , processTypeId=$2 , processId=$3 userName=$4"
if [ $processTypeId -eq 1 ]; then
    sh $(dirname $0)/flume.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 2 ]; then
    $AIRFLOW_INSTALL_DIR/airflow backfill $dag -s `date +%Y-%m-%dT%T`
elif [ $processTypeId -eq 4 ]; then
    airflow backfill $dag -s `date +%Y-%m-%dT%T`
elif [ $processTypeId -eq 5 ]; then
    airflow backfill $dag -s `date +%Y-%m-%dT%T`
elif [ $processTypeId -eq 18 ]; then
    airflow backfill $dag -s `date +%Y-%m-%dT%T`
elif [ $processTypeId -eq 19 ]; then
    airflow backfill $dag -s `date +%Y-%m-%dT%T`
elif [ $processTypeId -eq 28 ]; then
    airflow backfill $dag -s `date +%Y-%m-%dT%T`
elif [ $processTypeId -eq 31 ]; then
        python $(dirname $0)/Workflow.py $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 26 ]; then
    sh $(dirname $0)/filemonitor.sh $processId $userName
elif [ $processTypeId -eq 39 ]; then
    python $(dirname $0)/Workflow.py $busDomainId $processTypeId $processId
else
    echo "Don't know how to execute busDomainId=$1 , processTypeId=$2 , processId=$3 by userName=$4"
fi

