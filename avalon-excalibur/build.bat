@echo off

rem Script to invoke the Ant that comes with jakarta-avalon
rem
rem Tries to guess location of jakarta-avalon\tools directory containing Ant.
rem To specify this directly, set the AVALON_TOOLS variable, eg:
rem set AVALON_TOOLS=c:\Jakarta\jakarta-avalon
rem
rem This can also be called from other batch scripts. If so, the BASE variable
rem should be preset to the path from the caller to this script (generally
rem '..\'s)


if "%BASE%"=="" set BASE=.
if exist "%BASE%\tools\bin\ant.bat" set LOCAL_AVALON_TOOLS=tools
if exist "%BASE%\..\jakarta-avalon\tools\bin\ant.bat" set LOCAL_AVALON_TOOLS=%BASE%\..\jakarta-avalon\tools
if exist "%BASE%\..\..\jakarta-avalon\tools\bin\ant.bat" set LOCAL_AVALON_TOOLS=%BASE%\..\..\jakarta-avalon\tools
if not "%AVALON_TOOLS%"=="" set LOCAL_AVALON_TOOLS=%AVALON_TOOLS%

if not "%LOCAL_AVALON_TOOLS%"=="" goto runAnt

echo "Unable to locate tools directory at "
echo "..\jakarta-avalon\tools or ..\..\jakarta-avalon\tools or tools\. "
echo "Aborting."
goto end

:runAnt
set OLD_ANT_HOME=%ANT_HOME%
set ANT_HOME=%LOCAL_AVALON_TOOLS%
%LOCAL_AVALON_TOOLS%\bin\ant.bat -logger org.apache.tools.ant.NoBannerLogger -emacs -Dtools.dir=%LOCAL_AVALON_TOOLS% %1 %2 %3 %4 %5 %6 %7 %8
set ANT_HOME=%OLD_ANT_HOME%
set OLD_ANT_HOME=

:end
set LOCAL_AVALON_TOOLS=
set BASE=
