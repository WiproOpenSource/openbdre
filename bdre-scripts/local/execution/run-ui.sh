#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

java -jar  $BDRE_HOME/lib/webapps/jetty-runner.jar --port 28850 --path /mdui $BDRE_HOME/lib/webapps/md-ui-$bdreVersion.war --path /mdrest $BDRE_HOME/lib/webapps/md-rest-api-$bdreVersion.war --path /auth  $BDRE_HOME/lib/webapps/auth-rest-api-$bdreVersion.war