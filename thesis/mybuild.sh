SRC1=$(find src/main/java/* | grep java$)
SRC3=$(find src/main/resources/gen-java/* | grep java$)

JARS_DIR=jars
CLASSPATH=$JARS_DIR/libthrift-0.7.0.jar:$JARS_DIR/supportjars/jackson-core-asl-1.9.4.jar:$JARS_DIR/supportjars/jackson-mapper-asl-1.9.4.jar:$JARS_DIR/supportjars/JaCoP-3.1.2.jar:$JARS_DIR/supportjars/hadoop-0.20.1-core.jar:$JARS_DIR/supportjars/commons-codec-1.3.jar:$JARS_DIR/supportjars/commons-logging-1.1.1.jar:$JARS_DIR/supportjars/httpcore-4.0.1.jar:$JARS_DIR/supportjars/log4j-1.2.14.jar:$JARS_DIR/supportjars/slf4j-api-1.5.8.jar:$JARS_DIR/supportjars/commons-lang-2.5.jar:$JARS_DIR/supportjars/httpclient-4.0.1.jar:$JARS_DIR/supportjars/servlet-api-2.5.jar:$JARS_DIR/supportjars/slf4j-log4j12-1.5.8.jar

CMD="javac -cp $CLASSPATH $SRC1 $SRC3 -d bin/"
echo $CMD
$CMD
