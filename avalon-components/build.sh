#!/bin/sh

echo
echo "Cornerstone Build System"
echo "------------------------"

unset ANT_HOME

chmod u+x ./tools/bin/antRun
chmod u+x ./tools/bin/ant

./tools/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs $@ 
