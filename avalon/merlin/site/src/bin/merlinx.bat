@echo off

if "%MERLIN_HOME%" == "" set MERLIN_HOME=%USERPROFILE%\.merlin

set WORKING_REPOSITORY=%USERPROFILE%\.maven\repository
if "%MAVEN_HOME_LOCAL%" == "" goto DoneMerlinSystem
set WORKING_REPOSITORY=%MAVEN_HOME_LOCAL%\repository
:DoneMerlinSystem

set MERLIN_CMD_LINE_ARGS=%*
set MERLIN_BOOTSTRAP_JAR=%MERLIN_HOME%\bin\lib\merlin-cli-3.2.jar
set MERLIN_EXT_DIR=%MERLIN_HOME%\ext
java -Djava.security.policy=%MERLIN_HOME%\bin\security.policy -Djava.ext.dirs=%MERLIN_EXT_DIR% -jar %MERLIN_BOOTSTRAP_JAR% %MERLIN_CMD_LINE_ARGS% -system %WORKING_REPOSITORY% -repository %WORKING_REPOSITORY% 
goto :EndOfScript

:EndOfScript
