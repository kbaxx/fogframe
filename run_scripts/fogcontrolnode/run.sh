#!/usr/bin/env bash
docker rm -f redisFCN
docker run -d -p 6380:6379 --name redisFCN hypriot/rpi-redis:latest

docker run -d -p 6383:6379 --name redisShared hypriot/rpi-redis:latest
docker start redisShared

sh stopMonitor.sh
rm hostmonitor.log
nohup java -jar hostmonitor-0.0.1-SNAPSHOT.jar > hostmonitor.log &

docker build -t fogcontrolnode .
docker rm -f fogcontrolnode
docker run -ti -p 8080:8080 --name fogcontrolnode -v /usr/bin/docker:/usr/bin/docker -v /var/run/docker.sock:/var/run/docker.sock --link redisFCN fogcontrolnode
