#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_WFD=~/bdre-wfd
BDRE_HOME=~/bdre
BDRE_REMOTE_HOME=~$edgeNodeUserName/bdre
EDGE_NODE_URL=$edgeNodeUserName@$edgeNodeHostName

#1,'ingestion',null
#2, 'Semantic', null
#3, 'Export',null
#4, 'Data Extraction', null
#5, 'Hive Data Load', null
#15, 'File Registration', null
#18, 'Hive Generation_Parent', null
#19, 'DQ_Parent', null
#26,'Filemon Parent',null
#39,'SuperWorkflow Parent',null


if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ]; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3
userName=$4

if [ $processTypeId -eq 2 ]; then
    current_dir=`pwd`
    echo "current dir: $current_dir"
    cd $BDRE_WFD
    tar -czf userfile-$processId.tar.gz $processId
    scp userfile-$processId.tar.gz $EDGE_NODE_URL:$BDRE_REMOTE_HOME/tmp
    rm -r userfile-$processId.tar.gz
    cd $current_dir
fi

ssh $EDGE_NODE_URL sh $BDRE_REMOTE_HOME/bdre-scripts/deployment/process-type-$processTypeId.sh $busDomainId $processTypeId $processId $userName