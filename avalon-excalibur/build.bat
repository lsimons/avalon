@echo off

if exist "tools\bin\ant.bat" set LOCAL_AVALON_TOOLS=tools
if exist "..\jakarta-avalon\tools\bin\ant.bat" set LOCAL_AVALON_TOOLS=..\jakarta-avalon\tools
if not "%AVALON_TOOLS%"=="" set LOCAL_AVALON_TOOLS=%AVALON_TOOLS%

if not "%LOCAL_AVALON_TOOLS%"=="" goto runAnt

echo "Unable to locate tools directory at "
echo "../jakarta-avalon/tools/ or tools/. "
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
