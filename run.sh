#!/usr/bin/env bash
#docker build -t kotlin-async .
docker run -v $(pwd)/output:/app/output -e MAX=10000000000 -e SCALE=1000000000 --cpus=4 kotlin-async