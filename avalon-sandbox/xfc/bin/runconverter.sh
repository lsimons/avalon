#!/bin/bash
#
# Startup script.
#

#
# Determine if JAVA_HOME is set and if so then use it
#
if [ -z "$JAVA_HOME" ] ;  then
  JAVA=`which java`
  if [ -z "$JAVA" ] ; then
    echo "Cannot find JAVA. Please set your PATH."
    exit 1
  fi
  JAVA_BINDIR=`dirname $JAVA`
  JAVA_HOME=$JAVA_BINDIR/..
fi

if [ "$JAVACMD" = "" ] ; then
   # it may be defined in env - including flags!!
   JAVACMD=$JAVA_HOME/bin/java
fi

# Main.java has hard coded config values so this script must be run from
# xfc/bin (any better ideas ?)
XFC_HOME=..

#
# Build the runtime classpath
#
for i in ${XFC_HOME}/build/lib/*.jar ; do
    CP=${CP}:$i
done

CP=${CP}:${XFC_HOME}/build/classes

echo $CP

# Run the example application
$JAVACMD -classpath $CP org.apache.excalibur.xfc.Main $@

