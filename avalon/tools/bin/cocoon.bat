@echo off
:: -----------------------------------------------------------------------------
:: run.bat - Win32 Run Script for Apache Cocoon
::
:: $Id: cocoon.bat,v 1.1 2001/07/16 20:12:41 bloritsch Exp $
:: -----------------------------------------------------------------------------

:: ----- Verify and Set Required Environment Variables -------------------------

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
goto cleanup
:gotJavaHome

:: ----- Verify and Set Required Environment Variables -------------------------

if not "%COCOON_LIB%" == "" goto gotCocoonLib
set COCOON_LIB=..\lib
:gotCocoonLib

if not "%COCOON_WORK%" == "" goto gotCocoonWork
set COCOON_WORK=..\work
:gotCocoonWork

:: ----- Set Up The Runtime Classpath ------------------------------------------

set CP=%JAVA_HOME%\lib\tools.jar;%COCOON_WORK%
for %%i in (%COCOON_LIB%\*.jar) do call lcp.bat %%i
for %%i in (..\..\lib\*.jar) do call lcp.bat %%i
for %%i in (..\..\build\lib\*.jar) do call lcp.bat %%i

:: ----- Run Cocoon ------------------------------------------------------------

if "%OS%" == "Windows_NT" goto nt
%JAVA_HOME%\bin\java.exe %COCOON_OPTS% -classpath %CP% org.apache.cocoon.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
goto cleanup
:nt
%JAVA_HOME%\bin\java.exe %COCOON_OPTS% -classpath %CP% org.apache.cocoon.Main %*

:: ----- Cleanup the environment -----------------------------------------------

:cleanup
set CP=


