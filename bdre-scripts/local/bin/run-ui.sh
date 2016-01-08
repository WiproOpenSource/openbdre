#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
LOG_FILE="$1"
if [ -z "$1" ]; then
        echo Log file path not provided.
        LOG_FILE = "/dev/stdout"
fi
echo "writing logs to $LOG_FILE"

java -jar  $BDRE_HOME/lib/webapps/jetty-runner.jar --out $LOG_FILE --port 28850 --path /mdui $BDRE_HOME/lib/webapps/md-ui.war --path /mdrest $BDRE_HOME/lib/webapps/md-rest-api-$bdreVersion.war --path /auth  $BDRE_HOME/lib/webapps/auth-rest-api-$bdreVersion.war &
