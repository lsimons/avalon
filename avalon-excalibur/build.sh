#!/bin/sh

echo
echo "Avalon/Excalibur Build System"
echo "-----------------------------"

if [ "$AVALON_TOOLS" = "" ] ; then
    if [ -d ../jakarta-avalon/tools ] ; then 
        AVALON_TOOLS=../jakarta-avalon/tools
    elif [ -d tools ] ; then 
        AVALON_TOOLS=tools
    else
        echo "Unable to locate tools directory at "
        echo "../jakarta-avalon/tools/ or tools/. "
        echo "Aborting."
        exit 1
    fi
fi

chmod u+x $AVALON_TOOLS/bin/antRun
chmod u+x $AVALON_TOOLS/bin/ant

$AVALON_TOOLS/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs -Dtools.dir=$AVALON_TOOLS $@ 
