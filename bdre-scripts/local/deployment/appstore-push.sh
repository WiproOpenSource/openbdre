#!/bin/sh

. $(dirname $0)/../env.properties
BDRE_HOME=~/bdre
BDRE_APPS_HOME=~/bdre_apps
echo "$1"
echo "$2"
echo "$3"
echo "$4"
if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ]; then
        echo Insufficient parameters !
        exit 1
fi
mkdir -p ~/bdreappstore/$2/$3
if [ $? -ne 0 ]
then exit 2
fi

cp $BDRE_HOME-wfd/$1/* ~/bdreappstore/$2/$3
if [ $? -ne 0 ]
then exit 3
fi

cd ~/bdreappstore
git add $2/$3
if [ $? -ne 0 ]
then exit 4
fi

git add store.json
if [ $? -ne 0 ]
then exit 5
fi

git commit -m "adding new app"

git push $4  develop
if [ $? -ne 0 ]
    then exit 7
fi



