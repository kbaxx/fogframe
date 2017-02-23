#!/usr/bin/env bash
docker build -t fogcell .
docker rm -f fogcell
docker run -ti -p 8081:8081 --name fogcell -v /usr/bin/docker:/usr/bin/docker -v /var/run/docker.sock:/var/run/docker.sock --link redisFC fogcell