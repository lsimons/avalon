@echo off

rem set JAVA CMD
if not "%JAVA_HOME%" == "" goto USE_JAVA_HOME

set JAVA=java

goto SET_METRO

:USE_JAVA_HOME
set JAVA=%JAVA_HOME%\bin\java

:SET_METRO
if "%METRO_HOME%" == "" set METRO_HOME=%USERPROFILE%\.metro
if "%METRO_CACHE%" == "" set METRO_CACHE=%METRO_HOME%\main
set METRO_CMD_LINE_ARGS=%*
set METRO_SECURITY_POLICY=-Djava.security.policy=%METRO_HOME%\bin\security.policy
set METRO_CLASSPATH=@WINDOWS-CLI-CLASSPATH@

:RUN_METRO
%JAVA% -Dmetro.home=%METRO_HOME% -Dmetro.initial.cache=%METRO_CACHE% %METRO_SECURITY_POLICY% %METRO_JVM_OPTS% -classpath %METRO_CLASSPATH% org.apache.metro.transit.Main %METRO_CMD_LINE_ARGS%
goto EndOfScript
:EndOfScript
