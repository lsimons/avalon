#!/bin/sh

echo
echo "Phoenix Build System"
echo "--------------------"

export CP=$CLASSPATH
export CLASSPATH=lib/xerces_1_2_3.jar

chmod u+x ./tools/bin/antRun
chmod u+x ./tools/bin/ant

unset ANT_HOME

$PWD/tools/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs $@ 

export CLASSPATH=$CP