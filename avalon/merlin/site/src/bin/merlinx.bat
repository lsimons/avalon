@echo off

if "%MERLIN_HOME%" == "" set MERLIN_HOME=%USERPROFILE%\.merlin

set MERLIN_SYSTEM_REPOSITORY=%USERPROFILE%\.maven\repository
if "%MAVEN_HOME_LOCAL%" == "" goto DoneMerlinSystem
set MERLIN_SYSTEM_REPOSITORY=%MAVEN_HOME_LOCAL%\repository
:DoneMerlinSystem

set MERLIN_USER_REPOSITORY=%USERPROFILE%\.maven\repository
if "%MAVEN_HOME_LOCAL%" == "" goto DoneMerlinUser
set MERLIN_USER_REPOSITORY=%MAVEN_HOME_LOCAL%\repository
:DoneMerlinUser

set MERLIN_CMD_LINE_ARGS=%*
set MERLIN_BOOTSTRAP_JAR=%MERLIN_HOME%\bin\lib\merlin-bootstrap-1.0.jar
set MERLIN_EXT_DIR=%MERLIN_HOME%\ext
java -Djava.security.policy=%MERLIN_HOME%\bin\security.policy -Dmerlin.home=%MERLIN_HOME% -Dmerlin.system.repository=%MERLIN_SYSTEM_REPOSITORY% -Dmerlin.local.repository=%MERLIN_USER_REPOSITORY% -Djava.ext.dirs=%MERLIN_EXT_DIR% -jar %MERLIN_BOOTSTRAP_JAR% %MERLIN_CMD_LINE_ARGS%
goto :EndOfScript

:EndOfScript


