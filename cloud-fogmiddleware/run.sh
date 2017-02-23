#!/usr/bin/env bash
docker build -t cloud-fogmiddleware .
docker rm -f cloud-fogmiddleware
docker run -ti -p 8082:8082 --name cloud-fogmiddleware -v /usr/bin/docker:/usr/bin/docker -v /var/run/docker.sock:/var/run/docker.sock --link redisCFM cloud-fogmiddleware