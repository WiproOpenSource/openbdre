#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

echo "writing logs to $1 and pid file is $2"

java -jar  -XX:MaxPermSize=2048m $BDRE_HOME/lib/webapps/jetty-runner.jar --out $1 --port 28850 --path /mdui $BDRE_HOME/lib/webapps/md-ui-$bdreVersion.war --path /mdrest $BDRE_HOME/lib/webapps/md-rest-api-$bdreVersion.war --path /auth  $BDRE_HOME/lib/webapps/auth-rest-api-$bdreVersion.war --path /store $BDRE_HOME/bdre-scripts/appstore-context.xml &
echo $! > $2
