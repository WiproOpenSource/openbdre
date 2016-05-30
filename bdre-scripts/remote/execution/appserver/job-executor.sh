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
#37, 'Super Workflow', null

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
BDRE_REMOTE_HOME=~$edgeNodeUserName/bdre
BDRE_REMOTE_EXECUTION_DIR=$BDRE_REMOTE_HOME/bdre-scripts/execution
EDGE_NODE_URL=$edgeNodeUserName@$edgeNodeHostName

if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3
userName=$4
echo "busDomainId=$1 , processTypeId=$2 , processId=$3 userName=$4"
if [ $processTypeId -eq 1 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/flume.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 2 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 4 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 5 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 18 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 19 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 28 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/workflow.sh $busDomainId $processTypeId $processId
elif [ $processTypeId -eq 26 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/filemonitor.sh $processId $userName
elif [ $processTypeId -eq 37 ]; then
    ssh $EDGE_NODE_URL sh $BDRE_REMOTE_EXECUTION_DIR/workflow.sh $busDomainId $processTypeId $processId

else
    echo "Don't know how to execute busDomainId=$1 , processTypeId=$2 , processId=$3"
fi

