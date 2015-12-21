
cd

java -jar target/dependency/jetty-runner.jar --lib ../md-ui/target/md-ui/WEB-INF/lib --lib ../md-rest-api/target/md-rest-api-1.1-SNAPSHOT/WEB-INF/lib --classes ../md-ui/target/md-ui/WEB-INF/classes --classes ../md-rest-api/target/md-rest-api-1.1-SNAPSHOT/WEB-INF/classes ../md-ui/context.xml ../md-rest-api/context.xml