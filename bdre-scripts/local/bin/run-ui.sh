#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
LOG_FILE_OPTION="--out $1"
if [ -z "$1" ]; then
        echo Log file path not provided.
        LOG_FILE_OPTION=""
fi
echo "writing logs to $1"

java -jar  $BDRE_HOME/lib/webapps/jetty-runner.jar $LOG_FILE_OPTION --port 28850 --path /mdui $BDRE_HOME/lib/webapps/md-ui-$bdreVersion.war --path /mdrest $BDRE_HOME/lib/webapps/md-rest-api-$bdreVersion.war --path /auth  $BDRE_HOME/lib/webapps/auth-rest-api-$bdreVersion.war &
