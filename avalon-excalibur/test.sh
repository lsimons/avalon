#!/bin/sh

LOCAL_AVALON_TOOLS=$AVALON_TOOLS

S=":"

if [ "$TERM" = "dumb" ] ; then
    S=";"
fi

if [ "$TERM" = "cygwin" ] ; then
    S=";"
fi

if [ "$AVALON_TOOLS" = "" ] ; then
    if [ -d ../jakarta-avalon/tools ] ; then
        LOCAL_AVALON_TOOLS=../jakarta-avalon/tools
    elif [ -d tools ] ; then
        LOCAL_AVALON_TOOLS=tools
    else
        echo "Unable to locate tools directory at "
        echo "../jakarta-avalon/tools/ or tools/. "
        echo "Aborting."
        exit 1
    fi
fi

CLASSPATH="./build/scratchpad/$S./build/classes/$S./build/testclasses/$S$CLASSPATH"

for i in $LOCAL_AVALON_TOOLS/ext/*.jar; do
    CLASSPATH="$CLASSPATH$S$i"
done

for i in $LOCAL_AVALON_TOOLS/lib/*.jar; do
    CLASSPATH="$CLASSPATH$S$i"
done

for i in lib/*.jar; do
    CLASSPATH="$CLASSPATH$S$i"
done

java -classpath $CLASSPATH $@

