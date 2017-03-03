#!/usr/bin/env bash
docker rm -f redisFC
docker run -d -p 6381:6379 --name redisFC hypriot/rpi-redis:latest

sh stopMonitor.sh
rm hostmonitor.log
nohup java -jar hostmonitor-0.0.1-SNAPSHOT.jar > hostmonitor.log &

docker build -t fogcell .
docker rm -f fogcell
docker run -it -p 8081:8081 --name fogcell -v /usr/bin/docker:/usr/bin/docker -v /var/run/docker.sock:/var/run/docker.sock --link redisFC fogcell
