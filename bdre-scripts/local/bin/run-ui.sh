#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

echo "writing logs to $1"

java -jar  $BDRE_HOME/lib/webapps/jetty-runner.jar -o $1 --port 28850 --path /mdui $BDRE_HOME/lib/webapps/md-ui-$bdreVersion.war --path /mdrest $BDRE_HOME/lib/webapps/md-rest-api-$bdreVersion.war --path /auth  $BDRE_HOME/lib/webapps/auth-rest-api-$bdreVersion.war &
echo $! > $2
