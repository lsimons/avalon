#! /bin/sh

echo "Starting Merlin $MERLIN_VERSION."
echo "===================="
echo "             Platform: $PLATFORM"
echo "            Java Home: $JAVA_HOME"
echo "          Merlin Home: $MERLIN_HOME"
echo "      Security policy: $MERLIN_HOME/bin/security.policy"
echo "          JVM Options: $MERLIN_JVM_OPTS"
echo "        Bootstrap JAR: $MERLIN_BOOTSTRAP_JAR"
echo "     Merlin Arguments: $MERLIN_ARGS $@"
echo ""

JAVA="$JAVA_HOME/bin/java"

ARGS="$MERLIN_JVM_OPTS \"-Djava.security.policy=$MERLIN_HOME/bin/security.policy\" \"-Djava.ext.dirs=$MERLIN_HOME/ext\" -jar \"$MERLIN_BOOTSTRAP_JAR\" $MERLIN_ARGS $@"
echo -n "$JAVA" >$MERLIN_HOME/command-line.log
echo "$ARGS" >>$MERLIN_HOME/command-line.log

echo $ARGS | xargs "$JAVA"

 
