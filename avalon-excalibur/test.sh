#!/bin/sh

LOCAL_AVALON_TOOLS=$AVALON_TOOLS

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

CLASSPATH="./build/scratchpad/;./build/classes/;./build/testclasses/;$CLASSPATH"

for i in $LOCAL_AVALON_TOOLS/ext/*.jar; do
    CLASSPATH="$CLASSPATH;$i"
done

for i in $LOCAL_AVALON_TOOLS/lib/*.jar; do
    CLASSPATH="$CLASSPATH;$i"
done

for i in lib/*.jar; do
    CLASSPATH="$CLASSPATH;$i"
done

java -classpath $CLASSPATH $@

