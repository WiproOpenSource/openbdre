#!/bin/bash

BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

new_date=`date +"%s"`

. $(dirname $0)/../env.properties


java -cp "$BDRE_HOME/lib/process-deploy/*" com.wipro.ats.bdre.pdeploy.ProcessDeployMain --num 5 >> $logDir/process-deploy$new_date.log 2>> $logDir/process-deploy-error$new_date.log

find $logDir/process-deploy/* -mmin +60 -delete
