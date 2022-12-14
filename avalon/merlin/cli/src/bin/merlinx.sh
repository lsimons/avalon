#! /bin/sh
#

echo "DEPRECATED!!!"
echo "  use 'merlin' instead. This file will disappear in Merlin 4.0"
echo ""

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
esac

# Checking for JAVA_HOME is required on *nix due
# to some distributions stupidly including kaffe in /usr/bin
if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi

# Checking for JAVA_HOME is required on *nix due
# to some distributions stupidly including kaffe in /usr/bin
if [ "$MERLIN_HOME" = "" ] ; then
  echo "ERROR: MERLIN_HOME not found in your environment."
  echo
  echo "Please, set the MERLIN_HOME variable in your environment to match the"
  echo "location of Merlin distribution."
  exit 1
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  # HOME is always in Unix format
  JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  MERLIN_HOME=`cygpath --unix "$MERLIN_HOME"`
  [ -n "$MERLIN_HOME_LOCAL" ] && MERLIN_HOME_LOCAL=`cygpath --unix "$MERLIN_HOME_LOCAL"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# Checking for REPOSITORY
if [ "$MAVEN_HOME_LOCAL" = "" ] 
then
 REPOSITORY="$HOME/.maven/repository"
else
 REPOSITORY="$MAVEN_HOME_LOCAL/repository"
fi

JAVA=$JAVA_HOME/bin/java

# switch necessary paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  MERLIN_HOME=`cygpath --windows "$MERLIN_HOME"`
  REPOSITORY=`cygpath --windows "$REPOSITORY"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

MERLIN_BOOTSTRAP_JAR=$MERLIN_HOME/system/merlin/jars/@MERLIN_CLI_JAR@

echo "Starting Merlin."
echo "================"
echo "      Security policy: $MERLIN_HOME/bin/security.policy"
echo "        Bootstrap JAR: $MERLIN_BOOTSTRAP_JAR"
echo "               System: $REPOSITORY"
echo "           Repository: $REPOSITORY"
echo ""

"$JAVA" $MERLIN_JVM_OPTS "-Djava.security.policy=$MERLIN_HOME/bin/security.policy" -jar "$MERLIN_BOOTSTRAP_JAR" -system "$REPOSITORY" -repository "$REPOSITORY" "$@"
