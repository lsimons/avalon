#!/bin/sh

echo
echo "Phoenix Build System"
echo "--------------------"

export CLASSPATH=`echo $PWD/lib/*.jar | tr ' ' ':'`

chmod u+x ./tools/bin/antRun
chmod u+x ./tools/bin/ant

export PROPOSAL=""

unset ANT_HOME

if [ "$1" = "proposal" ]; then
    export PROPOSAL="-buildfile proposal/make/proposal.xml"
fi

$PWD/tools/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs $PROPOSAL $@ 
