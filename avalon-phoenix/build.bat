@echo off

echo ------------
echo Build System
echo ------------

if not "%AVALON_TOOLS%"=="" goto runAnt

if exist "..\jakarta-avalon\tools\bin\ant.bat" set AVALON_TOOLS=..\jakarta-avalon\tools
if exist "tools\bin\ant.bat" set AVALON_TOOLS=tools

if not "%AVALON_TOOLS%"=="" goto runAnt

echo "Unable to locate tools directory at "
echo "../jakarta-avalon/tools/ or tools/. "
echo "Aborting."
goto end

:runAnt
set ANT_HOME=%AVALON_TOOLS%
%AVALON_TOOLS%\bin\ant.bat -logger org.apache.tools.ant.NoBannerLogger -emacs -Dtools.dir=%AVALON_TOOLS% -Dxerces.jar=%AVALON_TOOLS%\lib\xerces.jar %1 %2 %3 %4 %5 %6 %7 %8

:end
