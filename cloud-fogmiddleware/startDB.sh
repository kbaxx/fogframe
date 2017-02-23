#!/usr/bin/env bash
docker run -d -p 6382:6379 --name redisCFM redis:latest
docker start redisCFM