#!/bin/bash

BDRE_HOME=~/bdre

while true ; do
 $BDRE_HOME/bdre-scripts/deployment/process-deploy.sh
 sleep 1m;
done