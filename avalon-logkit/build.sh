#!/bin/sh

echo
echo "Phoenix Build System"
echo "--------------------"

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

chmod u+x $LOCAL_AVALON_TOOLS/bin/antRun
chmod u+x $LOCAL_AVALON_TOOLS/bin/ant

$LOCAL_AVALON_TOOLS/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs -Dtools.dir=$LOCAL_AVALON_TOOLS $@
