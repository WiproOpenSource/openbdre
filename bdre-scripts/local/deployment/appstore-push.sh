#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps

mkdir ~/bdreappstore/$2/$3
cp $BDRE_HOME-wfd/$1/* ~/bdreappstore/$2/$3
cd ~/bdreappstore
git add ~/bdreappstore/$2/$3
git commit -m "adding new app"
git push origin develop



