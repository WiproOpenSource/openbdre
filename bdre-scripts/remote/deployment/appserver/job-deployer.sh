#!/bin/sh

BDRE_WFD=~/bdre-wfd

#1,'ingestion',null
#2, 'Semantic', null
#3, 'Export',null
#4, 'Data Extraction', null
#5, 'Hive Data Load', null
#15, 'File Registration', null
#18, 'Hive Generation_Parent', null
#19, 'DQ_Parent', null
#26,'Filemon Parent',null


if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] ; then
        echo Insufficient parameters !
        exit 1
fi

busDomainId=$1
processTypeId=$2
processId=$3

if [ $processTypeId -eq 2 ]; then
    pushd $BDRE_WFD/$processId
    tar -czf userfile-$processId.tar.gz .
    scp userfile-$processId.tar.gz biadmin@169.55.78.217:/home/biadmin/bdre/tmp
    rm -r userfile-$processId.tar.gz
    popd
fi

ssh biadmin@169.55.78.217 sh /home/biadmin/bdre/bdre-scripts/deployment/process-type-$processTypeId.sh $busDomainId $processTypeId $processId