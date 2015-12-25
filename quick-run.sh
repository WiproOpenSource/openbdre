#!/bin/sh

java -jar md-rest-api/target/dependency/jetty-runner.jar  --port 9999 \
--lib md-ui/target/md-ui/WEB-INF/lib \
--lib md-rest-api/target/md-rest-api-1.1-SNAPSHOT/WEB-INF/lib \
--lib auth-rest-api/target/auth-rest-api-1.1-SNAPSHOT/WEB-INF/lib \
 --classes auth-rest-api/target/classes \
 --classes md-ui/target/classes \
 --classes md-rest-api/target/classes \
           auth-rest-api/context.xml \
           md-ui/context.xml \
           md-rest-api/context.xml