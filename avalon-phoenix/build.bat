@echo off

echo --------------------
echo Phoenix Build System
echo --------------------

if not "%AVALON_TOOLS%"=="" goto runAnt
set AVALON_TOOLS=..\jakarta-avalon\tools

:runAnt
set ANT_HOME=%AVALON_TOOLS%
%AVALON_TOOLS%/bin/ant -logger org.apache.tools.ant.NoBannerLogger -emacs -Dtools.dir=%AVALON_TOOLS% %1 %2 %3 %4 %5 %6 %7 %8

