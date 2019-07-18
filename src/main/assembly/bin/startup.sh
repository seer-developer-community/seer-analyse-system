#!/bin/sh
CURRENT_PATH=$(dirname $0)
APP_HOME=$(readlink -f $CURRENT_PATH/..)
echo "APP_HOME":$APP_HOME

JAVA_OPTS="-Xmx500M -Xms500M -Xss256k -XX:+UseG1GC"

JAVA_CMD="java"

CLASS_PATH="$APP_HOME/conf:$APP_HOME/lib/*"

MAIN_CLASS="org.springframework.boot.loader.JarLauncher"

EXE_CMD="$JAVA_CMD $JAVA_OPTS -cp $CLASS_PATH $MAIN_CLASS"
echo "EXE_CMD:"$EXE_CMD

$EXE_CMD &
