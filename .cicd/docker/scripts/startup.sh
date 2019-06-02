#!/bin/sh

current_path=$(dirname $(readlink -f $0))
java $JAVA_OPTS -jar ${current_path}/service.jar $@