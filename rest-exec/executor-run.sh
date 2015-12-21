#!/bin/sh

java -jar target/dependency/jetty-runner.jar --port 7777 --stop-port 7778 --stop-key bdre123 \
--lib ../rest-exec/target/rest-exec-1.1-SNAPSHOT/WEB-INF/lib \
 --classes ../rest-exec/target/classes \
  ../rest-exec/context.xml