#!/usr/bin/env bash
docker build -t fogcontrolnode .
docker rm -f fogcontrolnode
docker run -ti -p 8080:8080 --name fogcontrolnode -v /usr/bin/docker:/usr/bin/docker -v /var/run/docker.sock:/var/run/docker.sock --link redisFCN fogcontrolnode