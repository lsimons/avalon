@echo off
if "%MERLIN_HOME%" == "" set MERLIN_HOME=%USERPROFILE%\.merlin
if "%REPOSITORY_HOME%" == "" set REPOSITORY_HOME=%MERLIN_HOME%
set CMD_LINE_ARGS=%*
set BOOTSTRAP_JAR=%REPOSITORY_HOME%\system\avalon-repository\jars\avalon-repository-cli-@AVALON_CLI_VERSION@.jar
set SECURITY_POLICY=-Djava.security.policy=%REPOSITORY_HOME%\bin\security.policy
java %SECURITY_POLICY% %REPOSITORY_JVM_OPTS% -jar %BOOTSTRAP_JAR% %CMD_LINE_ARGS%
goto EndOfScript
:EndOfScript
