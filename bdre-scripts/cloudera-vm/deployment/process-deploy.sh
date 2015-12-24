#!/usr/bin/env bash
source ./deploy-env.properties
java -cp $pathForProcessDeployJar/process-deploy-$bdreVersion.jar:$pathForAllMDJars/*  com.wipro.ats.bdre.pdeploy.ProcessDeployMain --num 5
