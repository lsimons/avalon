@echo off

echo -------------------
echo LogKit Build System
echo -------------------

set ANT_HOME=tools

%ANT_HOME%\bin\ant.bat -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8
set ANT_HOME=

