#!/bin/sh
# -----------------------------------------------------------------------------
# run.sh - Unix Run Script for Apache Cocoon
#
# $Id: cocoon.sh,v 1.2 2001/07/19 09:54:39 donaldp Exp $
# -----------------------------------------------------------------------------

# ----- Verify and Set Required Environment Variables -------------------------
 
if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Verify and Set Required Environment Variables -------------------------

if [ "$COCOON_LIB" = "" ] ; then
  COCOON_LIB=../lib
fi

if [ "$COCOON_WORK" = "" ] ; then
  COCOON_WORK=../work
  mkdir $COCOON_WORK
fi

# ----- Set Up The Runtime Classpath ------------------------------------------

CP=`echo $COCOON_LIB/*.jar | tr ' ' ':'`:$JAVA_HOME/lib/tools.jar:$COCOON_WORK:$CLASSPATH:`echo ../../lib/*.jar | tr ' ' ':'`:`echo ../../build/lib/*.jar | tr ' ' ':'`

# ----- Run Cocoon ------------------------------------------------------------

$JAVA_HOME/bin/java $COCOON_OPTS -classpath $CP org.apache.cocoon.Main $*



