#!/bin/sh

# Script for invoking the Ant bundled with jakarta-avalon, from Avalon
# projects. Location-independent; will run build.xml in whichever directory
# you're in.

BASE=`dirname $0`  # Directory containing this script. Not the same as $PWD.
                   # The rest of this script assumes it's in a directory
                   # at the level of jakarta-avalon

LOCAL_AVALON_TOOLS=$AVALON_TOOLS

if [ "$AVALON_TOOLS" = "" ] ; then
    # Absolutize directory; it's not safe to pass relative dirs to buildfiles
    MAYBE_TOOLS=$PWD/$BASE/../jakarta-avalon/tools 
    if [ -d $MAYBE_TOOLS ] ; then
        LOCAL_AVALON_TOOLS=$MAYBE_TOOLS
    elif [ -d tools ] ; then
        LOCAL_AVALON_TOOLS=$PWD/$BASE/tools
    else
        echo "Unable to locate tools directory at "
        echo "$MAYBE_TOOLS or $BASE/tools/. "
        echo "Aborting."
        exit 1
    fi
fi

chmod u+x $LOCAL_AVALON_TOOLS/bin/antRun
chmod u+x $LOCAL_AVALON_TOOLS/bin/ant

$LOCAL_AVALON_TOOLS/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs -Dtools.dir=$LOCAL_AVALON_TOOLS $@
