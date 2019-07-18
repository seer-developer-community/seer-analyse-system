#!/bin/sh
CURRENT_PATH=$(dirname $0)
APP_HOME=$(readlink -f $CURRENT_PATH/..)

pids=`(ps -ef | grep $APP_HOME | grep -v "grep") | awk -F " " '{print $2}'`
for pid in ${pids[*]}
do
	echo "$APP_HOME $pid"
done

