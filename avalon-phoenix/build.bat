@echo off

echo --------------------
echo Phoenix Build System
echo --------------------

set ANT_HOME=tools

set CP=%CLASSPATH%
set CLASSPATH=lib\xerces_1_2_3.jar

%ANT_HOME%\bin\ant.bat -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8

set CLASSPATH=%CP%
