#!/usr/bin/env bash

mvn package
docker build . -t auth0-demo
docker kill auth0-demo > /dev/null 2>&1
docker rm -f auth0-demo > /dev/null 2>&1
docker run -t -p 8080:8080 --rm --name auth0-demo auth0-demo