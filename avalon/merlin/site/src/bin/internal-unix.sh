#! /bin/sh

echo "Starting Merlin $MERLIN_VERSION."
echo "===================="
echo "            Java Home: $JAVA_HOME"
echo "          Merlin Home: $MERLIN_HOME"
echo "      Security policy: $MERLIN_HOME/bin/security.policy"
echo "          JVM Options: $MERLIN_JVM_OPTS"
echo "        Bootstrap JAR: $MERLIN_BOOTSTRAP_JAR"
echo "     Merlin Arguments: $MERLIN_ARGS $@"
echo ""

"$JAVA_HOME/bin/java" $MERLIN_JVM_OPTS "-Djava.security.policy=$MERLIN_HOME/bin/security.policy" "-Djava.ext.dirs=$MERLIN_HOME/ext" -jar "$MERLIN_BOOTSTRAP_JAR" $MERLIN_ARGS "$@"
 
