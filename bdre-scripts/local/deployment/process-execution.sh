#!/bin/bash

BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

new_date=`date +"%s"`

. $(dirname $0)/../env.properties
echo "java -cp "$BDRE_HOME/lib/process-deploy/*" com.wipro.ats.bdre.pdeploy.ProcessExecutionMain >> $logDir/process-execution$new_date.log 2>> $logDir/process-execution-error$new_date.log"
java -cp "$BDRE_HOME/lib/process-deploy/*" com.wipro.ats.bdre.pdeploy.ProcessExecutionMain >> $logDir/process-execution$new_date.log 2>> $logDir/process-execution-error$new_date.log
echo "java -cp "$BDRE_HOME/lib/process-deploy/*" com.wipro.ats.bdre.pdeploy.ProcessExecutionMain >> $logDir/process-execution$new_date.log 2>> $logDir/process-execution-error$new_date.log"
find $logDir/process-deploy* -mmin +60 -delete

