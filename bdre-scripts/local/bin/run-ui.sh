#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

echo "writing logs to $1 and pid file is $2"

java -jar -XX:MaxPermSize=2048m $BDRE_HOME/lib/webapps/jetty-runner.jar --out $1 --port 28850 --path /mdui $BDRE_HOME/lib/webapps/mdui --path /mdrest $BDRE_HOME/lib/webapps/mdrest --path /auth  $BDRE_HOME/lib/webapps/auth --path /aui  $BDRE_HOME/lib/webapps/analyticui --path /store $BDRE_HOME/bdre-scripts/appstore-context.xml &
echo $! > $2
