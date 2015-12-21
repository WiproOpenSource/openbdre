#!/bin/sh
hadoop fs -rm -r $1
zip genconf.jar config.properties table.properties data.properties
hadoop jar hive-data-gen-1.1-SNAPSHOT-jar-with-dependencies.jar -libjars genconf.jar $1