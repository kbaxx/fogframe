#!/usr/bin/env bash
docker run -d -p 6380:6379 --name redisFCN redis:latest
docker start redisFCN

docker run -d -p 6383:6379 --name redisShared redis:latest
docker start redisShared

docker run -d -p 6381:6379 --name redisFC redis:latest
docker start redisFC

docker run -d -p 6382:6379 --name redisCFM redis:latest
docker start redisCFM