#!/usr/bin/env bash

BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

java -cp "$BDRE_HOME/lib/process-deploy/*" com.wipro.ats.bdre.pdeploy.ProcessDeployMain --num 5
