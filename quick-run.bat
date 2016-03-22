java -jar md-rest-api/target/dependency/jetty-runner.jar --port 28850 ^
  --classes md-rest-api/target/classes ^
  --classes md-ui/target/classes ^
  --classes auth-rest-api/target/classes ^
  --lib auth-rest-api/target/auth-rest-api-1.1-SNAPSHOT/WEB-INF/lib ^
  --lib md-rest-api/target/md-rest-api-1.1-SNAPSHOT/WEB-INF/lib ^
  --lib md-ui/target/md-ui-1.1-SNAPSHOT/WEB-INF/lib  ^
  auth-rest-api/context.xml ^
  md-rest-api/context.xml ^
  md-ui/context.xml ^
  appstore-context.xml

