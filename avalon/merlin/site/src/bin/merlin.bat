@echo off
if "%MERLIN_HOME%" == "" set MERLIN_HOME=%USERPROFILE%\.merlin
set MERLIN_CMD_LINE_ARGS=%*
set MERLIN_BOOTSTRAP_JAR=%MERLIN_HOME%\bin\lib\merlin-cli-3.2.jar
set MERLIN_SECURITY_POLICY=-Djava.security.policy=%MERLIN_HOME%\bin\security.policy
java %MERLIN_SECURITY_POLICY% %MERLIN_JVMARGS% -jar %MERLIN_BOOTSTRAP_JAR% %MERLIN_CMD_LINE_ARGS%
goto EndOfScript
:EndOfScript
