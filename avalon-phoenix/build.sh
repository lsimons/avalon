#!/bin/sh

echo
echo "Phoenix Build System"
echo "--------------------"

if [ "$AVALON_TOOLS" = "" ] ; then
    AVALON_TOOLS=../jakarta-avalon/tools
fi

chmod u+x $AVALON_TOOLS/bin/antRun
chmod u+x $AVALON_TOOLS/bin/ant

$AVALON_TOOLS/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs -Dtools.dir=$AVALON_TOOLS $@ 