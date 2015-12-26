#!/usr/bin/env bash

if [ $# -ne 1 ]
  then
    echo "usage $0 <local|remote>"
    exit 1
fi


rm -f -r ~/bdre
mkdir -p ~/bdre/bdre-scripts
mkdir -p ~/bdre/lib
mkdir -p ~/bdre_apps

cp -f -r bdre-scripts/$1/* ~/bdre/bdre-scripts

cp -r -f target/lib/* ~/bdre/lib
java -cp "target/lib/genconf-dump/*" com.wipro.ats.bdre.md.util.DumpConfigMain -cg scripts_config -f ~/bdre/bdre-scripts/env.properties