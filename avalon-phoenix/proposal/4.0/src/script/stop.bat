@echo off
rem
rem Phoenix shutdown script.
rem
rem Author: Leo Simons [mail@leosimons.com]

echo THIS DOES _NOT_ WORK YET! Sorry.
goto end

rem
rem Determine if JAVA_HOME is set and if so then use it
rem
if not "%JAVA_HOME%"=="" goto found_java

set PHOENIX_JAVACMD=java
goto file_locate

:found_java
set PHOENIX_JAVACMD=%JAVA_HOME%\bin\java

:file_locate
set PHOENIX_HOME=..\

rem
rem Locate where phoenix is in filesystem
rem
if not "%OS%"=="Windows_NT" goto start

rem %~dp0 is name of current script under NT
set PHOENIX_HOME=%~dp0

rem : operator works similar to make : operator
set PHOENIX_HOME=%PHOENIX_HOME:\bin\=%

:start

if not "%PHOENIX_HOME%" == "" goto phoenix_home

echo.
echo Warning: PHOENIX_HOME environment variable is not set.
echo   This needs to be set for Win9x as it's command prompt 
echo   scripting bites
echo.
goto end

:phoenix_home

rem echo "Home directory: %PHOENIX_HOME%"
rem echo "Home ext directory: %PHOENIX_HOME%\lib"

rem
rem This is needed as some JVM vendors do foolish things
rem like placing jaxp/jaas/xml-parser jars in ext dir
rem thus breaking Phoenix
rem

rem figure out the machine name...
if not "%1" == "" goto run_phoenix

echo.
echo Warning: you must supply the network name of your
echo   computer as an argument. This is needed to be able
echo   to talk to the RMI Registry.
echo.
goto end

:run_phoenix

rem Kicking the tires and lighting the fires!!!

%PHOENIX_JAVACMD% -Djava.ext.dirs=%PHOENIX_HOME%\lib -Dcomputer.name=%1 %PHOENIX_JVM_OPTS% -cp %PHOENIX_HOME%\lib\jmx.jar -jar phoenix-shutdown.jar %2 %3 %4 %5 %6 %7 %8 %9

:end