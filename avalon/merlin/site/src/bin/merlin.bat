@echo off
if "%MERLIN_HOME%" == "" set MERLIN_HOME=%USERPROFILE%\.merlin

:MerlinRuntime
set MERLIN_CMD_LINE_ARGS=%*
set MERLIN_BOOTSTRAP_JAR=%MERLIN_HOME%\bin\lib\merlin-bootstrap-1.1.jar
set MERLIN_EXT_DIR=%MERLIN_HOME%\ext
java -Djava.security.policy=%MERLIN_HOME%\bin\security.policy -Dmerlin.home=%MERLIN_HOME% -Djava.ext.dirs=%MERLIN_EXT_DIR% -jar %MERLIN_BOOTSTRAP_JAR% %MERLIN_CMD_LINE_ARGS%
goto EndOfScript

:EndOfScript
