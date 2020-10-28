#!/bin/sh
set -e
# Bistoury Agent for Java installation script
#


mkdir -p /kfz/gzzd

if [ -f /kfz/gzzd/agent-bin/pid/bistoury-agent.pid ] ;then
  cd /kfz/gzzd/agent-bin/bin/
  ./bistoury-agent.sh  stop
fi

cd /kfz/gzzd
rm -rf agent-bin agent-bin.tgz

proxyServerUrl=$1

wget  http://${proxyServerUrl}/agent-bin.tgz

tar -zxvf agent-bin.tgz

cd agent-bin/bin


sed -i 's,BISTOURY_APP_LIB_CLASS,BISTOURY_PROXY_HOST="'${proxyServerUrl}'"\nBISTOURY_APP_LIB_CLASS,' bistoury-agent-env.sh

target_pid=`jps | grep -iv jps | awk '{print $1}' | sort -n -k 1 | head -n 1`



./bistoury-agent.sh -p ${target_pid} -j $JAVA_HOME start

