#! /bin/sh

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

RUN_CMD="$JAVA_HOME/bin/java -Djava.security.policy=$MERLIN_HOME/bin/security.policy -Dmerlin.home=$MERLIN_HOME -Djava.ext.dirs=$MERLIN_HOME/ext -jar $MERLIN_HOME/bin/lib/merlin-cli-3.2-dev.jar $*"
echo "RUN CMD IS: $RUN_CMD"
exec $RUN_CMD
