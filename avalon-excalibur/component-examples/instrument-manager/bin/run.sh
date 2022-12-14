#!/bin/bash 
# 
# Copyright 2004 The Apache Software Foundation
# Licensed  under the  Apache License,  Version 2.0  (the "License");
# you may not use  this file  except in  compliance with the License.
# You may obtain a copy of the License at 
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed  under the  License is distributed on an "AS IS" BASIS,
# WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
# implied.
#  
# See the License for the specific language governing permissions and
# limitations under the License.
#
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
# altprofile/bin (any better ideas ?)
EXAMPLE_HOME=..

#
# Build the runtime classpath
#
for i in ${EXAMPLE_HOME}/lib/*.jar ; do
    CP=${CP}:$i
done

#echo $CP

# Run the example application
$JAVACMD -classpath $CP org.apache.avalon.excalibur.component.example_im.Main $@

