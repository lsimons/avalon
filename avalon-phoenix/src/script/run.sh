#! /bin/sh
#
# Phoenix start script.
#
# Author: Peter Donald <donaldp@apache.org>
#
# The user may choose to supply parameters to the JVM (such as memory settings)
# via setting the environment variable PHOENIX_JVM_OPTS.
#
# The user may also disable the security manager by setting PHOENIX_SECURE=false
#

# Checking for JAVA_HOME is required on *nix due
# to some distributions stupidly including kaffe in /usr/bin
if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi

#
# Locate where phoenix is in filesystem
#
THIS_PROG=`dirname $0`

if [ "$THIS_PROG" = "." ] ; then
  THIS_PROG=$PWD
fi

PHOENIX_HOME=$THIS_PROG/..
unset THIS_PROG

# echo "Home directory: $PHOENIX_HOME"
# echo "Home ext directory: $PHOENIX_HOME/lib"

#
# Command to overide JVM ext dir
#
# This is needed as some JVM vendors do foolish things
# like placing jaxp/jaas/xml-parser jars in ext dir
# thus breaking Phoenix
#
PROPS="-Djava.ext.dirs=$PHOENIX_HOME/lib -Dphoenix.home=$PHOENIX_HOME"
LOADER_JAR="$PHOENIX_HOME/bin/phoenix-loader.jar"
POLICY="-Djava.security.policy=jar:file:$LOADER_JAR!/META-INF/java.policy"
JVM_OPTS="$PROPS $POLICY $PHOENIX_JVM_OPTS"

if [ "$PHOENIX_SECURE" != "false" ] ; then
  # Make phoenix run with security manager enabled
  JVM_OPTS="$JVM_OPTS -Djava.security.manager"
fi

# Kicking the tires and lighting the fires!!!
$JAVA_HOME/bin/java $JVM_OPTS -jar $LOADER_JAR $*
