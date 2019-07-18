#!/bin/sh
CURRENT_PATH=$(dirname $0)
APP_HOME=$(readlink -f $CURRENT_PATH/..)

echo "$APP_HOME restart .."
$APP_HOME/bin/stop.sh &
sleep 1
$APP_HOME/bin/startup.sh &
echo "$APP_HOME restart success"

