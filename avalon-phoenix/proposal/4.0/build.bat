@echo off

echo -------------------
echo Avalon Build System
echo -------------------

set _ANT_HOME=%ANT_HOME%
set ANT_HOME=tools

set CP=%CLASSPATH%
set CLASSPATH=lib\xerces.jar;lib\oracle.jar

:runAnt
%ANT_HOME%\bin\ant.bat -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8

set ANT_HOME=%_ANT_HOME%
set _ANT_HOME=

set CLASSPATH=%CP%
