@echo off

if exist "tools\bin\ant.bat" set LOCAL_AVALON_TOOLS=tools
if exist "..\jakarta-avalon\tools\bin\ant.bat" set LOCAL_AVALON_TOOLS=..\jakarta-avalon\tools
if not "%AVALON_TOOLS%"=="" set LOCAL_AVALON_TOOLS=%AVALON_TOOLS%

if not "%LOCAL_AVALON_TOOLS%"=="" goto buildcp

echo "Unable to locate tools directory at "
echo "../jakarta-avalon/tools/ or tools/. "
echo "Aborting."
goto end

:buildcp
set _CP=./build/scratchpad;./build/classes;./build/testclasses;%CLASSPATH%
set _LIBJARS=
for %%i in (%LOCAL_AVALON_TOOLS%\ext\*.jar) do call cpappend.bat %%i
for %%i in (%LOCAL_AVALON_TOOLS%\lib\*.jar) do call cpappend.bat %%i
for %%i in (lib\*.jar) do call cpappend.bat %%i
if not "%_LIBJARS%" == "" goto run

echo Unable to set CLASSPATH dynamically.
goto end

:run
set _CP=%_CP%%_LIBJARS%

java -classpath "%_CP%" %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
set LOCAL_AVALON_TOOLS=
set _LIBJARS=
set _CP=

