#!/bin/sh
#

DEST=target/classes
DIST=target/dist

if [ -z $MAVEN_LOCAL_HOME ] ; then
    MAVEN_LOCAL_HOME=$HOME/.maven
fi

REPO=$MAVEN_LOCAL_HOME/repository

CP=$REPO/avalon-framework/jars/avalon-framework-api-4.2.0.jar
CP=$CP:$REPO/avalon-framework/jars/avalon-framework-impl-4.2.0.jar
CP=$CP:$REPO/bsh/jars/bsh-2.0b1.jar

if [ $ANT_HOME ] ; then
    CP=$CP:$ANT_HOME/lib/ant.jar
else
    CP=$CP:$REPO/ant/jars/ant-1.6.jar
fi

SRC=`find src/java -name "*.java"`
mkdir -p $DEST
javac -d $DEST -classpath $CP $SRC
RESULT="$#"

if [ $RESULT = 0 ] ; then
    cd $DEST
    jar cf magic.jar *
    RESULT=$#
    cd -
fi

if [ $RESULT = 0 ] ; then
    for FILE in `find src/dist -type f | grep -v .svn` ; do
        BASEDIR=`dirname $FILE`
        TODIR=`echo $DIST$BASEDIR/ | sed 's/src\/dist//'`
        mkdir -p $TODIR
        cp $FILE $TODIR
    done
    cp $REPO/avalon-framework/jars/avalon-framework-api-4.2.0.jar $DIST/bin
    cp $REPO/avalon-framework/jars/avalon-framework-impl-4.2.0.jar $DIST/bin
    cp $REPO/bsh/jars/bsh-2.0b1.jar $DIST/bin
    if [ $ANT_HOME ] ; then
        cp $ANT_HOME/lib/ant.jar $DIST/bin
    else
        cp $REPO/ant/jars/ant-1.6.jar  $DIST/bin
    fi
    cp $DEST/magic.jar $DIST/bin
    chmod +x $DIST/bin/magic
fi

