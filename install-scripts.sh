#!/usr/bin/env bash

if [ $# -ne 1 ]
  then
    echo "usage $0 <local|remote>"
    exit 1
fi

if [ "$1" != "local" ] && [ "$1" != "remote" ]
    then
        echo "usage $0 <local|remote>"
        exit;
fi

BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

rm -f -r $BDRE_HOME
mkdir -p $BDRE_HOME/bdre-scripts
mkdir -p $BDRE_HOME/lib
mkdir -p $BDRE_HOME/airflow
mkdir -p $BDRE_APPS_HOME
mkdir -p $BDRE_HOME-wfd

BDRE_CODE=`pwd`


#Stop BDRE if running
sudo service bdre stop
cp -f -r bdre-scripts/$1/* $BDRE_HOME/bdre-scripts
cp -f -r appstore-context.xml $BDRE_HOME/bdre-scripts
cp -f -r bdre-scripts/hql $BDRE_HOME/bdre-scripts

codeDir=`pwd`
echo $codeDir

cp -r -f target/lib/* $BDRE_HOME/lib
cd $BDRE_HOME/lib/webapps
mkdir analyticui
cd analyticui
jar -xvf ../analytic-ui-1.1-SNAPSHOT.war
cd ..
mkdir auth
cd auth
jar -xvf ../auth-rest-api-1.1-SNAPSHOT.war
cd ..
mkdir mdrest
cd mdrest
jar -xvf ../md-rest-api-1.1-SNAPSHOT.war
cd ..
mkdir mdui
cd mdui
jar -xvf ../md-ui-1.1-SNAPSHOT.war

cd $codeDir

java -cp "target/lib/genconf-dump/*" com.wipro.ats.bdre.md.util.DumpConfigMain -cg scripts_config -f $BDRE_HOME/bdre-scripts/env.properties
. $BDRE_HOME/bdre-scripts/env.properties

#Adding sudo because its a non user dir
sudo mkdir -p $flumeLibDir/plugins.d/bdre-hdfs/lib
sudo cp target/lib/flume-hdfs-sink/* $flumeLibDir/plugins.d/bdre-hdfs/lib

chmod +x $BDRE_HOME/bdre-scripts/deployment/*
chmod +x $BDRE_HOME/bdre-scripts/execution/*
chmod +x $BDRE_HOME/bdre-scripts/bin/*

#Install crontab for deployment daemon * * * * * - every min
echo " installing crontab for $BDRE_HOME/bdre-scripts/deployment/process-deploy.sh"
(crontab -l ; echo "* * * * * $BDRE_HOME/bdre-scripts/deployment/process-deploy.sh") 2>&1 | grep -v "no crontab" | sort | uniq | crontab -


#Create log dir
bdre_user=`whoami`
sudo mkdir /var/log/BDRE
sudo chown $bdre_user:$bdre_user /var/log/BDRE


#Update java softlink
right_java=`which java`
sudo ln -s -f $right_java /usr/bin/java

#add bdre as a service
sed s/%USER%/$bdre_user/ $BDRE_HOME/bdre-scripts/bin/bdre > bdre.service
sudo mv bdre.service /etc/init.d/bdre
sudo chmod +x /etc/init.d/bdre
#Making bdre autostart
sudo chkconfig bdre on

cd $BDRE_HOME
if [[ ! -f $flumeLibDir/plugins.d/twitter/lib/flume-sources-1.0-SNAPSHOT.jar || ! -f $BDRE_HOME/lib/hive-serdes-1.0-SNAPSHOT.jar ]] ; then
  echo $flumeLibDir/plugins.d/twitter/lib/flume-sources-1.0-SNAPSHOT.jar or $BDRE_HOME/lib/hive-serdes-1.0-SNAPSHOT.jar not installed.
  git clone https://github.com/cloudera/cdh-twitter-example.git
  cd cdh-twitter-example/flume-sources
  mvn package
  sudo mkdir -p $flumeLibDir/plugins.d/twitter/lib
  sudo cp target/flume-sources-1.0-SNAPSHOT.jar $flumeLibDir/plugins.d/twitter/lib
  sudo cp target/flume-sources-1.0-SNAPSHOT.jar $BDRE_HOME/lib
  cd ../hive-serdes
  mvn package
  sudo cp target/hive-serdes-1.0-SNAPSHOT.jar $BDRE_HOME/lib
  echo "add jar $BDRE_HOME/lib/hive-serdes-1.0-SNAPSHOT.jar" > ~/.hiverc
  cd $BDRE_HOME
  rm -r -f cdh-twitter-example
fi

#Create usual hive DBs
hive -e "create database if not exists raw;create database if not exists base;"

#Add java in path
sudo ln -s `which java` /usr/sbin/java

cd $BDRE_CODE
sh appstore.sh
