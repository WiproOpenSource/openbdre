#!/usr/bin/env bash

if [ $# -ne 1 ]
  then
    echo "usage $0 <local|remote>"
    exit 1
fi

BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

rm -f -r $BDRE_HOME
mkdir -p $BDRE_HOME/bdre-scripts
mkdir -p $BDRE_HOME/lib
mkdir -p $BDRE_HOME_apps
mkdir $BDRE_HOME-wfd

cp -f -r bdre-scripts/$1/* $BDRE_HOME/bdre-scripts

cp -r -f target/lib/* $BDRE_HOME/lib
java -cp "target/lib/genconf-dump/*" com.wipro.ats.bdre.md.util.DumpConfigMain -cg scripts_config -f $BDRE_HOME/bdre-scripts/env.properties


#Install crontab for deployment daemon * * * * * - every min
(crontab -l ; echo "* * * * * sh $BDRE_HOME/bdre-scripts/deployment/process-deploy.sh") 2>&1 | grep -v "no crontab" | sort | uniq | crontab -