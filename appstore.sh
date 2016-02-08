#!/usr/bin/env bash
BDRE_HOME=~/bdre
. $BDRE_HOME/bdre-scripts/env.properties
BDRE_APPSTORE_REPO=~/bdreappstore-repo
BDRE_APPSTORE_APPS=~/bdreappstore-apps
rm -f -r $BDRE_APPSTORE_APPS
mkdir -p $BDRE_APPSTORE_APPS
#Pull latest apps from the repo
if [ -d "$BDRE_APPSTORE_REPO" ]; then
    echo "refresing repo"
    cd $BDRE_APPSTORE_REPO
    git pull $repoName $branchName
    if [ $? -ne 0 ]
        then exit 1
    fi
else
    echo "cloning repo for first time"
    cd ~
    git clone $gitURL
    if [ $? -ne 0 ]
        then exit 1
    fi
    cd $BDRE_APPSTORE_REPO
fi
for d in */ ; do
    echo "checking $d related apps"
    cd "$d"
    for sd in */ ; do
        echo "archiving $sd app"
        cd "$sd"
        zip -r -X ../"${sd%?}" *
        if [ $? -ne 0 ]
            then exit 1
        fi
        echo "archiving done"
        cd ..
    done
    mkdir -p $BDRE_APPSTORE_APPS/"$d"
    rm -f $BDRE_APPSTORE_APPS/"$d"/*.zip
    mv ./*.zip $BDRE_APPSTORE_APPS/"$d"
    cd ../
done
