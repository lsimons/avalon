@echo off
rem
rem Phoenix start script.
rem
rem Author: Peter Donald [donaldp@apache.org]
rem
rem The user may choose to supply parameters to the JVM (such as memory settings)
rem via setting the environment variable PHOENIX_JVM_OPTS
rem
rem The user may also disable the security manager by setting PHOENIX_SECURE=false
rem

rem
rem Determine if JAVA_HOME is set and if so then use it
rem
if not "%JAVA_HOME%"=="" goto found_java

set PHOENIX_JAVACMD=java
goto file_locate

:found_java
set PHOENIX_JAVACMD=%JAVA_HOME%\bin\java

:file_locate

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

set PHOENIX_SM=

if "%PHOENIX_SECURE%" == "false" goto postSecure

rem Make Phoenix run with security Manager enabled
set PHOENIX_SM="-Djava.security.manager"

:postSecure

rem
rem -Djava.ext.dirs= is needed as some JVM vendors do foolish things
rem like placing jaxp/jaas/xml-parser jars in ext dir
rem thus breaking Phoenix
rem

rem Kicking the tires and lighting the fires!!!
%PHOENIX_JAVACMD% -Djava.ext.dirs=%PHOENIX_HOME%\lib -Dphoenix.home=%PHOENIX_HOME% -Djava.security.policy=jar:file:%PHOENIX_HOME%/bin/phoenix-loader.jar!/META-INF/java.policy %PHOENIX_JVM_OPTS% %PHOENIX_SECURE% -jar %PHOENIX_HOME%\bin\phoenix-loader.jar %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
