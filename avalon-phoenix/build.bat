@echo off

echo --------------------
echo Phoenix Build System
echo --------------------

set ANT_HOME=tools
set BUILD_FILE=build.xml

set LOCALCLASSPATH=
for %%i in (lib\*.jar) do call %ANT_HOME%\bin\lcp.bat %%i
set CLASSPATH=%LOCALCLASSPATH%
set LOCALCLASSPATH=

:runAnt
%ANT_HOME%\bin\ant.bat -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8
set BUILD_FILE=
set ANT_HOME=
set CLASSPATH=
