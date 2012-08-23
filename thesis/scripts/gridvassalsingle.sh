#!/bin/bash
#arg 1 is lord host
#arg 2 is number of threads to use
THESIS_DIR=/home/sryza/thesis/dco/thesis
JARS_DIR=$THESIS_DIR/jars
JAVA_CLASSPATH=$THESIS_DIR/bin/:$JARS_DIR/libthrift-0.7.0.jar:$JARS_DIR/supportjars/commons-codec-1.3.jar:$JARS_DIR/supportjars/commons-logging-1.1.1.jar:$JARS_DIR/supportjars/httpcore-4.0.1.jar:$JARS_DIR/supportjars/log4j-1.2.14.jar:$JARS_DIR/supportjars/slf4j-api-1.5.8.jar:$JARS_DIR/supportjars/commons-lang-2.5.jar:$JARS_DIR/supportjars/httpclient-4.0.1.jar:$JARS_DIR/supportjars/servlet-api-2.5.jar:$JARS_DIR/supportjars/slf4j-log4j12-1.5.8.jar

java -classpath $JAVA_CLASSPATH bnb.tsp.run.VassalMain $1 1 $2