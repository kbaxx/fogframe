#!/usr/bin/env bash
docker run -d -p 6381:6379 --name redisFC redis:latest
docker start redisFC