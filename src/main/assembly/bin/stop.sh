#!/bin/sh
CURRENT_PATH=$(dirname $0)
APP_HOME=$(readlink -f $CURRENT_PATH/..)

echo "$APP_HOME stop ...."

pids=`(ps -ef | grep $APP_HOME | grep -v "grep") | awk -F " " '{print $2}'`
for pid in ${pids[*]}
do
	kill -9 $pid
done

echo "$APP_HOME stop success"
