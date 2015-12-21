#!/bin/sh

java -jar target/dependency/jetty-runner.jar \
--lib ../eventing-fwk/target/eventing-fwk-1.1-SNAPSHOT/WEB-INF/lib \
 --classes ../eventing-fwk/target/classes \
  ../eventing-fwk/context.xml